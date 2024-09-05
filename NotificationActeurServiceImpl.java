package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.TacheActeur;
import fr.vdm.referentiel.refadmin.service.NotificationActeurService;
import fr.vdm.referentiel.refadmin.service.SendEmailService;
import fr.vdm.referentiel.refadmin.service.TacheActeurService;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Objects;

@Service
@Log4j2
public class NotificationActeurServiceImpl implements NotificationActeurService {

    private final TacheActeurService tacheActeurService;
    private final SendEmailService sendEmailService;

    public NotificationActeurServiceImpl(TacheActeurService tacheActeurService, SendEmailService sendEmailService) {
        this.tacheActeurService = tacheActeurService;
        this.sendEmailService = sendEmailService;
    }

    public void notifyCreate(Serializable tacheId, String cellule) {

        TacheActeur tache = tacheActeurService.findById(((TacheActeur) tacheId).getId());

        // niveau dans le cas d'une tache intermediaire
        Long niveau = null;
        if (tache.getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE)) {
            niveau = tache.getNiveau() + 1;
        }

        String fonctionUtiliateur = "";
        if (tache.getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_FINAL)) {
            fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_FINAL;
        } else if (tache.getActivite().equals(ConstanteWorkflow.ACTIVITY_TECHNIQUE)) {
            fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_TECHNIQUE;
        } else if (tache.getActivite().equals(ConstanteWorkflow.ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE)) {
            if (niveau.equals("1")) {
                fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_1;
            } else if (niveau.equals("2")) {
                fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_2;
            } else if (niveau.equals("3")) {
                fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_3;
            } else if (niveau.equals("4")) {
                fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_4;
            } else if (niveau.equals("5")) {
                fonctionUtiliateur = ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_5;
            }
        }

        if (!Objects.equals(fonctionUtiliateur, ConstanteWorkflow.ROLE_ACTEUR_TECHNIQUE)) {
            sendEmailService.sendMailsValideursPotentiels(cellule, fonctionUtiliateur, tache.getDemandeActeur().getId());
        } else {
            sendEmailService.sendMailsValideursPotentiels(null,fonctionUtiliateur, tache.getDemandeActeur().getId());
        }
    }

    public void notifyRefus(long idDemande) {
        // Envoi du mail.
        sendEmailService.sendMailsRefusActeur(idDemande);
    }
}
