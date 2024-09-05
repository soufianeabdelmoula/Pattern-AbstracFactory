package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.FiltreRegleValidationDto;
import fr.vdm.referentiel.refadmin.model.RegleValidVue;

import java.util.List;

public interface RegleValidVueService {

    List<RegleValidVue> find(FiltreRegleValidationDto filtre);
}
