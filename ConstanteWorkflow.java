package fr.vdm.referentiel.refadmin.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstanteWorkflow {

    public static final String STATUT_TACHE_EN_COURS = "EN_COURS";
    public static final Long STATUT_TACHE_EN_COURS_ID = 1L;
    public static final String STATUT_TACHE_VALIDEE = "VALIDEE";
    public static final Long STATUT_TACHE_VALIDEE_ID = 2L;

    public static final String STATUT_TACHE_REFUSEE = "REFUSEE";
    public static final Long STATUT_TACHE_REFUSEE_ID = 3L;

    public static final String STATUT_TACHE_SUPPRIMEE = "SUPPRIMEE";
    public static final Long STATUT_TACHE_SUPPRIMEE_ID = 4L;

    public static final String STATUT_TACHE_ATTENTE_DROITS = "ATTENTE_DEP";
    public static final Long STATUT_TACHE_ATTENTE_DROITS_ID = 5L;

    public static final String STATUT_TACHE_ATTENTE_DEP = "ATTENTE_DEP";
    public static final Long STATUT_TACHE_ATTENTE_DEP_ID = 6L;

    public static final String STATUT_DEMANDE_EN_COURS_H = "EN_COURS_H";

    public static final String STATUT_DEMANDE_EN_COURS_T = "EN_COURS_T";

    public static final String STATUT_DEMANDE_ACCEPTEE = "ACCEPTEE";

    public static final String STATUT_DEMANDE_REFUSEE = "REFUSEE";

    public static final Long STATUT_DEMANDE_EN_COURS_H_ID = 1L;

    public static final Long STATUT_DEMANDE_EN_COURS_T_ID = 2L;

    public static final Long STATUT_DEMANDE_ACCEPTEE_ID = 3L;

    public static final Long STATUT_DEMANDE_REFUSEE_ID = 4L;

    public static final String ACTIVITE_DEMANDE_TECHNIQUE = "T";

    public static final String ACTIVITE_DEMANDE_HIERARCHIQUE_FINAL = "H Finale";

    public static final String ACTIVITE_DEMANDE_HIERARCHIQUE_INTER = "H Inter";

    public static final String TYPe_ETAPE_CREATION = "C";

    public static final String TYPe_ETAPE_SUPPRESSION = "S";

    public static final String TYPe_ETAPE_MODIFICATION = "M";
    public static final int MAX_HIERARCHIE = 5;
    public static final int MAX_NB_NIV_AFFECTATION = 15;
    public static final String ACTIVITE_TECHNIQUE_INTERMEDIAIRE = "Val T Inter";
    public static final String ROLE_OFFRE_HIERARCHIQUE_FINAL = "HIERARCHIQUE_FINAL";
    public static final String ROLE_OFFRE_HIERARCHIQUE_1 = "HIERARCHIQUE1" ;
    public static final String ROLE_OFFRE_HIERARCHIQUE_2 = "HIERARCHIQUE2" ;

    public static final String ROLE_OFFRE_HIERARCHIQUE_3 = "HIERARCHIQUE3" ;

    public static final String ROLE_OFFRE_HIERARCHIQUE_4 = "HIERARCHIQUE4" ;

    public static final String ROLE_OFFRE_HIERARCHIQUE_5 = "HIERARCHIQUE5" ;


    public static final String ROLE_ACTEUR_HIERARCHIQUE_FINAL = "HIERARCHIQUE_FINAL";
    public static final String ROLE_ACTEUR_HIERARCHIQUE_1 = "HIERARCHIQUE1" ;
    public static final String ROLE_ACTEUR_HIERARCHIQUE_2 = "HIERARCHIQUE2" ;

    public static final String ROLE_ACTEUR_HIERARCHIQUE_3 = "HIERARCHIQUE3" ;

    public static final String ROLE_ACTEUR_HIERARCHIQUE_4 = "HIERARCHIQUE4" ;

    public static final String ROLE_ACTEUR_HIERARCHIQUE_5 = "HIERARCHIQUE5" ;

    public static final String ACTIVITE_HIERARCHIQUE_INTERMEDIAIRE = "Val H Inter";

    public static final String ACTIVITE_HIERARCHIQUE_FINALE = "Val H Finale";

    public static final String  ACTIVITE_TECHNIQUE = "Val T";
    public static final String TYPE_ETAPE_COMPLETE = "C" ;
    public static final String TYPE_ETAPE_TECHNIQUE = "T" ;
    public static final String TYPE_ETAPE_SIMPLIFIEE = "S";

    public static final String DEMANDE_HAB = "H";

    public static final String ROLE_RFA_ADMIN = "RFA-ADMIN";
    /** Constante pour le type Agent. **/
    public static final String TYPE_AGENT = "A";
    /** Constante pour le type Compte Applicatif. **/
    public static final String TYPE_APPLICATION = "C";
    /** Constante pour le type externe. **/
    public static final String TYPE_EXTERNE = "E";
    /** Constante pour le type partenaire. **/
    public static final String TYPE_PARTENAIRE = "P";
    /**
     * ActivityName pour une tache ayant besoin d'un valideur technique
     */
    public static final String ACTIVITY_TECHNIQUE = "Val T";
    /**
     * ActivityName pour une tache ayant besoin d'un valideur technique (étape
     * intermédiaire lors du cas d'un accord préalable.
     */
    public static final String ACTIVITY_TECHNIQUE_INTERMEDIAIRE = "Val T Inter";

    public static final String ACTIVITY_TECHNIQUE_DEP = "Vat T Attente Dep";
    /**
     * ActivityName pour une tache ayant besoin d'un valideur hierarchique final
     */
    public static final String ACTIVITY_HIERARCHIQUE_FINAL = "Val H Finale";

    /**
     * ActivityName pour une tache ayant besoin d'un valideur hierarchique
     * intermediaire
     */
    public static final String ACTIVITY_HIERARCHIQUE_INTERMEDIAIRE = "Val H Inter";

    /**
     * Constante utilisé pour une demande de création d'un acteur.
     */
    public static final char DEM_ACTEUR_CREATION = 'C';
    public static final String DEM_ACTEUR_CREATION_STRING = "C";

    /**
     * Constante utilisé pour la modification d'un acteur
     */
    public static final char DEM_ACTEUR_MODIFICATION = 'M';
    public static final String DEM_ACTEUR_MODIFICATION_STRING = "M";
    /**
     * Constante utilisé pour une demande de suppression d'un acteur.
     */
    public static final char DEM_ACTEUR_SUPPRESSION = 'S';
    public static final String DEM_ACTEUR_SUPPRESSION_STRING = "S";

    /**
     * Constante utilisé pour une demande de création d'une offre.
     */
    public static final char DEM_OFFRE_CREATION = 'C';
    public static final String DEM_OFFRE_CREATION_STRING = "C";

    /**
     * Constante utilisé pour la modification d'une offre.
     */
    public static final char DEM_OFFRE_MODIFICATION = 'M';
    public static final String DEM_OFFRE_MODIFICATION_STRING = "M";
    /**
     * Constante utilisé pour une demande de suppression d'une offre.
     */
    public static final char DEM_OFFRE_SUPPRESSION = 'S';
    public static final String DEM_OFFRE_SUPPRESSION_STRING = "S";


    public static final String SUFFIXE_GROUPE_TECHNIQUE = "-T";
    public static final String SUFFIXE_GROUPE_EXTERNE = "-E";
    public static final String SUFFIXE_GROUPE_INTERNE = "-I";

    /**
     * Constante correspondant au code du role acteur technique.
     */
    public static final String ROLE_ACTEUR_TECHNIQUE = "TECHNIQUE";

    public static final String DMANDE_CREATION = "Création";

    public static final String DMANDE_MODIFICATION = "Modification";

    public static final String DMANDE_SUPPRESSION = "Suppression";

    /**
     * Constante correspondant au code du role offre technique.
     */
    public static final String ROLE_OFFRE_TECHNIQUE = "TECHNIQUE";

    public static final List<String> LIST_STATUT_DEMANDE = new ArrayList<>(
            Arrays.asList(ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_H,
                    ConstanteWorkflow.STATUT_DEMANDE_EN_COURS_T)
    );
}