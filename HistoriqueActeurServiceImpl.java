package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.FicheHistoriqueActeurDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueAffectActeurDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueHabilitActeurDto;
import fr.vdm.referentiel.refadmin.dto.HistoriqueModifActeurDto;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.service.EmailService;
import fr.vdm.referentiel.refadmin.service.HistoriqueActeurService;
import fr.vdm.referentiel.refadmin.utils.ConstanteActeur;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class HistoriqueActeurServiceImpl implements HistoriqueActeurService {

    private final HistoriqueDroitRepository historiqueDroitRepository;
    private final ActeurVueRepository acteurVueRepository;
    private final HistoriqueActeurRepository historiqueActeurRepository;
    private final DroitRepository droitRepository;
    private final EmailService emailService;
    private final HistoriqueEmailRepository historiqueEmailRepository;
    private final CelluleRepository celluleRepository;


    public HistoriqueActeurServiceImpl(HistoriqueDroitRepository historiqueDroitRepository,
                                       ActeurVueRepository acteurVueRepository,
                                       HistoriqueActeurRepository historiqueActeurRepository,
                                       DroitRepository droitRepository,
                                       EmailService emailService,
                                       HistoriqueEmailRepository historiqueEmailRepository,
                                       CelluleRepository celluleRepository) {
        this.historiqueDroitRepository = historiqueDroitRepository;
        this.acteurVueRepository = acteurVueRepository;
        this.historiqueActeurRepository = historiqueActeurRepository;
        this.droitRepository = droitRepository;
        this.emailService = emailService;
        this.historiqueEmailRepository = historiqueEmailRepository;
        this.celluleRepository = celluleRepository;
    }



    /**
     * Permet de charger les détails de l'historique d'un acteur.
     * @param idActeur l'identifiant d'un acteur
     * @return FicheHistoriqueActeurDto Informations détaillées de l'historique d'un acteur.
     */
    @Override
    public FicheHistoriqueActeurDto getDetailHistoriqueActeur(Long idActeur) {
        // On vérifie si l'historique existe
        if (idActeur != null) {

            // Chargement des changements d'affectations (onglet Affectations)
            List<HistoriqueAffectActeurDto> historiqueAffectation =
                    findHistoriqueAffectation(idActeur);
            // Chargement des changements d'habilitation
            List<HistoriqueHabilitActeurDto> historiqueHabilitActeurDto =
                    findHistoriqueHabilitation(idActeur, historiqueAffectation);
            // Chargements des autres changements (ongles Autres)
            List<HistoriqueModifActeurDto> historiqueModificationList =
                    findHistoriqueModification(idActeur);
            // Assemblement du resultat
            return new FicheHistoriqueActeurDto(historiqueAffectation, historiqueModificationList, historiqueHabilitActeurDto);
        } else {
            return null;
        }
    }

    /**
     * Recherche les modifications d'affectations dans l'historique acteurs
     * @param idActeur
     *            : l'id de l'acteur
     * @return la liste des modifications des acteurs
     */
    public List<HistoriqueAffectActeurDto> findHistoriqueAffectation(
            final Long idActeur) {
        // Recherche de la liste des historiques d'acteurs pour un acteur donné.
        List<HistoriqueAffectActeurDto> hisAffectationsList =
                Stream.of(this.getHisAffectationOffiByIdActeur(idActeur), this.getHisAffectationTerrainByIdActeur(idActeur))
                        .filter(list -> list!=null && !list.isEmpty())
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

        ActeurVue acteurVue = this.acteurVueRepository.findById(idActeur).orElse(null);

        if (acteurVue!=null){
            if (acteurVue.getColl() != null && acteurVue.getCellule() != null){
                this.processAffectation(acteurVue, hisAffectationsList,
                        acteurVue.getColl(), acteurVue.getSsColl(), acteurVue.getCellule(), false);
            }
            if (acteurVue.getCollTerrain() != null && acteurVue.getCelluleTerrain() != null){
                this.processAffectation(acteurVue, hisAffectationsList,
                        acteurVue.getCollTerrain(), acteurVue.getSsCollTerrain(), acteurVue.getCelluleTerrain(), true);
            }
        }

        if (!hisAffectationsList.isEmpty()){
            hisAffectationsList.sort(
                    Comparator.<HistoriqueAffectActeurDto, Boolean>comparing(
                            dto -> dto.getFinHisto() == null,
                            Comparator.reverseOrder()
                    ).thenComparing(
                            HistoriqueAffectActeurDto::getDebHisto,
                            Comparator.reverseOrder()
                    ).thenComparing(
                            HistoriqueAffectActeurDto::getFinHisto,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    )
            );
        }
        return hisAffectationsList;

    }

    private List<HistoriqueAffectActeurDto> getHisAffectationOffiByIdActeur(Long idActeur){
        List<HistoriqueAffectActeurDto> hisAffectationsOfficiel =
                this.historiqueActeurRepository.findHisAffectationOffiByIdActeur(idActeur);
        if (hisAffectationsOfficiel == null || hisAffectationsOfficiel.isEmpty()){
            return null;
        }else{
            return hisAffectationsOfficiel;
        }
    }

    private List<HistoriqueAffectActeurDto> getHisAffectationTerrainByIdActeur(Long idActeur){
        List<HistoriqueAffectActeurDto> hisAffectationsTerrain =
                this.historiqueActeurRepository.findHisAffectationTerrainByIdActeur(idActeur);
        if (hisAffectationsTerrain == null || hisAffectationsTerrain.isEmpty()){
            return null;
        }else{
            return hisAffectationsTerrain;
        }
    }

    private void processAffectation(ActeurVue acteurVue, List<HistoriqueAffectActeurDto> histoAffectationsList,
                                    String coll, String ssColl, String cellule, boolean isCollTerrain) {
        if (Objects.nonNull(acteurVue) && Objects.nonNull(acteurVue.getColl()) &&
                !affectationExists(histoAffectationsList, coll, ssColl, cellule)) {
            Cellule celluleActiveByCode = this.celluleRepository.findCelluleActiveByCode(cellule);
            if (celluleActiveByCode != null){
                histoAffectationsList.add(new HistoriqueAffectActeurDto(cellule, celluleActiveByCode.getCreation(),
                        null, isCollTerrain, coll, ssColl, celluleActiveByCode.getLibLdapGrCel()));
            }
        } else {
            histoAffectationsList.stream()
                    .filter(s -> Objects.nonNull(s.getColl()) && s.getColl().equals(coll) &&
                            s.getSsColl().equals(ssColl) && s.getCellule().equals(cellule))
                    .forEach(s -> s.setFinHisto(null));
        }
    }

    private boolean affectationExists(List<HistoriqueAffectActeurDto> hisAffectationsList,
                                      String coll, String ssColl, String cellule) {
        return hisAffectationsList.stream()
                .anyMatch(s -> Objects.nonNull(s.getColl()) && s.getColl().equals(coll) &&
                        s.getSsColl().equals(ssColl) && s.getCellule().equals(cellule));
    }


    /**
     * Rechercher les mdofications d'habilitation dans l'historique offre
     * @param idActeur
     *            : l'id de l'offre
     * @param historiqueAffectActeurDtoList
     *            : la liste des modifications d'affectations
     * @return la liste des modifications des acteurs
     */

    public List<HistoriqueHabilitActeurDto> findHistoriqueHabilitation(
            final Long idActeur,
            final List<HistoriqueAffectActeurDto> historiqueAffectActeurDtoList) {

        // Ajouter les droits terminés
        List<HistoriqueHabilitActeurDto>  histoHabilitActeurs = this.getHistoHabilitActeurByIdActeur(idActeur);

        // Ajouter les droits actifs
        this.addHabilitActeur(histoHabilitActeurs, idActeur);

        // Ajout de l'affectation aux éléments présents dans le DTO
        if (!histoHabilitActeurs.isEmpty()) {

            // Ajout de l'affectation dans le dto en fonction des dates
            for (HistoriqueHabilitActeurDto hab : histoHabilitActeurs) {
                for (HistoriqueAffectActeurDto aff : historiqueAffectActeurDtoList) {
                    boolean affectationValide = hab.getDateFin() == null
                            || !hab.getDateFin().isBefore(aff.getDebHisto());
                    if (hab.getDateFin() != null
                            && hab.getDateFin().isBefore(aff.getDebHisto())) {
                        affectationValide = false;
                    }
                    if (affectationValide) {
                        hab.getAffectations().add(aff.getCelluleNomComple() + " (" + aff.getCellule() + ")");
                    }
                }
            }

            histoHabilitActeurs.sort(
                    Comparator.<HistoriqueHabilitActeurDto, Boolean>comparing(
                            dto -> dto.getDateFin() == null,
                            Comparator.reverseOrder()
                    ).thenComparing(
                            HistoriqueHabilitActeurDto::getDateDebut,
                            Comparator.reverseOrder()
                    ).thenComparing(
                            HistoriqueHabilitActeurDto::getDateFin,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    )
            );
        }


        return histoHabilitActeurs;
    }

    private List<HistoriqueHabilitActeurDto> getHistoHabilitActeurByIdActeur(Long idActeur){
        List<HistoriqueHabilitActeurDto>  histoHabilitActeurs =
                this.historiqueDroitRepository.findHistoHabilitActeurByIdActeur(idActeur);
        if (histoHabilitActeurs == null || histoHabilitActeurs.isEmpty()){
            return new ArrayList<>();
        }else{
            return histoHabilitActeurs;
        }
    }

    private void addHabilitActeur(List<HistoriqueHabilitActeurDto> histoHabilitActeurs, Long idActeur){

        // Recherche de la liste des droits atcuels pour un acteur donne
        List<Droit> droitActuelList = this.droitRepository.findAllByActeurBenef_IdActeur(idActeur);

        if (droitActuelList != null && !droitActuelList.isEmpty()){
            // Ajout des éléments de la liste des drtoits actuels dans le DTO
            histoHabilitActeurs.addAll(
                    droitActuelList.stream()
                    .map(HistoriqueHabilitActeurDto::new)
                    .collect(Collectors.toList())
            );
        }
    }



    /**
     * Recherche les autres modifications dans l'historique acteurs
     * @param idActeur
     *            : l'id de l'acteur
     * @return la liste des modifications des acteurs
     */
    public List<HistoriqueModifActeurDto> findHistoriqueModification(
            final Long idActeur) {
        List<HistoriqueModifActeurDto> historiqueModifActeurDtoList = null;
        // Recherche de la liste des historiques d'acteur pour un acteur donne
        List<HistoriqueActeur> modifList =
                this.historiqueActeurRepository.findAllByIdActeur(idActeur);

        // Construction de la liste par comparaison 2 a 2 pour les modification
        // autres
        for (int i = 0; i < modifList.size() - 1; i++) {
            HistoriqueActeur hisAct1 = modifList.get(i);
            HistoriqueActeur hisAct2 = modifList.get(i + 1);
            historiqueModifActeurDtoList =
                    compareHistoriqueModification(
                            historiqueModifActeurDtoList, hisAct1, hisAct2);
        }

        // MEME Chose mais cette fois pour les emails
        // Recherche de la liste des historiques d'email pour un acteur donne
        List<HistoriqueEmail> historiqueEmailList =
                this.historiqueEmailRepository.findAllByIdEmail(idActeur);

        // Construction de la liste par comparaison 2 à 2 pour les modifications
        // emails
        for (int i = 0; i < historiqueEmailList.size() - 1; i++) {
            HistoriqueEmail hisAct1 = historiqueEmailList.get(i);
            HistoriqueEmail hisAct2 = historiqueEmailList.get(i + 1);
            historiqueModifActeurDtoList =
                    addHistoriqueEmailModification(
                            historiqueModifActeurDtoList, hisAct1, hisAct2);
        }

        ActeurVue acteurVue = this.acteurVueRepository.findById(idActeur).orElse(null);

        // Ajout des lignes correspodantes à l'Email courant
        Email emailCourant = null;
        Instant dateModif = null;
        String ancienneValeur = null;
        if (acteurVue != null && acteurVue.getIdEmail() != null) {
            emailCourant = this.emailService.getEmailByIdEmail(acteurVue.getIdEmail());
        }

        if (historiqueModifActeurDtoList != null && emailCourant != null){
            for (HistoriqueModifActeurDto modifActeurDto: historiqueModifActeurDtoList){
                if (modifActeurDto.getAttribut().equals(ConstanteActeur.HIST_MOD_MESSAGERIE)){
                    if (dateModif == null){
                        dateModif = modifActeurDto.getDateFin();
                    }
                    if (dateModif.compareTo(modifActeurDto.getDateFin()) <= 0){
                        dateModif = modifActeurDto.getDateFin();
                        if (!modifActeurDto.getValeurApres().equals(emailCourant.getEmail())){
                            ancienneValeur = modifActeurDto.getValeurApres();
                        }else {
                            ancienneValeur = null;
                            break;
                        }
                    }
                }
            }

            if (ancienneValeur!=null){
                historiqueModifActeurDtoList.add(
                        new HistoriqueModifActeurDto(
                                dateModif, emailCourant.getTsModif(), ConstanteActeur.HIST_MOD_MESSAGERIE,
                                ancienneValeur, emailCourant.getEmail()));
            }
        }

        if (historiqueModifActeurDtoList != null
                && !historiqueModifActeurDtoList.isEmpty()) {

            // Trie par Date de la date la plus ancienne à la date la plus
            // recente
            historiqueModifActeurDtoList.sort(Comparator.comparing(HistoriqueModifActeurDto::getDateDebut).reversed());
        }
        return historiqueModifActeurDtoList;
    }

    /**
     * Compare deux changements autres et mets le resultat de la différence dans
     * un dto.
     * @param historiqueModifActeurDtoList
     *            : liste des historiques autres dto à mettre à jour
     * @param affAct1
     *            : Premier objet à comparer
     * @param affAct2
     *            : Deuxieme objet à comparer
     * @return la liste des dto mise à jour
     */
    private List<HistoriqueModifActeurDto> compareHistoriqueModification(
            List<HistoriqueModifActeurDto> historiqueModifActeurDtoList,
            final HistoriqueActeur affAct1, final HistoriqueActeur affAct2) {
        // Détection d'un changement de modification
        if (affAct1 != null && affAct2 != null && affAct1.getDebHisto() != null) {
            List<HistoriqueModifActeurDto> dto =
                    detecterChangement(affAct1, affAct2);
            // Ajout de l'historique i
            if (historiqueModifActeurDtoList == null) {
                historiqueModifActeurDtoList =
                        new ArrayList<>();
            }
            historiqueModifActeurDtoList.addAll(dto);
        }
        return historiqueModifActeurDtoList;
    }

    /**
     * Compare deux changements autres et mets le resultat de la différence dans
     * un dto.
     * @param historiqueModifActeurDtoList
     *            : liste des historiques autres dto à mettre à jour
     * @param affAct1
     *            : Premier objet à comparer
     * @param affAct2
     *            : Deuxieme objet à comparer
     * @return la liste des dto mise à jour
     */
    private List<HistoriqueModifActeurDto> addHistoriqueEmailModification(
            List<HistoriqueModifActeurDto> historiqueModifActeurDtoList,
            final HistoriqueEmail affAct1, final HistoriqueEmail affAct2) {

        // D�tection d'un changement de modification
        HistoriqueModifActeurDto dto =
                detecterChangementEmail(affAct1, affAct2);
        // Ajout de l'historique i
        if (dto != null) {
            if (historiqueModifActeurDtoList == null) {
                historiqueModifActeurDtoList =
                        new ArrayList<>();
            }
            historiqueModifActeurDtoList.add(dto);
        }
        return historiqueModifActeurDtoList;
    }

    /**
     * Detection d'un changement d'état entre 2 objets HistoriqueModificationDto
     * @param affAct1
     *            l'adresse de messagerie 1
     * @param affAct2
     *            l'adresse de messagerie 2
     * @return le dto comportant le changement d'etat.
     */
    private List<HistoriqueModifActeurDto> detecterChangement(
            final HistoriqueActeur affAct1, final HistoriqueActeur affAct2) {
        Instant dateDebut;
        Instant dateFin;
        String attribut;
        String valeurAvant;
        String valeurApres;
        HistoriqueModifActeurDto historiqueModification;
        List<HistoriqueModifActeurDto> listModifications = new ArrayList<>();
        dateDebut = affAct1.getDebHisto();
        dateFin = affAct2.getFinHisto();
        if (!StringUtils.equals(affAct1.getPrenom(), affAct2.getPrenom())) {
            attribut = ConstanteActeur.HIST_MOD_PRENOM;
            valeurAvant = affAct1.getPrenom();
            valeurApres = affAct2.getPrenom();
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
            listModifications.add(historiqueModification);
        }
        if (!StringUtils.equals(affAct1.getPrenomUsuel(), affAct2.getPrenomUsuel())) {
            attribut = ConstanteActeur.HIST_MOD_PRENOM_USUEL;
            valeurAvant = affAct1.getPrenomUsuel();
            valeurApres = affAct2.getPrenomUsuel();
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
            listModifications.add(historiqueModification);
        }
        if (!StringUtils.equals(affAct1.getNom(), affAct2.getNom())) {
            attribut = ConstanteActeur.HIST_MOD_NOM;
            valeurAvant = affAct1.getNom();
            valeurApres = affAct2.getNom();
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
            listModifications.add(historiqueModification);
        }

        if (!StringUtils.equals(affAct1.getNomUsuel(), affAct2.getNomUsuel())) {
            attribut = ConstanteActeur.HIST_MOD_NOM_USUEL;
            valeurAvant = affAct1.getNomUsuel();
            valeurApres = affAct2.getNomUsuel();
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
            listModifications.add(historiqueModification);
        }

        if (!StringUtils.equals(affAct1.getNomMarital(), affAct2.getNomMarital())) {
            attribut = ConstanteActeur.HIST_MOD_NOM_MARITAL;
            valeurAvant = affAct1.getNomMarital();
            valeurApres = affAct2.getNomMarital();
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
            listModifications.add(historiqueModification);
        }
        if (!StringUtils.equals(affAct1.getLogin(), affAct2.getLogin())) {
            attribut = ConstanteActeur.HIST_MOD_LOGIN;
            valeurAvant = affAct1.getLogin();
            valeurApres = affAct2.getLogin();
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
            listModifications.add(historiqueModification);
        }
        return listModifications;
    }

    /**
     * Detection d'un changement d'état entre 2 objets EmailActeur
     * @param modAct1
     *            l'adresse de messagerie 1
     * @param modAct2
     *            l'adresse de messagerie 2
     * @return le dto comportant le changement d'etat.
     */
    private HistoriqueModifActeurDto detecterChangementEmail(
            final HistoriqueEmail modAct1, final HistoriqueEmail modAct2) {
        boolean changement;
        Instant dateDebut = null;
        Instant dateFin = null;
        String attribut = null;
        String valeurAvant = null;
        String valeurApres = null;
        HistoriqueEmail historiqueEmail = null;
        Long idEmail2 = null;
        HistoriqueModifActeurDto historiqueModification = null;
        // Premier cas modAct 1 et modAct2 sont nulles
        if (modAct1 == null && modAct2 == null) {
            changement = false;
        }
        // Second cas mod 1 est nulle pas mod 2
        else if (modAct1 == null) {
            changement = true;
        } else if (modAct2 == null) {
            historiqueEmail = modAct1;
            dateFin = historiqueEmail.getFinHisto();
            changement = true;
        } else {
            // Detection de changement
            Long idEmail1 = modAct1.getIdEmail();
            idEmail2 = modAct2.getIdEmail();
            historiqueEmail = modAct1;
            Instant dateDebut1 = historiqueEmail.getDebHisto();
            ActeurVue acteurVue2 = acteurVueRepository.findActeurVueByIdEmail(modAct2.getIdEmail());
            if (acteurVue2 != null) {
                changement =
                        this.emailService.isDifferent(idEmail1, dateDebut1,
                                idEmail2);
            } else {
                Instant dateDebut2 = modAct2.getDebHisto();
                changement =
                        this.emailService.isHistoriqueDifferent(idEmail1,
                                dateDebut1, idEmail2, dateDebut2);
            }
        }
        if (changement && modAct2 != null && modAct2.getIdEmail() != null) {
            attribut = ConstanteActeur.HIST_MOD_MESSAGERIE;
            if (historiqueEmail != null) {
                dateDebut = historiqueEmail.getDebHisto();
                valeurAvant = historiqueEmail.getEmail();
            }
            ActeurVue acteurVue2 = acteurVueRepository.findActeurVueByIdEmail(modAct2.getIdEmail());
            if (acteurVue2 != null) {
                Email email2 = this.emailService.getEmailByIdEmail(idEmail2);
                if (email2 != null) {
                    dateFin = email2.getTsModif();
                    valeurApres = email2.getEmail();
                } else {
                    if (historiqueEmail != null) {
                        dateFin = historiqueEmail.getFinHisto();
                    }
                }
            } else {
                dateFin = modAct2.getFinHisto();
                valeurApres = modAct2.getEmail();
            }
            historiqueModification =
                    new HistoriqueModifActeurDto(dateDebut, dateFin, attribut,
                            valeurAvant, valeurApres);
        }
        return historiqueModification;
    }


}
