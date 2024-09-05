package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.Cellule;

import java.util.List;

public interface CelluleService {

    List<CelluleDto> findCellulesActivesByNiveau(Long niveau);

    List<CelluleDto> findCellulesActivesByInput(String input);

    List<CelluleDto> findCellulesActivesFilles(String code);

    CelluleDto findCelluleActiveByCode(String code);

    CelluleDto findCelluleByCode(String code);

    List<CelluleDto> findAllfilsActiveInclus(String code);

    Long findIdCelluleByCode(String code);

    CelluleDto findCelluleCollMarByCode(String code);

    Cellule select(final String cle);


    List<CelluleDto> findPeres(final String coll, final String ssColl,
                               final String code);

    CelluleDto findCelluleByLibelleLdap(String cnGroupe);

    CelluleDto findCelluleDtoByCle(String collectivite, String ssCollectivite, String codePere);

    List<Cellule> findAllParentsActiveInclus(String codeCellule);

    Cellule findCellulePere(ActeurVue acteur);

    Cellule findCelluleByCle(String collectivite, String ssCollectivite, String codePere);

    List<String> findClePeres(String coll, String sousColl, String code);

    /**
     * Récupère la description d'une cellule. Cette description est stockée dans
     * un cache.
     *
     * @param cle la clé de la cellule
     * @return la description de la cellule
     */
    public String selectDescriptionCellule(final String cle);

    public String findPere(final String coll, final String ssColl,
                           final String code, final int niveau);

}