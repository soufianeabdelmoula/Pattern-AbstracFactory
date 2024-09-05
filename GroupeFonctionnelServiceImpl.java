
package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.dto.GroupeDto;
import fr.vdm.referentiel.refadmin.dto.GroupeFonctionnelDto;
import fr.vdm.referentiel.refadmin.mapper.ActeurVueMapper;
import fr.vdm.referentiel.refadmin.mapper.GroupeFonctionnelMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Log4j2
public class GroupeFonctionnelServiceImpl implements GroupeFonctionnelService {
    private final RegleValidVueRepository regleValidVueRepository;
    private final GroupeFonctionnelRepository groupeFonctionnelRepository;
    private final GroupeFonctionnelMapper groupeFonctionnelMapper = GroupeFonctionnelMapper.INSTANCE;
    @Autowired
    LienActeurGroupeFonctionnelRepository lienActeurGroupeFonctionnelRepository;
    @Autowired
    ActeurVueRepository acteurVueRepository;

    @Autowired
    CelluleService celluleService;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    ActeurService acteurService;

    @Autowired
    GroupeADService groupeADService;

    private static final Logger K_LOGGER = LoggerFactory.getLogger(GroupeFonctionnelServiceImpl.class);
    private final ResponsableGrpFoncRepository responsableGrpFoncRepository;


    public GroupeFonctionnelServiceImpl(GroupeFonctionnelRepository groupeFonctionnelRepository,
                                        ResponsableGrpFoncRepository responsableGrpFoncRepository,
                                        RegleValidVueRepository regleValidVueRepository) {
        this.groupeFonctionnelRepository = groupeFonctionnelRepository;

        this.responsableGrpFoncRepository = responsableGrpFoncRepository;
        this.regleValidVueRepository = regleValidVueRepository;
    }

    @Override
    public List<GroupeFonctionnelDto> findAll() {
        return this.groupeFonctionnelMapper.groupeFonctionnelToDtoList(this.groupeFonctionnelRepository.findAll());
    }

    public ActeurVueDto getActeurVueById(Long idActeur) {
        Optional<ActeurVue> oActeur = this.acteurVueRepository.findById(idActeur);
        ActeurVue acteur = oActeur.orElse(null);
        return ActeurVueMapper.INSTANCE.acteurVueToActeurVueDto(acteur);
    }

    @Override
    public List<ActeurVueDto> getActeurs(Long idGrpFonc) {
        List<LienActeurGrpFonc> liensActeursGrpFoncbyId = this.lienActeurGroupeFonctionnelRepository.findAllByIdGrp(idGrpFonc);
        List<ActeurVueDto> acteurs = new ArrayList<>();
        for (LienActeurGrpFonc l : liensActeursGrpFoncbyId) {
            acteurs.add(getActeurVueById(l.getIdActeur()));

        }
        return acteurs;
    }

    @Transactional
    @Override
    public void setTypeParametrage(String typeParametrage,  long idGrpFonc)  {

        GroupeFonctionnel groupeFonctionnel = this.groupeFonctionnelRepository.findById(idGrpFonc);
        groupeFonctionnel.setTypeParametrage(typeParametrage);
        this.groupeFonctionnelRepository.save(groupeFonctionnel);
    }

    @Override
    public List<ActeurVueDto> addActeurs(List<Long> idActeurs, Long idGrpFonc) {
        List<ActeurVueDto> acteursExistants = getActeurs(idGrpFonc);
        List<LienActeurGrpFonc> lienActeurGroupeFonctionnelList = new ArrayList<>();

        for (Long idacteur : idActeurs) {
            if (!acteursExistants.contains(getActeurVueById(idacteur))) {
                LienActeurGrpFonc lienActeurGrpFonc = new LienActeurGrpFonc();
                lienActeurGrpFonc.setIdActeur(idacteur);
                lienActeurGrpFonc.setIdGrp(idGrpFonc);
                lienActeurGroupeFonctionnelRepository.save(lienActeurGrpFonc);
                lienActeurGroupeFonctionnelList.add(lienActeurGrpFonc);
                acteursExistants.add(getActeurVueById(idacteur));
            }
        }


        lienActeurGroupeFonctionnelRepository.saveAll(lienActeurGroupeFonctionnelList);

        return acteursExistants;
    }

    @Transactional
    @Override
    public void deleteActeur(Long idGrpFonc, Long idActeur) {
        this.lienActeurGroupeFonctionnelRepository.deleteByIdActeurAndIdGrp(idGrpFonc, idActeur);
    }


    public void gestionChangementAffectation(String ancienneAffectation, String nouvelleAffectation, long idActeur) throws ServiceException {
        // Récupération de l'acteur
        ActeurVue acteur = acteurVueRepository.findById(idActeur).orElseThrow(() -> new ServiceException(ServiceException.ID_INCONNU));
        // Récupération des affectations
        Cellule ancienneCellule = celluleService.select(ancienneAffectation);
        Cellule nouvelleCellule = celluleService.select(nouvelleAffectation);

        // Récupération des groupes de l'acteur
        List<GroupeDto> groupes = this.findGrpFonc(idActeur);
        // Pour chaque groupe, on gère le changement d'affectation en fonction du
        // paramétrage du groupe
        for (GroupeDto groupe : groupes) {
            // Alerte mail seulement
            if (groupe.getTypeParametrage() != null && groupe.getTypeParametrage().equals("A")) {
                // On récupère les personnes à qui envoyer le mail en fonction du groupe
                List<String> dest = findResponsablesOuValideursHierarchiques(groupe, ancienneCellule);
                sendEmailService.sendMailAlerteChangementAffectationGroupeFonctionnel(ancienneCellule,
                        nouvelleCellule, groupe.getNomCn(), acteur, dest);
            }
            // Suppression automatique
            else if (groupe.getTypeParametrage() != null && groupe.getTypeParametrage().equals("S")) {
                // On récupère les personnes à qui envoyer le mail en fonction du groupe
                List<String> dest = findResponsablesOuValideursHierarchiques(groupe, ancienneCellule);
                sendEmailService.sendMailSuppressionAutoGroupeFonctionnel(ancienneCellule, nouvelleCellule,
                        groupe.getNomCn(), acteur, dest);
                // Suppression de l'affectation en BDD
                LienActeurGrpFonc lien = lienActeurGroupeFonctionnelRepository.findByIdActeurAndIdGrp(acteur.getIdActeur(), groupe.getIdGroupe());
                if (lien != null) {
                    lienActeurGroupeFonctionnelRepository.delete(lien);
                } else {
                    log.warn("lien entre l'acteur " + acteur.getLogin() + " et le groupe " + groupe.getNomCn() + " non trouvé ");
                }

                // Suppression de l'affectation dans le LDAP
                groupeADService.supprimerActeurGroupe(acteur.getLogin(), groupe.getNomCn(), 'F');

            }
        }
    }


    private List<String> findResponsablesOuValideursHierarchiques(GroupeDto groupe, Cellule ancienneCellule) {

        List<ResponsableGrpFonc> responsables = responsableGrpFoncRepository.findAllByIdGrFc(groupe.getIdGroupe());
        List<String> dest = new ArrayList<>();
        // S'il n'y a pas de responsable pour le groupe, on envoie le mail aux VH finaux
        // de l'ancienne affectation de l'acteur
        if (responsables == null || responsables.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Le groupe fonctionnel " + groupe.getNomCn()
                        + "ne possède pas de responsable, les mails seront envoyés aux valideurs hierarchiques de la cellule "
                        + ancienneCellule.getLibLongGrCellule());
            }
            // Recherche des VH
            List<RegleValidVue> listeAllReglesHierarchiqueFinal = regleValidVueRepository.findReglesHierarchiquesActeur()
                    .stream().filter(r -> r.getRoleActeur().getCode().equals("HIERARCHIQUE_FINAL"))
                    .collect(Collectors.toList());
            Set<ActeurVue> responsablesCellule = new HashSet<>();
            Cellule cellule = ancienneCellule;

            while (responsablesCellule.isEmpty() && cellule.getCellulePere() != null) {
                for (RegleValidVue regle : listeAllReglesHierarchiqueFinal) {
                    List<LienRegleCellule> liens = regle.getLienRegleCellules();
                    String nomCellule = cellule.getCode();
                    if (liens.stream().anyMatch(l -> l.getCellule().equals(nomCellule))) {
                        ArrayList<ActeurVue> acteurs = new ArrayList<>();
                        for (LienRegleActeur lien : regle.getLienRegleActeurs()) {
                            ActeurVue acteurVue = acteurVueRepository.findById(lien.getIdActeur()).get();
                            if (acteurVue != null) {
                                acteurs.add(acteurVue);
                            }
                        }
                        responsablesCellule.addAll(acteurs);
                        break;
                    }
                }

                cellule = cellule.getCellulePere();
                if (!responsablesCellule.isEmpty()) {
                    List<String> emails = responsablesCellule.stream().map(ActeurVue::getEmail).collect(Collectors.toList());
                    for (String email : emails) {
                        if (StringUtils.hasText(email)) {
                            dest.add(email);
                        }
                    }
                }
            }
        } else {

            for (ResponsableGrpFonc responsable : responsables) {
                ActeurVue acteurResponsable = acteurVueRepository.findActeurVueByLogin(responsable.getLogin());
                if (acteurResponsable != null && acteurResponsable.getEmail() != null) {
                    dest.add(acteurResponsable.getEmail());
                }
            }
        }
        return dest;
    }


    /**
     * Retourne la liste des groupes fonctionnels appartenant à un acteur.
     *
     * @param idActeur Identifiant de l'acteur.
     * @return List<GroupeDto>
     */
    public List<GroupeDto> findGrpFonc(final long idActeur) {
        // recuperation du lien liant un acteur à ses groupes
        List<LienActeurGrpFonc> lienGroupesFonc =
                lienActeurGroupeFonctionnelRepository.findAllByIdActeur(idActeur);
        List<GroupeDto> groupesFoncDto = new LinkedList<>();

        // pour chaque groupe on recupere ses informations (du groupe et de son
        // responsable)
        for (LienActeurGrpFonc groupe : lienGroupesFonc) {
            // recuperation info du groupe
            /*GrpFonctionnel groupFonc =
                    getGrpFonctionnelDao().select(
                            groupe.getCnGroupeFonctionnel());*/
            //WVE - 31/10/2014 - 8853 Changement nom de groupe, utilisation de l'idCNGRP plutôt que le cn
            GroupeFonctionnel groupFonc =
                    groupeFonctionnelRepository.findById(groupe.getIdGrp()).get();
            // recuperation des responsables du groupe
            List<ResponsableGrpFonc> responsables =
                    responsableGrpFoncRepository.findAllByIdGrFc(groupFonc.getId());
            List<ActeurVue> acteursVue = new LinkedList<ActeurVue>();
            // Si l'acteur en cours de session est un des responsables du groupe
            boolean isResponsable = false;
            for (ResponsableGrpFonc responsable : responsables) {
                ActeurVue acteur =
                        acteurVueRepository.findActeurVueByLogin(
                                responsable.getLogin());
                if (acteur.getIdActeur() == idActeur) {
                    isResponsable = true;
                }
                acteursVue.add(acteur);
            }

            // creation du groupe fonctionnel dto
            GroupeDto groupeDto = new GroupeDto(groupFonc, acteursVue);
            groupeDto.setProprietaire(isResponsable);
            groupesFoncDto.add(groupeDto);
        }

        //On récupère également les groupes fonctionnels dont l'acteur est responsable
        ActeurVue acteur = acteurVueRepository.findById(idActeur).get();
        List<ResponsableGrpFonc> lienRespGrFonc = responsableGrpFoncRepository.findAllByLogin(acteur.getLogin());
        for (ResponsableGrpFonc lien : lienRespGrFonc) {
            if (groupesFoncDto.stream().filter(g -> g.getIdGroupe() == lien.getIdGrFc()).collect(Collectors.toList()).size() == 0) {
                GroupeFonctionnel groupFonc = lien.getGrpFonc();

                List<ResponsableGrpFonc> responsables = responsableGrpFoncRepository.findAllByIdGrFc(groupFonc.getId());
                List<ActeurVue> acteursVue = new ArrayList<ActeurVue>();
                for (ResponsableGrpFonc responsable : responsables) {
                    ActeurVue resp = acteurVueRepository.findActeurVueByLogin(responsable.getLogin());
                    acteursVue.add(resp);
                }
                GroupeDto groupeDto = new GroupeDto(groupFonc, acteursVue);
                groupeDto.setProprietaire(true);
                groupesFoncDto.add(groupeDto);
            }
        }
        return groupesFoncDto;
    }

}
