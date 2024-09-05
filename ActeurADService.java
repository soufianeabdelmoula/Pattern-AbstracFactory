package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.Cellule;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.ad.ActeurAD;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import javax.naming.InvalidNameException;
import java.util.List;

public interface ActeurADService {

    ActeurADDto findByIdentifiant(String identifiant, String... type);
    List<ActeurAD> getAllActeurAD();

    void updatePassword(String username, String password, String... type);

    void desactiver(String login, String... type) throws ServiceException;

    void activer(String login, String... type) throws ServiceException;

    Boolean isActive(String login, String... typeActeur);

    void affecterHierarchieGroupe(ActeurADDto acteurADDto, boolean simu) throws ServiceException;

    void deleteActeur(String login, String typeActeur) throws ServiceException;

    boolean isMailLibre(String mail);

    boolean isLoginLibre(String login);

    void renommerActeur(String ancienLogin, String nouveauLogin, String typeActeur);

    ActeurADDto convert(DemandeActeur demandeActeur, Cellule affectationTerrain,
                        Cellule affectationOfficielle, ChampTechniqueDto champTechnique, String email);

    void saveChampsTechniquesAD(String login,
                                ChampTechniqueDto dto);

    void save(DemandeActeur demandeActeur, Cellule affectationTerrain,
              Cellule affectationOfficielle, ChampTechniqueDto champTechnique, String nouveauMail) throws InvalidNameException, ServiceException;

    void save(ActeurADDto dto, boolean affectationOrga, boolean simu) throws InvalidNameException, ServiceException;

    void motDePasseRequis(String login);
}