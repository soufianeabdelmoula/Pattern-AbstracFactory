package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.mapper.CelluleMapper;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.Cellule;
import fr.vdm.referentiel.refadmin.repository.CelluleRepository;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.utils.CelluleUtils;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.StringUtil;
import fr.vdm.referentiel.refadmin.utils.cache.Cache;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class CelluleServiceImpl implements CelluleService {

    @Autowired
    private CelluleRepository celluleRepository;

    private final CelluleMapper celluleMapper = CelluleMapper.INSTANCE;

    /**
     * Cache utilisé pour stocker le résultat du service remontant l'ensemble
     * des cellules filles d'une cellule de l'organigramme.
     */
    public Cache<String, List<CelluleDto>[]> cacheAllFils;

    /**
     * Cache utilisé pour stocker la description des cellules.
     */
    public Cache<String, String> cacheDescription;

    public List<CelluleDto> findCellulesActivesByNiveau(Long niveau) {
        List<CelluleDto> cellules = this.celluleMapper.celluleToCelluleDto(celluleRepository.findCelluleActiveByNiveau(niveau));
        cellules.forEach(this::populateCellulesActivesFillesDto);

        return cellules;
    }


    private void populateCellulesActivesFillesDto(CelluleDto cellule) {
        cellule.setCellulesFilles(this.celluleMapper.celluleToCelluleDto(celluleRepository.findAllFilsActiveDirects(cellule.getCode())));
    }

    public List<CelluleDto> findCellulesActivesByInput(String input) {
        return this.celluleMapper.celluleToCelluleDto(this.celluleRepository.findCellulesActiveByInput(input));
    }

    public List<CelluleDto> findCellulesActivesFilles(String code) {
        List<CelluleDto> cellules = this.celluleMapper.celluleToCelluleDto(celluleRepository.findAllFilsActiveDirects(code));
        cellules.forEach(this::populateCellulesActivesFillesDto);

        return cellules;
    }

    public CelluleDto findCelluleActiveByCode(String code) {
        return this.celluleMapper.celluleToCelluleDto(this.celluleRepository.findCelluleActiveByCode(code));
    }

    public CelluleDto findCelluleByCode(String code) {
        return this.celluleMapper.celluleToCelluleDto(this.celluleRepository.findCelluleByCode(code));
    }

    @Override
    public List<CelluleDto> findAllfilsActiveInclus(String code) {
        return CelluleMapper.INSTANCE.celluleToCelluleDto(this.celluleRepository.findAllfilsActiveInclus(code));
    }

    public List<CelluleDto> findPeres(final String coll, final String ssColl, final String code) {
        return CelluleMapper.INSTANCE.celluleToCelluleDto(celluleRepository.findPeresByCollAndSsCollAndCode(coll, ssColl, code));
    }

    public List<String> findClePeres(String coll, String sousColl, String code) {
        List<Object[]> objects = celluleRepository.findClePeres(coll, sousColl, code);
        String[] affectations = new String[ConstanteWorkflow.MAX_NB_NIV_AFFECTATION];
        for (Object[] t : objects) {
            String cle =
                    CelluleUtils.getCle((String) t[0], (String) t[1], (String) t[2]);

            affectations[(((Number) t[3]).intValue())] = cle;
        }
        return Arrays.asList(affectations);
    }

    @Override
    public CelluleDto findCelluleByLibelleLdap(String cnGroupe) {
        return CelluleMapper.INSTANCE.celluleToCelluleDto(celluleRepository.findByLibLdapGrCel(cnGroupe));
    }

    @Override
    public Long findIdCelluleByCode(String code) {
        return this.celluleRepository.findIdCelluleCollMarByCode(code);
    }

    @Override
    public CelluleDto findCelluleDtoByCle(String collectivite, String ssCollectivite, String codePere) {
        return CelluleMapper.INSTANCE.celluleToCelluleDto(celluleRepository.findByCollAndSsCollAndCode(collectivite, ssCollectivite, codePere));
    }

    @Override
    public Cellule findCelluleByCle(String collectivite, String ssCollectivite, String code) {
        if (collectivite == null || ssCollectivite == null) {
            return celluleRepository.findCelluleByCode(code);
        }
        return celluleRepository.findByCollAndSsCollAndCode(collectivite, ssCollectivite, code);
    }

    @Override
    public List<Cellule> findAllParentsActiveInclus(String codeCellule) {
        return celluleRepository.findAllParentsActiveInclus(codeCellule);
    }

    @Override
    public CelluleDto findCelluleCollMarByCode(String code) {
        return this.celluleMapper.celluleToCelluleDto(celluleRepository.findCelluleCollMarByCode(code));
    }

    public Cellule findCellulePere(ActeurVue acteur) {
        Cellule cellule;
        log.info("Calcul de la cellule pére de niveau 1.");

        if (!org.springframework.util.StringUtils.isEmpty(acteur.getCelluleTerrain())) {
            cellule = celluleRepository.findCelluleActiveByCode(acteur.getCelluleTerrain());
        } else {
            cellule = celluleRepository.findCelluleActiveByCode(acteur.getCellule());
        }

        while (cellule.getNiveau() > 1L) {
            cellule = this.celluleRepository.findCelluleActiveByCode(cellule.getCellulePere().getCode());
        }

        if (cellule.getNiveau() == 1L) {
            log.info("cleDG => " + cellule.getCode());
        } else {
            log.warn("Cellule pére non trouvé");
            return null;
        }
        return cellule;
    }


    /**
     * Récupère la description d'une cellule. Cette description est stockée dans
     * un cache.
     *
     * @param cle la clé de la cellule
     * @return la description de la cellule
     */
    public String selectDescriptionCellule(final String cle) {
        synchronized (this.cacheDescription) {
            String cached = this.cacheDescription.get(cle);
            if (cached != null) {
                // l'élément à été trouvé dans le cache
                return cached;
            }
            if (StringUtil.isNotBlank(cle)) {
                String coll = CelluleUtils.getColl(cle);
                String sousColl = CelluleUtils.getSsColl(cle);
                String code = CelluleUtils.getCode(cle);
                Cellule cellule = select(coll, sousColl, code);

                if (cellule != null) {
                    String description = CelluleUtils.getDescription(cellule);
                    // on place le résultat dans le cache
                    this.cacheDescription.put(cle, description);
                    return description;
                }
            }
        }
        return cle;
    }

    /**
     * Sélectionne une cellule de l'organigramme
     * Surchage de la methode Select pour choisir de récupérer les cellules non actives ou pas.
     *
     * @param coll        la collectivité de la cellule.
     * @param sousColl    la sous collectivité de la cellule.
     * @param code        le code de la cellule.
     * @param checkActive bool de vérification.
     * @return la cellule demandée.
     */
    public Cellule select(String coll, String sousColl, String code, boolean checkActive) {
        if (checkActive) {
            return this.celluleRepository.findCelluleActiveByCode(code);
        }
        return this.celluleRepository.findByCollAndSsCollAndCode(coll, sousColl, code);

    }

    /**
     * Sélection d'une cellule.
     *
     * @param coll     : le collectivité de la cellule.
     * @param sousColl : la sous collectivité de la cellule.
     * @param code     : le code de la cellule.
     * @return la cellule demandée si elle existe, null sinon.
     */
    public Cellule select(final String coll, final String sousColl,
                          final String code) {
        return this.select(coll, sousColl, code, false);
    }

    /**
     * Sélectionne une cellule à partir de sa clé.
     *
     * @param cle la clé associée à une cellule.
     * @return Cellule la cellule demandée.
     */
    public Cellule select(final String cle) {

        return findCelluleByCle(CelluleUtils.getColl(cle), CelluleUtils
                .getSsColl(cle), CelluleUtils.getCode(cle));
    }

    /**
     * Retourne la cellule pere d'un niveau donné d'une cellule
     *
     * @param coll   la collectivite
     * @param ssColl la sous collectivite
     * @param code   le code
     * @param niveau
     * @return la cle de la cellule demandée si elle existe, null sinon
     */
    public String findPere(final String coll, final String ssColl,
                           final String code, final int niveau) {

        List<Object[]> objects = this.celluleRepository.findClePeres(coll, ssColl, code);

        for (Object[] t : objects) {
            if (((Number) t[3]).intValue() == niveau) {
                String cle =
                        CelluleUtils.getCle((String) t[0], (String) t[1],
                                (String) t[2]);
                return cle;
            }
        }

        return null;
    }

}