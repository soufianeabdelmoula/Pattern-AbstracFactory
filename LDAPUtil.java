package fr.vdm.referentiel.refadmin.utils;

import org.apache.commons.lang3.StringUtils;

public class LDAPUtil {

    /**
     * Séparateur d'attribut du dn LDAP.
     */
    protected static final String SEPARATEUR_COMPOSANT_DN = ",";

    /**
     * Séparateur utilisé entre le nom de l'attribut et sa valeur dans un dn.
     */
    protected static final String SEPARATEUR_ATTRIBUT_VALEUR_DN = "=";

    public static String extraireValeurAttributFromDn(final String dn,
                                                      final String nomAttribut) {
        // Premier test : pour un dn vide, on renvoie null.
        if (StringUtils.isBlank(dn)) {
            return null;
        }
        // Un dn est de la forme suivante :
        // <nom_attribut>=<valeur>,<nom_attribut>=<valeur>,<nom_attribut>=<valeur>
        String[] listeComposantsDN = dn.split(SEPARATEUR_COMPOSANT_DN);
        // Après le premier découpage, on obtient des composants de la forme
        // suivante : <nom_attribut>=<valeur>
        String[] composantsComposantDN = null;
        for (String composantDN : listeComposantsDN) {
            // Avec ce découpage, on sépare normalement les éléments de la
            // manière suivante : <nom_attribut>,<valeur>
            composantsComposantDN =
                    composantDN.split(SEPARATEUR_ATTRIBUT_VALEUR_DN);
            // On vérifie que le nom d'attribut obtenu correspond au nom
            // d'attribut passé en paramètre et qu'il possède bien une valeur
            if (composantsComposantDN[0].trim().equalsIgnoreCase(nomAttribut.trim()) && composantsComposantDN.length > 1) {
                return composantsComposantDN[1];
            }
        }
        // Sinon, on retourne null.
        return null;
    }
}
