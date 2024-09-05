package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.dto.BouquetDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.Bouquet;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {},nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface ActeurVueMapper {

    ActeurVueMapper INSTANCE = Mappers.getMapper(ActeurVueMapper.class);

    @Mappings({})
    ActeurVueDto acteurVueToActeurVueDto(ActeurVue acteurVue);
    ActeurVue acteurVueDtoToActeurVue(ActeurVueDto acteurVueDto);


    List<ActeurVueDto> listActeurVueToActeurVueDto(List<ActeurVue> listActeurVue);
    List<ActeurVue> acteurVueDtoToActeurVueList(List<ActeurVueDto> acteursDto);
}
