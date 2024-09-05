package fr.vdm.referentiel.refadmin.utils;

public class ConstanteActeur {
    public static final String TYPE_EXTERNE = "E";
    public static final String TYPE_AGENT = "A" ;
    public static final String TYPE_SERVICE = "S";
    public static final String TYPE_RESSOURCE = "R";
    public static final String TYPE_APPLICATIF = "C";
    public static final String TYPE_PARTENAIRE = "P" ;


    public static final String CODE_TYPE_MAIL_INTERNE = "INTERNE";
    public static final String CODE_TYPE_MAIL_EXTERNE = "EXTERNE";
    public static int UF_ACCOUNTDISABLE = 0x0002;

    /**
     * Constante correspondant aux attributs des données historisees de
     * changement d'un Acteur
     */
    public static final String HIST_MOD_PRENOM = "Prenom";
    public static final String HIST_MOD_PRENOM_USUEL = "PrenomUsuel";
    public static final String HIST_MOD_NOM = "Nom";
    public static final String HIST_MOD_NOM_USUEL = "NomUsuel";
    public static final String HIST_MOD_NOM_MARITAL = "NomMarital";
    public static final String HIST_MOD_MESSAGERIE = "Messagerie";
    public static final String HIST_MOD_LOGIN = "Login";
    public static final String HIST_MOD_DATESORTIE = "DateDeSortiePrevionnelle";

}