package fr.vdm.referentiel.refadmin.service;

import java.io.Serializable;

public interface NotificationActeurService {

    void notifyCreate(Serializable tacheId, String cellule);

    void notifyRefus(long idDemande);
}
