package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;
import fr.vdm.referentiel.refadmin.dto.UtilisateurDto;
import fr.vdm.referentiel.refadmin.mapper.DemandeMapper;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.ActeurVueRepository;
import fr.vdm.referentiel.refadmin.repository.DemandeDroitRepository;
import fr.vdm.referentiel.refadmin.repository.DroitRepository;
import fr.vdm.referentiel.refadmin.repository.TacheHabilitationRepository;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.service.impl.mail.I18NMessagesBundle;
import fr.vdm.referentiel.refadmin.utils.CelluleUtils;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import fr.vdm.referentiel.refadmin.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChangementAffectationServiceImpl implements ChangementAffectationService {
    @Autowired
    private TacheHabilitationRepository tacheHabilitationRepository;
    @Autowired
    private DemandeDroitRepository demandeDroitRepository;
    @Autowired
    private ActeurVueRepository acteurVueRepository;
    @Autowired
    private DroitRepository droitRepository;

    private GroupeFonctionnelService groupeFonctionnelService;

    @Autowired
    private DemandeDroitService demandeDroitService;

    @Autowired
    CelluleService celluleService;

    @Autowired
    UtilisateurService utilisateurService;

    @Autowired
    SendEmailService sendEmailService;

    /**
     * Traitement des modifications/suppressions de droits sur les offres de
     * services déclenchées suite au changement d'affectation de l'acteur.
     *
     * @param ancienneAffectation
     *            ancienne affectation terrain de l'acteur (peut être "")
     * @param nouvelleAffectation
     *            nouvelle affectation terrain de l'acteur
     * @param idActeur
     *            identifiant de l'acteur
     */
    @Override
    public void traiterChangementAffectation(String ancienneAffectation,
                                             String nouvelleAffectation, final long idActeur) throws ServiceException {

        if (log.isDebugEnabled()) {
            log
                    .debug("Lancement du traitement des modifications/suppressions de droits sur les offres de services déclenchées suite au changement d'affectation d'un acteur");
        }

        if (ancienneAffectation != null) {
            ancienneAffectation = CelluleUtils
                    .completerCle(ancienneAffectation);
        }
        if (nouvelleAffectation != null) {
            nouvelleAffectation = CelluleUtils
                    .completerCle(nouvelleAffectation);
        }
        if (!ancienneAffectation.equals(nouvelleAffectation)) {

            // Récupération de la liste des pères de l'ancienne affectation
            String[] listePeresAncienneAffectation = celluleService
                    .findClePeres(CelluleUtils.getColl(ancienneAffectation),
                            CelluleUtils.getSsColl(ancienneAffectation),
                            CelluleUtils.getCode(ancienneAffectation)).toArray(new String[0]);

            // Récupération de la liste des pères de la nouvelle affectation
            String[] listePeresNouvelleAffectation = celluleService
                    .findClePeres(CelluleUtils.getColl(nouvelleAffectation),
                            CelluleUtils.getSsColl(nouvelleAffectation),
                            CelluleUtils.getCode(nouvelleAffectation)).toArray(new String[0]);

            int i = 0;
            int niveau = 0;
            String comment = null;
            String loginUtilisateur = null;
            DemandeDroitDto demDroitDto = null;

            // Initialisation de finBoucle avec la taille la plus grande entre
            // la liste des pères de l'ancienne et de la nouvelle affectation
            int finBoucle = listePeresAncienneAffectation.length;
            if (finBoucle < listePeresNouvelleAffectation.length) {
                finBoucle = listePeresNouvelleAffectation.length;
            }

            // Recherche du niveau à partir duquel il n'y a plus de similitudes
            for (i = 0; i < finBoucle; i++) {
                if (!StringUtil.isEquals(listePeresAncienneAffectation[i],
                        listePeresNouvelleAffectation[i])) {
                    niveau = i;
                    break;
                }
            }

            // Récupération de la liste des offres surlesquels l'acteur à des
            // droits
            List<Droit> droits = droitRepository.findAllByActeurBenef_IdActeur(idActeur);
            // Parcours des droits de l'acteur
            for (Droit droit : droits) {
                ActeurVue acteur = acteurVueRepository.findById(droit.getActeurBenef().getIdActeur()).orElse(null);
                String nom = "";
                if (acteur != null) {
                    nom = acteur.getNom();
                    if (StringUtil.isNotBlank(acteur.getPrenom())) {
                        nom += " " + acteur.getPrenom();
                    }
                }
                if (droit.getOffre().getNivSupp() >= niveau) {

                    comment = I18NMessagesBundle
                            .getString(I18NMessagesBundle.OFFRE_DROIT_SUPPRESSION,nom,
                                    this.celluleService.selectDescriptionCellule(ancienneAffectation),
                                    this.celluleService.selectDescriptionCellule(nouvelleAffectation));

                    // Initialisation de la demande de suppression de droit sur
                    // l'offre
                    demDroitDto = demandeDroitService.saveDemandeAuto(
                            idActeur, droit.getOffre().getId(), comment,
                            ConstanteWorkflow.DEM_OFFRE_SUPPRESSION);

                } else if (droit.getOffre().getNivMod() >= niveau) {
                    comment = I18NMessagesBundle
                            .getString(
                                    I18NMessagesBundle.OFFRE_DROIT_MODIFICATION,
                                    nom,
                                    this.celluleService
                                            .selectDescriptionCellule(ancienneAffectation),
                                    this.celluleService
                                            .selectDescriptionCellule(nouvelleAffectation));
                    this.demandeDroitService.saveDemandeAuto(idActeur, droit
                                    .getOffre().getId(), comment,
                            ConstanteWorkflow.DEM_OFFRE_MODIFICATION);

                }
            }

            List<DemandeDroit> demandes = demandeDroitRepository.findByIdActeur(idActeur);
            List<Offre> offres = new ArrayList<>();
            // filtrer les droits encore acquis par l'acteur
            droits.stream().forEach(d -> offres.add(d.getOffre()));
            demandes = demandes.stream().filter(d -> offres.contains(d.getOffre())).collect(Collectors.toList());

            //Une fois toutes les demandes automatiques effectuées, on valide automatiquement celles dont une suppression auto est demandée
            for (DemandeDroit demande : demandes) {
                if (Boolean.TRUE.equals(demande != null && Objects.equals(demande.getTopDem(), "S") && demande.getOffre() != null
                        && demande.getOffre().getBooSuppAutoHabilitation()) && demande.getStatut().getId() == 2) {
                    accepterDemandeSuppressionAuto(demande, idActeur);
                }
            }
            //Gestion des groupes fonctionnels de l'acteur (alertes mail + suppression auto)
            groupeFonctionnelService.gestionChangementAffectation(ancienneAffectation, nouvelleAffectation, idActeur);
        }
    }

    public void accepterDemandeSuppressionAuto(DemandeDroit demande, Long idActeur) {
        UtilisateurDto user = utilisateurService.getUser();

        List<TacheHabilitation> tache = tacheHabilitationRepository.findByDemandeDroitId(demande.getId());

        this.sendEmailService.sendMailsValideursTechniques(demande.getOffre(), acteurVueRepository.findById(idActeur).get());


        try {
            demandeDroitService.save(DemandeMapper.INSTANCE.demandeToDemandeDto(demande), user, 'S', true, false, tache.get(0).getId(), false);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
