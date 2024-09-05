package fr.vdm.referentiel.refadmin.utils;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumTypeDonneeReference {
    //TYPE_TELEPHONE("typeTelephone", "Type de téléphone"),
    //TYPE_MAIL("typeMail", "Type d'adresse email"),
    //ROLE_ACTEUR("roleActeur", "Rôles du workflow de gestion des acteurs"),
    //ROLE_DROIT("roleDroit", "Rôles du workflow de gestion des habilitations"),
    //STATUT_DEMANDE("statutDemande", "Statut des demandes"),
    DRIVER("driver", "Driver JDBC"),
    //SERVEUR_MAIl("serveurMail", "Serveur de messagerie"),
    //QUOTA_MAIL("quotaMail", "Quota de messagerie"),
    TYPE_COMPTE_RESSOURCE("typeCompteRessource", "Type de compte de ressource"),
    TYPE_COMPTE_SERVICE("typeCompteService", "Type de compte de service"),
    TYPE_COMPTE_APPLICATIF("typeCompteApplicatif", "Type de compte applicatif"),
    TYPE_TELEPHONIE("typeTelephonie", "Type de téléphonie");

    public final String code;
    public final String libelle;

    EnumTypeDonneeReference(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String code() {
        return code;
    }

    private String libelle() {
        return libelle;
    }
}