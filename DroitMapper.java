package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.DroitDto;
import fr.vdm.referentiel.refadmin.model.Droit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(uses = {OffreMapper.class, ActeurMapper.class, DemandeMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DroitMapper {
    DroitMapper INSTANCE = Mappers.getMapper(DroitMapper.class);



    @Mappings({
            @Mapping(target = "acteur", source = "acteurBenef"),
            @Mapping(target = "demande", source = "demande"),
            @Mapping(target = "offre", source = "offre")
    })
    DroitDto droitToDroitDto(Droit droit);

    List<DroitDto> droitsToDroitDtos(List<Droit> droit);
}