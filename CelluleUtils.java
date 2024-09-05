package fr.vdm.referentiel.refadmin.utils;

import fr.vdm.referentiel.refadmin.dto.CelluleDto;
import fr.vdm.referentiel.refadmin.model.Cellule;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe utile permettant manipuler la clé d'une cellule pour retouver la
 * collectivité,sous-collectivité et code.
 * @author jMattio
 */
public class CelluleUtils {

    /**
     * Séparateur utilisé dans la clé.
     */
    public static final String SEPARATEUR = "/";

    /**
     * Longueur du code d'une cellule.
     */
    public static final int LONGUEUR_CODE_CELLULE = 10;

    /**
     * Retourne une collectivité à partir d'une clé.
     * @param cle
     *            La clé Collectivite/Sous Collectivite/Cellule
     * @return La collectivité.
     */
    public static String getColl(final String cle) {

        int index = cle.indexOf(SEPARATEUR);
        return cle.substring(0, index);
    }

    /**
     * Retourne une sous collectivité à partir d'une clé.
     * @param cle
     *            La clé Collectivite/Sous Collectivite/Cellule
     * @return La sous collectivité.
     */
    public static String getSsColl(final String cle) {
        int indexFirst = cle.indexOf(SEPARATEUR) + 1;
        int indexSecond = cle.indexOf(SEPARATEUR, indexFirst);
        return cle.substring(indexFirst, indexSecond);
    }

    /**
     * Retourne le code de la cellule é partir d'une clé.
     * @param cle
     *            La clé Collectivite/Sous Collectivite/Cellule
     * @return Le code de la cellule.
     */
    public static String getCode(final String cle) {
        int indexFirst = cle.indexOf(SEPARATEUR) + 1;
        int indexSecond = cle.indexOf(SEPARATEUR, indexFirst) + 1;
        int indexThird = cle.length();
        return cle.substring(indexSecond, indexThird);
    }

    /**
     * Construit une clé à partir de collectivité, sous collectivité et code
     * cellule passé en paramètre.
     * @param coll
     *            Le nom de la collectivité.
     * @param ssColl
     *            Le nom de la sous collectivité.
     * @param code
     *            Le code de la cellule.
     * @return La clé générée.
     */
    public static String getCle(final String coll, final String ssColl, final String code) {
        if (StringUtils.isBlank(coll)) {
            return null;
        }
        return (coll + SEPARATEUR + ssColl + SEPARATEUR + code);
    }

    /**
     * Construit une celé par défaut sur MAR-   -Code
     *
     * @param codeCellule
     * @return la clé généré
     */
    public static String getCle(final String codeCellule) {
        return getCle("MAR", "", codeCellule);
    }

    /**
     * Construit une clé à partir d'une Cellule
     *
     * @param cellule La cellule pour laquelle on souhaite récupérer sa clé.
     * @return la clé générée.
     */
    public static String getCle(final Cellule cellule) {
        return getCle(cellule.getColl(), cellule.getSsColl(), cellule.getCode());
    }

    /**
     * Retourne la description de la Cellule.
     * @param cellule
     *            la cellule
     * @return la description.
     */
    public static String getDescription(final Cellule cellule) {
        return getDescription(cellule.getLibLdapGrCel(), cellule.getCode());
    }

    /**
     * Retourne la description d'une cellule
     * @param libLDAP
     *            le libelle ldap de la cellule
     * @param cellule
     *            le code de la cellule
     * @return la description.
     */
    public static String getDescription(final String libLDAP,
            final String cellule) {
        return (libLDAP + " (" + cellule.trim() + ")");
    }

    /**
     * Complete le code cellule pour obtenir 10 caracteres Ajoute des blanc a
     * droite si necessaire.
     * @param code
     *            Code à transformer.
     * @return Le code sur 10 caractères.
     */
    public static final String completerCode(final String code) {
        return String.format("%1$-" + LONGUEUR_CODE_CELLULE + "."
                + LONGUEUR_CODE_CELLULE + "s", code);
    }

    /**
     * Complete le code cellule pour obtenir 10 caracteres Ajoute des blanc a
     * droite si necessaire.
     * @param cle
     *            Code à transformer.
     * @return Le code sur 10 caractères.
     */
    public static final String completerCle(final String cle) {
        String coll = getColl(cle);
        String ssColl = getSsColl(cle);
        String code = getCode(cle);
        return getCle(coll, ssColl, completerCode(code));

    }

    /**
     * Filtre une hiérarchie de cellule. Supprime les cellules null.
     * @param hierarchie
     *            Hiérarchie de cellule.
     * @return Liste des cellules en ayant supprimé les cellules null.
     */
    public static List<CelluleDto> filtrerHierarchie(final List<CelluleDto> hierarchie) {
        if (hierarchie == null) {
            return Collections.emptyList();
        }
        return hierarchie.stream()
                .filter(Objects::nonNull) // Filtrer les éléments non nuls
                .collect(Collectors.toList());
    }

}
