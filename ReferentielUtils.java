package fr.vdm.referentiel.refadmin.utils;

public class ReferentielUtils {

    public static String getIdTa(String matricule) {
        if (matricule.length() < 8) return "";
        return matricule.substring(0, 3);
    }

    public static String getIdTn(String matricule) {
        if (matricule.length() < 8) return "";
        return matricule.substring(4, 7);
    }


    /**
     * Methode permettant de retourner un matricule en fonction de l'identifiant
     * Ta et de l'identifiant Tn
     *
     * @param idTa L'identifiant Ta
     * @param idTn L'identifiant Tn
     * @return Le matricule
     */
    public static String getMatricule(final String idTa, final String idTn) {
        return "" + idTa + "" + idTn;
    }
}
