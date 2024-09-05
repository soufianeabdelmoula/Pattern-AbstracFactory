package fr.vdm.referentiel.refadmin.utils;

import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Classe utilitaire de gestion des chaînes de caractères.
 */
public final class StringUtil {

	private StringUtil() {}

	/**
	 * Permet de savoir si une chaîne est vide (null ou taille "trimee" egale a
	 * 0).
	 * 
	 * @param chaine
	 *            Chaine de test
	 * @return true si vide, false sinon
	 */
	public static boolean isBlank(final String chaine) {
		return ((chaine == null) || (chaine.trim().length() == 0));
	}

	/**
	 * Permet de savoir si la chaîne passée en paramètre est vide (null ou
	 * taille égale à 0).
	 * 
	 * @param chaine
	 *            Chaine de test
	 * @return true si vide, false sinon.
	 */
	public static boolean isEmpty(final String chaine) {
		return ((chaine == null) || (chaine.length() == 0));
	}

	/**
	 * Indique si une chaîne de caractère est plus longue qu'une taille donnée.
	 * Prend en compte sans erreur le cas des chaines nulles.
	 * 
	 * @param chaine
	 * @param size
	 * @return true si la chaine est plus longue
	 */
	public static boolean isLonger(final String chaine, final int size) {
		return ((chaine != null) && (chaine.length() > size));
	}

	/**
	 * Permet de savoir si une chaîne est non vide (null ou taille "trimee"
	 * égale a 0).
	 * 
	 * @param chaine
	 *            Chaine de test
	 * @return ^false si vide, true sinon
	 */
	public static boolean isNotBlank(final String chaine) {
		return ((chaine != null) && (chaine.trim().length() != 0));
	}

	/**
	 * Effectue un trim sur une chaine de caractere uniquement si elle est non
	 * nulle. Prend en compte sans erreur le cas des chaînes nulles.
	 * 
	 * @param chaine
	 *            la chaine d'origine.
	 * @return la chaine sans espaces en fin.
	 */
	public static String trim(final String chaine) {
		if (chaine != null) {
			return chaine.trim();
		}
		return null;
	}

	/**
	 * Permet de savoir si une chaine contient un entier. Prend en compte sans
	 * erreur le cas des chaînes nulles.
	 * 
	 * @param chaine
	 *            La chaine a tester.
	 * @return true si c'est un nombre, false sinon
	 */
	public static boolean isInteger(final String chaine) {
		boolean isInteger;
		if (isBlank(chaine)) {
			isInteger = false;
		} else {
			try {
				Integer.parseInt(chaine.trim());
				isInteger = true;
			} catch (NumberFormatException e) {
				isInteger = false;
			}
		}
		return isInteger;
	}

	/**
	 * Transforme une liste java, en liste de String avec parenthèses et séparée
	 * par des virgules.
	 * 
	 * @param l
	 *            La liste à transformer
	 * @param bundle
	 *            Si bundle != null, on considère le l[i] comme des clés.
	 * @param isParenthesized
	 *            doit-on entourer la liste avec des parenthèses
	 * @return Les element sous la forme (l1,l2,...,l3)
	 */
	private static String getList(final List l, final ResourceBundle bundle,
			final boolean isParenthesized) {

		String par = "";

		// Si la liste n'est pas vide
		if ((l != null) && !l.isEmpty()) {

			// Si on entoure avec des parentheses
			par = (isParenthesized) ? "(" : "";

			// On insere les valeurs
			for (int i = 0; i < l.size(); i++) {

				par += ((i > 0) ? ", " : "");
				if (bundle == null) {
					par += l.get(i).toString();
				} else {
					try {
						par += bundle.getString(l.get(i).toString());
					} catch (Exception e) {
						par += l.get(i).toString();
					}
				}
			}

			// Si on entoure avec des parentheses
			par += (isParenthesized) ? ")" : "";
		}

		return par;
	}

	/**
	 * Retourne une liste parenthèse, en considérant les valeurs comme des clés
	 * du bundle
	 * 
	 * @param l
	 *            la liste
	 * @param bundle
	 *            le bundle pour les conversion
	 * @return une liste de valeur issues du bundle
	 */
	public static String getParenthesized(final List l,
			final ResourceBundle bundle) {
		return getList(l, bundle, true);
	}

	/**
	 * Supprime les chaines vides d'une liste de chaines.
	 * 
	 * @param values
	 *            la liste à filtrer
	 * @return une nouvelle liste sans les valeurs vides
	 */
	public static List<String> filtrer(final List<String> values) {
		List<String> res = new LinkedList<String>();
		if (values != null) {
			for (String val : values) {
				if (StringUtil.isNotBlank(val)) {
					res.add(val);
				}
			}
		}
		return res;
	}

	/**
	 * Retourne une liste de String à partir d'un tableau de string
	 * 
	 * @param values
	 *            Tableau de String
	 * @return liste de string
	 */
	public static List<String> getList(final String[] values) {
		List<String> res = new LinkedList<String>();
		if (values != null) {
			for (String val : values) {
				res.add(val);
			}
		}
		return res;
	}

	/**
	 * Méthode permettant de savoir si deux chaines de caractères sont égales
	 * (avec la possibilité de tester même si une des deux string est null).
	 * 
	 * @param s1
	 *            La première chaine de caractère
	 * @param s2
	 *            La seconde chaine de caractère
	 * @return true si égales false sinon.
	 */
	public static boolean isEquals(final String s1, final String s2) {

		return String.format("%1$s", s1).equals(String.format("%1$s", s2));
	}

	/**
	 * Méthode permettant de savoir si deux listes sont égales.
	 * 
	 * @param list1
	 *            La première liste
	 * @param list2
	 *            La seconde liste
	 * @return true si égales false sinon.
	 */
	public static boolean isEquals(final List<String> list1,
			final List<String> list2) {

		if (list1 == null) {
			return list2 == null;
		}
		if (list2 == null) {
			return list1 == null;
		}

		if (list1.size() != list2.size()) {
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			if (!list2.contains(list1.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Methode utilisée pour rajouter le caractère % au début et à la fin d'un
	 * filtre de recherche (les recherches sont de type LIKE %mot%.
	 * 
	 * @return String filtre de recherche ou il faut rajouter le caractère %
	 */
	public static String addJoker(final String s1) {
		if (s1 == null || s1.trim().equals("")) {
			return s1;
		}
		return "%" + s1 + "%";
	}

	/**
	 * Méthode permettant de tester si la chaine de caractères passée en
	 * paramètre contient une minuscule
	 * 
	 * @param s
	 * @return
	 */
	public static boolean contientMinuscule(String s) {
		final String reg = "[\\p{Alnum}\\p{Punct}]*[\\p{Lower}]+[\\p{Alnum}\\p{Punct}]*";

		return Pattern.matches(reg, s);
	}

	/**
	 * Méthode permettant de tester si la chaine de caractères passée en
	 * paramètre contient une majuscule
	 * 
	 * @param s
	 * @return
	 */
	public static boolean contientMajuscule(String s) {
		final String reg = "[\\p{Alnum}\\p{Punct}]*[\\p{Upper}]+[\\p{Alnum}\\p{Punct}]*";

		return Pattern.matches(reg, s);
	}

	/**
	 * Méthode permettant de tester si la chaine de caractères passée en
	 * paramètre contient un chiffre
	 * 
	 * @param s
	 * @return
	 */
	public static boolean contientChiffre(String s) {
		final String reg = "[\\p{Alnum}\\p{Punct}]*[\\p{Digit}]+[\\p{Alnum}\\p{Punct}]*";

		return Pattern.matches(reg, s);
	}
	
	/**
	 * Méthode permettant de tester si la chaine de caractères passée en
	 * paramètre contient un caractère spécial
	 * 
	 * @param s
	 * @return
	 */
	public static boolean contientCaractereSpecial(String s) {
		final String reg = "[\\p{Alnum}\\p{Punct}]*[\\p{Punct}]+[\\p{Alnum}\\p{Punct}]*";

		return Pattern.matches(reg, s);
	}

	/**
	 * Méthode permettant de transformer la chaîne de caractères passées en
	 * paramètres en son équivalent sans accent et en majuscules.
	 * 
	 * @param s La chaîne à transformer
	 * @return La chaîne transformée
	 */
	public static String toUnAccentUpperCase(String s) {

		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		temp = pattern.matcher(temp).replaceAll("");
		temp = temp.replace("Ø", "O");
		temp = temp.replace("ø", "o");
		return temp.toUpperCase();
	}

    public static String toPascalCase(String string) {
		if (string == null || string.isEmpty()){
			return string;
		} else {
			return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
		}

    }
}
