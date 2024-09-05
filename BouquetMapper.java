package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.BouquetDto;
import fr.vdm.referentiel.refadmin.model.Bouquet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {ProfilMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BouquetMapper {

    BouquetMapper INSTANCE = Mappers.getMapper(BouquetMapper.class);

    @Mappings({
            @Mapping(source = "offre.libelle", target = "libelle"),
            @Mapping(source = "offre.description", target = "description"),
            @Mapping(source = "groupeApplicatif", target = "profil"),
            @Mapping(source = "idOffre", target = "idOffre"),
            @Mapping(source = "idGrp", target = "idGrApp")
    })
    BouquetDto bouquetToBouquetDto(Bouquet bouquet);

    List<BouquetDto> bouquetsToBouquetDtoList(List<Bouquet> bouquets);


    @Mappings({
            @Mapping(target = "idOffre", source = "idOffre"),
            @Mapping(target = "idGrp", source = "idGrApp")
    })
    Bouquet bouquetDtoToBouquet(BouquetDto bouquetDto);

    List<Bouquet> bouquetDtoToBouquetList(List<BouquetDto> bouquetDtos);
}