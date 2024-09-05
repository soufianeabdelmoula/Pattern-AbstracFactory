package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.dto.GroupeADDto;
import fr.vdm.referentiel.refadmin.mapper.GroupeADMapper;
import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;
import fr.vdm.referentiel.refadmin.model.ad.*;
import fr.vdm.referentiel.refadmin.repository.CompteApplicatifRepository;
import fr.vdm.referentiel.refadmin.repository.ad.*;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.service.GroupeADService;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class GroupeADServiceImpl implements GroupeADService {

    private static final Log LOGGER =
            LogFactory.getLog(GroupeADServiceImpl.class);

    @Autowired
    GroupeADRepository groupeADRepository;

    @Autowired
    GroupeTechniqueADRepository groupeTechniqueAdRepository;

    @Autowired
    GroupeTechniqueInterneADRepository groupeTechniqueInterneAdRepository;

    @Autowired
    private ActeurADRepository acteurADRepository;

    @Autowired
    private CelluleService celluleService;

    @Autowired
    private LdapTemplate ldapTemplate;
    @Autowired
    private CompteServiceADRepository compteServiceADRepository;
    @Autowired
    private CompteRessourceADRepository compteRessourceADRepository;
    @Autowired
    private CompteApplicatifRepository compteApplicatifRepository;
    @Autowired
    private CompteApplicatifADRepository compteApplicatifADRepository;

    public void ajouterActeurGroupeApplicatif(Name dnActeur, List<GroupeAD> listeGroupes) throws ServiceException {
        for (GroupeAD groupe : listeGroupes) {
            // Si notre acteur n'est pas membre de ce groupe de l'offre, il faut l'ajouter
            if (!groupe.getMembers().contains(dnActeur)) {
                // On ajoute l'acteur au groupe representant l'offre
                LOGGER.info(String.format("Ajout de l'acteur %s au groupe %s", dnActeur, groupe.getDn()));
                groupe.getMembers().add(dnActeur);
                this.getGroupeADRepository().save(groupe);
                LOGGER.info(String.format(" OK - l'acteur %s a été ajouté au groupe %s", dnActeur.toString(), groupe.getDn().toString()));
            } else {
                LOGGER.error(String.format(" NO - l'acteur %s n' a pas été ajouté au groupe %s. L'acteur est déjà un membre du groupe", dnActeur.toString(), groupe.getDn().toString()));
                throw new ServiceException(String.format(" NO - l'acteur %s n' a pas été ajouté au groupe %s. L'acteur est déjà un membre du groupe", dnActeur.toString(), groupe.getDn().toString()));
            }
        }
    }

    public void ajouterActeurGroupeApplicatif(ActeurAD acteur, List<GroupeAD> listeGroupes) throws ServiceException {
        this.ajouterActeurGroupeApplicatif(acteur.getDn(), listeGroupes);
    }

    @Override
    public void ajouterActeurGroupeApplicatif(String loginActeur, List<GroupeAD> listeGroupes) throws ServiceException {
        LOGGER.info(String.format("Recherche de l'acteur de login %s dans l'annuaire", loginActeur));
        ActeurAD acteurAD = this.getActeurADRepository().findByIdentifiant(loginActeur);

        // On vérifie que l'acteur existe dans le LDAP
        if (acteurAD != null) {
            this.ajouterActeurGroupeApplicatif(acteurAD, listeGroupes);
        } else {
            LOGGER.error(String.format("Acteur %s non trouvé dans l'annuaire, aucune action effectuée.", loginActeur));
            throw new ServiceException(String.format("Acteur %s non trouvé dans l'annuaire, aucune action effectuée.", loginActeur));
        }
    }

    public List<GroupeAD> listeGroupeApplicatifToGroupeAD(Name dnActeur, List<GroupeApplicatif> listeGroupes){

        ArrayList<GroupeAD> listeGroupeAD = new ArrayList<>();
        for(GroupeApplicatif groupe : listeGroupes){
            try {
                Name dnGroupe = new LdapName(groupe.getDn());
                GroupeAD groupeAD = this.getGroupeADRepository().findById(dnGroupe).orElse(null);
            if(null == groupeAD){
                LOGGER.error(String.format("le groupe %s n'a pas été trouvé dans l'annuaire", dnGroupe));
            }
            listeGroupeAD.add(groupeAD);
            } catch (InvalidNameException e) {
                LOGGER.error(String.format("Erreur lors de la creation du DistinguishedName %s",groupe.getDn()));
            }
        }
        return listeGroupeAD;
    }

    public List<GroupeADDto> getGroupesActeur(String login) throws ServiceException {
        List<Name> dns;
        ActeurAD acteur = this.acteurADRepository.findByIdentifiant(login);
        if (acteur != null) {
            dns = acteur.getMemberOf().stream().map(DnUtils::truncateDn).collect(Collectors.toList());
        } else {
            CompteServiceAD cs = compteServiceADRepository.findByIdentifiant(login);
            if (cs != null) {
                dns = cs.getMemberOf().stream().map(DnUtils::truncateDn).collect(Collectors.toList());
            } else {
                CompteRessourceAD cr = compteRessourceADRepository.findByIdentifiant(login);
                if (cr != null) {
                    dns = cr.getMemberOf().stream().map(DnUtils::truncateDn).collect(Collectors.toList());
                } else {
                    CompteApplicatifAD ca = compteApplicatifADRepository.findByIdentifiant(login);
                    if (ca != null) {
                        dns = ca.getMemberOf().stream().map(DnUtils::truncateDn).collect(Collectors.toList());
                    } else {
                        LOGGER.error(String.format("L'acteur %s n'a pas été trouvé dans l'annuaire.", login));
                        return null;
                    }
                }
            }
        }
        return GroupeADMapper.INSTANCE.groupesADToGroupesADDto(this.groupeADRepository.findAllById(dns));
    }

    public GroupeADDto getGroupeByCn(String cn) {
        return GroupeADMapper.INSTANCE.groupeADToGroupeADDto(this.groupeADRepository.findByCommonName(cn));
    }

    public GroupeADDto getGroupeTechniqueByCn(String cn) {
        return GroupeADMapper.INSTANCE.groupeTechniqueADToGroupeADDto(this.groupeTechniqueAdRepository.findByCommonName(cn));
    }

    @Override
    public GroupeADDto getGroupeInterneByCn(String cn) {
        return null;
    }

    @Override
    public GroupeADDto getGroupeExterneByCn(String cn) {
        return null;
    }

    @Override
    public GroupeADDto getGroupeTechniqueInterneByCn(String cn) {
        return GroupeADMapper.INSTANCE.groupeTechniqueInterneADToGroupeADDto(this.groupeTechniqueInterneAdRepository.findByCommonName(cn));

    }

    @Override
    public GroupeADDto getGroupeTechniqueExterneByCn(String cn) {
        return null;
    }

    /**
     * Renvoie la liste de tous les groupes ORGA ascendants au groupe ORGA dont le cn est
     * pass� en param�tre.
     *
     * @param cnGroupe le cn du groupe
     * @return Liste des groupes ascendants du groupe dont le cn est pass� en
     * param�tre.
     */
    @Override
    public List<GroupeADDto> findGroupesAscendants(final String cnGroupe) throws ServiceException {
        LOGGER.info(String.format("Chargement des groupes ascendants au groupe : %1$s", cnGroupe));

        List<GroupeADDto> listeAscendants = new ArrayList<>();
        GroupeADDto ascendant = selectGroupePere(cnGroupe);
        while (ascendant != null) {
            listeAscendants.add(ascendant);
            ascendant = selectGroupePere(ascendant.getCommonName());

        }
        LOGGER.debug(String.format("Nombre de groupes ascendants trouvés dans LDAP pour le groupe '%1$s' : %2$s",
                cnGroupe, listeAscendants.size()));
        return listeAscendants;
    }


    /**
     * Retourne les informations du groupe LDAP p�re du groupe dont le cn est pass�
     * en param�tre.
     *
     * @param cnGroupe cn du groupe dont on cherche le groupe p�re.
     * @return Les informations du groupe p�re si ce dernier existe, null sinon.
     */
    @Override
    public GroupeADDto selectGroupePere(final String cnGroupe) throws ServiceException {
        CelluleDto cellule = this.celluleService.findCelluleByLibelleLdap(cnGroupe);
        if (cellule != null) {
            CelluleDto parent = this.getCelluleService().findCelluleByCode(cellule.getCodePere());
            if (parent != null && !parent.getLibelleLDAP().isEmpty()) {
                return this.getGroupeByCn(parent.getLibelleLDAP());
            }
        }
        return null;
    }

    /**
     * Affecte un acteur � une liste de groupes.
     *
     * @param login  login de l'acteur � attacher.
     * @param Groupe groupe auxquel on souhaite
     *               affecter l'acteur.
     */
    @Override
    public void affecterActeur(final String login, GroupeADDto groupe, String... typeActeur) throws ServiceException {

        LOGGER.debug(String.format("Affectation de l'acteur '%1$s' au groupe '%2$s' dans l'annuaire AD",
                login, groupe.getDn()));
        try {
            this.addActeur(login, groupe.getDn(), typeActeur);
        } catch (InvalidNameException e) {
            throw new ServiceException(String.format("Erreur lors de l'affectation de l'acteur %s au groupe %s dans l'annuaire. Aucune action effectuée.", login, groupe.getDn()));
        }

    }

    /**
     * Affecte un acteur � une liste de groupes.
     *
     * @param login        login de l'acteur � attacher.
     * @param listeGroupes Liste des groupes auxquels on souhaite
     *                     affecter l'acteur.
     */
    @Override
    public void affecterActeur(final String login, final List<GroupeADDto> listeGroupes, String... typeActeur) throws ServiceException {
        for (GroupeADDto groupe : listeGroupes) {
            LOGGER.debug(String.format("Affectation de l'acteur '%1$s' au groupe '%2$s' dans l'annuaire AD",
                    login, groupe.getDn()));
            try {
                this.addActeur(login, groupe.getDn(), typeActeur);
            } catch (InvalidNameException e) {
                throw new ServiceException(String.format("Erreur lors de l'affectation de l'acteur %s au groupe %s dans l'annuaire. Aucune action effectuée.", login, groupe.getDn()));
            }
        }
    }

    public GroupeAD findByCnAndType(String cn, char type) {
        String typologie = "";
        switch (type) {
            case 'A':
                typologie = ConstanteAD.TYPE_GROUPE_APPLICATIF;
                break;
            case 'F':
                typologie = ConstanteAD.TYPE_GROUPE_FONCTIONNEL;
                break;
            case 'O':
                typologie = ConstanteAD.TYPE_GROUPE_ORGA;
                break;
        }
        return this.findByCnAndType(cn, typologie);
    }

    public GroupeAD findByCnAndType(String cn, String type) {
        return this.groupeADRepository.findByCommonNameAndTypeGroupe(cn, type);
    }

    private void addActeur(String login, Name dnGroupe, String... typeActeur) throws InvalidNameException, ServiceException {
        Name dn;
        if (typeActeur.length == 0 || typeActeur[0].equals(ConstanteActeur.TYPE_AGENT) || typeActeur[0].equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = this.acteurADRepository.findByIdentifiant(login);
            if (acteur == null)
                throw new ServiceException(String.format("Acteur %s non trouvé dans l'annuaire, aucune action effectuée.", login));
            dn = DnUtils.getDnComplet(acteur.getDn());
        } else if (typeActeur[0].equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD acteur = compteServiceADRepository.findByIdentifiant(login);
            if (acteur == null)
                throw new ServiceException(String.format("Acteur %s non trouvé dans l'annuaire, aucune action effectuée.", login));
            dn = DnUtils.getDnComplet(acteur.getDn());
        } else if (typeActeur[0].equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD acteur = compteRessourceADRepository.findByIdentifiant(login);
            if (acteur == null)
                throw new ServiceException(String.format("Acteur %s non trouvé dans l'annuaire, aucune action effectuée.", login));
            dn = DnUtils.getDnComplet(acteur.getDn());
        } else {
            CompteApplicatifAD acteur = compteApplicatifADRepository.findByIdentifiant(login);
            if (acteur == null)
                throw new ServiceException(String.format("Acteur %s non trouvé dans l'annuaire, aucune action effectuée.", login));
            dn = DnUtils.getDnComplet(acteur.getDn());
        }
        GroupeAD groupe = this.ldapTemplate.findByDn(dnGroupe, GroupeAD.class);
        if (groupe == null)
            throw new ServiceException(String.format("Groupe %s non trouvé dans l'annuaire, aucune action effectuée.", dnGroupe.toString()));

        groupe.getMembers().add(dn);
        this.groupeADRepository.save(groupe);
    }


    public void supprimerActeurGroupe(String login, Name dnGroupe) {
        ActeurAD acteur = this.acteurADRepository.findByIdentifiant(login);
        if (acteur != null) {
            GroupeAD groupe = this.groupeADRepository.findByCommonNameAndMember(LDAPUtil.extraireValeurAttributFromDn(dnGroupe.toString(), ConstanteAD.ATTR_AD_CN), DnUtils.getDnComplet(acteur.getDn()).toString());
            if (groupe != null) {
                Attribute attr = new BasicAttribute(ConstanteAD.ATTR_AD_MEMBER, DnUtils.getDnComplet(acteur.getDn()).toString());
                ModificationItem item = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
                ldapTemplate.modifyAttributes(dnGroupe, new ModificationItem[]{item});
            }
        }
    }

    public void supprimerActeurGroupe(String login, String cnGroupe, char typeGroupe) {

        GroupeAD groupe = this.findByCnAndType(cnGroupe, typeGroupe);

        if (groupe != null) {
            supprimerActeurGroupe(login, groupe.getDn());
        }
    }

}