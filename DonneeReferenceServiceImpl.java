package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.DonneeReferenceDto;
import fr.vdm.referentiel.refadmin.dto.TypeDonneeReferenceDto;
import fr.vdm.referentiel.refadmin.mapper.DonneesReferenceMapper;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.service.DonneeReferenceService;
import fr.vdm.referentiel.refadmin.utils.EnumTypeDonneeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DonneeReferenceServiceImpl implements DonneeReferenceService {

    @Autowired
    private TypeCompteApplicatifRepository typeCompteApplicatifRepository;

    @Autowired
    private TypeCompteServiceRepository typeCompteServiceRepository;

    @Autowired
    private TypeCompteRessourceRepository typeCompteRessourceRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TypeTelephonieRepository typeTelephonieRepository;

    @Override
    public List<TypeDonneeReferenceDto> getAllTypes() {
        List<TypeDonneeReferenceDto> toReturn = new ArrayList<>();
        Arrays.asList(EnumTypeDonneeReference.values()).forEach(type -> {
            TypeDonneeReferenceDto dto = new TypeDonneeReferenceDto();
            dto.setCode(type.code);
            dto.setLibelle(type.libelle);
            toReturn.add(dto);
        });
        return toReturn;
    }

    @Override
    public List<DonneeReferenceDto> getByCode(String code) {
        EnumTypeDonneeReference enumeration = Arrays.stream(EnumTypeDonneeReference.values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);

        if (enumeration == null) return null;

        switch (enumeration) {
            case TYPE_COMPTE_APPLICATIF:
                return DonneesReferenceMapper.INSTANCE.typeCompteApplicatifListToDonneeReferenceDtoList(this.typeCompteApplicatifRepository.findAll());
            case TYPE_COMPTE_RESSOURCE:
                return DonneesReferenceMapper.INSTANCE.typeCompteRessourceListToDonneeReferenceDtoList(this.typeCompteRessourceRepository.findAll());
            case TYPE_COMPTE_SERVICE:
                return DonneesReferenceMapper.INSTANCE.typeCompteServiceListToDonneeReferenceDtoList(this.typeCompteServiceRepository.findAll());
            case DRIVER:
                return DonneesReferenceMapper.INSTANCE.driverListToDonneeReferenceDtoList(this.driverRepository.findAll());
            case TYPE_TELEPHONIE:
                return DonneesReferenceMapper.INSTANCE.typeTelephonieListToDonneeReferenceDtoList(this.typeTelephonieRepository.findAll());
            default:
                return null;
        }
    }

    @Override
    public void saveDonneeReference(DonneeReferenceDto donnee) {
        EnumTypeDonneeReference enumeration = Arrays.stream(EnumTypeDonneeReference.values()).filter(e -> e.code.equals(donnee.getType().getCode())).findFirst().orElse(null);

        if (enumeration == null) return;

        switch (enumeration) {
            case TYPE_COMPTE_APPLICATIF:
                this.typeCompteApplicatifRepository.save(DonneesReferenceMapper.INSTANCE.donneReferenceDtoToTypeCompteApplicatif(donnee));
                break;
            case TYPE_COMPTE_RESSOURCE:
                this.typeCompteRessourceRepository.save(DonneesReferenceMapper.INSTANCE.donneReferenceDtoToTypeCompteRessource(donnee));
                break;
            case TYPE_COMPTE_SERVICE:
                this.typeCompteServiceRepository.save(DonneesReferenceMapper.INSTANCE.donneReferenceDtoToTypeCompteService(donnee));
                break;
            case DRIVER:
                this.driverRepository.save(DonneesReferenceMapper.INSTANCE.DonneeReferenceDtoToDriver(donnee));
                break;
            case TYPE_TELEPHONIE:
                this.typeTelephonieRepository.save(DonneesReferenceMapper.INSTANCE.donneReferenceDtoToTypeTelephonie(donnee));
                break;
            default:
                break;
        }
    }

    @Override
    public void deleteDonneeReference(String codeType, Long id) {
        EnumTypeDonneeReference enumeration = Arrays.stream(EnumTypeDonneeReference.values()).filter(e -> e.code.equals(codeType)).findFirst().orElse(null);

        if (enumeration == null) return;

        switch (enumeration) {
            case TYPE_COMPTE_APPLICATIF:
                this.typeCompteApplicatifRepository.deleteById(id);
                break;
            case TYPE_COMPTE_RESSOURCE:
                this.typeCompteRessourceRepository.deleteById(id);
                break;
            case TYPE_COMPTE_SERVICE:
                this.typeCompteServiceRepository.deleteById(id);
                break;
            case DRIVER:
                this.driverRepository.deleteById(id);
                break;
            case TYPE_TELEPHONIE:
                this.typeTelephonieRepository.deleteById(id);
                break;
            default:
                break;
        }
    }


}