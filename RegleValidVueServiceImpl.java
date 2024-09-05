package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.FiltreRegleValidationDto;
import fr.vdm.referentiel.refadmin.dto.QueryDto;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.RegleValidVueRepository;
import fr.vdm.referentiel.refadmin.service.RegleValidVueService;
import fr.vdm.referentiel.refadmin.service.specifications.RegleValidVueSpecifications;
import fr.vdm.referentiel.refadmin.utils.CelluleUtils;
import fr.vdm.referentiel.refadmin.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegleValidVueServiceImpl implements RegleValidVueService {

    private final RegleValidVueRepository regleValidVueRepository;

    public RegleValidVueServiceImpl(RegleValidVueRepository regleValidVueRepository) {
        this.regleValidVueRepository = regleValidVueRepository;
    }

    public List<RegleValidVue> find(FiltreRegleValidationDto filter) {
        //return regleValidVueRepository.findDistinctRegleValidVueByOrderByIdreglevalidDesc();
        Specification<RegleValidVue> regleValidVueSpecification = RegleValidVueSpecifications.filterByName(filter);
        return regleValidVueRepository.findAll(regleValidVueSpecification);
    }

    private QueryDto constructQuerySearch(final FiltreRegleValidationDto filtre) {

        StringBuilder queryStrFrom = new StringBuilder();
        StringBuilder queryStrWhere = new StringBuilder();
        HashMap<String, Object> params = new HashMap<>();

        // Si affectation.size < 2
        // Recuperer toutes les directions de la délégation choisies.
        boolean isDelegation= StringUtils.isBlank(filtre.getAffectation().getAffectation(3));

        // Le boolean Acteur
        queryStrWhere.append("WHERE rvv.type = ");
        if (filtre.getActeur()) {
            queryStrWhere.append("'" + RegleValidVue.TYPE_ACTEUR + "' ");
        } else {
            queryStrWhere.append("'" + RegleValidVue.TYPE_DROIT + "' ");
        }

        // Le champ de recherche nom, pr�nom, login, matricule porte sur le set
        // d'acteur de la regle de validation
        if (StringUtil.isNotBlank(filtre.getNom())
                || StringUtil.isNotBlank(filtre.getPrenom())
                || StringUtil.isNotBlank(filtre.getLogin())
                || StringUtil.isNotBlank(filtre.getMatricule())
                || filtre.getIdActeur() != null) {
            // Ajout de la table vue dans la clause FROM
            queryStrFrom.append(",ActeurVue av ");
            // Le reste de la query (a partir du WHERE)
            queryStrWhere.append("AND  av in elements(rvv.acteurs) ");
            if (StringUtil.isNotBlank(filtre.getNom())) {
                queryStrWhere.append("AND ( UPPER(av.nom) LIKE UPPER(:nom) ");
                // 29/10/2015 � MMN - EVT 10241 : Prise en compte du nom Marital
                // + nom Usuel
                queryStrWhere
                        .append("OR UPPER(av.nomMarital) LIKE UPPER(:nom) ");
                queryStrWhere
                        .append("OR UPPER(av.nomUsuel) LIKE UPPER(:nom)) ");
                params.put("nom", StringUtil.addJoker(filtre.getNom()));
            }
            if (StringUtil.isNotBlank(filtre.getPrenom())) {
                queryStrWhere
                        .append("AND UPPER(av.prenom) LIKE UPPER(:prenom) ");
                params.put("prenom", StringUtil.addJoker(filtre.getPrenom()));
            }
            if (StringUtil.isNotBlank(filtre.getLogin())) {
                queryStrWhere.append("AND UPPER(av.login) LIKE UPPER(:login) ");
                params.put("login", StringUtil.addJoker(filtre.getLogin()));
            }
            if (StringUtil.isNotBlank(filtre.getMatricule())
                    && filtre.getMatricule().length() == 8) {
                queryStrWhere.append("AND UPPER(av.idta) LIKE :idta ");
                params.put("idta", filtre.getMatricule().substring(0, 4));
                queryStrWhere.append("AND UPPER(av.idtn) LIKE :idtn ");
                params.put("idtn", filtre.getMatricule().substring(4, 8));
            }
            if (filtre.getIdActeur() != null) {
                queryStrWhere.append("AND av.idActeurVue = :idActeur ");
                params.put("idActeur", filtre.getIdActeur());
            }
        }

        // Le champ de recherche Offre
        // 18/11/2015 � MMN - EVT 10262 : Si lblOffreStrict est � vrai, faire
        // une recherche stricte de l�offre.
        if (!filtre.getActeur() && StringUtils.isNotBlank(filtre.getOffre())) {
            if (filtre.isLblOffreStrict()) {
                queryStrWhere.append("AND UPPER(rvv.offre.libelle) = :offre ");
                params.put("offre", filtre.getOffre());
            } else {
                queryStrWhere
                        .append("AND UPPER(rvv.offre.libelle) like UPPER(:offre)");
                params.put("offre", StringUtil.addJoker(filtre.getOffre()));
            }
        }
        if (StringUtil.isNotBlank(filtre.getCodeOffre())) {
            queryStrWhere.append("AND UPPER(rvv.offre.code) LIKE UPPER(:codeOffre) ");
            params.put("codeOffre", StringUtil.addJoker(filtre.getCodeOffre()));
        }
        if (filtre.getIdNomResponsable() != null) {
            queryStrWhere.append("AND rvv.offre.idNomResponsable = :idNomResponsable ");
            params.put("idNomResponsable", filtre.getIdNomResponsable());
        }

        if(filtre.isMetier() && filtre.isTransverse()){
            queryStrWhere.append("AND rvv.offre.transverse = true AND rvv.offre.metier = true ");
        }else if(filtre.isMetier() || filtre.isTransverse()){
            if(filtre.isMetier()){
                queryStrWhere.append("AND rvv.offre.transverse = false AND rvv.offre.metier = true ");
            }else{
                queryStrWhere.append("AND rvv.offre.transverse = true AND rvv.offre.metier = false ");
            }
        }

        if(StringUtils.isNotBlank(filtre.getLabel())){
            queryStrWhere.append("AND CONVERT(UPPER(rvv.offre.label), 'US7ASCII') like CONVERT(UPPER(:label), 'US7ASCII') ");
            params.put("label",StringUtil.addJoker(filtre.getLabel()));
        }

        /*
         * if (!filtre.getActeur() && StringUtils.isNotBlank(filtre.getOffre()))
         * { queryStrWhere
         * .append("AND UPPER(rvv.offre.libelle) like UPPER(:offre)");
         * params.put("offre", StringUtil.addJoker(filtre.getOffre())); }
         */

        // Le champ de recherche Fonction-Droit
        if (!filtre.getActeur() && filtre.getFonctionDroit() != null) {
            queryStrWhere.append("AND UPPER(rvv.roleDroit.id) like UPPER(:roleDroit)");
            params.put("roleDroit", filtre.getFonctionDroit());
        }

        // Le champ de recherche Fonction-Acteur
        if (filtre.getActeur() && filtre.getFonctionActeur() != null) {
            queryStrWhere.append("AND UPPER(rvv.roleActeur.id) like UPPER(:roleActeur)");
            params.put("roleActeur", filtre.getFonctionActeur());
        }

        // Les champs de recherche sur la cellule (Collectivite,D�l�gation
        // G�n�rale...)
        if (filtre.getAffectation() != null
                && StringUtils.isNotBlank(filtre.getAffectation()
                .getCodeAffectation())) {
            // "coll/sscoll/code" est la cl� primaire du pointeur
            // reglevalidation->cellule
            String coll = CelluleUtils.getColl(filtre.getAffectation().getCodeAffectation());
            String sscoll = CelluleUtils.getSsColl(filtre.getAffectation().getCodeAffectation());
            String code = CelluleUtils.getCode(filtre.getAffectation().getCodeAffectation());

            // Le cast as varchar2(255) est n�cessaire pour qu'hibernate puisse
            // caster la liste d'�l�ment.
            List<Object> celList = new ArrayList();
            if (isDelegation){
                /*celList = getSession().createSQLQuery(
                        " SELECT cast(cel.cellule as varchar2(255)) FROM RFSTGRCEL cel "
                                + " WHERE cel.CELPERE= '" + code + "'"
                                + " OR cel.cellule = '" + code + "'").list();*/
            } else{
                /*celList  = getSession().createSQLQuery(
                        " SELECT cast(cel.cellule as varchar2(255)) FROM RFSTGRCEL cel "
                                + " connect by prior  cel.idgrcelpere=cel.idgrcel"
                                + " START WITH cel.coll= '" + coll + "'"
                                + " AND cel.sscoll = '" + sscoll + "'"
                                + " AND cel.cellule = '" + code + "'").list();*/
            }

            // Ajout de la table vue dans la clause FROM
            queryStrFrom.append(",LienRegleCellule cel ");
            // Le reste de la query (a partir du WHERE)
            queryStrWhere.append("AND (cel in elements(rvv.lienRegleCellules) or cel.cellule is null) ");

            queryStrWhere.append("AND (cel.collectivite = :coll OR cel.collectivite is null) ");
            params.put("coll", coll);

            queryStrWhere.append("AND (cel.sousCollectivite = :sscoll OR cel.sousCollectivite is null)");
            params.put("sscoll", sscoll);

            queryStrWhere.append("AND (cel.cellule IN (:list) OR cel.cellule is null) ");
            params.put("list", celList);

        }

        return new QueryDto(queryStrFrom, queryStrWhere, params);
    }

    protected Query bindParameters(final Map<String, Object> params, final Query query) {
        for (String key : params.keySet()) {
            Object param = params.get(key);
            if (param instanceof Collection) {
                query.setParameterList(key, (Collection) param);
            } else {
                query.setParameter(key, param);
            }
        }
        return query;
    }
}
