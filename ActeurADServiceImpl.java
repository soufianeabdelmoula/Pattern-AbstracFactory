package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.mapper.ActeurADMapper;
import fr.vdm.referentiel.refadmin.model.Cellule;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.ad.ActeurAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteApplicatifAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteRessourceAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteServiceAD;
import fr.vdm.referentiel.refadmin.model.enums.Operator;
import fr.vdm.referentiel.refadmin.repository.ad.*;
import fr.vdm.referentiel.refadmin.service.ActeurADService;
import fr.vdm.referentiel.refadmin.service.GroupeADService;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.NoPermissionException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;


@Service
@Log4j2
public class ActeurADServiceImpl implements ActeurADService {
    private final GroupeADRepository groupeADRepository;
    private static final Logger K_LOGGER = LoggerFactory.getLogger(ActeurADServiceImpl.class);
    private final ActeurADRepository acteurADRepository;

    @Value("${spring.ldap.base}")
    public String baseDn;

    private final GroupeADService groupeADService;
    private final CompteServiceADRepository compteServiceADRepository;
    private final CompteRessourceADRepository compteRessourceADRepository;
    private final CompteApplicatifADRepository compteApplicatifADRepository;
    private final LdapTemplate ldapTemplate;
    private static final ActeurADMapper acteurADMapper = ActeurADMapper.INSTANCE;


    private List<ActeurAD> acteurADList;

    public ActeurADServiceImpl(ActeurADRepository acteurADRepository, LdapTemplate ldapTemplate, CompteServiceADRepository compteServiceADRepository, CompteRessourceADRepository compteRessourceADRepository, CompteApplicatifADRepository compteApplicatifADRepository, GroupeADService groupeADService,
                               GroupeADRepository groupeADRepository) {
        this.acteurADRepository = acteurADRepository;
        this.ldapTemplate = ldapTemplate;
        this.compteServiceADRepository = compteServiceADRepository;
        this.compteRessourceADRepository = compteRessourceADRepository;
        this.compteApplicatifADRepository = compteApplicatifADRepository;
        this.groupeADService = groupeADService;
        this.groupeADRepository = groupeADRepository;
    }

    @Override
    public ActeurADDto findByIdentifiant(String identifiant, String... type) {
        boolean booTypeInconnu = (type.length == 0);

        if (booTypeInconnu || type[0].equals(ConstanteActeur.TYPE_AGENT) || type[0].equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = acteurADRepository.findByIdentifiant(identifiant);
            if (acteur != null && !booTypeInconnu) return acteurADMapper.acteurADToActeurADDto(acteur);
        }
        if (booTypeInconnu || type[0].equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteService = compteServiceADRepository.findByIdentifiant(identifiant);
            if (compteService != null && !booTypeInconnu)
                return acteurADMapper.compteServiceAdToActeurADDto(compteService);
        }
        if (booTypeInconnu || type[0].equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessource = compteRessourceADRepository.findByIdentifiant(identifiant);
            if (compteRessource != null && !booTypeInconnu)
                return acteurADMapper.compteRessourceAdToActeurADDto(compteRessource);
        }
        if (booTypeInconnu || type[0].equals(ConstanteActeur.TYPE_APPLICATIF)) {
            CompteApplicatifAD compteApplicatif = compteApplicatifADRepository.findByIdentifiant(identifiant);
            if (compteApplicatif != null && !booTypeInconnu)
                return acteurADMapper.compteApplicatifAdToActeurADDto(compteApplicatif);
        }
        return null;
    }

    @Override
    public List<ActeurAD> getAllActeurAD() {
        return acteurADRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassword(String username, String password, String... type) {
        if (type.length == 0 || type[0].equals(ConstanteActeur.TYPE_AGENT) || type[0].equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = this.acteurADRepository.findByIdentifiant(username);
            K_LOGGER.info(String.format("Changement du mot de passe pour l'acteur %s", username));
            String newQuotedPassword = "\"" + password + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes(StandardCharsets.UTF_16LE);

            Attribute attr = new BasicAttribute("unicodePwd", newUnicodePassword);
            ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

            ldapTemplate.modifyAttributes(acteur.getDn(), new ModificationItem[]{item});
        } else if (type[0].equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteServiceAD = this.compteServiceADRepository.findByIdentifiant(username);
            K_LOGGER.info(String.format("Changement du mot de passe pour l'acteur %s", username));
            String newQuotedPassword = "\"" + password + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes(StandardCharsets.UTF_16LE);

            Attribute attr = new BasicAttribute("unicodePwd", newUnicodePassword);
            ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

            ldapTemplate.modifyAttributes(compteServiceAD.getDn(), new ModificationItem[]{item});
        } else if (type[0].equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessourceAD = this.compteRessourceADRepository.findByIdentifiant(username);
            K_LOGGER.info(String.format("Changement du mot de passe pour l'acteur %s", username));
            String newQuotedPassword = "\"" + password + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes(StandardCharsets.UTF_16LE);

            Attribute attr = new BasicAttribute("unicodePwd", newUnicodePassword);
            ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

            ldapTemplate.modifyAttributes(compteRessourceAD.getDn(), new ModificationItem[]{item});
        }
    }

    public void desactiver(String login, String... type) throws ServiceException {
        K_LOGGER.info(String.format("Désactivation du compte %s", login));
        if (type.length == 0 || type[0].equals(ConstanteActeur.TYPE_AGENT) || type[0].equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = this.acteurADRepository.findByIdentifiant(login);
            if (acteur == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(acteur.getUserAccountControl());
            userAccountControl += ConstanteActeur.UF_ACCOUNTDISABLE;
            acteur.setUserAccountControl(String.valueOf(userAccountControl));
            this.acteurADRepository.save(acteur);
        } else if (type[0].equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteServiceAD = this.compteServiceADRepository.findByIdentifiant(login);
            if (compteServiceAD == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(compteServiceAD.getUserAccountControl());
            userAccountControl += ConstanteActeur.UF_ACCOUNTDISABLE;
            compteServiceAD.setUserAccountControl(String.valueOf(userAccountControl));
            this.compteServiceADRepository.save(compteServiceAD);
        } else if (type[0].equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessourceAD = this.compteRessourceADRepository.findByIdentifiant(login);
            if (compteRessourceAD == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(compteRessourceAD.getUserAccountControl());
            userAccountControl += ConstanteActeur.UF_ACCOUNTDISABLE;
            compteRessourceAD.setUserAccountControl(String.valueOf(userAccountControl));
            this.compteRessourceADRepository.save(compteRessourceAD);
        } else if (type[0].equals(ConstanteActeur.TYPE_APPLICATIF)) {
            CompteApplicatifAD compteApplicatifAD = this.compteApplicatifADRepository.findByIdentifiant(login);
            if (compteApplicatifAD == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(compteApplicatifAD.getUserAccountControl());
            userAccountControl += ConstanteActeur.UF_ACCOUNTDISABLE;
            compteApplicatifAD.setUserAccountControl(String.valueOf(userAccountControl));
            this.compteApplicatifADRepository.save(compteApplicatifAD);
        } else {
            throw new ServiceException("La typologie du compte acteur n'a pas été reconnue. Aucune action effectuée.");
        }
    }

    public void activer(String login, String... type) throws ServiceException {
        K_LOGGER.info(String.format("Activation du compte %s", login));
        if (type.length == 0 || type[0].equals(ConstanteActeur.TYPE_AGENT) || type[0].equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = this.acteurADRepository.findByIdentifiant(login);
            if (acteur == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(acteur.getUserAccountControl());
            userAccountControl -= ConstanteActeur.UF_ACCOUNTDISABLE;
            acteur.setUserAccountControl(String.valueOf(userAccountControl));
            this.acteurADRepository.save(acteur);
        } else if (type[0].equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteServiceAD = this.compteServiceADRepository.findByIdentifiant(login);
            if (compteServiceAD == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(compteServiceAD.getUserAccountControl());
            userAccountControl -= ConstanteActeur.UF_ACCOUNTDISABLE;
            compteServiceAD.setUserAccountControl(String.valueOf(userAccountControl));
            this.compteServiceADRepository.save(compteServiceAD);
        } else if (type[0].equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessourceAD = this.compteRessourceADRepository.findByIdentifiant(login);
            if (compteRessourceAD == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(compteRessourceAD.getUserAccountControl());
            userAccountControl -= ConstanteActeur.UF_ACCOUNTDISABLE;
            compteRessourceAD.setUserAccountControl(String.valueOf(userAccountControl));
            this.compteRessourceADRepository.save(compteRessourceAD);
        } else if (type[0].equals(ConstanteActeur.TYPE_APPLICATIF)) {
            CompteApplicatifAD compteApplicatifAD = this.compteApplicatifADRepository.findByIdentifiant(login);
            if (compteApplicatifAD == null)
                throw new ServiceException(String.format("Le compte %s n'a pas été trouvé dans l'annuaire. Aucune action effectuée.", login));
            int userAccountControl = Integer.parseInt(compteApplicatifAD.getUserAccountControl());
            userAccountControl -= ConstanteActeur.UF_ACCOUNTDISABLE;
            compteApplicatifAD.setUserAccountControl(String.valueOf(userAccountControl));
            this.compteApplicatifADRepository.save(compteApplicatifAD);
        } else {
            throw new ServiceException("La typologie du compte acteur n'a pas été reconnue. Aucune action effectuée.");
        }
    }

    @Override
    public Boolean isActive(String login, String... typeActeur) {
        if (typeActeur.length == 0 || typeActeur[0].equals(ConstanteActeur.TYPE_AGENT) || typeActeur[0].equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteurAD = this.acteurADRepository.findByIdentifiant(login);
            return (acteurAD != null && (Integer.parseInt(acteurAD.getUserAccountControl()) & ConstanteActeur.UF_ACCOUNTDISABLE) == 0);
        } else if (typeActeur[0].equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteServiceAD = this.compteServiceADRepository.findByIdentifiant(login);
            return (compteServiceAD != null && (Integer.parseInt(compteServiceAD.getUserAccountControl()) & ConstanteActeur.UF_ACCOUNTDISABLE) == 0);
        } else if (typeActeur[0].equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessourceAD = this.compteRessourceADRepository.findByIdentifiant(login);
            return (compteRessourceAD != null && (Integer.parseInt(compteRessourceAD.getUserAccountControl()) & ConstanteActeur.UF_ACCOUNTDISABLE) == 0);
        } else if (typeActeur[0].equals(ConstanteActeur.TYPE_APPLICATIF)) {
            CompteApplicatifAD compteApplicatifAD = this.compteApplicatifADRepository.findByIdentifiant(login);
            return (compteApplicatifAD != null && (Integer.parseInt(compteApplicatifAD.getUserAccountControl()) & ConstanteActeur.UF_ACCOUNTDISABLE) == 0);
        }
        return false;
    }


    /**
     * Affecte l'acteur a toute la hierarchie de groupe correspondant a son
     * affectation terrain si elle existe et son affectation officielle si elle
     * existe.
     *
     * @param acteurADDto l'acteur.
     */
    @Override
    public void affecterHierarchieGroupe(ActeurADDto acteurADDto, boolean simu) throws ServiceException {
        K_LOGGER.info(String.format("Affectation a une hierarchie de groupe pour l'acteur de login : %1$s", acteurADDto.getIdentifiant()));
        // On v�rifie dans un premier temps que l'affectation terrain ou
        // officielle existe
        if (!StringUtils.hasText(acteurADDto.getAffectationTerrain()) && !StringUtils.hasText(acteurADDto.getAffectationOfficielle())) {
            // Notre acteur n'a aucune affectation, on ne peut rien faire ici
            return;
        }

        // On traite l'affectation terrain
        if (StringUtils.hasText(acteurADDto.getAffectationTerrain())) {
            K_LOGGER.debug(String.format(
                    "Affectation terrain a la hierarchie de groupe du groupe '%1$s' pour l'acteur de login : %2$s",
                    acteurADDto.getAffectationTerrain(), acteurADDto.getIdentifiant()));

            affectationGroupe(acteurADDto, simu, acteurADDto.getAffectationTerrain());
        }
        // on traite l'affectation l'officielle.
        if (StringUtils.hasText(acteurADDto.getAffectationOfficielle())) {
            if (StringUtils.hasText(acteurADDto.getAffectationTerrain())
                    && acteurADDto.getAffectationTerrain().equals(acteurADDto.getAffectationOfficielle())) {
                // les deux cas d'affectations sont identiques on ne fait rien
                K_LOGGER.debug(String.format(
                        "Les cas d'affectation officielle et de terrain de groupe sont identiques et ont pour valeur du groupe '%1$s' pour l'acteur de login : %2$s  ",
                        acteurADDto.getAffectationOfficielle(), acteurADDto.getIdentifiant()));
            }
        } else {
            K_LOGGER.debug(String.format(
                    "Affectation officielle a la hierarchie de groupe du groupe '%1$s' pour l'acteur de login : %2$s",
                    acteurADDto.getAffectationOfficielle(), acteurADDto.getIdentifiant()));
            affectationGroupe(acteurADDto, simu, acteurADDto.getAffectationOfficielle());
        }
    }


    /**
     * Affecte l'acteur au groupe passe en parametre si son cn est valide existe.
     *
     * @param acteurADDto l'acteur
     * @param cnGroupe    Cn du groupe � affecter.
     * @param simu        Booleen precisant si le ldap doit etre modifie ou non le ldap
     *                    sera modifi� si simu est � false.
     */
    private void affectationGroupe(final ActeurADDto acteurADDto, boolean simu, String cnGroupe) throws ServiceException {

        String groupeOption = "";

        if (acteurADDto != null && !acteurADDto.getEmployeeType().isEmpty()) {
            if (acteurADDto.getEmployeeType().equals(ConstanteActeur.TYPE_AGENT))
                groupeOption = ConstanteWorkflow.SUFFIXE_GROUPE_INTERNE;
            if (acteurADDto.getEmployeeType().equals(ConstanteActeur.TYPE_EXTERNE))
                groupeOption = ConstanteWorkflow.SUFFIXE_GROUPE_EXTERNE;
        }
        List<GroupeADDto> listeGroupes;

        List<GroupeADDto> listeGroupesTechniques = new ArrayList<>();
        List<GroupeADDto> listeGroupesTechniquesAnnexes = new ArrayList<>();
        List<GroupeADDto> listeGroupesAnnexes = new ArrayList<>();

        if (StringUtils.hasText(cnGroupe)) {
            GroupeADDto groupe = this.groupeADService.getGroupeByCn(cnGroupe);
            GroupeADDto groupeTech = this.groupeADService.getGroupeTechniqueByCn(cnGroupe);

            GroupeADDto groupeTechAnnexe = null;
            GroupeADDto groupeInterneExterne = null;

            // Acteur interne ou externe
            if (!groupeOption.isEmpty()) {
                if (groupeOption.equals(ConstanteWorkflow.SUFFIXE_GROUPE_EXTERNE)) // Externe
                {
                    groupeInterneExterne = this.groupeADService.getGroupeExterneByCn(cnGroupe.concat(groupeOption));
                    groupeTechAnnexe = this.groupeADService.getGroupeInterneByCn(cnGroupe
                            .concat(ConstanteWorkflow.SUFFIXE_GROUPE_TECHNIQUE).concat(ConstanteWorkflow.SUFFIXE_GROUPE_EXTERNE));

                }
                if (groupeOption.equals(ConstanteWorkflow.SUFFIXE_GROUPE_INTERNE)) // Interne
                {
                    groupeInterneExterne = this.groupeADService.getGroupeInterneByCn(cnGroupe.concat(groupeOption));
                    groupeTechAnnexe = this.groupeADService.getGroupeTechniqueInterneByCn(cnGroupe
                            .concat(ConstanteWorkflow.SUFFIXE_GROUPE_TECHNIQUE).concat(ConstanteWorkflow.SUFFIXE_GROUPE_INTERNE));
                }
            }

            if (groupe != null) {
                listeGroupes = this.groupeADService.findGroupesAscendants(cnGroupe);
                if (listeGroupes == null) {
                    listeGroupes = new ArrayList<>();
                }
                for (GroupeADDto groupeParent : listeGroupes) { //ajouts des groupes tech/annexes parents
                    if (!groupeOption.isEmpty()) {

                        GroupeADDto groupeParentAnnexe = null;

                        if (groupeOption.equals(ConstanteWorkflow.SUFFIXE_GROUPE_EXTERNE)) // Externe
                        {
                            groupeParentAnnexe = this.groupeADService.getGroupeExterneByCn(groupeParent.getCommonName().concat(groupeOption));
                        }
                        if (groupeOption.equals(ConstanteWorkflow.SUFFIXE_GROUPE_INTERNE)) // Interne
                        {
                            groupeParentAnnexe = this.groupeADService.getGroupeInterneByCn(groupeParent.getCommonName().concat(groupeOption));
                        }
                        if (groupeParentAnnexe != null) {
                            listeGroupesAnnexes.add(groupeParentAnnexe);
                        }

                    }
                }

                listeGroupes.add(groupe);
                // 29/06/2016 - BGE - AZUR / Fic-jaune
                if (!groupeOption.isEmpty()) {
                    if (groupeTech != null) {
                        listeGroupesTechniques.add(groupeTech);
                    }

                    if (groupeTechAnnexe != null) {
                        listeGroupesTechniquesAnnexes.add(groupeTechAnnexe);
                    }

                    if (groupeInterneExterne != null) {
                        listeGroupesAnnexes.add(groupeInterneExterne);
                    }
                }

                // On n'affecte l'acteur qu'e des groupes dont le niveau n'est
                // pas null et vaut plus de 0 (0 etant le niveau Collectivite).
                if (!simu) {
                    if (acteurADDto != null) {
                        this.groupeADService.affecterActeur(acteurADDto.getIdentifiant(), listeGroupes, acteurADDto.getEmployeeType());

                        //TODO : Affectation des agents et externes aux groupes annexes.
                        /**
                         // Seul les Agents et externes sont affectés aux groupes Tech
                         if (!groupeOption.isEmpty()) {
                         this.groupeADService.affecterActeurTech(acteurADDto.getCn(), listeGroupesTechniques, true, 0);

                         this.groupeADService.affecterActeurTechAnnexes(acteurADDto.getCn(), listeGroupesTechniquesAnnexes, true,
                         0, groupeOption);

                         this.groupeADService.affecterActeurAnnexes(acteurADDto.getCn(), listeGroupesAnnexes, true, 0,
                         groupeOption);
                         }
                         **/
                    }
                } else {
                    throw new NotImplementedException("Impossible de faire la simulation.");
                    /**
                     if (traceSimu != null && traceSimu.isInfoEnabled()) {
                     for (GroupeLDAPDto g : listeGroupes) {
                     // 04/12/2014 - BGE - EVT-9177 : Modification des
                     // traces de simulation

                     traceSimu.info(String.format(
                     "PSEUDO AD - INSERT VIRTUEL DU GROUPE ORGA DE L'ACTEUR / ACTEUR LOGIN %s / GROUPE DN %s",
                     acteurADDto.getAccountName(), g.getDn()));

                     }
                     if (!listeGroupesTechniques.isEmpty()) {
                     for (GroupeLDAPDto g : listeGroupesTechniques) {

                     traceSimu.info(String.format(
                     "PSEUDO AD - INSERT VIRTUEL DU GROUPE TECHNIQUE DE L'ACTEUR / ACTEUR LOGIN %s / GROUPE DN %s",
                     acteurADDto.getAccountName(), g.getDn()));

                     }
                     }
                     if (!listeGroupesTechniquesAnnexes.isEmpty()) {
                     for (GroupeLDAPDto g : listeGroupesTechniquesAnnexes) {
                     traceSimu.info(String.format(
                     "PSEUDO AD - INSERT VIRTUEL DU GROUPE TECHNIQUE ANNEXE DE L'ACTEUR / ACTEUR LOGIN %s / GROUPE DN %s",
                     acteurADDto.getAccountName(), g.getDn()));
                     }
                     }
                     if (!listeGroupesAnnexes.isEmpty()) {
                     for (GroupeLDAPDto g : listeGroupesAnnexes) {
                     if (!g.getCn().equals("MAR-")) {
                     traceSimu.info(String.format(
                     "PSEUDO AD - INSERT VIRTUEL DU GROUPE ANNEXE DE L'ACTEUR / ACTEUR LOGIN %s / GROUPE DN %s",
                     acteurADDto.getAccountName(), g.getDn()));
                     }
                     }
                     }
                     }
                     **/
                }
            }
        }
    }


    @Override
    public void deleteActeur(String login, String typeActeur) throws ServiceException {
        ActeurADDto acteur = this.findByIdentifiant(login, typeActeur);
        if (acteur == null)
            throw new ServiceException("L'acteur n'a pas été trouvé dans l'annuaire.");

        try {
            switch (acteur.getEmployeeType()) {
                case ConstanteActeur.TYPE_AGENT:
                case ConstanteActeur.TYPE_EXTERNE:
                    this.acteurADRepository.deleteById(new LdapName(acteur.getDn()));
                    break;
                case ConstanteActeur.TYPE_SERVICE:
                    this.compteServiceADRepository.deleteById(new LdapName(acteur.getDn()));
                    break;
                case ConstanteActeur.TYPE_RESSOURCE:
                    this.compteRessourceADRepository.deleteById(new LdapName(acteur.getDn()));
                    break;
                case ConstanteActeur.TYPE_APPLICATIF:
                    this.compteApplicatifADRepository.deleteById(new LdapName(acteur.getDn()));
                    break;
                default:
                    throw new ServiceException("Impossible de reconnaître la typologie du compte. Aucune action effectuée.");
            }
        } catch (InvalidNameException e) {
            throw new ServiceException("Erreur lors de la suppression de l'acteur dans l'annuaire.");
        }
    }

    /**
     * Indique si le champ Mail existe deje dans l'AD.
     *
     * @param mail L'adresse Email pour lequel on souhaite faire le test
     * @return True si l'acteur est present, false sinon.
     */
    @Override
    public boolean isMailLibre(final String mail) {

        ContainerCriteria criteria = query().countLimit(Integer.MAX_VALUE).where(ConstanteAD.ATTR_AD_OBJECTCLASS).is(ConstanteAD.ATTR_AD_USER);
        ContainerCriteria mailCriteria = LdapQueryBuilder.query().where(ConstanteAD.ATTR_AD_MAIL).is(mail);
        ContainerCriteria mailADCriteria = LdapQueryBuilder.query().where(ConstanteAD.ATTR_AD_OTHER_MAIL).is(mail);
        ContainerCriteria mailOrMailADCriteria = mailCriteria.or(mailADCriteria);
        criteria.and(mailOrMailADCriteria);


        List<ActeurAD> listeActeurs = ldapTemplate.find(criteria, ActeurAD.class);

        return listeActeurs.isEmpty();
    }

    /**
     * Indique si le champ Login existe deje dans l'AD.
     *
     * @param login Login pour lequel on souhaite faire le test
     * @return True si l'acteur est present, false sinon.
     */
    @Override
    public boolean isLoginLibre(final String login) {

        ContainerCriteria criteria = query().countLimit(Integer.MAX_VALUE).where(ConstanteAD.ATTR_AD_OBJECTCLASS).is(ConstanteAD.ATTR_AD_USER);
        criteria.and(ConstanteAD.ATTR_AD_ACCOUNT_NAME).is(login);
        List<ActeurAD> listeActeurs = ldapTemplate.find(criteria, ActeurAD.class);
        return listeActeurs.isEmpty();
    }


    @Override
    public void renommerActeur(final String ancienLogin, final String nouveauLogin, final String typeActeur) {
        if (log.isInfoEnabled()) {
            log.debug(String.format("Renommage de l'acteur '%1$s' vers '%2$s' demandee dans l'annuaire AD",
                    ancienLogin, nouveauLogin));
        }
        // On renomme l'acteur
        Attribute attr = new BasicAttribute(ConstanteAD.ATTR_AD_ACCOUNT_NAME, nouveauLogin);
        ModificationItem item =
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        this.ldapTemplate.modifyAttributes(getDNfromAD(ancienLogin), new ModificationItem[]{item});

    }


    /**
     * Calcule le DN � partir du Login en searchant dans l'AD
     * MIGRATION AD 5.0
     *
     * @param login
     * @return DistinguishedName cn=...,ou=...,dc=vdm,dc=mars
     */
    public Name getDNfromAD(final String login) {
        List<LabelValueBean> paramsDn = new LinkedList<>();

        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(ConstanteAD.ATTR_AD_ACCOUNT_NAME, login));
        ActeurAD acteurs = acteurADRepository.findByIdentifiant(login);
        if (acteurs == null) return null;

        return acteurs.getDn();
    }


    /**
     * Calcule le DN � partir du Login en searchant dans l'AD
     * MIGRATION AD 5.0
     *
     * @param login
     * @param typeActeur
     * @return DistinguishedName cn=...,ou=...,dc=vdm,dc=mars
     */
    public Name getDNfromAD(final String login, final String typeActeur) {

        if (typeActeur.equals(ConstanteActeur.TYPE_AGENT) || typeActeur.equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = acteurADRepository.findByIdentifiant(login);
            return acteur.getDn();
        }
        if (typeActeur.equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteService = compteServiceADRepository.findByIdentifiant(login);
            return compteService.getDn();
        }
        if (typeActeur.equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessource = compteRessourceADRepository.findByIdentifiant(login);
            return compteRessource.getDn();
        }
        if (typeActeur.equals(ConstanteActeur.TYPE_APPLICATIF)) {
            CompteApplicatifAD compteApplicatif = compteApplicatifADRepository.findByIdentifiant(login);
            return compteApplicatif.getDn();
        }
        return null;
    }


    /**
     * Construit le Sn de l'acteur � partir de son nom et de son nom marital.
     *
     * @param nom        Nom de l'acteur pour lequel on cherche le Sn.
     * @param nomMarital Nom marital de l'acteur pour lequel on cherche le Sn.
     * @param nomUsuel   Nom usuel de l'acteur
     * @return Sn de l'acteur.
     */
    protected String calculerSn(final String nom, final String nomMarital, final String nomUsuel) {
        if (StringUtil.isBlank(nomMarital) && StringUtil.isBlank(nom) && StringUtil.isBlank(nomUsuel)) {
            return null;
        } else if (StringUtil.isNotBlank(nomUsuel)) {
            return nomUsuel.toUpperCase();
        } else if (StringUtil.isNotBlank(nomMarital)) {
            return nomMarital.toUpperCase();
        } else {
            return nom.toUpperCase();
        }
    }

    public String calculerDisplayName(final String nomPatro, final String nomMarital, final String nomUsuel, final String prenom, final String prenomUsuel) {

        String nom = StringUtil.isNotBlank(nomUsuel) ? nomUsuel : StringUtil.isNotBlank(nomMarital) ? nomMarital : nomPatro;
        String pnom = StringUtil.isNotBlank(prenomUsuel) ? prenomUsuel : prenom;

        if (StringUtil.isNotBlank(nom) && StringUtil.isNotBlank(pnom)) {
            return nom + ' ' + pnom;
        } else if (StringUtil.isNotBlank(nom)) {
            return nom;
        } else if (StringUtil.isNotBlank(pnom)) {
            return pnom;
        } else {
            return null;
        }
    }

    /**
     * Methode permettant de calculer l'attribut givenName du LDAP (attibut
     * correspondant au prenom).
     *
     * @param prenom      le pr�nom de l'acteur
     * @param prenomUsuel le pr�nom usuel de l'acteur
     * @return La valeur � mettre dans l'attribut LDAP givenName
     */
    protected String calculerGivenName(final String prenom, final String prenomUsuel) {
        if (StringUtil.isBlank(prenom) && StringUtil.isBlank(prenomUsuel)) {
            return null;
        } else if (StringUtil.isNotBlank(prenomUsuel)) {
            return prenomUsuel;
        } else {
            return prenom;
        }
    }


    /**
     * Construit le Cn de l'acteur � partir de son nom et de son prenom.
     *
     * @param nom    Nom de l'acteur pour lequel on veut construire le CN.
     * @param prenom Prenom de l'acteur pour lequel on veut construire le CN.
     * @return Cn de l'acteur.
     */
    private String construireCn(final String nom, final String prenom) {
        if (StringUtil.isNotBlank(nom) && StringUtil.isNotBlank(prenom)) {
            return nom.toUpperCase() + " " + StringUtil.toPascalCase(prenom);
        } else if (StringUtil.isBlank(nom)) {
            return StringUtil.toPascalCase(prenom);
        } else {
            return nom.toUpperCase();
        }
    }

    /**
     * Prepare les donnees de l'acteur a mettre dans le LDAP a partir d'une demande
     * acteur.
     *
     * @param demandeActeur         La demande concernant l'acteur
     * @param affectationTerrain    L'affectation terrain de la demande.
     * @param affectationOfficielle L'affectation officielle de la demande.
     * @param champTechnique        les champs techniques relatifs a AD
     * @param email                 La nouvelle adresse de messagerie.
     * @return un objet de type <code>ActeurLDAPDto</code>
     */
    @Override
    public ActeurADDto convert(final DemandeActeur demandeActeur, final Cellule affectationTerrain,
                               final Cellule affectationOfficielle, final ChampTechniqueDto champTechnique, final String email) {
        {
            ActeurADDto dto = new ActeurADDto();
            // CC_08286 : 25/03/2014 - BGE - Modification du calcul de sn
            // (vdmCnUnique)
            dto.setSn(calculerSn(demandeActeur.getNom(), demandeActeur.getNomMarital(), demandeActeur.getNomUsuel()));
            dto.setDn(calculerDisplayName(demandeActeur.getNom(), demandeActeur.getNomMarital(), demandeActeur.getNomUsuel(), demandeActeur.getPrenom(), demandeActeur.getPrenomUsuel()));
            // 27/01/2014 - BGE - Le champ description doit contenir l'affectation
            // officielle et non l'affectation terrain.
            dto.setDescription(affectationOfficielle != null ? affectationOfficielle.getLibLdapGrCel() : null);
            dto.setTitle(demandeActeur.getFonction());
            if (!demandeActeur.getTypeDemande().equals(ConstanteWorkflow.DEM_ACTEUR_MODIFICATION_STRING)) {
                dto.setMailPerso(demandeActeur.getEmail());
            }
            if (StringUtil.isNotBlank(email)) {
                dto.setEmail(email);
            }

            if (StringUtil.isNotBlank(demandeActeur.getIdta()) && StringUtil.isNotBlank(demandeActeur.getIdtn())) {
                dto.setMatricule(ReferentielUtils.getMatricule(demandeActeur.getIdta(), demandeActeur.getIdtn()));
            }
            dto.setEmployeeType(demandeActeur.getTypeActeur());
            // CC_08286 : 25/03/2014 - BGE - Mise en place du calcul de givenName
            // (vdmCnUnique)
            dto.setGivenName(
                    StringUtil.toPascalCase(calculerGivenName(demandeActeur.getPrenom(), demandeActeur.getPrenomUsuel())));
            // CC_08286 : 25/03/2014 - BGE - Modification de la construction du cn
            // (vdmCnUnique)
            dto.setCn(construireCn(dto.getSn(), dto.getGivenName()));
            dto.setIdentifiant(demandeActeur.getLogin());
            dto.setVdmNomPatronymique(StringUtil.isNotBlank(demandeActeur.getNomMarital()) ? demandeActeur.getNom() : null);
            GroupeADDto groupeLdapDto = null;



            /*
             * A modifier mantis 17693 Si affectation terrain alors : - Owner = Affectation
             * terrain - Department = Affectation terrain - Description = Affection
             * officielle
             */

            //dto.setOwner(groupeLdapDto != null ? groupeLdapDto.getDn() : null);
            dto.setAffectationTerrain(affectationTerrain != null ? affectationTerrain.getLibLdapGrCel() : dto.getDescription());
            //dto.setVdmDepartmentTexte(affectationTerrain != null ? affectationTerrain.getLibelleLong() : affectationOfficielle.getLibelleLong());
            /*
             * Fin de la modification
             */

            dto.setDepartmentNumber(affectationTerrain != null ? affectationTerrain.getCode() : affectationOfficielle.getCode());
            //dto.setVdmUsrAdDomain(champTechnique.getDomaineAD());

            dto.setTelephoneFixePerso(demandeActeur.getTelExterne());
            return dto;
        }
    }


    /**
     * Sauve, modifie ou supprime les champs techniques AD de l'acteur dont le
     * login est passé en paramètre.
     *
     * @param login Login de l'utilisateur pour lequel on d�sire mettre � jour les
     *              champs techniques.
     * @param dto   Données techniques à enregistrer.
     */
    @Override
    public void saveChampsTechniquesAD(final String login,
                                       final ChampTechniqueDto dto) {
        int nbAttente = 0;
        ActeurAD acteurAD = null;

        // On charge le compte AD correspondant au login.
        acteurAD = acteurADRepository.findByIdentifiant(login);
        if (acteurAD != null) {
            // Si le compte existe, on modifie les attribut du compte et on
            // sort.
            updateChampsTechniquesAD(login, dto);
            return;
        }

        // Si on arrive ici, c'est que l'on n'a pas trouv� de compte AD dans le
        // d�lai imparti.
        log.error(String.format("Le compte AD pour le login '%1$s' n'a pas �t� trouv� ",
                login));
    }


    /**
     * Ajoute, modifie et supprime les champs techniques du compte AD
     * correspondant au login pass� en param�tre. Aucune attente ici.
     *
     * @param login Login du compte AD � mettre � jour.
     * @param dto   Informations � mettre dans le compte AD.
     */
    public void updateChampsTechniquesAD(final String login,
                                         final ChampTechniqueDto dto) {
        if (log.isInfoEnabled()) {
            log.info(String.format(
                    "Mise à jour du compte AD de login '%1$s'", login));
        }

        if (log.isInfoEnabled()) {
            log.info(String
                    .format("Enregistrement de champs techniques demande pour le login '%1$s'",
                            login));
        }

        ActeurAD acteurAD = acteurADRepository.findByIdentifiant(login);


        // Dans un premier temps, on s'occupe du stockage des serveurs de
        // fichiers.
        if (dto != null) {


            if (StringUtil.isNotBlank(dto.getServeurFichier())) {
                acteurAD.setServeurFichier(dto.getServeurFichier());

            } else {
                acteurAD.setServeurFichier(null);
            }

            // On stocke le serveur de fichier complementaire if
            if (StringUtil.isNotBlank(dto.getServeurFichierComplementaire())) {
                acteurAD.setServeurFichierComplemntaire(dto.getServeurFichierComplementaire());
            } else {
                acteurAD.setServeurFichierComplemntaire(null);
            }
            if (StringUtil.isNotBlank(dto.getScriptPath())) {
                // Enfin on s'occupe du stockage du script de demarrage
                acteurAD.setScriptPath(dto.getScriptPath());
            } else {
                acteurAD.setScriptPath(null);
            }
            acteurADRepository.save(acteurAD);
        }

    }


    /**
     * Sauve les donnees d'un acteur dans l'annuaire LDAP a partir de la demande de
     * compte acteur.
     *
     * @param demandeActeur         Demande de compte acteur pour l'acteur.
     * @param affectationTerrain    Affectation terrain de l'acteur.
     * @param affectationOfficielle Affectation officielle de l'acteur.
     * @param nouveauMail           L'adresse de messagerie de l'acteur
     */
    @Override
    public void save(final DemandeActeur demandeActeur, final Cellule affectationTerrain,
                     final Cellule affectationOfficielle, final ChampTechniqueDto champTechnique, final String nouveauMail) throws InvalidNameException, ServiceException {
        save(convert(demandeActeur, affectationTerrain, affectationOfficielle, champTechnique, nouveauMail), true, false);
    }

    @Override
    public void save(final ActeurADDto dto, boolean affectationOrga, boolean simu) throws InvalidNameException, ServiceException {
        if (log.isInfoEnabled()) {
            log.info(String.format("Demande de sauvegarde de l'acteur '%1$s' dans l'annuaire AD", dto.getIdentifiant()));
        }


        // 09/03/2015 - BGE - EVT-9456 - FIN
        if (!this.isLoginLibre(dto.getIdentifiant())) {
            if (!simu) {
                // 09/03/2015 - BGE - EVT-9456 : Gestion des homonynes
                this.updateActeur(dto);
            } else {
                if (log.isInfoEnabled()) {
                    log.info(String.format("PSEUDO AD - UPDATE VIRTUEL DE L'ACTEUR / LOGIN %s", dto.getIdentifiant()));
                }
            }
        } else {
            if (!simu) {
                // 09/03/2015 - BGE - EVT-9456 : Gestion des homonynes
                this.createActeur(dto);
            } else {
                if (log.isInfoEnabled()) {
                    log.info(String.format("PSEUDO AD - INSERT VIRTUEL DE L'ACTEUR / LOGIN %s", dto.getIdentifiant()));
                }
            }
        }

        if (affectationOrga) {
            // On affecte l'acteur � la hierarchies de groupes de son affectation.
            affecterHierarchieGroupe(dto, simu);
        }
    }


    /**
     * Cree un acteur dans le LDAP.
     *
     * @param dto Donnees e utiliser pour creer l'acteur.
     */
    public void createActeur(final ActeurADDto dto) throws ServiceException {
        if (log.isInfoEnabled()) {
            log
                    .info(String
                            .format(
                                    "Demande de creation dans l'annuaire AD d'un acteur de login : %1$s",
                                    dto.getIdentifiant()));
        }

        dto.setCn(gestionDoublonCn(dto.getCn(), dto.getIdentifiant(), dto.getEmployeeType()));

        //Name newDn = new LdapName(String.format("CN=%s,OU=Utilisateurs", dto.getCn()));
        ActeurAD acteurAD = new ActeurAD();
        //acteurAD.setDn(newDn);
        remplirContexte(acteurAD, dto);
        acteurADRepository.save(acteurAD);


    }


    /**
     * Met � jour les donnees LDAP d'un acteur � partir des donnees passees en
     * parametre.
     *
     * @param dto Donnees de l'acteur e mettre e jour
     */
    public void updateActeur(final ActeurADDto dto) throws InvalidNameException {
        if (log.isInfoEnabled()) {
            log.info(String.format("Demande de mise � jour dans l'annuaire LDAP pour l'acteur de login : %1$s", dto.getIdentifiant()));
        }
        dto.setCn(gestionDoublonCn(dto.getCn(), dto.getIdentifiant(), dto.getEmployeeType()));
        Name dn = getDNfromAD(dto.getIdentifiant(), dto.getEmployeeType());
        // Chargement du contexte du LDAP
        try {
            String cnPropre = dto.getCn().replace(",", "");
            LdapName newDn = (LdapName) new LdapName("cn=" + cnPropre).add(0, dn.remove(0).toString());
            ldapTemplate.rename(dn, newDn);
            ActeurAD acteurAD = acteurADRepository.findById(newDn).get();
            remplirContexte(acteurAD, dto);
            acteurADRepository.save(acteurAD);
            //Sauvegarde de l'acteur
        } catch (InvalidNameException | NoPermissionException ine) {
            log.error("Erreur lors de la sauvegarde de l'acteur " + dto.getIdentifiant());
            log.error(ine.getMessage());
            throw ine;
        }
    }

    /**
     * Remplit le contexte AD � partir des informations contenues dans le dto.
     *
     * @param contexte Contexte LDAP.
     * @param dto      Donnees de l'acteur � sauver dans l'annuaire LDAP.
     */
    protected void remplirContexte(final ActeurAD contexte,
                                   final ActeurADDto dto) {


        contexte.setCn(dto.getCn().replace(",", ""));
        // Le sn stocke le nom de famille en majuscule. Il s'agit du nom marital
        // pour les femmes mariees.
        contexte.setSn(dto.getSn());

        contexte.setDisplayName(dto.getDisplayName());

        contexte.setDescription(dto.getDescription());


        // gestio nde l'attribut telephone mono value
        if (StringUtils.hasText(dto.getTelephoneMobilePerso())) {

            dto.getTelephoneMobilePro();
            contexte.setTelephoneMobilePerso(dto.getTelephoneMobilePerso());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            contexte.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getMailPerso())) {
            contexte.setMailPerso(dto.getMailPerso());
        }

        if (dto.getEmployeeType().equals(ConstanteActeur.TYPE_AGENT)) {
            contexte.setFonction(dto.getTitle());
        }

        contexte.setMatricule(dto.getMatricule());

        contexte.setEmployeeType(dto.getEmployeeType());
        contexte.setGivenName(dto.getGivenName());
        contexte.setIdentifiant(dto.getIdentifiant());
        contexte.setAffectationTerrain(dto.getAffectationTerrain());
        contexte.setDepartmentNumber(dto.getDepartmentNumber());
        contexte.setStreetAddress(dto.getStreetAddress());
        contexte.setPostalCode(dto.getPostalCode());
        contexte.setVdmDepartmentTexte(dto.getVdmDepartmentTexte());
        contexte.setUserPrincipalName(dto.getIdentifiant() + "@vdm.mars");
    }


    /**
     * @param cn          le cn de l'acteur
     * @param accountName le login de l'acteur
     * @param typeActeur  le type de l'acteur
     * @return True s'il existe un doublon, false sinon
     */
    private Boolean doublonCNExiste(String cn, String accountName, String typeActeur) {
        ActeurADDto acteurs = this.getActeursByCN(cn, typeActeur);

        //On cherche s'il y en a un qui poss�de le m�me cn mais un login diff�rent
        return acteurs != null && acteurs.getIdentifiant() != null && acteurs.getIdentifiant().equals(accountName);
    }

    /**
     * @param cn          le cn de l'acteur
     * @param accountName le login de l'acteur
     * @param typeActeur  le type de l'acteur
     * @return le cn unique � ins�rer dans l'AD
     */
    public String gestionDoublonCn(String cn, String accountName, String typeActeur) {
        if (!doublonCNExiste(cn, accountName, typeActeur)) return cn;
        return cn + " " + "(" + accountName + ")";
    }


    /**
     * @param cn         le cn de l'acteur
     * @param typeActeur le type de l'acteur
     * @return ActeurADDto l'acteur AD
     */
    public ActeurADDto getActeursByCN(String cn, String typeActeur) {


        if (typeActeur.equals(ConstanteActeur.TYPE_AGENT) || typeActeur.equals(ConstanteActeur.TYPE_EXTERNE)) {
            ActeurAD acteur = acteurADRepository.findByCn(cn);
            if (acteur != null) return acteurADMapper.acteurADToActeurADDto(acteur);
        }
        if (typeActeur.equals(ConstanteActeur.TYPE_SERVICE)) {
            CompteServiceAD compteService = compteServiceADRepository.findByCn(cn);
            if (compteService != null)
                return acteurADMapper.compteServiceAdToActeurADDto(compteService);
        }
        if (typeActeur.equals(ConstanteActeur.TYPE_RESSOURCE)) {
            CompteRessourceAD compteRessource = compteRessourceADRepository.findByCn(cn);
            if (compteRessource != null)
                return acteurADMapper.compteRessourceAdToActeurADDto(compteRessource);
        }
        if (typeActeur.equals(ConstanteActeur.TYPE_APPLICATIF)) {
            CompteApplicatifAD compteApplicatif = compteApplicatifADRepository.findByCn(cn);
            if (compteApplicatif != null)
                return acteurADMapper.compteApplicatifAdToActeurADDto(compteApplicatif);
        }
        return null;
    }

    @Override
    public void motDePasseRequis(String login) {
        ActeurAD acteurAD = this.acteurADRepository.findByIdentifiant(login);
        if (acteurAD != null) {
            try {
                int userAccountControl = Integer.parseInt(acteurAD.getUserAccountControl());
                acteurAD.setUserAccountControl(String.valueOf(userAccountControl - ConstanteAD.UF_PASSWD_NOTREQD));
                acteurAD.setPwdLastSet(Integer.toString(0)); // On force le changement de mdp
                acteurADRepository.save(acteurAD);

            } catch (NumberFormatException nfe) {
                log.error(String.format("L'attribut UserAccountControl du compte %1s n'est pas un nombre.", login));
                throw nfe;
            } catch (Exception e) {
                log.error(String.format("Une erreur est survenue lors de la modfication de l'UAC du compte %1s", login));
                log.error(e);
                throw e;
            }
        }
    }

}