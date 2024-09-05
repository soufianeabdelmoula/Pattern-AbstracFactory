package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = {ProfilMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DemandeMapper {

    DemandeMapper INSTANCE = Mappers.getMapper(DemandeMapper.class);

    @Named("listEtapeDemandeToListLong")
    static List<Long> listEtapeDemandeToListLong(List<EtapeDemDroit> etapes) {
        return etapes.stream().map(EtapeDemDroit::getId).collect(Collectors.toList());
    }

    @Mappings({@Mapping(target="statutDemande", source ="statut")})
    DemandeDroitDto demandeToDemandeDto(DemandeDroit demande);

    @Mappings({})
    EtapeDemDroit etapeDemandeToEtapeDemandeDto(EtapeDemDroitDto etape);

    List<EtapeDemDroitDto> etapesDemandeToEtapesDemandeDto(List<EtapeDemDroitDto> etape);

    @Mappings({})
    StatutDemandeDto statutDemandeToStatutDemandeDto(StatutDemande statutDemande);

    @Mappings({@Mapping(target = "statut", source = "statutDemande")})
    DemandeDroit demandeDtoToDemande(DemandeDroitDto demandeDroitDto);

    @Mapping(source = "messagerie", target = "topMessagerie")
    @Mapping(source = "agenda", target = "topAgenda")
    @Mapping(source = "internet", target = "topInternet")
    @Mapping(source = "terrain", target = "topReaffecter")
    DemandeActeur demandeActeurDtoToDemandeActeur(DemandeActeurDto demandeActeurDto);

    @Mappings({})
    DemandeActeurDto demandeActeurToDemandeActeurDto(DemandeActeur demandeActeurDto);

    EtapeDemActeur etapeDemandeToEtapeDemandeDto(EtapeDemGenericDto etapeDemGenericDto);

    @AfterMapping
    default void checkBooleans(DemandeActeurDto demandeActeurDto, @MappingTarget DemandeActeur demandeActeur) {
        if (demandeActeur.getTopMessagerie() == null) demandeActeur.setTopMessagerie(false);
        if (demandeActeur.getTopAgenda() == null) demandeActeur.setTopAgenda(false);
        if (demandeActeur.getTopInternet() == null) demandeActeur.setTopInternet(false);
        if (demandeActeur.getTopReaffecter() == null) demandeActeur.setTopReaffecter(false);
    }

}