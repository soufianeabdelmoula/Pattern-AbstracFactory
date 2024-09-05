package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.DonneeReferenceDto;
import fr.vdm.referentiel.refadmin.dto.TypeDonneeReferenceDto;

import java.util.List;

public interface DonneeReferenceService {
    List<TypeDonneeReferenceDto> getAllTypes();

    List<DonneeReferenceDto> getByCode(String code);

    void saveDonneeReference(DonneeReferenceDto donnee);

    void deleteDonneeReference(String codeType, Long id);
}