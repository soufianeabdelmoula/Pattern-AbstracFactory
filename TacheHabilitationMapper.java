package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.TacheHabilitationDto;
import fr.vdm.referentiel.refadmin.model.StatutDemande;
import fr.vdm.referentiel.refadmin.model.TacheHabilitation;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TacheHabilitationMapper {

    TacheHabilitationMapper INSTANCE = Mappers.getMapper(TacheHabilitationMapper.class);


    @Mapping(source = "demandeDroit.id", target = "idDemande")
    @Mapping(source = "offrePrincipale.libelle", target = "nomOffrePrincipal")
    @Mapping(source = "demandeDroit.acteurBenef.login", target = "acteurBenefLogin")
    @Mapping(source = "demandeDroit.acteurBenef.nom", target = "nom")
    @Mapping(source = "demandeDroit.acteurBenef.prenom", target = "prenom")
    @Mapping(source = "demandeDroit.acteurBenef.prenomUsuel", target = "prenomUsuel")
    @Mapping(source = "demandeDroit.acteurBenef.nomUsuel", target = "nomUsuel")
    @Mapping(source = "acteurAssigne", target = "acteurAssigne")
    @Mapping(source = "demandeDroit.statut", target = "statutDemande")
    @Mapping(source = "idassigne", target = "idAssigne")
    TacheHabilitationDto tacheHabilitationToDto(TacheHabilitation tacheHabilitation);


    List<TacheHabilitationDto> tacheHabilitationToDtoList(List<TacheHabilitation> tacheHabilitationList);

    @BeforeMapping
    default void checkNull(TacheHabilitation tacheHabilitation, @MappingTarget TacheHabilitationDto tacheHabilitationDto) {
        StatutDemande statut = tacheHabilitation.getDemandeDroit().getStatut();
        if (statut != null) {
            tacheHabilitationDto.setStatut(statut.getLibelle());
        }
    }


}
