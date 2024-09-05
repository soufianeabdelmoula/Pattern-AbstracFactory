package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.ActeurHabiliteDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import org.mapstruct.*;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface ActeurHabiliteMapper {

    ActeurHabiliteDto acteurVueToActeurHabiliteDto(ActeurVue acteurVue);

    @BeforeMapping
    default void EnricheDto(ActeurVue acteurVue, @MappingTarget ActeurHabiliteDto acteurHabiliteDto) {

        if (acteurVue.getIdta() != null && acteurVue.getIdtn() != null) {
            acteurHabiliteDto.setMatricule(acteurVue.getIdta() + acteurVue.getIdtn());
        }
    }
}
