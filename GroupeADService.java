package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.GroupeADDto;
import fr.vdm.referentiel.refadmin.model.GroupeApplicatif;
import fr.vdm.referentiel.refadmin.model.ad.ActeurAD;
import fr.vdm.referentiel.refadmin.model.ad.GroupeAD;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import javax.naming.Name;
import java.util.List;

public interface GroupeADService {

    List<GroupeAD> listeGroupeApplicatifToGroupeAD(Name dnActeur, List<GroupeApplicatif> listeGroupes);

    void ajouterActeurGroupeApplicatif(Name dnActeur, List<GroupeAD> listeGroupes) throws ServiceException;

    void ajouterActeurGroupeApplicatif(ActeurAD acteur, List<GroupeAD> listeGroupes) throws ServiceException;

    List<GroupeADDto> getGroupesActeur(String login) throws ServiceException;

    GroupeADDto getGroupeByCn(String cn) throws ServiceException;

    GroupeADDto getGroupeTechniqueByCn(String cn);

    GroupeADDto getGroupeInterneByCn(String cn);

    GroupeADDto getGroupeExterneByCn(String cn);

    GroupeADDto getGroupeTechniqueInterneByCn(String cn);

    GroupeADDto getGroupeTechniqueExterneByCn(String cn);

    List<GroupeADDto> findGroupesAscendants(String cnGroupe) throws ServiceException;

    GroupeADDto selectGroupePere(String cnGroupe) throws ServiceException;


    void affecterActeur(String login, GroupeADDto groupe, String... typeActeur) throws ServiceException;

    void affecterActeur(String login, List<GroupeADDto> listeGroupes, String... typeActeur) throws ServiceException;

    GroupeAD findByCnAndType(String cn, char type);

    GroupeAD findByCnAndType(String cn, String type);

    void ajouterActeurGroupeApplicatif(String loginActeur, List<GroupeAD> listeGroupes) throws ServiceException;


    void supprimerActeurGroupe(String login, String cnGroupe, char typeGroupe);
}