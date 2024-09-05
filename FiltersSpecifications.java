package fr.vdm.referentiel.refadmin.utils;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.DemandeDroit;
import fr.vdm.referentiel.refadmin.utils.ConstanteActeur;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.stream.Collectors;

public class FiltersSpecifications {

    /**
     * Crée une spécification (Specification) pour filtrer les demandes d'habilitation en fonction des critères spécifiés
     * dans le filtre et la liste de cellules fournies.
     *
     * @param filter          Objet contenant les critères de filtre pour la recherche des demandes d'habilitation.
     * @param celluleDtoList  Liste de cellules pour affiner le filtre.
     * @return                Une spécification (Specification) prête à être utilisée dans une requête JPA pour filtrer les demandes d'habilitation.
     */
    public static Specification<DemandeDroit> filterDemandesHabilitation(FiltreHistoriqueDemandesDto filter, List<CelluleDto> celluleDtoList) {
        return  (root, query, criteriaBuilder) -> {
            // Utilisez les champs du DTO pour créer la logique de recherche
            Predicate predicate = criteriaBuilder.conjunction();

            // Filtrage sur le champ "Nom"
            if (filter.getNom() != null && !filter.getNom().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("nom")),
                                        "%" + filter.getNom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("nomUsuel")),
                                        "%" + filter.getNom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("nomMarital")),
                                        "%" + filter.getNom().toLowerCase() + "%")
                        ));
            }

            // Filtrage sur le champ "Prenom"
            if (filter.getPrenom() != null && !filter.getPrenom().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("prenom")),
                                        "%" + filter.getPrenom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("prenomUsuel")),
                                        "%" + filter.getPrenom().toLowerCase() + "%")
                        ));
            }

            // Filtrage sur le champ "Login"
            if (filter.getLogin() != null && !filter.getLogin().isEmpty()) {

                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("login")),
                                "%" + filter.getLogin().toLowerCase() + "%"));
            }

            // Filtrage sur le champ "Matricule"
            if ((filter.getIdta() != null && !filter.getIdta().isEmpty()) || (filter.getIdtn() != null && !filter.getIdtn().isEmpty())) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder
                        .and(
                                criteriaBuilder.and(predicate,
                                        criteriaBuilder
                                                .like(criteriaBuilder.lower(root.get("acteurBenef").get("idta")),
                                                        filter.getIdta().toLowerCase()))
                                ,
                                criteriaBuilder.and(predicate,
                                        criteriaBuilder.like(criteriaBuilder.lower(root.get("acteurBenef").get("idtn")),
                                                filter.getIdtn().toLowerCase()))
                        )
                );

            }


            // Filtrage sur le champ "typeDemande"
            if (filter.getTypeDemande() != null && !filter.getTypeDemande().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("topDem")),
                                filter.getTypeDemande().toLowerCase()));
            }

            // Filtrage sur le champ "Date Min"
            if (filter.getDateMin() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("tsCreat"), filter.getDateMin()));
            }

            // Filtrage sur le champ "Date Max"
            if (filter.getDateMax() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("tsCreat"), filter.getDateMax()));
            }

            // Filtrage sur le champ "Etat" ou "Statut de la demande"
            if (filter.getEtat() != null && !filter.getEtat().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("statut").get("code")),
                                filter.getEtat().toLowerCase()));
            }

            // Filtrage sur le champ "Offre"
            if (filter.getOffre() != null && !filter.getOffre().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("offre").get("libelle")),
                                filter.getOffre().toLowerCase()));
            }

            // Filtrage sur le champ "Affectation"
            if (filter.getCodeAffectation() != null && !filter.getCodeAffectation().isEmpty()) {

                /*
                 * Si la taille de la liste de cellules est inférieure ou égale à 1000,
                 * nous appliquons une certaine logique.
                 */
                if (celluleDtoList.size() <= 1000){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.and(
                                    criteriaBuilder
                                            .trim(root.get("acteurBenef").get("cellule"))
                                            .in(celluleDtoList.stream()
                                                    .map(CelluleDto::getCode)
                                                    .map(String::trim)
                                                    .collect(Collectors.toList()))));
                }else {
                    /*
                     * Si la taille de la liste de cellules est supérieure ou égale à 1000,
                     * l'utilisation de l'opérateur 'in' dans une requête avec cette liste peut poser des problèmes de performance
                     * ou dépasser les limitations de certaines bases de données.
                     * Nous appliquons une logique alternative.
                     */

                    // Taille totale de la liste
                    int tailleTotal = celluleDtoList.size();

                    // Taille de la tranche
                    int compteur = 1000;

                    List<CelluleDto> pageCelluleDto = celluleDtoList.subList(0, compteur);

                    // Pagination
                    int page = compteur;

                    while (tailleTotal > compteur) {
                        predicate = criteriaBuilder.and(predicate,
                                criteriaBuilder.and(
                                        criteriaBuilder
                                                .trim(root.get("acteurBenef").get("cellule"))
                                                .in(pageCelluleDto.stream()
                                                        .map(CelluleDto::getCode)
                                                        .map(String::trim)
                                                        .collect(Collectors.toList()))));

                        if (tailleTotal - compteur >= 1000){
                            page = compteur;
                            compteur = compteur + 1000;
                            pageCelluleDto = celluleDtoList.subList(page, compteur);
                        }else {
                            page = compteur;
                            compteur = compteur + (tailleTotal - compteur);
                            pageCelluleDto = celluleDtoList.subList(page, tailleTotal);
                        }
                    }

                }
            }

            return predicate;
        };
    }

    /**
     * Crée une spécification (Specification) pour filtrer les demandes liées au compte de l'acteur en fonction des critères spécifiés
     * dans le filtre et la liste de cellules fournies.
     *
     * @param filter          Objet contenant les critères de filtre pour la recherche des demandes liées au compte de l'acteur.
     * @param celluleDtoList  Liste de cellules pour affiner le filtre.
     * @return                Une spécification (Specification) prête à être utilisée dans une requête JPA pour filtrer les demandes liées au compte de l'acteur.
     */
    public static Specification<DemandeActeur> filterDemandesCompteActeur(FiltreHistoriqueDemandesDto filter, List<CelluleDto> celluleDtoList) {
        return  (root, query, criteriaBuilder) -> {
            // Utilisez les champs du DTO pour créer la logique de recherche
            Predicate predicate = criteriaBuilder.conjunction();

            // Filtrage sur le champ "Nom"
            if (filter.getNom() != null && !filter.getNom().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")),
                                        "%" + filter.getNom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nomUsuel")),
                                        "%" + filter.getNom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nomMarital")),
                                        "%" + filter.getNom().toLowerCase() + "%")
                        ));
            }

            // Filtrage sur le champ "Prenom"
            if (filter.getPrenom() != null && !filter.getPrenom().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")),
                                        "%" + filter.getPrenom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenomUsuel")),
                                        "%" + filter.getPrenom().toLowerCase() + "%")
                        ));
            }

            // Filtrage sur le champ "Login"
            if (filter.getLogin() != null && !filter.getLogin().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("login")),
                                "%" + filter.getLogin().toLowerCase() + "%"));
            }

            // Filtrage sur le champ "Matricule"
            if (filter.getMatricule() != null && !filter.getMatricule().isEmpty()) {
                Expression<String> matriculeExpression = criteriaBuilder.concat(
                        criteriaBuilder.concat(
                                criteriaBuilder.lower(root.get("idta")),
                                criteriaBuilder.lower(root.get("idtn"))
                        ),
                        ""
                );
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(matriculeExpression, filter.getMatricule().toLowerCase())
                );
            }

            // Filtrage sur le champ "typeDemande"
            if (filter.getTypeDemande() != null && !filter.getTypeDemande().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("typeDemande")),
                                filter.getTypeDemande().toLowerCase()));
            }

            // Filtrage sur le champ "Date Min"
            if (filter.getDateMin() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("tsCreat"), filter.getDateMin()));
            }

            // Filtrage sur le champ "Date Max"
            if (filter.getDateMax() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("tsCreat"), filter.getDateMax()));
            }

            // Filtrage sur le champ "Etat" ou "Statut de la demande"
            if (filter.getEtat() != null && !filter.getEtat().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("statut").get("code")),
                                filter.getEtat().toLowerCase()));
            }

            // Filtrage sur le champ "Affectation"
            if (filter.getCodeAffectation() != null && !filter.getCodeAffectation().isEmpty()) {
                /*
                 * Si la taille de la liste de cellules est inférieure ou égale à 1000,
                 * nous appliquons une certaine logique.
                 */
                if (celluleDtoList.size() <= 1000){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.and(
                                    criteriaBuilder
                                            .trim(root.get("cellule"))
                                            .in(celluleDtoList.stream()
                                                    .map(CelluleDto::getCode)
                                                    .map(String::trim)
                                                    .collect(Collectors.toList()))));
                }else {

                    /*
                     * Si la taille de la liste de cellules est supérieure ou égale à 1000,
                     * l'utilisation de l'opérateur 'in' dans une requête avec cette liste peut poser des problèmes de performance
                     * ou dépasser les limitations de certaines bases de données.
                     * Nous appliquons une logique alternative.
                     */

                    // Taille totale de la liste
                    int tailleTotal = celluleDtoList.size();

                    // Taille de la tranche
                    int compteur = 1000;

                    List<CelluleDto> pageCelluleDto = celluleDtoList.subList(0, compteur);

                    // Pagination
                    int page = compteur;

                    while (tailleTotal > compteur) {
                        predicate = criteriaBuilder.and(predicate,
                                criteriaBuilder.and(
                                        criteriaBuilder
                                                .trim(root.get("cellule"))
                                                .in(pageCelluleDto.stream()
                                                        .map(CelluleDto::getCode)
                                                        .map(String::trim)
                                                        .collect(Collectors.toList()))));


                        if (tailleTotal - compteur >= 1000){
                            page = compteur;
                            compteur = compteur + 1000;
                            pageCelluleDto = celluleDtoList.subList(page, compteur);
                        }else {
                            page = compteur;
                            compteur = compteur + (tailleTotal - compteur);
                            pageCelluleDto = celluleDtoList.subList(page, tailleTotal);
                        }
                    }

                }
            }

            return predicate;
        };
    }

    /**
     * Crée une spécification (Specification) pour filtrer des acteurs en fonction des critères spécifiés
     * dans le filtre et la liste de cellules fournies et liste des droits.
     *
     * @param filter          Objet contenant les critères de filtre pour la recherche des acteurs.
     * @param celluleDtoList  Liste de cellules pour affiner le filtre.
     * @param droitDtos  Liste de droits pour affiner le filtre.
     * @return                Une spécification (Specification) prête à être utilisée dans une requête JPA pour filtrer les acteurs.
     */
    public static Specification<ActeurVue> filtersActeurs(FiltreHistoriqueDemandesDto filter, List<CelluleDto> celluleDtoList, List<DroitDto> droitDtos) {
        return  (root, query, criteriaBuilder) -> {
            // Utilisez les champs du DTO pour créer la logique de recherche
            Predicate predicate = criteriaBuilder.conjunction();

            // Filtrage sur le champ "Nom"
            if (filter.getNom() != null && !filter.getNom().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")),
                                        "%" + filter.getNom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nomUsuel")),
                                        "%" + filter.getNom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nomMarital")),
                                        "%" + filter.getNom().toLowerCase() + "%")
                        ));
            }

            // Filtrage sur le champ "Prenom"
            if (filter.getPrenom() != null && !filter.getPrenom().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")),
                                        "%" + filter.getPrenom().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenomUsuel")),
                                        "%" + filter.getPrenom().toLowerCase() + "%")
                        ));
            }

            // Filtrage sur le champ "Login"
            if (filter.getLogin() != null && !filter.getLogin().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("login")),
                                "%" + filter.getLogin().toLowerCase() + "%"));
            }

            // Filtrage sur le champ "Matricule"
            if (filter.getMatricule() != null && !filter.getMatricule().isEmpty()) {
                Expression<String> matriculeExpression = criteriaBuilder.concat(
                        criteriaBuilder.concat(
                                criteriaBuilder.lower(root.get("idta")),
                                criteriaBuilder.lower(root.get("idtn"))
                        ),
                        ""
                );
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(matriculeExpression, filter.getMatricule().toLowerCase())
                );
            }

            // Filtrage sur le champ agent "typeActeur"
            if (filter.isEstAgent() && !filter.isEstExterne()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("typeActeur"),
                                ConstanteActeur.TYPE_AGENT));
            }

            // Filtrage sur le champ externe "typeActeur"
            if (!filter.isEstAgent() && filter.isEstExterne()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("typeActeur"),
                                ConstanteActeur.TYPE_EXTERNE));
            }

            // Filtrage sur le champ agent et externe "typeActeur"
            if (filter.isEstAgent() && filter.isEstExterne()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("typeActeur"),
                                        ConstanteActeur.TYPE_AGENT),
                                criteriaBuilder.equal(root.get("typeActeur"),
                                        ConstanteActeur.TYPE_EXTERNE)
                        ));
            }

            // Filtrage sur le champ "Offre"
            if (filter.getIdOffre() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.and((root.get("idActeur")).
                                in(droitDtos.stream().
                                        map(DroitDto::getActeur).
                                        map(ActeurDto::getIdActeur).
                                        collect(Collectors.toList()))));
            }

            // Filtrage sur le champ "Affectation"
            if (filter.getCodeAffectation() != null && !filter.getCodeAffectation().isEmpty()) {

                /*
                 * Si la taille de la liste de cellules est inférieure ou égale à 1000,
                 * nous appliquons une certaine logique.
                 */

                if (celluleDtoList.size() <= 1000){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.and(
                                    criteriaBuilder
                                            .trim(root.get("cellule"))
                                            .in(celluleDtoList.stream()
                                                    .map(CelluleDto::getCode)
                                                    .map(String::trim)
                                                    .collect(Collectors.toList()))));
                }else {

                    /*
                     * Si la taille de la liste de cellules est supérieure ou égale à 1000,
                     * l'utilisation de l'opérateur 'in' dans une requête avec cette liste peut poser des problèmes de performance
                     * ou dépasser les limitations de certaines bases de données.
                     * Nous appliquons une logique alternative.
                     */

                    // Taille totale de la liste
                    int tailleTotal = celluleDtoList.size();

                    // Taille de la tranche
                    int compteur = 1000;
                    // Pagination
                    int page = 0;

                    List<CelluleDto> pageCelluleDto = celluleDtoList.subList(page, compteur);

                    while (tailleTotal > compteur) {
                        predicate = criteriaBuilder.and(predicate,
                                criteriaBuilder.and(
                                        criteriaBuilder
                                                .trim(root.get("cellule"))
                                                .in(pageCelluleDto.stream()
                                                        .map(CelluleDto::getCode)
                                                        .map(String::trim)
                                                        .collect(Collectors.toList()))));

                        if (tailleTotal - compteur >= 1000){
                            page = compteur;
                            compteur = compteur + 1000;
                            pageCelluleDto = celluleDtoList.subList(page, compteur);
                        }else {
                            page = compteur;
                            compteur = compteur + (tailleTotal - compteur);
                            pageCelluleDto = celluleDtoList.subList(page, tailleTotal);
                        }
                    }

                }
            }

            return predicate;
        };
    }
}
