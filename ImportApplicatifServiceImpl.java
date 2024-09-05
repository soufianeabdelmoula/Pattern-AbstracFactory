package fr.vdm.referentiel.refadmin.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.ConstanteMessage;
import fr.vdm.referentiel.refadmin.utils.ConstanteWorkflow;
import fr.vdm.referentiel.refadmin.utils.DnUtils;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.*;

@Service
@Log4j2
public class ImportApplicatifServiceImpl implements ImportApplicatifService {

    private final ActeurVueService acteurVueService;
    private final GroupeApplicatifService groupeApplicatifService;
    private final ParametrageLdapRepository parametrageLdapRepository;
    private final DemandeDroitService demandeDroitService;
    private final StatutDemandeRepository statutDemandeRepository;
    private final DroitService droitService;
    private final LienDemGrpApplicatifRepository demGrpApplicatifRepository;
    private final DroitRepository droitRepository;
    private final DemandeDroitRepository demandeDroitRepository;
    private final ImportActeurGroupeApplicatifRepository importActeurGrpeAppRepository;
    private final TrtImportGrpApplicatifRepository trtImportGrpApplicatifRepository;
    private final PropagationLDAPService propagationLDAPService;
    public ImportApplicatifServiceImpl(ActeurVueService acteurVueService,
                                       GroupeApplicatifService groupeApplicatifService,
                                       ParametrageLdapRepository parametrageLdapRepository,
                                       DemandeDroitService demandeDroitService,
                                       StatutDemandeRepository statutDemandeRepository,
                                       DroitService droitService,
                                       LienDemGrpApplicatifRepository demGrpApplicatifRepository,
                                       DroitRepository droitRepository,
                                       DemandeDroitRepository demandeDroitRepository,
                                       ImportActeurGroupeApplicatifRepository importActeurGrpeAppRepository,
                                       TrtImportGrpApplicatifRepository trtImportGrpApplicatifRepository,
                                       PropagationLDAPService propagationLDAPService) {
        this.acteurVueService = acteurVueService;
        this.groupeApplicatifService = groupeApplicatifService;
        this.parametrageLdapRepository = parametrageLdapRepository;
        this.demandeDroitService = demandeDroitService;
        this.statutDemandeRepository = statutDemandeRepository;
        this.droitService = droitService;
        this.demGrpApplicatifRepository = demGrpApplicatifRepository;
        this.droitRepository = droitRepository;
        this.demandeDroitRepository = demandeDroitRepository;
        this.importActeurGrpeAppRepository = importActeurGrpeAppRepository;
        this.trtImportGrpApplicatifRepository = trtImportGrpApplicatifRepository;
        this.propagationLDAPService = propagationLDAPService;
    }

    @Override
    public ImportFileDto readFileCsvImported(MultipartFile file) throws ServiceException {
        ImportFileDto importFileDtos;

        List<ImportMessageErreur> importInexisted = new ArrayList<>();
        List<ImportApplicatifDto> importExisted = new ArrayList<>();


        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            //CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build(); // ignore la première ligne
            CSVReader csvReader = new CSVReaderBuilder(reader).build();

            List<String[]> records = csvReader.readAll();
            long competeurLigne = 0;

            if (records.isEmpty()){
                log.warn("Votre fichier est vide");
                importInexisted.add(new ImportMessageErreur("Votre fichier est vide"));
            }else {
                for (String[] record : records) {
                    // Accédez à chaque champ dans la ligne du fichier CSV
                    log.info("Accédez à chaque champ dans la ligne du fichier CSV");

                    competeurLigne++;

                    if (record == null || record.length < 2 || record.length > 3){
                        String message = String.format("La ligne %d ne doit contenir que 2 à 3 éléments.", competeurLigne);
                        log.warn(message);
                        importInexisted.add(new ImportMessageErreur(competeurLigne, Arrays.toString(record), message));
                    }else {
                        // Accédez à chaque champ dans la ligne du fichier CSV
                        String columnLogin = StringUtils.hasText(record[0]) ? record[0] : null;
                        String columnOffre = StringUtils.hasText(record[1]) ? record[1] : null;
                        String columnGrpSec = record.length > 2 && StringUtils.hasText(record[2]) ? record[2] : null;

                        // Vérifiez l'existence de chaque colonne dans sa table respective
                        boolean columnLoginExists = isActeurVueExists(columnLogin);
                        boolean columnOffreExists = isParametrageLdapExists(columnOffre);
                        boolean columnGrpSecExists = isGroupeApplicatifExists(columnGrpSec);

                        if (!columnLoginExists) {
                            String message = String.format("La colonne login ligne %d n'existe pas en base.", competeurLigne);
                            log.warn(message);
                            importInexisted.add(new ImportMessageErreur(competeurLigne, "Login", Arrays.toString(record), message));

                        } else if (!columnOffreExists) {
                            String message = String.format("La colonne groupe principale ligne %d n'existe pas en base.", competeurLigne);
                            log.warn(message);
                            importInexisted.add(new ImportMessageErreur(competeurLigne, "Groupe principale", Arrays.toString(record), message));

                        }  else if (columnGrpSec != null && !columnGrpSecExists) {

                            String message = String.format("La colonne groupe secondaire ligne %d n'existe pas en base.", competeurLigne);
                            log.warn(message);
                            importInexisted.add(new ImportMessageErreur(competeurLigne, "Groupe secondaire", Arrays.toString(record), message));

                        } else {
                            if (columnGrpSecExists) {
                                log.info(String.format("La colonne %s, %s et %s ligne %d existe en base.", columnLogin, columnOffre, columnGrpSec, competeurLigne));
                                this.filterDataFormCsv(columnLogin, columnOffre, columnGrpSec, competeurLigne, importInexisted, importExisted);
                            } else {
                                log.info(String.format("La colonne %s et %s ligne %d existe en base et la colonne groupe secondaire n'existe pas en base.", columnLogin, columnOffre, competeurLigne));
                                this.filterDataFormCsv(columnLogin, columnOffre, competeurLigne, importInexisted, importExisted);
                            }
                        }
                    }
                }
            }

            // Fermez le lecteur CSV
            csvReader.close();
        } catch (Exception e) {
            log.error("Une erreur s'est produite lors de la lecture du fichier CSV.");
            throw new ServiceException(ConstanteMessage.MESSAGE_ERREUR_GLOBAL);
            // Gérez les erreurs de traitement du fichier ici
        }

        importFileDtos = new ImportFileDto(importExisted, importInexisted);
        return importFileDtos;
    }

    private void filterDataFormCsv(String columnLogin, String columnOffre, String columnGrpSec, long competeurLigne,
                                   List<ImportMessageErreur> importInexisted,
                                   List<ImportApplicatifDto> importExisted) throws ServiceException {

        ActeurVue acteurVue = this.acteurVueService.getActeurVueByLogin(columnLogin);
        String dn =DnUtils.getDnCompletByCn(columnOffre);
        ParametrageLdap parametrageLdap = this.parametrageLdapRepository.findParametrageLdapByDnPrinc(DnUtils.getDnCompletByCn(columnOffre));
        if (parametrageLdap == null){
            log.error(String.format("Une erreur s'est produite lors de la lecture du fichier CSV. L'offre %s n'existe pas en base.", columnGrpSec));
            throw new ServiceException(ConstanteMessage.MESSAGE_ERREUR_GLOBAL);
        }
        Offre offreLdap = parametrageLdap.getOffre();
        GroupeApplicatif groupeApplicatifSec = this.groupeApplicatifService.getGroupeApplicatifByDn(DnUtils.getDnCompletByCn(columnGrpSec));
        if (offreLdap == null) {
            String message = String.format("L'offre LDAP de la colonne groupe principale, ligne %d n'existe pas en base.", competeurLigne);
            log.debug(message);
            importInexisted.add(new ImportMessageErreur(competeurLigne, "Groupe secondaire", columnOffre, message));

        } else if (groupeApplicatifSec == null || !this.isOffreParentGrpSec(offreLdap, groupeApplicatifSec, acteurVue.getLogin()) ) {
            String message = String.format("Le profil %s de l'utilisateur "+acteurVue.getLogin()+
                    " n'appartient pas à l'offre %s ou la colonne du groupe secondaire ligne %d n'existe pas en base.",
                    columnGrpSec, offreLdap.getCodeOffre(), competeurLigne);

            log.debug(message);
            importInexisted.add(new ImportMessageErreur(competeurLigne, "Groupe secondaire", columnOffre, message));

        } else {
            ImportApplicatifDto importApplicatifDto = new ImportApplicatifDto(acteurVue.getNom(), acteurVue.getPrenom(),
                    acteurVue.getLogin(), offreLdap.getLibelle(), parametrageLdap.getDnPrinc(), groupeApplicatifSec.getDn(), groupeApplicatifSec.getDn(),
                    acteurVue.getIdActeur(), offreLdap.getId(), groupeApplicatifSec.getId());

            importExisted.add(importApplicatifDto);
        }

    }

    private void filterDataFormCsv(String columnLogin, String columnOffre, long competeurLigne,
                                   List<ImportMessageErreur> importInexisted,
                                   List<ImportApplicatifDto> importExisted) {

        ActeurVue acteurVue = this.acteurVueService.getActeurVueByLogin(columnLogin);
        ParametrageLdap parametrageLdap = this.parametrageLdapRepository.findParametrageLdapByDnPrinc(DnUtils.getDnCompletByCn(columnOffre));
        Offre offreLdap = parametrageLdap.getOffre();

        if (offreLdap == null) {
            String message = String.format("L'offre LDAP de la colonne groupe principale ligne %d n'existe pas en base.", competeurLigne);
            log.debug(message);
            importInexisted.add(new ImportMessageErreur(competeurLigne, "Groupe secondaire", columnOffre, message));
        } else {
            ImportApplicatifDto importApplicatifDto = new ImportApplicatifDto(
                    acteurVue.getNom(), acteurVue.getPrenom(), acteurVue.getLogin(), offreLdap.getLibelle(),
                    parametrageLdap.getDnPrinc(), null, null, acteurVue.getIdActeur(), offreLdap.getId(), null
            );

            importExisted.add(importApplicatifDto);
        }


    }

    private boolean isOffreParentGrpSec(Offre offre, GroupeApplicatif grpAppSec, String login) {
        List<GroupeApplicatif> listProfilOffre = this.groupeApplicatifService.findByParametrageLdap_Offre_IdOrderByDnAsc(offre.getId());

        boolean isListProfilOffre = listProfilOffre.stream()
                .anyMatch(grp -> grp.getDn().equals(grpAppSec.getDn()));

        if (listProfilOffre.isEmpty() || !isListProfilOffre) {
            log.debug("Le profil " + grpAppSec.getDn() + " de l'utilisateur "+login+" n'appartient pas à l'offre " + offre.getCodeOffre());
            return false;
        }

        return true;
    }


    /**
     * Méthode pour vérifier si ActeurVue existe par login
     * @param columnLogin colonne login du fichier CSV
     * @return retourner un booléan
     */
    private boolean isActeurVueExists(String columnLogin) {
        return columnLogin != null && acteurVueService.existsActeurVueByLogin(columnLogin);
    }

    /**
     * Méthode pour vérifier si ParametrageLdap existe par DN principal
     * @param columnOffre DN principal
     * @return retourner un booléan
     */
    private boolean isParametrageLdapExists(String columnOffre) {
        return columnOffre != null && parametrageLdapRepository.existsParametrageLdapByDnPrinc(DnUtils.getDnCompletByCn(columnOffre));
    }

    /**
     * Méthode pour vérifier si GroupeApplicatif existe par DN
     * @param columnGrpSec DN
     * @return retourner un booléan
     */
    private boolean isGroupeApplicatifExists(String columnGrpSec) {
        return columnGrpSec != null && groupeApplicatifService.existsGroupeApplicatifByDn(DnUtils.getDnCompletByCn(columnGrpSec));
    }

    @Transactional(rollbackOn = {ServiceException.class})
    @Override
    public ImportFileDto confirmImportCSVApplicatif(ImportFileDto importAppList, String username) throws ServiceException {
        /* Verification des permissions (A vénir) */

        ActeurVue acteurVueUser = this.acteurVueService.getActeurVueByLogin(username);

        if (acteurVueUser == null){
            log.error(String.format("Une erreur s'est produite lors de la confirmation de l'import du fichier CSV. Le login %s n'existe pas dans la base de donnée", username));
            throw new ServiceException("Ce login n' existe pas. "+ ConstanteMessage.MESSAGE_ERREUR_GLOBAL);
        }

        ImportFileDto importFileDto = new ImportFileDto();

        List<ImportMessageErreur> importError = new ArrayList<>();
        List<ImportApplicatifDto> importExisted = new ArrayList<>();
        String message = "";
        String grpAppFind = "";
        List<String> dnProfilsActeur;

        for (ImportApplicatifDto importApp: importAppList.getImportExisted()){
            try {
                ActeurVue acteurVue =  acteurVueService.getActeurVueByIdActeurIfIdIsNotNull(importApp.getIdActeur());
                if (acteurVue != null){
                    ParametrageLdap parametrageLdap = this.parametrageLdapRepository.findParametrageLdapByDnPrinc(importApp.getDnGrpPrinc());
                    Offre offre = parametrageLdap != null ? parametrageLdap.getOffre(): null;
                    GroupeApplicatif groupeApplicatif = this.groupeApplicatifService.getGroupeAppSecByIdGrpAppAsecIfIdIsNotNull(importApp.getIdGroupApp());
                    if (offre != null && offre.getId() != null && offre.getId().equals(parametrageLdap.getOffre().getId())){
                        StatutDemande statutDemande = this.statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE);
                        boolean hasDroit = this.droitService.existsDroitByIdActeurAndIdOffre(acteurVue.getIdActeur(), offre.getId());

                        /* Création d'habilitation */
                        if (hasDroit){
                            log.info(String.format("L'acteur %s possède l'habilitation %s.", acteurVue.getLogin(), offre.getCodeOffre()));

                            if (groupeApplicatif != null){

                                Droit droit = this.droitService.getDroitByIdActeurAndIdOffre(acteurVue.getIdActeur(), offre.getId());
                                boolean hasProfil = droit.getDemande().getProfils().stream().anyMatch(groupe -> groupe.getId().equals(groupeApplicatif.getId()));

                                LienDemGrpApplicatif demGrpApplicatif = this.demGrpApplicatifRepository.findLienDemGrpApplicatifByIdGrpAndAndIdDemande(groupeApplicatif.getId(), droit.getDemande().getId());
                                if (hasProfil){
                                    message = String.format("L'acteur %s possède déjà le même profils pour cette offre %s.", acteurVue.getLogin(), offre.getCodeOffre());
                                    log.warn(message);

                                    importError.add(new ImportMessageErreur(message));
                                } else if (demGrpApplicatif != null) {
                                    message = String.format("L'acteur %s possède déjà le même profils pour cette demande d'offre %s.", acteurVue.getLogin(), offre.getCodeOffre());
                                    log.warn(message);

                                    importError.add(new ImportMessageErreur(message));
                                } else {
                                    dnProfilsActeur = new ArrayList<>(Collections.singleton(groupeApplicatif.getDn()));
                                    List<GroupeApplicatif> profils = droit.getDemande().getProfils();
                                    profils = (profils != null && !profils.isEmpty()) ? profils : new ArrayList<>();
                                    profils.add(groupeApplicatif);
                                    droit.getDemande().setProfils(profils);
                                    this.droitRepository.save(droit);

                                    LienDemGrpApplicatif lienDemGrpApplicatif = new LienDemGrpApplicatif();
                                    lienDemGrpApplicatif.setIdGrp(groupeApplicatif.getId());
                                    lienDemGrpApplicatif.setIdDemande(droit.getDemande().getId());
                                    demGrpApplicatifRepository.save(lienDemGrpApplicatif);

                                    message = String.format("L'acteur %s possède déjà le offre %s. Nous avons ajouté le profil %s.",
                                            acteurVue.getLogin(), offre.getCodeOffre(), DnUtils.extraireValeurAttributFromDn(groupeApplicatif.getDn(), "cn"));
                                    log.info(message);

                                    importExisted.add(new ImportApplicatifDto(acteurVue.getNom(), acteurVue.getPrenom(),
                                            acteurVue.getLogin(), offre.getLibelle(), parametrageLdap.getDnPrinc(),
                                            null, groupeApplicatif.getDn(),
                                            null, null, null));

                                    // On propage la creation du droit dans le LDAP
                                    this.propagationLDAPService.propagerSauvegardeDroitActeur(acteurVue, dnProfilsActeur);
                                }
                            } else {
                                message = String.format("Impossible d'effectuer la demande. L'acteur %s possède l'habilitation %s.", acteurVue.getLogin(), offre.getCodeOffre());
                                log.warn(message);
                                importError.add(new ImportMessageErreur(message));
                            }

                        } else {
                            log.warn(String.format("L'acteur %s ne possède pas l'habilitation %s.", acteurVue.getLogin(), offre.getCodeOffre()));

                            boolean hasDemandeDroits = this.demandeDroitService.existeDemandeDroit(
                                    acteurVue.getIdActeur(), offre.getId(), ConstanteWorkflow.LIST_STATUT_DEMANDE
                            );

                            if (hasDemandeDroits){
                                message = String.format("L'acteur %s a déjà une demande d'habilitation %s en cours.", acteurVue.getLogin(), offre.getCodeOffre());
                                log.warn(message);
                                importError.add(new ImportMessageErreur(message));
                            } else {

                                // Creation de la demande de droit
                                DemandeDroit demandeDroit = new  DemandeDroit();
                                demandeDroit.setTopDem(ConstanteWorkflow.DEM_OFFRE_CREATION_STRING);
                                demandeDroit.setOffre(offre);
                                demandeDroit.setActeurBenef(acteurVue);
                                demandeDroit.setStatut(statutDemande);
                                demandeDroit.setIdentifiant(acteurVue.getLogin());

                                if (groupeApplicatif != null){
                                    List<GroupeApplicatif> profils = new ArrayList<>();
                                    profils.add(groupeApplicatif);
                                    demandeDroit.setProfils(profils);
                                    demandeDroit = this.demandeDroitRepository.save(demandeDroit);
                                    dnProfilsActeur = new ArrayList<>(Arrays.asList(parametrageLdap.getDnPrinc(), groupeApplicatif.getDn()));

                                } else {
                                    demandeDroit = this.demandeDroitRepository.save(demandeDroit);
                                    dnProfilsActeur = new ArrayList<>(Collections.singleton(parametrageLdap.getDnPrinc()));
                                }

                                EtapeDemDroit etapeDemDroit = this.demandeDroitService.saveEtapeDemandeDroit(
                                        "Import applicatif en masse",
                                        acteurVue.getLogin(),
                                        demandeDroit,
                                        ConstanteWorkflow.DEM_OFFRE_CREATION_STRING
                                );

                                boolean demandeIsValid = false;
                                if (etapeDemDroit != null){
                                    demandeIsValid = this.droitService.saveDroitImport(demandeDroit, acteurVue.getIdActeur(), offre.getId());
                                }

                                if (demandeIsValid){

                                    log.info(String.format("L'import de l'offre %s pour l'acteur %s est terminé avec succès.", offre.getCodeOffre(), acteurVue.getLogin()));

                                    TrtImportGrpApplicatif trtImportGrpApplicatif = new TrtImportGrpApplicatif();
                                    trtImportGrpApplicatif.setDateExe(Instant.now());
                                    trtImportGrpApplicatif.setIdOffre(offre.getId());
                                    trtImportGrpApplicatif.setIntervenant(username);
                                    trtImportGrpApplicatif.setVersion(0);
                                    trtImportGrpApplicatifRepository.save(trtImportGrpApplicatif);

                                    ImportActeurGroupeApplicatif importActeurGroupeApplicatif = new ImportActeurGroupeApplicatif();
                                    importActeurGroupeApplicatif.setIdActeur(acteurVue.getIdActeur());
                                    importActeurGroupeApplicatif.setTraitement(trtImportGrpApplicatif);
                                    importActeurGroupeApplicatif.setVersion(0);
                                    this.importActeurGrpeAppRepository.save(importActeurGroupeApplicatif);

                                    log.info("L'historisation de l'import terminé avec succès.");

                                    grpAppFind = groupeApplicatif != null ? DnUtils.extraireValeurAttributFromDn(groupeApplicatif.getDn(), "cn"): null;
                                    message = String.format("L'historique de l'import de l'offre %s pour l'acteur %s est terminé avec succès.", importApp.getOffre(), acteurVue.getLogin());
                                    log.info(message);
                                    importExisted.add(new ImportApplicatifDto(acteurVue.getNom(), acteurVue.getPrenom(),
                                            acteurVue.getLogin(), offre.getLibelle(), parametrageLdap.getDnPrinc(),
                                            null, grpAppFind,
                                            null, null, null));

                                    // On propage la creation du droit dans le LDAP
                                    this.propagationLDAPService.propagerSauvegardeDroitActeur(acteurVue, dnProfilsActeur);

                                    log.info("La propagation LDAP de l'import terminé avec succès.");
                                } else {
                                    message = String.format("L'import de l'offre %s pour l'acteur %s a été interrompu.", offre.getCodeOffre(), acteurVue.getLogin());
                                    log.error(message);
                                    throw new ServiceException(message+" "+ConstanteMessage.MESSAGE_ERREUR_GLOBAL);
                                }
                            }
                        }
                    } else {
                        message = String.format("Impossible de créer la demande de droit. L'offre %s. n' existe pas.", importApp.getOffre());
                        log.warn(message);
                        importError.add(new ImportMessageErreur(message));
                    }

                } else {
                    message = String.format("Impossible de créer la demande de droit. L'acteur %s n' existe pas.", importApp.getLogin());
                    log.warn(message);
                    importError.add(new ImportMessageErreur(message));
                }

            } catch (NullPointerException nullPointerException){
                log.error("Nous avons rencontrer un problème lors de la création de la demande de droit");
                log.error("Id Acteur est : " + importApp.getIdActeur());
                log.error("Id Offre est : " + importApp.getIdOffre());
                log.error("Id Groupe applicatif secondaire est : " + importApp.getIdGroupApp());
                throw new ServiceException(String.format("L'import de l'offre %s pour l'acteur %s a été interrompu. "+ConstanteMessage.MESSAGE_ERREUR_GLOBAL, importApp.getOffre(), importApp.getLogin()));
            }
        }
        importFileDto.setImportExisted(importExisted);
        importFileDto.setImportInexisted(importError);

        return importFileDto;
    }

    @Override
    public Page<HistoriqueImportDto> findAllHistoriqueImportAppByFilter(FiltreHistoriqueDemandesDto filtre, Pageable pageable) {
        return importActeurGrpeAppRepository.findAllImportAppByFilter(filtre.getNom(), filtre.getPrenom(), filtre.getLogin(), filtre.getOffre(), filtre.getIntervenant(), pageable);
    }
    @Override
    public Page<HistoriqueImportDto> findAllHistoriqueImportApp(Pageable pageable) {
        return importActeurGrpeAppRepository.findAllImportApp(pageable);
    }

    @Override
    public byte[] getExportHistoriqueImportAppService() {
        return ExportFileCsvUtils.exportCsvFile(this.importActeurGrpeAppRepository.findAllImportApp());
    }
}