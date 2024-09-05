package fr.vdm.referentiel.refadmin.utils;

public class ConstanteAD {


    public static final String TYPE_GROUPE_FONCTIONNEL = "GROUPE FONCTIONNEL";
    public static final String TYPE_GROUPE_APPLICATIF = "GROUPE APPLICATIF";
    public static final String TYPE_GROUPE_ORGA = "GROUPE ORGANISATIONNEL";

    public static int UF_ACCOUNTDISABLE = 0x0002;
    /**
     * bit de l'attribut USER_ACCOUNT_CONTROL correspondant à un compte avec un
     * mot de passe non requis.
     */
    public static int UF_PASSWD_NOTREQD = 0x0020;
    /**
     * bit de l'attribut USER_ACCOUNT_CONTROL correspondant à un compte avec un
     * mot de passe ne pouvant pas changé.
     */
    public static int UF_PASSWD_CANT_CHANGE = 0x0040;
    /**
     * bit de l'attribut USER_ACCOUNT_CONTROL correspondant à un compte normal.
     */
    public static int UF_NORMAL_ACCOUNT = 0x0200;
    /**
     * bit de l'attribut USER_ACCOUNT_CONTROL correspondant à un compte avec un
     * mot de passe ne pouvant pas expiré.
     */
    public static int UF_DONT_EXPIRE_PASSWD = 0x10000;
    /**
     * bit de l'attribut USER_ACCOUNT_CONTROL correspondant à un compte avec un
     * mot de passe qui expire.
     */
    public static int UF_PASSWORD_EXPIRED = 0x800000;

    public static String ATTR_AD_SN = "sn";
    public static String ATTR_AD_GIVE_NAME = "givenName";

    /**
     * login.
     */
    public static String ATTR_AD_ACCOUNT_NAME = "sAMAccountName";

    public static String ATTR_AD_OBJECT_SID = "objectSid";
    public static String ATTR_AD_LAST_LOG_ON = "lastLogon";
    public static String ATTR_AD_ACCOUNT_EXPIRES = "accountExpires";
    public static String ATTR_AD_CN = "cn";
    public static String ATTR_AD_DN = "distinguishedName";
    public static String ATTR_AD_USER_ACCOUNT_CONTROL = "userAccountControl";

    public static String ATTR_AD_MEMBER = "member";
    /**
     * Adresse mail.
     */
    public static String ATTR_AD_MAIL = "mail";

    /**
     * les alias de messageries
     */
    public static String ATTR_AD_OTHER_MAIL = "otherMailbox";

    /**
     * Pour le téléphone mobile principal.
     */
    public static String ATTR_AD_MOBILE = "mobile";

    /**
     * Les autres téléphones mobiles.
     */
    public static String ATTR_AD_OTHER_MOBILE = "otherMobile";

    /**
     * Nom affiché.
     */
    public static String ATTR_AD_DISPLAY_NAME = "displayName";
    public static String ATTR_AD_OBJECTCLASS = "objectClass";

    public static String ATTR_AD_OBJECTCLASS_GROUP = "group";

    /**
     * Description de l'utilisateur.
     */
    public static String ATTR_AD_DESCRIPTION = "description";

    /**
     * Nom.
     */
    public static String ATTR_AD_NAME = "name";

    /**
     * Titre.
     */
    public static String ATTR_AD_TITLE = "title";

    /**
     * Nom principal de l'utilisateur.
     */
    public static String ATTR_AD_USER_PRINCIPAL_NAME = "userPrincipalName";

    /**
     * Numéro de téléphone fixe principal.
     */
    public static String ATTR_AD_TELEPHONE_NUMBER = "telephoneNumber";

    /**
     * Les autres numéros de téléphone fixe.
     */
    public static String ATTR_AD_OTHER_TELEPHONE = "otherTelephone";

    /**
     * L'attribut gérant la TOIP : desktopProfil.
     */
    public static String ATTR_AD_TOIP_DESKTOP_PROFIL = "desktopProfile";

    /**
     * L'attribut gérant la TOIP : ipphone.
     */
    public static String ATTR_AD_TOIP_IP_PHONE = "ipPhone";

    /**
     * Code postal.
     */
    public static String ATTR_AD_POSTAL_CODE = "postalCode";
    public static String ATTR_AD_O = "o";

    /**
     * Département.
     */
    public static String ATTR_AD_DEPARTEMENT = "department";
    public static String ATTR_AD_L = "l";

    /**
     * Durée de verrouillage du compte AD.
     */
    public static String ATTR_AD_LOCKOUTTIME = "lockouttime";

    /**
     * Serveur de fichier
     */
    public static String ATTR_AD_ST = "st";

    /**
     * Serveur de fichier complémentaire
     */
    public static String ATTR_AD_HOUSEIDENTIFIER = "houseIdentifier";

    /**
     * Script de démarrage.
     */
    public static String ATTR_AD_SCRIPT_PATH = "scriptPath";

    /**
     * Attribut de stockage du mobile perso de l'agent
     */
    public static String ATTR_AD_VDM_MOBILE_PERSO = "vdmMobilePerso";

    /**
     * Attribut de stockage de l'email perso de l'agent
     */
    public static String ATTR_AD_VDM_EMAIL_PERSO = "vdmMailPerso";

    /**
     * Attribut de stockage de l'adresse perso de l'agent
     */
    public static String ATTR_AD_VDM_POSTAL_ADRESSE_PERSO = "vdmPostalAdressPerso";

    /**
     * Attribut de stockage du code postal de l'adresse perso de l'agent
     */
    public static String ATTR_AD_VDM_POSTAL_CODE_PERSO = "vdmPostalCodePerso";

    /**
     * Attribut de stockage de l'adresse perso de l'agent
     */
    public static String ATTR_AD_VDM_L_PERSO = "vdmLPerso";

    /**
     * Attribut qui permet sauvegarde la derniére date de modification du password.
     * Peut être mis à 0 pour obliger l'utilisateur à changer son mdp à la prochaine connexion.
     */
    public static String PWD_LAST_SET = "pwdLastSet";

    public static String ATTR_AD_FAC_SIMILE_TELEPHONE_NUMBER = "facSimileTelephoneNumber";

    public static String ATTR_AD_EMPLOYEE_NUMBER = "employeeNumber";
    public static String ATTR_AD_GIVEN_NAME = "givenName";

    public static String ATTR_AD_VDM_NOM_PATRONYMIQUE = "vdmNomPatronymique";

    public static String ATTR_AD_DEPARTMENT = "department";

    public static String ATTR_AD_POSTAL_ADDRESS = "postalAddress";
    public static String ATTR_AD_STREET_ADDRESS = "streetAddress";
    public static String ATTR_AD_VDM_DEPARTMENT_TEXTE = "vdmDepartmentTexte";
    public static String ATTR_AD_THUMBNAIL_PHOTO = "thumbnailPhoto";
    public static String ATTR_AD_SEE_ALSO = "seeAlso";
    public static String ATTR_AD_OBJECTCLASS_GROUP_OF_NAMES = "groupOfNames";

    public static String ATTR_AD_OBJECTCLASS_MAIL_RECIPIENT = "mailRecipient";
    public static String ATTR_AD_OBJECTCLASS_TOP = "top";
    public static String ATTR_AD_OBJECTCLASS_PERSON = "person";
    public static String ATTR_AD_OBJECTCLASS_ORGANIZATIONAL_PERSON = "organizationalPerson";
    public static String ATTR_AD_OBJECTCLASS_VDM_PERSON = "vdmpersonne";
    public static String ATTR_AD_OBJECTCLASS_INET_ORG_PERSON = "inetOrgPerson";
    public static String ATTR_AD_OBJECTCLASS_ICS_CALENDAR_USER = "icsCalendarUser";
    public static String ATTR_AD_PREFERREDLANGUAGE = "preferredLanguage";
    public static String ATTR_AD_PREFERREDLANGUAGE_FR = "fr";
    public static String ATTR_AD_ICSSTATUS_DELETED = "deleted";
    public static String ATTR_AD_ICSSTATUS = "icsStatus";
    public static String ATTR_AD_USER = "user";


    public static String ATTR_AD_DEPARTMENT_NUMBER = "departmentNumber";

    public static String ATTR_AD_OBJECTCLASS_IP_USER = "ipUser";

    public static String ATTR_AD_OBJECTCLASS_USER_PRESENCE_PROFILE = "userPresenceProfile";

    public static String ATTR_AD_OBJECTCLASS_INET_MAIL_USER = "inetMailUser";

    public static String ATTR_AD_OBJECTCLASS_INET_LOCAL_MAIL_RECIPIENT = "inetLocalMailRecipient";
    public static String ATTR_AD_MAIL_QUOTA = "mailQuota";
    public static String ATTR_AD_VACATION_START_DATE = "vacationStartDate";
    public static String ATTR_AD_VACATION_END_DATE = "vacationEndDate";
    public static String ATTR_AD_MAIL_AUTO_REPLY_MODE_REPLY = "reply";
    public static String ATTR_AD_MAIL_AUTO_REPLY_MODE = "mailAutoReplyMode";
    public static String ATTR_AD_MAIL_DELIVERY_OPTION = "mailDeliveryOption";
    public static String ATTR_AD_MAIL_DELIVERY_OPTION_AUTO = "autoreply";
    public static String ATTR_AD_MAIL_DELIVERY_OPTION_MAILBOX = "mailbox";
    public static String ATTR_AD_MAIL_DELIVERY_OPTION_FORWARD = "forward";
    public static String ATTR_AD_MAIL_AUTO_REPLY_SUBJECT = "mailAutoReplySubject";
    public static String ATTR_AD_MAIL_AUTO_REPLY_TEXT = "mailAutoReplyText";
    public static String ATTR_AD_MAIL_ALLOWED_SERVICE_ACCESS = "mailAllowedServiceAccess";
    public static String ATTR_AD_MAIL_ALTERNATE_ADDRESS = "mailAlternateAddress";
    public static String ATTR_AD_MAIL_FORWARDING_ADDRESS = "mailForwardingAddress";
    public static String ATTR_AD_MAIL_HOST = "mailHost";
    public static String ATTR_AD_MAIL_USER_STATUS = "mailUserStatus";
    public static String ATTR_AD_MAIL_MESSAGE_STORE = "mailMessageStore";
    public static String ATTR_AD_MAIL_USER_STATUS_ACTIVE = "active";
    public static String ATTR_AD_MAIL_USER_STATUS_BLOQUE = "hold";

    public static String BUSINESS_CATEGORY_GROUPE_APPLICATIF = "GROUPE APPLICATIF";
    public static String BUSINESS_CATEGORY_GROUPE_FONCTIONNEL = "GROUPE FONCTIONNEL";
    public static String BUSINESS_CATEGORY_GROUPE_ORGANISATIONNEL = "GROUPE ORGANISATIONNEL";

    public static String ORGANIZATIONAL_UNIT_GROUPES_TECHNIQUES = "Groupes techniques";

    public static String ORGANIZATIONAL_UNIT_GROUPES_TECHNIQUES_INTERNES = "Groupes techniques internes";

    public static String ORGANIZATIONAL_UNIT_GROUPES_TECHNIQUES_EXTERNES = "Groupes techniques externes";

    public static String ORGANIZATIONAL_UNIT_GROUPES_INTERNES = "Groupes internes";

    public static String ORGANIZATIONAL_UNIT_GROUPES_EXTERNES = "Groupes externes";
}
