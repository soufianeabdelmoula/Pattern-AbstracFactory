package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.DemandeActeurDto;
import fr.vdm.referentiel.refadmin.dto.EtapeDemActeurDto;
import fr.vdm.referentiel.refadmin.model.DemandeActeur;
import fr.vdm.referentiel.refadmin.model.EtapeDemActeur;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import java.util.List;

public interface DemandeActeurService {
    void saveDemandeActeur(DemandeActeurDto contents, String login) throws ServiceException;

    DemandeActeurDto findDemandeActeurByIdDemandeAc(Long idDemandeAc);

    void saveEtapeDemandeComplete(EtapeDemActeurDto etapeDemActeurDto) throws ServiceException;

    EtapeDemActeur findFirstEtap(Long idDemande);

    List<DemandeActeur> findDemandesByActeur(Long idActeur);
}