package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.Preference;
import fr.vdm.referentiel.refadmin.repository.PreferenceRepository;
import fr.vdm.referentiel.refadmin.service.PreferenceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PreferenceServiceImpl implements PreferenceService {
    private final PreferenceRepository preferenceRepository;

    public PreferenceServiceImpl(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public Boolean selectPrefTopMailById(Long idActeur) {
        Preference preference = preferenceRepository.findByActeurIdActeur(idActeur);
        return preference != null &&  preference.getTopMail();
    }
}
