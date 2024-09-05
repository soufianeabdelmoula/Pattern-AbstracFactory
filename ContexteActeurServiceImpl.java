package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.ActeurValidationDto;
import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.dto.OffreValidationDto;
import fr.vdm.referentiel.refadmin.dto.OffresValidationDto;
import fr.vdm.referentiel.refadmin.mapper.CelluleMapper;
import fr.vdm.referentiel.refadmin.model.LienRegleCellule;
import fr.vdm.referentiel.refadmin.model.Offre;
import fr.vdm.referentiel.refadmin.model.RegleValidVue;
import fr.vdm.referentiel.refadmin.repository.RegleValidVueRepository;
import fr.vdm.referentiel.refadmin.service.CelluleService;
import fr.vdm.referentiel.refadmin.service.ContexteActeurService;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class ContexteActeurServiceImpl implements ContexteActeurService {


    @Autowired
    RegleValidVueRepository regleValidVueRepository;

    @Autowired
    CelluleService celluleService;

    CelluleMapper celluleMapper = CelluleMapper.INSTANCE;


    @CacheEvict(value = {"validationOffreDto", "validationActeurDto"}, key = "#idActeur")
    public void clearCache(Long idActeur) {
        log.info("cache is cleared");
    }

    /**
     * Recupere l'ensemble des cellules sur lesquelles un utilisateur peut
     * intervenir dans le workflow de gestion des acteurs.
     *
     * @param idActeur l'identifiant de l'utilisateur
     * @return un Dto regroupant l'ensemble des cellules sur lesquelles
     * l'utilisateur est suceptible d'intervenir en tant que valideur.
     */
    @Cacheable(value = "validationActeurDto", key = "#idActeur")
    public ActeurValidationDto buildValidationActeurDto(final Long idActeur) {
        ActeurValidationDto acteurValidation = new ActeurValidationDto();

        boolean isValideur = false;
        // La liste des cellules pour lequel l'acteur a un droit de valideur
        // hierarchique final.
        List<String> listeCellules = new LinkedList<String>();

        // La liste des cellules pour lesquels l'acteur a un droit de valideur
        // hierarchique non final
        List<String>[] tabCellules = new LinkedList[ConstanteWorkflow.MAX_HIERARCHIE];

        // Initialisation du tableau contenant les cellules sous forme de
        // LabelValueBean
        // trié par niveau pour une validation hiérarchique finale.

        List<CelluleDto>[] celluleValFinalParNiveau = new LinkedList[ConstanteWorkflow.MAX_NB_NIV_AFFECTATION];
        // Initialisation du tabCellules
        for (int i = 0; i < ConstanteWorkflow.MAX_HIERARCHIE; i++) {
            tabCellules[i] = new LinkedList<String>();
        }
        // Initialisation du CelluleValFinalParNiveau
        for (int i = 0; i < ConstanteWorkflow.MAX_NB_NIV_AFFECTATION; i++) {
            celluleValFinalParNiveau[i] = new LinkedList<CelluleDto>();
        }

        boolean isTechnique = construireValidationActeur(idActeur,
                listeCellules, tabCellules, celluleValFinalParNiveau);


        // Permet de savoir si l'acteur est valideur ou non.
        if (listeCellules.size() != 0 || isTechnique) {
            isValideur = true;
        } else {
            for (int i = 0; i < tabCellules.length; i++) {
                if (!tabCellules[i].isEmpty()) {
                    isValideur = true;
                    break;
                }
            }
        }
        // on construit le bean avec les valeurs obtenues
        acteurValidation.setValideur(isValideur);
        acteurValidation.setValidateurTechniqueAct(isTechnique);
        acteurValidation.setValidateurHierarchiqueFinal(listeCellules);
        acteurValidation.setValidateursHierarchique(tabCellules);
        acteurValidation.setCelluleValFinalParNiveau(celluleValFinalParNiveau);
        return acteurValidation;

    }

    /**
     * Permet de construire la liste des cellules pour lesquelles un acteur est
     * validateur hierarchique pour le workflow des acteurs.
     *
     * @param idActeur
     *            L'identifiant de l'acteur
     * @param listeCellules
     *            Liste des cellules pour lequel l'acteur est valideur
     *            hierarchiques
     * @param tabCellules
     *            Un tableau contenant la liste des cellules pour chaque niveau
     *            de l'organigramme.
     * @param celluleValFinalParNiveau
     *            Un tableau contenant une liste de ValueBean représentant les
     *            cellules pour lequel l'utilisateur est valideur hierarchique
     *            final trié par niveau.
     */
    private boolean construireValidationActeur(final Long idActeur,
                                               final List<String> listeCellules, final List<String>[] tabCellules,
                                               final List<CelluleDto>[] celluleValFinalParNiveau) {
        boolean isTechnique = false;
        // On sélectionne l'ensemble des regles hierarchiques pour l'acteur
        List<RegleValidVue> reglesHierarchiques = this.regleValidVueRepository
                .findReglesHierarchiquesActeur(idActeur);

        // On selectionne l'ensemble des regles techniques pour un acteur.
        List<RegleValidVue> reglesTech = this.regleValidVueRepository
                .findReglesTechniquesActeur(idActeur);
        if ((reglesTech != null) && (reglesTech.size() != 0)) {
            isTechnique = true;
        }
        // Pour chaque regle on recupere les cellules qui lui sont associés
        for (RegleValidVue regle : reglesHierarchiques) {
            // si la regle est une regle Role Acteur et que c'est une regle
            // valideur hierarchique on ajoute au tableau les cellules
            // correspondantes.
            if (regle.getRoleActeur() != null
                    && regle.getRoleActeur().getCode()
                    .equals(ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_FINAL)) {
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        listeCellules, celluleValFinalParNiveau);
            }
            if (regle.getRoleActeur() != null
                    && regle.getRoleActeur().getCode()
                    .equals(ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_1)) {
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        tabCellules[0], null);
            }
            if (regle.getRoleActeur() != null
                    && regle.getRoleActeur().getCode()
                    .equals(ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_2)) {
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        tabCellules[1], null);
            }
            if (regle.getRoleActeur() != null
                    && regle.getRoleActeur().getCode()
                    .equals(ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_3)) {
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        tabCellules[2], null);
            }
            if (regle.getRoleActeur() != null
                    && regle.getRoleActeur().getCode()
                    .equals(ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_4)) {
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        tabCellules[3], null);
            }
            if (regle.getRoleActeur() != null
                    && regle.getRoleActeur().getCode()
                    .equals(ConstanteWorkflow.ROLE_ACTEUR_HIERARCHIQUE_5)) {
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        tabCellules[4], null);
            }
        }
        return isTechnique;
    }


    /**
     * Recupere l'ensemble des cellules sur lesquelles un utilisateur peut
     * intervenir dans le workflow de gestion des acteurs.
     *
     * @param idActeur l'identifiant de l'utilisateur
     * @return un Dto regroupant l'ensemble des cellules sur lesquelles
     * l'utilisateur est suceptible d'intervenir en tant que valideur.
     */
    @Cacheable(value = "validationOffreDto", key = "#idActeur")
    public OffresValidationDto buildValidationOffreDto(final long idActeur) {
        // On récupère les offres pour lequel l'acteur est valideur.
        OffresValidationDto offresValidation = new OffresValidationDto();
        Map<Long, OffreValidationDto> listOffres = new LinkedHashMap<>();
        construireValidationOffre(idActeur, listOffres);

        // Permet de savoir si l'acteur est valideur ou non.
        if (listOffres.size() != 0) {
            offresValidation.setValideur(true);
        }
        offresValidation
                .setOffresValidation(new LinkedList<>(
                        listOffres.values()));
        offresValidation.setMapOffresValidation(listOffres);
        return offresValidation;
    }

    /**
     * Construit une liste d'OffreValidationDto représentant les offres pour
     * lesquels l'acteur passé en paramètre est un valideur
     *
     * @param idActeur
     *            L'identifiant des acteurs
     * @param listOffres
     *            La liste contenant les offres.
     */
    private void construireValidationOffre(final long idActeur,
                                           final Map<Long, OffreValidationDto> listOffres) {
        // On sélectionne l'ensemble des regles hierarchiques concernant une
        // offre pour l'acteur
        List<RegleValidVue> reglesHierarchiques = this.regleValidVueRepository
                .findRegleValidVueHierarchiqueOffreByIdActeur(idActeur);

        // On selectionne l'ensemble des regles techniques pour un acteur.
        List<RegleValidVue> reglesTech = this.regleValidVueRepository
                .findRegleValidVueTechniqueOffreByIdActeur(idActeur);

        // On récupére les offres pour lequel l'acteur est valideur technique.
        for (RegleValidVue regle : reglesTech) {
            Offre offre = regle.getOffre();
            if (offre != null) {
                OffreValidationDto offreValidation = new OffreValidationDto(
                        offre.getId(), offre.getLibelle());
                offreValidation.setValideurTechnique(true);
                listOffres.put(offre.getId(), offreValidation);
            }
        }
        // Pour chaque regle de validation hierarchique on verifie si une offre
        // validation
        // est deja concerne si oui on lui ajoute les cellules pour la regle
        // sinon on crée une offre de validation et on lui affecte les cellules
        for (RegleValidVue regle : reglesHierarchiques) {
            Offre offre = regle.getOffre();
            OffreValidationDto offreValidation = listOffres.get(offre
                    .getId());
            if (offreValidation == null) {
                offreValidation = new OffreValidationDto(offre.getId(),
                        offre.getLibelle());
            }
            if (regle.getRoleDroit() != null
                    && regle.getRoleDroit().getCode()
                    .equals(ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_FINAL)) {
                List<String> listeCellules = offreValidation
                        .getValidateurHierarchiqueFinal();
                List<CelluleDto>[] celluleValParNiveau = offreValidation
                        .getCelluleValFinalParNiveau();
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        listeCellules, celluleValParNiveau);
                offreValidation
                        .setCelluleValFinalParNiveau(celluleValParNiveau);
                offreValidation.setValidateurHierarchiqueFinal(listeCellules);
                listOffres.put(offre.getId(), offreValidation);
            }
            // On regarde si la regle concerne la validation d'une offre pour un
            // niveau de hierarchie i+1
            int i = -1;
            if (regle.getRoleDroit() != null
                    && regle.getRoleDroit().getCode()
                    .equals(ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_1)) {
                i = 0;
            }
            if (regle.getRoleDroit() != null
                    && regle.getRoleDroit().getCode()
                    .equals(ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_2)) {
                i = 1;
            }
            if (regle.getRoleDroit() != null
                    && regle.getRoleDroit().getCode()
                    .equals(ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_3)) {
                i = 2;
            }
            if (regle.getRoleDroit() != null
                    && regle.getRoleDroit().getCode()
                    .equals(ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_4)) {
                i = 3;
            }
            if (regle.getRoleDroit() != null
                    && regle.getRoleDroit().getCode()
                    .equals(ConstanteWorkflow.ROLE_OFFRE_HIERARCHIQUE_5)) {
                i = 4;

            }
            if (i != -1) {
                List<String> listeCellules = offreValidation
                        .getValideursHierarchique()[i];
                ajouterAffectationHierarchique(regle.getLienRegleCellules(),
                        listeCellules, null);
                offreValidation.setValideursHierarchique(i, listeCellules);
                listOffres.put(offre.getId(), offreValidation);
            }

        }
    }

    /**
     * Methode permettant d'ajouter des cellules pour un tableau d'affectation
     * hierarchique passé en paramètre.
     *
     * @param liensRegleCellule
     *            La liste des liens ReglesCellules contenant les cellules à
     *            ajouter
     * @param listeCellules
     *            La liste des cellules ou l'acteur est validateur hierarchique.
     * @param celluleValFinalParNiveau
     *            Un tableau contenant une liste de ValueBean représentant les
     *            cellules pour lequel l'utilisateur est valideur hierarchique
     *            final trié par niveau.
     */
    private void ajouterAffectationHierarchique(
            final List<LienRegleCellule> liensRegleCellule,
            final List<String> listeCellules,
            final List<CelluleDto>[] celluleValFinalParNiveau) {
        // On parcourt chacun des liens pour ajouter les cellules
        // auquelles la regle est rattaché.
        for (LienRegleCellule lienRegleCellule : liensRegleCellule) {

            List<CelluleDto> liste = celluleService.findAllfilsActiveInclus(lienRegleCellule.getCellule());


            List<CelluleDto>[] listeParNiveau = new ArrayList[ConstanteWorkflow.MAX_NB_NIV_AFFECTATION];
            for (int i = 0; i < ConstanteWorkflow.MAX_NB_NIV_AFFECTATION; i++) {
                listeParNiveau[i] = new ArrayList<CelluleDto>();
            }
            for(CelluleDto cellule : liste){
                listeParNiveau[ (int) cellule.getNiveau()].add(cellule);
            }

            // On parcours pour chaque niveau et on ajoute à la liste la cle de
            // la cellule
            for (int i = 0; i < ConstanteWorkflow.MAX_NB_NIV_AFFECTATION; i++) {
                for (CelluleDto cellule : listeParNiveau[i]) {
                    String cle =  cellule.getCode();
                    if (cle != null && !listeCellules.contains(cle)) {
                        listeCellules.add(cle);
                        // Dans le cas d'une affectation hiérarchique finale on
                        // ajout
                        // le labelValueBean de la cellule et on l'ajoute par
                        // rapport à son niveau

                        if (celluleValFinalParNiveau != null) {
                            if (!celluleValFinalParNiveau[i].contains(cellule)) {
                                celluleValFinalParNiveau[i].add(cellule);
                            }
                        }
                    }
                }
            }
            // on ajoute aussi la cellule
            String cle = lienRegleCellule.getCellule();
            if (cle != null && !listeCellules.contains(cle)) {
                listeCellules.add(cle);
                if (celluleValFinalParNiveau != null) {
                    CelluleDto cellulePere = celluleService.findCelluleActiveByCode(lienRegleCellule.getCellule());

                    if (cellulePere != null) {
                        int niveau = (int) cellulePere.getNiveau();

                        if (!celluleValFinalParNiveau[niveau].contains(cellulePere)) {
                            celluleValFinalParNiveau[niveau].add(cellulePere);
                        }
                        // Pour que le tableau soit complet on cherche les Peres
                        // par niveau de celluleDao
                        // que l'on ajoute celluleValFinalParNiveau

                        List<CelluleDto> listPere = celluleMapper.celluleToCelluleDto(celluleService.findAllParentsActiveInclus(lienRegleCellule.getCellule()));


                        CelluleDto[] peres = new CelluleDto[ConstanteWorkflow.MAX_NB_NIV_AFFECTATION];

                        for(CelluleDto c : listPere){
                            peres[(int) c.getNiveau()] = c;
                        }

                        if (peres != null) {
                            for (int i = 0; i < niveau; i++) {
                                if (!celluleValFinalParNiveau[i]
                                        .contains(peres[i])) {
                                    celluleValFinalParNiveau[i].add(peres[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
