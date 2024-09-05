package fr.vdm.referentiel.refadmin.dto;

import lombok.Data;

@Data
public class ChampTechniqueDto {
    /**
     * valeur UID.
     */
    private static final long serialVersionUID = -4887294105221314158L;

    /** Serveur de fichiers utilisé par AD. **/
    private String serveurFichier;

    /** Serveur de fichiers secondaire utilisé par AD. **/
    private String serveurFichierComplementaire;

    /** Logon utilisé par AD. **/
    private String logon;

    /** Le répertoire de script utilisé par Ad au moment de la connexion. **/
    private String scriptPath;

    /** Le nom du domaine AD **/
    private String domaineAD;
}
