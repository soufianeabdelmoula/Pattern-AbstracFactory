
package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ActeurVueDto;
import fr.vdm.referentiel.refadmin.dto.GroupeFonctionnelDto;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import java.util.List;

public interface GroupeFonctionnelService {
    List<GroupeFonctionnelDto> findAll();
    public List<ActeurVueDto> getActeurs(Long idGrpFonc);
    public void setTypeParametrage(String typeParametrage, long idGrpFonc) ;
    public List<ActeurVueDto> addActeurs(List<Long> acteurs , Long idGrpFonc);

    public void gestionChangementAffectation(String ancienneAffectation, String nouvelleAffectation, long idActeur) throws ServiceException;

    public void deleteActeur(Long idGrp, Long idActeur);

}

