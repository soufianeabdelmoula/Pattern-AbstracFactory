package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.exception.rest.handler.ActeurException;
import fr.vdm.referentiel.refadmin.model.Acteur;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.naming.InvalidNameException;
import java.util.List;

public interface ActeurService {

    ActeurVue getActeurVueByLogin(String login);

    Page<ActeurVueDto> getAllActeurVue(String input, Pageable page);

    ActeurVueDto getActeurVueDtoByLogin(String login);

    List<ActeurVueDto> getAllActeurVue();

    ActeurVueDto getActeurVueById(Long idActeur);

    List<DroitDto> getDroits(Long idActeur);

    ActeurVue getActeurVueByIdtaAndIdtn(String idta, String idtn);

    Page<ActeurVueDto> getAllActeursByFilters(FiltreHistoriqueDemandesDto filter, Pageable pageable);

    Page<ActeurVueDto> getActeursTechniquesByFilter(String typoCompte, String nom, String codeCellule, Long idType, Long idOffre, String login, String codeMega, Long idDemandeur, Pageable page);

    ActeurVueDto getDemandeur(Long idActeur) throws ServiceException;

    void saveCompteTechnique(CreationCompteDto compte) throws ServiceException;

    void deleteActeur(Long idActeur) throws ServiceException;

    boolean save(DemandeActeurDto demandeDto,
                 ChampTechniqueDto champTechnique,
                 ExtensionMessagerieDto messagerie, String oldEmail,
                 String oldLogin, String oldAffectation, boolean isImport) throws InvalidNameException, ServiceException;

    AgentDto selectAgentRHByMatricule(String idTa, String idTn, UtilisateurDto utilisateurDto, boolean isImport) throws ActeurException;

    Acteur getActeurByLogin(String login);

    //ERN -> Mantis 16469 & 16475 : Gestion de la suppression des demandes de droits liées a un acteur avant suppression, et des taches d'habilitations liées à cette demande de droit (corrige tous les plantages dus aux suppressions, directe comme par workflow)
    void deleteDemandesActeur(Long idActeur);
    byte[] exportCsvFileActeurs();
    byte[] getExportActeursTechniques(String typoCompte);

}