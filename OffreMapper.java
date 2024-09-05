package fr.vdm.referentiel.refadmin.mapper;


import fr.vdm.referentiel.refadmin.dto.ExportOffreDto;
import fr.vdm.referentiel.refadmin.dto.OffreDto;
import fr.vdm.referentiel.refadmin.dto.OffreRequestDto;
import fr.vdm.referentiel.refadmin.model.Offre;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses={QuestionMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OffreMapper {

    OffreMapper INSTANCE = Mappers.getMapper(OffreMapper.class);

    @Mappings({})
    OffreDto offreToOffreDto(Offre offre);

    @InheritInverseConfiguration
    Offre offreDtoToOffre(OffreDto offreDto);

    List<OffreDto> offreToOffreDtoList(List<Offre> offres);

    Offre offreRequestDtoToOffre(OffreRequestDto offreRequestDto);


    default void fillFields(Offre offre, OffreDto offreDto) {
        offreDto.setAd(offre.getParametrageLdap() != null);
    }

    void updateOffreFromDto(@MappingTarget Offre existingOffre, OffreRequestDto dto);

    @Mappings({@Mapping(source = "parametrageLdap.id", target = "idParametrageLdap")})
    ExportOffreDto offreToExportOffreDto(Offre offre);

    List<ExportOffreDto> offreToExportOffreDtoList(List<Offre> offres);
}