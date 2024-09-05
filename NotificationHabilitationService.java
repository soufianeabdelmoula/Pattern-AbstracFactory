package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;

import java.io.Serializable;

public interface NotificationHabilitationService {

    void notifyCreate(Long tacheId, String cellule);

    /**
     * Traitement d'un évènement correspondant à un refus d'une demande d'habilitation
     */
    void notifyRefus(long idDemande, DemandeDroitDto demandeDroit);

    /**
     *  Traitement d'un évènement correspondant à l’acceptation d'une demande d'habilitation
     *  @param tacheId
     *
     *  */
    void notifyAccept (Serializable tacheId) ;
}
