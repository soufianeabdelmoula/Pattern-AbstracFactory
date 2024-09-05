package fr.vdm.referentiel.refadmin.utils;

/**
 * Exception levée par un des services de l'application
 *
 * @author eGauthier
 */
public class ServiceException extends Exception {

    public static String OFFRE_DEMANDE_NULL = "Demande d'habilitation pour une offre null";

    public static String ACTEUR_DEMANDE_NULL = "Demande pour un acteur null";

    public static String ERREUR_INNATENDU = "Une erreur Innatendu est survenu";

    public static String CELLULE_PERE_NULL = "La Cellule parent n'a pas été trouvé";

    public static String ID_INCONNU = "L'élément n'a pas été trouvé dans la base de donnée";

    public static String DROITS_DEMANDE_CREATION_ENCOURS = "Une demande de création est déjà en cours pour cet acteur et cette offre.";

    public static String DROITS_DEMANDE_SUPPRESSION_ENCOURS = "DROITS_DEMANDE_SUPPRESSION_ENCOURS";

    public ServiceException() {
        super();

    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);

    }

    public ServiceException(final String message) {
        super(message);

    }

    public ServiceException(final Throwable cause) {
        super(cause);

    }
}