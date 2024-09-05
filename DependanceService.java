package fr.vdm.referentiel.refadmin.service;

import java.util.List;

public interface DependanceService {
    //  25/11/2015 – MMN - EVT 10758 :renvoi la liste des dépendances ayant pour dépendance celle passés en paramètre
    List<Long> findOffresByDependance(Long idDependance);
}
