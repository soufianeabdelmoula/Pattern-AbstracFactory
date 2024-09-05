package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.utils.ServiceException;

public interface ChangementAffectationService {
    void traiterChangementAffectation(String ancienneAffectation,
                                      String nouvelleAffectation, long idActeur) throws ServiceException;
}
