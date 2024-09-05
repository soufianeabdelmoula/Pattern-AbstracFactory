package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.exception.rest.handler.ActeurException;
import fr.vdm.referentiel.refadmin.mapper.*;
import fr.vdm.referentiel.refadmin.model.*;
import fr.vdm.referentiel.refadmin.model.ad.CompteApplicatifAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteRessourceAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteServiceAD;
import fr.vdm.referentiel.refadmin.repository.*;
import fr.vdm.referentiel.refadmin.repository.ad.CompteApplicatifADRepository;
import fr.vdm.referentiel.refadmin.repository.ad.CompteRessourceADRepository;
import fr.vdm.referentiel.refadmin.repository.ad.CompteServiceADRepository;
import fr.vdm.referentiel.refadmin.service.*;
import fr.vdm.referentiel.refadmin.utils.*;
import lombok.extern.log4j.Log4j2;
import fr.vdm.referentiel.refadmin.mapper.ActeurVueMapper;
import fr.vdm.referentiel.refadmin.mapper.DroitMapper;
import fr.vdm.referentiel.refadmin.utils.ConstanteActeur;
import fr.vdm.referentiel.refadmin.utils.EnumTypeDonneeReference;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ActeurServiceImpl implements ActeurService {
    @Autowired
    private AgentInfoRepository agentInfoRepository;
    @Autowired
    private StatutDemandeRepository statutDemandeRepository;
    @Autowired
    private TacheHabilitationRepository tacheHabilitationRepository;
    @Autowired
    private DemandeDroitRepository demandeDroitRepository;
    @Autowired
    private DemandeActeurRepository demandeActeurRepository;
    private static final Logger K_LOGGER = LoggerFactory.getLogger(ActeurServiceImpl.class);

    @Autowired
    ActeurVueRepository acteurVueRepository;

    private static final ActeurVueMapper acteurVueMapper = ActeurVueMapper.INSTANCE;

    private static final AgentMapper agentMapper = AgentMapper.INSTANCE;

    @Autowired
    DroitRepository droitRepository;

    @Autowired
    ActeurADService acteurADService;

    @Autowired
    CompteApplicatifRepository compteApplicatifRepository;

    @Autowired
    PasswordService passwordService;
    private final CelluleService celluleService;
    private final DroitService droitService;
    @Autowired
    LienActeurGroupeFonctionnelRepository lienActeurGroupeFonctionnelRepository;
    @Autowired
    ResponsableGrpFoncRepository responsableGrpFoncRepository;
    @Autowired
    GroupeFonctionnelRepository groupeFonctionnelRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    PjService pjService;
    @Autowired
    private CompteServiceRepository compteServiceRepository;
    @Autowired
    private CompteServiceADRepository compteServiceADRepository;
    @Autowired
    private TypeMailRepository typeMailRepository;
    @Autowired
    private CompteRessourceRepository compteRessourceRepository;
    @Autowired
    private CompteRessourceADRepository compteRessourceADRepository;
    @Autowired
    private CompteApplicatifADRepository compteApplicatifADRepository;

    @Autowired
    private LdapTemplate ldapTemplate;
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private ExterneRepository externeRepository;
    @Autowired
    private ActeurRhService acteurRhService;
    @Autowired
    private AgentService agentService;

    private final ActeurRepository acteurRepository;
    @Autowired
    private TacheHabilitationService tacheHabilitationService;

    @Autowired
    private ChangementAffectationService changementAffectationService;

    @Autowired
    private SendEmailService sendEmailService;


    public ActeurServiceImpl(CelluleService celluleService, DroitService droitService, ActeurRepository acteurRepository) {
        this.celluleService = celluleService;
        this.droitService = droitService;
        this.acteurRepository = acteurRepository;
    }

    public Page<ActeurVueDto> getAllActeurVue(String input, Pageable page) {
        Page<ActeurVue> acteurVues = acteurVueRepository.findAllByInput(input, page);
        return acteurVues.map(acteurVueMapper::acteurVueToActeurVueDto);
    }

    public ActeurVueDto getActeurVueDtoByLogin(String login){

        return acteurVueMapper.acteurVueToActeurVueDto(this.acteurVueRepository.findActeurVueByLogin(login));
    }

    @Override
    public List<ActeurVueDto> getAllActeurVue() {
        List<ActeurVue> acteurVueList = acteurVueRepository.findAll();
        return acteurVueMapper.listActeurVueToActeurVueDto(acteurVueList);
    }

    public ActeurVue getActeurVueByLogin(String login){

        return this.acteurVueRepository.findActeurVueByLogin(login);
    }

    public ActeurVueDto getActeurVueById(Long idActeur) {
        Optional<ActeurVue> oActeur = this.acteurVueRepository.findById(idActeur);
        ActeurVue acteur = oActeur.orElse(null);
        return ActeurVueMapper.INSTANCE.acteurVueToActeurVueDto(acteur);
    }

    public List<DroitDto> getDroits(Long idActeur) {
        return DroitMapper.INSTANCE.droitsToDroitDtos(droitRepository.findByActeurBenef_IdActeur(idActeur));
    }

    public ActeurVue getActeurVueByIdtaAndIdtn(String idta, String idtn) {
        return acteurVueRepository.findByIdtaAndIdtn(idta, StringUtils.leftPad(idtn, 4, "0"));
    }

    @Override
    public Page<ActeurVueDto> getAllActeursByFilters(FiltreHistoriqueDemandesDto filter,  Pageable pageable) {

        // Filtrage sur le champ "Affectation"
        List<CelluleDto> celluleDtoList = null;
        if (filter.getCodeAffectation() != null && !filter.getCodeAffectation().isEmpty()) {
            celluleDtoList = celluleService.findAllfilsActiveInclus(filter.getCodeAffectation());
        }

        // Filtrage sur le champ "Offre"
        List<DroitDto> droitDtos = null;
        if (filter.getIdOffre() != null) {
            droitDtos = this.droitService.getAllDroitByidOffre(filter.getIdOffre());
        }

        Specification<ActeurVue> spec = FiltersSpecifications.filtersActeurs(filter, celluleDtoList, droitDtos);

        return acteurVueRepository.findAll(spec, pageable)
                .map(ActeurVueMapper.INSTANCE::acteurVueToActeurVueDto);
    }

    /**
     * Methode permettant de convertir une demande acteur en un agent
     *
     * @param demande     la demande acteur e convertir.
     * @param mailExterne permet de savoir si le mail est externe ou non.
     * @param messagerie  La messagerie de l'agent si il a une adresse Email interne
     * @return Agent l'agent obtenu e partir de la demandeDto.
     */
    private Agent convertAgent(final DemandeActeur demande,
                               final boolean mailExterne, final ExtensionMessagerieDto messagerie) {
        // Cas ou l'agent existe deja on le cherche alors dans le referentiel.
        Agent agent;
        if (demande.getActeurBenef() != null) {
            agent = this.agentRepository.findByIdAgent(demande.getActeurBenef().getIdActeur());
        } else {
            agent = new Agent();
        }

        agent.setColl(demande.getColl() != null ? demande.getColl() : "MAR");
        agent.setSsColl(demande.getSsColl() != null ? demande.getSsColl() : "   ");
        agent.setCellule(demande.getCellule());
        agent.setCodeEquFonc(demande.getCodeEquFonc());

        agent.setCelluleDet(demande.getCelluledet());

        agent.setCollTerrain(demande.getCollTerrain());
        agent.setSscollTerrain(demande.getSsCollTerrain());
        agent.setCelluleTerrain(demande.getCelluleTerrain());
        agent.setCodeEquFoncTerrain(demande.getCodeEquFoncTerrain());

        if (agent.getTsAffect() == null) {
            agent.setTsAffect(new Date().toInstant());
        }
        agent.setFonction(demande.getFonction());
        agent.setIdta(demande.getIdta());
        agent.setIdtn(demande.getIdtn());
        agent.setLogin(demande.getLogin());
        agent.setNom(demande.getNom());
        agent.setNomMarital(demande.getNomMarital());
        agent.setNomUsuel(demande.getNomUsuel());
        agent.setPrenom(demande.getPrenom());
        agent.setPrenomUsuel(demande.getPrenomUsuel());
        agent.setAReaffecter(demande.getTopReaffecter());
        // Rajout de l'email pour l'agent

        if (StringUtil.isNotBlank(demande.getEmail())) {
            Email email;
            // Si c'est une demande de creation
            if (!demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION))) {

                email = emailService.saveMail(agent.getIdActeur(),
                        demande.getEmail(), mailExterne);
                agent.setEmail(email);
            }
        } else {
            agent.setEmail(null);
        }

        return agent;
    }

    /**
     * Methode permettant de convertir une demande en un externe
     *
     * @param demande     la demande e convertir
     * @param mailExterne Savoir si le mail est externe ou non
     * @return l'externe obtenu.
     */
    private Externe convertExterne(final DemandeActeur demande,
                                   final boolean mailExterne) {
        // Cas où l'externe existe deja on le cherche alors dans le referentiel.
        Externe externe;
        if (demande.getActeurBenef() != null) {
            externe = externeRepository.findById(demande.getActeurBenef().getIdActeur()).get();
        } else {
            externe = new Externe();
        }
        externe.setCellule(demande.getCellule());
        externe.setColl(demande.getColl());
        externe.setSsColl(demande.getSsColl());
        if (externe.getTsAffect() == null) {
            externe.setTsAffect(new Date().toInstant());
        }
        externe.setTsSortiePrev(demande.getTsSortiePrev());
        externe.setLogin(demande.getLogin());
        externe.setNom(demande.getNom());
        externe.setPrenom(demande.getPrenom());
        externe.setCodeEquFonc(demande.getCodeEquFonc());
        externe.setTopReaffecter(demande.getTopReaffecter());
        // Rajout de l'email pour l'externe
        if (StringUtil.isNotBlank(demande.getEmail())) {
            Email email;
            // Si c'est une demande de creation
            if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION))) {
                email = null;
            } else {
                email = this.emailService.saveMail(externe.getIdActeur(),
                        demande.getEmail(), mailExterne);
            }
            externe.setEmail(email);
        } else {
            externe.setEmail(null);
        }

        return externe;
    }


    @Override
    public boolean save(final DemandeActeurDto demandeDto,
                        final ChampTechniqueDto champTechnique,
                        final ExtensionMessagerieDto messagerie, final String oldEmail,
                        final String oldLogin, final String oldAffectation, boolean isImport) throws InvalidNameException, ServiceException {
        /*
        // On filtre la valeur qui servira dans la construction du dn LDAP de
        // l'acteur
        if (StringUtil.isNotBlank(demandeDto.getLogin())) {
            demandeDto.setLogin(LDAPUtil.escapeDn(demandeDto.getLogin()));
        }
        *§
         */
        // Ensuite on continue le traitement normal
        Cellule affectationTerrain = null;
        Cellule affectationOfficielle = null;
        Long id = null;
        String nouveauMail = "";
        DemandeActeur demande = demandeActeurRepository.findById(demandeDto.getId()).get();

        if (demande.getTypeActeur().equals(ConstanteActeur.TYPE_AGENT)) {


            Agent agent = convertAgent(demande, demandeDto.isMailExterne(),
                    messagerie);
            if (agent.getEmail() != null) {
                nouveauMail = agent.getEmail().getEmail();
            }
            if (isImport) {
                if (StringUtil.isNotBlank(demande.getEmail())) {
                    Email email;
                    //Cas de la préembauche le Mail est set avec la valeur de la demande
                    email = this.emailService.saveMail(agent.getIdActeur(),
                            demande.getEmail(), false);
                    agent.setEmail(email);
                    nouveauMail = agent.getEmail().getEmail();
                } else {
                    agent.setEmail(null);
                }
                demande.setEmail("");
            }

            // On charge l'affectation terrain de l'agent si elle existe
            if (StringUtil.isNotBlank(agent.getCelluleTerrain())) {

                affectationTerrain = celluleService.findCelluleByCle(
                        agent.getCollTerrain(), agent.getSscollTerrain(),
                        agent.getCelluleTerrain());
            }
            // On charge l'affectation Officielle de l'agent si elle existe
            if (StringUtil.isNotBlank(agent.getCellule())) {
                affectationOfficielle = celluleService.findCelluleByCle(
                        agent.getColl(), agent.getSsColl(),
                        agent.getCellule());
            }
            // On sauvegarde ou met e jour l'agent.
            agent = this.agentRepository.save(agent);
            id = agent.getIdActeur();

        }
        if (demande.getTypeActeur().equals(ConstanteActeur.TYPE_EXTERNE)) {
            Externe externe = convertExterne(demande,
                    demandeDto.isMailExterne());
            // On charge l'affectation Officielle de l'agent si elle existe
            if (externe.getEmail() != null) {
                nouveauMail = externe.getEmail().getEmail();
            }
            if (StringUtil.isNotBlank(externe.getCellule())) {
                affectationOfficielle = celluleService.findCelluleByCle(
                        externe.getColl(),
                        externe.getSsColl(), externe.getCellule());
            }
            // On sauvegarde ou met e jour l'externe.
            externe = this.externeRepository.save(externe);
            id = externe.getIdActeur();

        }

        // On traite les eventuels suppression et modifications de droits suite
        // un eventuel changement d'affectation dans le cas d'une demande de
        // modification
        String cle;
        if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_MODIFICATION))) {
            if (StringUtil.isNotBlank(demande.getCelluleTerrain())) {
                cle = CelluleUtils.getCle(demande.getCollTerrain(),
                        demande.getSsCollTerrain(),
                        demande.getCelluleTerrain());
            } else {
                cle = CelluleUtils.getCle(demande.getColl(),
                        demande.getSsColl(), demande.getCellule());
            }
            if (demande.getTypeActeur().equals(ConstanteActeur.TYPE_EXTERNE)
                    || demande.getTypeActeur().equals(ConstanteActeur.TYPE_AGENT)) {
                changementAffectationService
                        .traiterChangementAffectation(
                                oldAffectation, cle, demande.getActeurBenef().getIdActeur());
            }
        }

        // On Change le login dans le LDAP en cas d'une demande de modification
        // si le login change
        if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_MODIFICATION))
                && !oldLogin.trim().equals(demande.getLogin().trim())) {
            // on renomme dans le LDAP
            acteurADService.renommerActeur(oldLogin.trim(),
                    demande.getLogin().trim(), demande.getTypeActeur());
        }
        // On sauve les infos dans le LDAP
        acteurADService.save(demande, affectationTerrain,
                affectationOfficielle, champTechnique, nouveauMail);


        // On enregistre les donnees de l'onglet Autre
        acteurADService.saveChampsTechniquesAD(demande.getLogin(),
                champTechnique);

        // On initialise le mot de passe si c'est une creation
        if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION))) {
            try {
                log.info("Enregistrement du password pour la creation de l'utilisateur"
                        + demande.getLogin());

                // 05/03/2014 - BGE - EVT-8295 : Modification de l'appel e la
                // methode de generation de password
                String password = passwordService.genererPassword(demande.getTypeActeur());
                acteurADService
                        .updatePassword(demande.getLogin(), password, demande.getTypeActeur());


                // 22/10/2018 - ZZU : modification de la r�gle en rajoutant un boolean bascule entre le mode
                // imports en masse et mode normal
                // Dans le premier cas on log les mots de passe g�n�r�s dans le log sans envoyer
                // de mail
                if (isImport) {


                    AgentInfo agentInfo = new AgentInfo();
                    agentInfo.setMatricule(demandeDto.getMatricule());
                    agentInfo.setNom(demande.getNom());
                    agentInfo.setPrenom(demande.getPrenom());
                    agentInfo.setLogin(demande.getLogin());

                    agentInfo.setEmail(demandeDto.getEmail());

                    String celluleName;
                    Cellule cellule = null;
                    if (demandeDto.getAffectationLabel() != null)
                        cellule = this.celluleService.select(demandeDto.getAffectationLabel());
                    String cellColl;
                    String cellSsColl;
                    String cellCode;
                    if (cellule != null) {
                        celluleName = cellule.getLibLongGrCellule();
                        cellColl = cellule.getColl();
                        cellSsColl = cellule.getSsColl();
                        cellCode = cellule.getCode();
                        //cellAdd = cellule.getAdressePrincipale() + " " + cellule.getCodePostale() + " " +cellule.getCommune();

                        agentInfo.setAdressePrincipale(cellule.getAdressPrincipale());
                        agentInfo.setAdresseSecondaire(cellule.getAdresseSecondaire());
                        agentInfo.setCodePostale(cellule.getCodePostal());
                        agentInfo.setCommune(cellule.getCommune());

                        agentInfo.setAffectation(celluleName);
                        agentInfo.setColl(cellColl);
                        agentInfo.setSsColl(cellSsColl);
                        agentInfo.setCellule(cellCode);

                    } else {
                        throw new ServiceException("Aucune cellule renseign�e");
                    }

                    agentInfoRepository.save(agentInfo);

                } else {

                    sendEmailService.sendPasswordActeur(demande.getLogin(), demande.getId(), password,
                            demande.getNomComplet(), demande.getIdta(), demande.getIdtn(), null);

                }
            } catch (ServiceException e) {
                log.error("Erreur au niveau de l'enregistrement du password pour la creation de l'utilisateur"
                        + demande.getLogin());

                log.error(e);
                return false;
            }

            //activation du compte
            try {
                log.info("Activation du compte de l'utilisateur"
                        + demande.getLogin());
                this.acteurADService.activer(demande.getLogin(), demande.getTypeActeur());
                this.acteurADService.motDePasseRequis(demande.getLogin());
            } catch (Exception e) {
                log.error("Erreur au niveau de l'activation du compte de l'utilisateur"
                        + demande.getLogin());
                log.error(e);
            }


        }
        // on enregistre les droits de messagerie, d'internet et d'agenda.
        if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION))) {
            // on ajoute les droits pour la messagerie, l'agenda et
            // internet.
            if (demande.getTopInternet()) {
                //this.droitService.save(id, ConstanteWorkflow.CODEINTERNET);
                // TODO - Bouquet d'offre de Service
            }

        }

        //Suppression du numero de telephone à la fin de la creation de l'acteur et sauvegarde de la demande.
        demande.setTelExterne(null);
        if (demande.getTypeDemande().equals(String.valueOf(ConstanteWorkflow.DEM_ACTEUR_CREATION))) {
            demande.setEmail(null);
        }
        this.demandeActeurRepository.save(demande);
        return true;
    }

    @Override
    public AgentDto selectAgentRHByMatricule(String idTa, String idTn, UtilisateurDto utilisateurDto, boolean isImport) throws ActeurException {

        List<AgentRH> agentRHList = acteurRhService.getAgentRhByMatricule(idTa + idTn);

        // On vérifie si l'agent existe
        if (ListsUtils.isNull(agentRHList)) {
            if (isImport) {
                log.warn(String.format("Attention le matricule %s n'existe pas.", idTa + idTn));
                return null;
            }
            throw new ActeurException(ActeurException.MATRICULE_RH_INTROUVABLE);
        }

        // On tente de charger les donnees de l'agent dans le referentiel
        List<AgentDto> agentList = agentService.getAgentByMatricule(idTa + idTn);

        // On vérifie si un agent existe dans le referentiel
        if (!ListsUtils.isEmptyOrNull(agentList)) {
            if (isImport) {
                log.warn(String.format("Attention le matricule %s existe deja dans le referentiel.", idTa + idTn));
                return null;
            }
            throw new ActeurException(ActeurException.MATRICULE_REFERENTIEL_EXISTE_DEJA);
        }
        // On vérifie si l'utilisateur courant e le droit d'effectuer une
        // demande de creation pour le matricule saisi.

        AgentRH agent = agentRHList.stream().findFirst().get();

        String cle = CelluleUtils.getCle(agent.getColl(), agent.getSscoll(), agent.getCellule());

        if (!isImport) {
            if (utilisateurDto.getActeurValidation() == null
                    || utilisateurDto.getActeurValidation()
                    .getValidateurHierarchiqueFinal() == null
                    || !utilisateurDto.getActeurValidation()
                    .getValidateurHierarchiqueFinal().contains(cle)) {
                throw new ActeurException(
                        ActeurException.DROITS_INSUFFISANTS_CREATION_AGENT);
            }
        }
        // Une fois toutes les verifications effectuees, on construit la reponse
        AgentDto agentDto = agentMapper.agentRHToAgentDto(agent);
        agentDto.setAffectationOfficielle(CelluleUtils.filtrerHierarchie(celluleService.findPeres(agent.getColl(), agent.getSscoll(), agent.getCellule())));

        return agentDto;
    }
    @Override
    public Acteur getActeurByLogin(String login) {
        return this.acteurRepository.findActeurByLogin(login);
    }


    @Override
    public Page<ActeurVueDto> getActeursTechniquesByFilter(String typoCompte, String nom, String codeCellule, Long idType, Long idOffre, String login, String codeMega, Long idDemandeur, Pageable page) {

        String typeActeur = typoCompte.equals(EnumTypeDonneeReference.TYPE_COMPTE_SERVICE.code) ? "S" : typoCompte.equals(EnumTypeDonneeReference.TYPE_COMPTE_RESSOURCE.code) ? "R" : "C";

        Page<ActeurVue> acteurVues = this.acteurVueRepository.findByFilter(typeActeur, nom, codeCellule, idType, idOffre, login, codeMega, idDemandeur, page);
        Page<ActeurVueDto> dtos = acteurVues.map(acteurVueMapper::acteurVueToActeurVueDto);

        dtos.getContent().forEach(dto -> dto.setActive(this.acteurADService.isActive(dto.getLogin(), dto.getTypeActeur())));
        return dtos;
    }


    @Override
    public ActeurVueDto getDemandeur(Long idActeur) throws ServiceException {
        CompteApplicatif compteApplicatif = compteApplicatifRepository.findByIdActeur(idActeur);

        if (compteApplicatif == null)
            throw new ServiceException("Le compte n'a pas été trouvé en base de données. Impossible de récupérer le demandeur.");

        if (compteApplicatif.getIdDemandeur() == null) return null;
        return ActeurVueMapper.INSTANCE.acteurVueToActeurVueDto(this.acteurVueRepository.findById(compteApplicatif.getIdDemandeur()).orElse(null));
    }

    @Override
    public void saveCompteTechnique(CreationCompteDto compte) throws ServiceException {

        if (compte.getIdActeur() == null && this.acteurVueRepository.findActeurVueByLogin(compte.getLogin()) != null)
            throw new ServiceException("Ce login existe déjà. Aucune action effectuée.");

        switch (compte.getTypologieCompte()) {
            case (ConstanteActeur.TYPE_SERVICE):

                //Gestion de la sauvegarde en BDD
                CompteService compteService;
                //S'il n'y a pas d'id acteur, le compte n'existe pas encore en BDD
                if (compte.getIdActeur() == null) {
                    compteService = ActeurMapper.INSTANCE.creationCompteDtoToCompteService(compte);
                } else {
                    compteService = compteServiceRepository.findByIdActeur(compte.getIdActeur());
                    ActeurMapper.INSTANCE.update(compteService, ActeurMapper.INSTANCE.creationCompteDtoToCompteService(compte));
                }

                if (compteService.getEmail() != null && !StringUtils.isEmpty(compte.getEmail())) {
                    //Par défaut, on met une adresse mail de type interne.
                    TypeMail typeMail = typeMailRepository.findByCode(ConstanteActeur.CODE_TYPE_MAIL_INTERNE);
                    if (typeMail == null)
                        throw new ServiceException("Type du mail non reconnu. Aucune action effectuée.");
                    compteService.getEmail().setTypeMail(typeMail);
                } else {
                    compteService.setEmail(null);
                }
                K_LOGGER.info(String.format("Sauvegarde en base de données du compte de service %s", compte.getLogin()));
                compteServiceRepository.save(compteService);

                //Gestion de la sauvegarde dans l'annuaire
                try {
                    Name newDn = new LdapName(String.format("CN=%s,OU=Services", compte.getCn()));
                    //S'il n'y a pas d'id acteur, le compte n'existe pas encore
                    if (compte.getIdActeur() != null) {
                        CompteServiceAD cs = this.compteServiceADRepository.findById(new LdapName(compte.getDn())).orElse(null);
                        if (cs == null)
                            throw new ServiceException("Le compte de service n'a pas été trouvé dans l'annuaire. Aucune action effectuée.");

                        Name oldDn = cs.getDn();

                        //Pour ne pas perdre l'id de l'entrée dans l'annuaire, il faut obligatoirement renommer l'entité avant de sauvegarder
                        //On ne le fait que si les dns sont effectivement différents entre les deux versions
                        if (!oldDn.equals(newDn)) {
                            ldapTemplate.rename(oldDn, newDn);
                        }
                    }
                    CompteServiceAD compteServiceAD;
                    if (compte.getIdActeur() == null) {
                        compteServiceAD = new CompteServiceAD(compte);
                    } else {
                        compteServiceAD = compteServiceADRepository.findById(newDn).orElse(null);

                        if (compteServiceAD == null) throw new ServiceException();
                        ActeurADMapper.INSTANCE.update(compteServiceAD, new CompteServiceAD(compte));
                        compteServiceAD.setDn(newDn);
                    }
                    K_LOGGER.info(String.format("Sauvegarde dans l'annuaire du compte de service %s", compte.getLogin()));
                    compteServiceADRepository.save(compteServiceAD);
                    if (compte.getIdActeur() == null) {

                        this.acteurADService.updatePassword(compteServiceAD.getIdentifiant(),
                                this.passwordService.genererPassword(ConstanteActeur.TYPE_SERVICE), ConstanteActeur.TYPE_SERVICE);
                        this.acteurADService.activer(compteServiceAD.getIdentifiant(), ConstanteActeur.TYPE_SERVICE);

                    }
                    return;
                } catch (InvalidNameException | ServiceException e) {
                    throw new ServiceException(String.format("Une erreur est survenue lors de la sauvegarde dans l'annuaire. %s", e.getMessage()));
                }
            case (ConstanteActeur.TYPE_RESSOURCE):
                //Gestion de la sauvegarde en BDD
                CompteRessource compteRessource;
                //S'il n'y a pas d'id acteur, le compte n'existe pas encore en BDD
                if (compte.getIdActeur() == null) {
                    compteRessource = ActeurMapper.INSTANCE.creationCompteDtoToCompteRessource(compte);
                } else {
                    compteRessource = compteRessourceRepository.findByIdActeur(compte.getIdActeur());
                    ActeurMapper.INSTANCE.update(compteRessource, ActeurMapper.INSTANCE.creationCompteDtoToCompteRessource(compte));
                }

                if (compteRessource.getEmail() != null && !StringUtils.isEmpty(compte.getEmail())) {
                    //Par défaut, on met une adresse mail de type interne.
                    TypeMail typeMail = typeMailRepository.findByCode(ConstanteActeur.CODE_TYPE_MAIL_INTERNE);
                    if (typeMail == null)
                        throw new ServiceException("Type du mail non reconnu. Aucune action effectuée.");
                    compteRessource.getEmail().setTypeMail(typeMail);
                } else {
                    compteRessource.setEmail(null);
                }
                K_LOGGER.info(String.format("Sauvegarde en base de données du compte de ressource %s", compte.getLogin()));
                compteRessourceRepository.save(compteRessource);

                //Gestion de la sauvegarde dans l'annuaire
                try {
                    Name newDn = new LdapName(String.format("CN=%s,OU=Ressources", compte.getCn()));
                    //S'il n'y a pas d'id acteur, le compte n'existe pas encore
                    if (compte.getIdActeur() != null) {
                        CompteRessourceAD cr = this.compteRessourceADRepository.findById(new LdapName(compte.getDn())).orElse(null);
                        if (cr == null)
                            throw new ServiceException("Le compte de ressource n'a pas été trouvé dans l'annuaire. Aucune action effectuée.");

                        Name oldDn = cr.getDn();

                        //Pour ne pas perdre l'id de l'entrée dans l'annuaire, il faut obligatoirement renommer l'entité avant de sauvegarder
                        //On ne le fait que si les dns sont effectivement différents entre les deux versions
                        if (!oldDn.equals(newDn)) {
                            ldapTemplate.rename(oldDn, newDn);
                        }
                    }
                    CompteRessourceAD compteRessourceAD;
                    if (compte.getIdActeur() == null) {
                        compteRessourceAD = new CompteRessourceAD(compte);
                    } else {
                        compteRessourceAD = compteRessourceADRepository.findById(newDn).orElse(null);

                        if (compteRessourceAD == null) throw new ServiceException();
                        ActeurADMapper.INSTANCE.update(compteRessourceAD, new CompteRessourceAD(compte));
                        compteRessourceAD.setDn(newDn);
                    }
                    K_LOGGER.info(String.format("Sauvegarde dans l'annuaire du compte de ressource %s", compte.getLogin()));
                    compteRessourceADRepository.save(compteRessourceAD);
                    if (compte.getIdActeur() == null) {

                        this.acteurADService.updatePassword(compteRessourceAD.getIdentifiant(),
                                this.passwordService.genererPassword(ConstanteActeur.TYPE_SERVICE), ConstanteActeur.TYPE_RESSOURCE);
                        this.acteurADService.activer(compteRessourceAD.getIdentifiant(), ConstanteActeur.TYPE_RESSOURCE);

                    }
                    return;
                } catch (InvalidNameException | ServiceException e) {
                    throw new ServiceException(String.format("Une erreur est survenue lors de la sauvegarde dans l'annuaire. %s", e.getMessage()));
                }
            case (ConstanteActeur.TYPE_APPLICATIF):
                //Gestion de la sauvegarde en BDD
                CompteApplicatif compteApplicatif;
                //S'il n'y a pas d'id acteur, le compte n'existe pas encore en BDD
                if (compte.getIdActeur() == null) {
                    compteApplicatif = ActeurMapper.INSTANCE.creationCompteDtoToCompteApplicatif(compte);
                } else {
                    compteApplicatif = compteApplicatifRepository.findByIdActeur(compte.getIdActeur());
                    ActeurMapper.INSTANCE.update(compteApplicatif, ActeurMapper.INSTANCE.creationCompteDtoToCompteApplicatif(compte));
                }

                if (compteApplicatif.getEmail() != null && !StringUtils.isEmpty(compte.getEmail())) {
                    //Par défaut, on met une adresse mail de type interne.
                    TypeMail typeMail = typeMailRepository.findByCode(ConstanteActeur.CODE_TYPE_MAIL_INTERNE);
                    if (typeMail == null)
                        throw new ServiceException("Type du mail non reconnu. Aucune action effectuée.");
                    compteApplicatif.getEmail().setTypeMail(typeMail);
                } else {
                    compteApplicatif.setEmail(null);
                }
                K_LOGGER.info(String.format("Sauvegarde en base de données du compte applicatif %s", compte.getLogin()));
                compteApplicatifRepository.save(compteApplicatif);

                //Gestion de la sauvegarde dans l'annuaire
                try {
                    Name newDn = new LdapName(String.format("CN=%s,OU=Applications,OU=Comptes", compte.getCn()));
                    //S'il n'y a pas d'id acteur, le compte n'existe pas encore
                    if (compte.getIdActeur() != null) {
                        CompteApplicatifAD ca = this.compteApplicatifADRepository.findById(new LdapName(compte.getDn())).orElse(null);
                        if (ca == null)
                            throw new ServiceException("Le compte applicatif n'a pas été trouvé dans l'annuaire. Aucune action effectuée.");

                        Name oldDn = ca.getDn();

                        //Pour ne pas perdre l'id de l'entrée dans l'annuaire, il faut obligatoirement renommer l'entité avant de sauvegarder
                        //On ne le fait que si les dns sont effectivement différents entre les deux versions
                        if (!oldDn.equals(newDn)) {
                            ldapTemplate.rename(oldDn, newDn);
                        }
                    }
                    CompteApplicatifAD compteApplicatifAD;
                    if (compte.getIdActeur() == null) {
                        compteApplicatifAD = new CompteApplicatifAD(compte);
                    } else {
                        compteApplicatifAD = compteApplicatifADRepository.findById(newDn).orElse(null);

                        if (compteApplicatifAD == null) throw new ServiceException();
                        ActeurADMapper.INSTANCE.update(compteApplicatifAD, new CompteApplicatifAD(compte));
                        compteApplicatifAD.setDn(newDn);
                    }
                    K_LOGGER.info(String.format("Sauvegarde dans l'annuaire du compte applicatif %s", compte.getLogin()));
                    compteApplicatifADRepository.save(compteApplicatifAD);
                    if (compte.getIdActeur() == null) {

                        this.acteurADService.updatePassword(compteApplicatifAD.getIdentifiant(),
                                this.passwordService.genererPassword(ConstanteActeur.TYPE_APPLICATIF), ConstanteActeur.TYPE_APPLICATIF);
                        this.acteurADService.activer(compteApplicatifAD.getIdentifiant(), ConstanteActeur.TYPE_APPLICATIF);

                    }
                    return;
                } catch (InvalidNameException | ServiceException e) {
                    throw new ServiceException(String.format("Une erreur est survenue lors de la sauvegarde dans l'annuaire. %s", e.getMessage()));
                }
            default:
                throw new ServiceException("La typologie du compte n'a pas été reconnue. Aucune action effectuée.");
        }
    }


    @Override
    public void deleteActeur(Long idActeur) throws ServiceException {
        ActeurVue acteur = this.acteurVueRepository.findById(idActeur).orElse(null);
        if (acteur == null)
            throw new ServiceException("L'acteur n'a pas été trouvé en base de données. Aucune action effectuée.");

        this.responsableGrpFoncRepository.deleteByLoginActeur(acteur.getLogin());

        K_LOGGER.info(String.format("Suppression en base de données de l'acteur %s", acteur.getLogin()));
        switch (acteur.getTypeActeur()) {
            case ConstanteActeur.TYPE_AGENT:
                agentRepository.deleteById(idActeur);
                break;
            case ConstanteActeur.TYPE_EXTERNE:
                externeRepository.deleteById(idActeur);
                break;
            case ConstanteActeur.TYPE_SERVICE:
                compteServiceRepository.deleteById(idActeur);
                break;
            case ConstanteActeur.TYPE_RESSOURCE:
                compteRessourceRepository.deleteById(idActeur);
                break;
            case ConstanteActeur.TYPE_APPLICATIF:
                compteApplicatifRepository.deleteById(idActeur);
                break;
            default:
                throw new ServiceException("Typologie de compte non reconnue. Aucune action effectuee.");
        }

        this.acteurADService.deleteActeur(acteur.getLogin(), acteur.getTypeActeur());
    }


    //ERN -> Mantis 16469 & 16475 : Gestion de la suppression des demandes de droits liées a un acteur avant suppression, et des taches d'habilitations liées à cette demande de droit (corrige tous les plantages dus aux suppressions, directe comme par workflow)
    @Override
    public void deleteDemandesActeur(Long idActeur) {
        // On supprime les demandes de droits de l'acteur. Avant cela, pour chacune de ses demandes de droits, on va supprimer les taches d'habilitation liées à cette demande, sinon la demande ne pourra pas etre supprimée, et par conséquent l'acteur non plus
        List<DemandeDroit> demandeDroitList = demandeDroitRepository.findByIdActeur(idActeur);
        demandeDroitList.forEach(demandeDroit -> {
            // on commence par supprimer les taches d'habilitations liées au demandes de droit de l'acteur, nécessaire pour supprimer ensuite ces demandes
            try {
                List<TacheHabilitation> tacheHabilitationList = tacheHabilitationService.findByIdDemande(demandeDroit.getId());
                tacheHabilitationRepository.deleteAll(tacheHabilitationList);
            } catch (ObjectNotFoundException e) {
                log.warn("Suppression de l'habilitation dans la table RFATWFHTACHE avec l'id demande => " + demandeDroit.getId() + " non possible, la ligne recherch� n'existe pas en BDD.");
                log.warn("Stack trace pour info : " + e);
            }
        });
        // enfin, on supprime la demande de droit
        demandeDroitRepository.deleteAll(demandeDroitList);

        // les demandes d'acteur qu'il faut garder(notamment demande suppression)
        List<DemandeActeur> demandeActeurList = demandeActeurRepository.findAllByActeurBenef_IdActeur(idActeur);
        for (DemandeActeur demandeActeur : demandeActeurList) {
            demandeActeur.setActeurBenef(null);
            demandeActeur.setStatut(statutDemandeRepository.findByCode(ConstanteWorkflow.STATUT_DEMANDE_ACCEPTEE));
            demandeActeurRepository.save(demandeActeur);
        }
    }


    @Override
    public byte[] exportCsvFileActeurs() {
        List<ActeurVue> acteurVueList = acteurVueRepository.findAll();
        return ExportFileCsvUtils.exportCsvFile(acteurVueList);
    }

    @Override
    public byte[] getExportActeursTechniques(String typeCompte) {
        String typeActeur = typeCompte.equals(EnumTypeDonneeReference.TYPE_COMPTE_SERVICE.code) ? "S" : typeCompte.equals(EnumTypeDonneeReference.TYPE_COMPTE_RESSOURCE.code) ? "R" : "C";
        List<ActeurVue> acteurVuesList = this.acteurVueRepository.findAllByTypeActeur(typeActeur);
        return ExportFileCsvUtils.exportCsvFile(acteurVuesList);
    }


}