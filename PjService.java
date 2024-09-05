package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.alfresco.dto.AlfrescoFileDTO;
import fr.vdm.referentiel.refadmin.alfresco.dto.PieceJointeDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;

import java.util.List;

public interface PjService {

    /**
     * Variable de demande pour une habilitation.
     */
    public static String DEMANDE_HAB = "H";
    /**
     * Variable de demande pour un acteur.
     */
    public static String DEMANDE_ACTEUR = "A";
    /**
     * Variable pour le nom de l offre de creation de compte.
     */
    public static String NOM_OFFRE_ACTEUR = "Création de compte refacteur";

    public void saveFile(AlfrescoFileDTO afd, PieceJointeDto pjDto, String nomoffre, ActeurVue beneficiaire, String loginCreat);

    List<PieceJointeDto> getAllByIdDemandeAndTypeDemande(final Long idDemande, final String typeDemande);

    void deletePieceJointe(Long idActeurBeneficiaire, String typeDemande, Long idOffre);
}
