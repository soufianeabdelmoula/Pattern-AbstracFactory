package fr.vdm.referentiel.refadmin.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.naming.InvalidNameException;
import javax.naming.Name;

public class DnUtils {
    private static final Log K_LOGGER =
            LogFactory.getLog(DnUtils.class);


    /**
     * Séparateur utilisé dans le cn des groupes.
     */
    protected final static String SEPARATEUR_CN_GROUPE = "-";

    /**
     * Séparateur d'attribut du dn LDAP.
     */
    protected final static String SEPARATEUR_COMPOSANT_DN = ",";

    /**
     * Séparateur utilisé entre le nom de l'attribut et sa valeur dans un dn.
     */
    protected final static String SEPARATEUR_ATTRIBUT_VALEUR_DN = "=";

    public static Name truncateDn(Name dnComplet) {
        try {
            Name nameToReturn = (Name) dnComplet.clone();
            nameToReturn.remove(0);
            nameToReturn.remove(0);
            return nameToReturn;
        } catch (InvalidNameException e) {
            K_LOGGER.error(String.format("Erreur lors de la réduction du DN %s", dnComplet));
            return null;
        }
    }

    public static Name getDnComplet(Name dnIncomplet) {
        try {
            Name nameToReturn = (Name) dnIncomplet.clone();
            nameToReturn.add(0, "DC=vdm");
            nameToReturn.add(0, "DC=mars");
            return nameToReturn;
        } catch (InvalidNameException e) {
            K_LOGGER.error(String.format("Erreur lors de la complétion du DN %s", dnIncomplet));
            return null;
        }
    }

    public static String getDnCompletByCn(String cn){
        return "cn=" + cn + "," + "ou=utilisateurs,dc=vdm,dc=mars";
    }

    /**
     * Extrait la valeur de l'attribut dont le nom est passé en paramètre du dn
     * passé en paramètre.
     *
     * @param dn          Dn dont on souhaite extraire la valeur de l'attribut.
     * @param nomAttribut Nom de l'attribut dont on souhaite extraire la valeur.
     * @return Valeur de l'attribut.
     */
    public static String extraireValeurAttributFromDn(final String dn,
                                                      final String nomAttribut) {
        // Premier test : pour un dn vide, on renvoie null.
        if (!StringUtils.hasText(dn)) {
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
            if (composantsComposantDN[0].trim().toLowerCase().equals(
                    nomAttribut.trim().toLowerCase())
                    && composantsComposantDN.length > 1) {
                return composantsComposantDN[1];
            }
        }
        // Sinon, on retourne null.
        return null;
    }
}