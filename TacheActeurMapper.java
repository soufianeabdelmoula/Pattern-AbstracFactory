package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.TacheActeurDto;
import fr.vdm.referentiel.refadmin.model.TacheActeur;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TacheActeurMapper {
    TacheActeurMapper INSTANCE = Mappers.getMapper(TacheActeurMapper.class);


    @Mapping(source = "demandeActeur.id", target = "idDemande")
    @Mapping(source = "demandeActeur.typeActeur", target = "type")
    @Mapping(source = "demandeActeur.nomMarital", target = "nomMarital")
    @Mapping(source = "demandeActeur.nom", target = "nom")
    @Mapping(source = "demandeActeur.prenom", target = "prenom")
    @Mapping(source = "demandeActeur.nomUsuel", target = "nomUsuel")
    @Mapping(source = "demandeActeur.prenomUsuel", target = "prenomUsuel")
    @Mapping(source = "demandeActeur.cellule", target = "affectation")
    @Mapping(source = "demandeActeur.celluleTerrain", target = "affectationTerrain")
    @Mapping(source = "demandeActeur.statut.libelle", target = "statut")
    @Mapping(source = "demandeActeur.tsCreat", target = "dateCreation")
    @Mapping(source = "demandeActeur.typeDemande", target = "typeDemande")
    TacheActeurDto tacheActeurToDto(TacheActeur tacheActeur);


    List<TacheActeurDto> tacheActeurToDtoList(List<TacheActeur> tacheActeurList);

    @BeforeMapping
    default void checkNull(TacheActeur tacheActeur, @MappingTarget TacheActeurDto tacheActeurDto) {
        if (tacheActeur.getAssigne() != null) {
            tacheActeurDto.setNom(tacheActeurDto.getNom());
        }
    }
}
