package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;
import fr.vdm.referentiel.refadmin.model.TacheHabilitation;
import fr.vdm.referentiel.refadmin.repository.TacheHabilitationRepository;
import fr.vdm.referentiel.refadmin.service.NotificationHabilitationService;
import fr.vdm.referentiel.refadmin.service.SendEmailService;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Optional;

@Service
@Log4j2
public class NotificationHabilitationServiceImpl implements NotificationHabilitationService {

    private final SendEmailService sendEmailService;
    private final TacheHabilitationRepository tacheHabilitationRepository;

    public NotificationHabilitationServiceImpl(SendEmailService sendEmailService, TacheHabilitationRepository tacheHabilitationRepository) {
        this.sendEmailService = sendEmailService;
        this.tacheHabilitationRepository = tacheHabilitationRepository;
    }

    @Override
    public void notifyCreate(Long tacheId, String cellule) {
        if(tacheId != null) {
            Optional<TacheHabilitation> tache = tacheHabilitationRepository.findById(tacheId);

            if(tache.isPresent()){

                // niveau dans le cas d'une tache intermediaire
                Long niveau = null;
                if (tache.get().getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE)) {
                    niveau = tache.get().getNiveau() + 1 ;
                }

                String fonctionUtiliateur = "";
                if (tache.get().getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL)) {
                    fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_FINAL;
                } else if (tache.get().getActivite().equals(ConstanteWorkflow.ACTIVITY_TECHNIQUE)) {
                    fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_TECHNIQUE;
                } else if (tache.get().getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE)) {
                    if (niveau.equals("1")) {
                        fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_1;
                    } else if (niveau.equals("2")) {
                        fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_2;
                    } else if (niveau.equals("3")) {
                        fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_3;
                    } else if (niveau.equals("4")) {
                        fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_4;
                    } else if (niveau.equals("5")) {
                        fonctionUtiliateur = ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_5;
                    }
                }

                if (!fonctionUtiliateur.equals(ConstanteWorkflow.ROLE_OFFRE_TECHNIQUE)) {
                    sendEmailService.sendMailsValideursPotentiels(cellule, fonctionUtiliateur, tache.get().getDemandeDroit().getOffre().getId(),
                            tache.get().getDemandeDroit().getId(), tache.get().getDemandeDroit().getActeurBenef().getIdActeur());
                } else {
                    sendEmailService.sendMailsValideursPotentiels(null, fonctionUtiliateur, tache.get().getDemandeDroit().getOffre().getId(),
                            tache.get().getDemandeDroit().getId(), tache.get().getDemandeDroit().getActeurBenef().getIdActeur());
                }
            } else { log.error(String.format("Fonction de création de notification : l'objet tache considéré est NULL"));}
        } else { log.error(String.format("Fonction de création de notification : Parametre tacheId égal à null "));}
    }

    @Override
    public void notifyRefus(long idDemande, DemandeDroitDto demandeDroit) {
        // Envoi du mail.
        sendEmailService.sendMailsRefusHabilitation(idDemande, demandeDroit);

        if(demandeDroit != null) {
            sendEmailService.sendMailsDemandeurBeneficiaire(false, demandeDroit.getOffre().getId(),
                    demandeDroit.getId(), demandeDroit.getActeurBenef().getIdActeur());

        }
    }

    @Override
    public void notifyAccept(Serializable tacheId) {
        if(tacheId != null) {
            Optional<TacheHabilitation> tache = tacheHabilitationRepository.findById((long) tacheId);

            if(tache.isPresent()){
                sendEmailService.sendMailsDemandeurBeneficiaire(true, tache.get().getDemandeDroit().getOffre().getId(),
                        tache.get().getDemandeDroit().getId(), tache.get().getDemandeDroit().getActeurBenef().getIdActeur());

            } else { log.error(String.format("Fonction de création de notification : l'objet tache considéré est NULL"));}
        } else { log.error(String.format("Fonction de création de notification : Parametre tacheId égal à null "));}
    }
}
