package fr.vdm.referentiel.refadmin.dto;

import fr.vdm.referentiel.refadmin.model.ActeurVue;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ActeurHabiliteDto {
    private static final long serialVersionUID = 1L;
    private String nom;
    private String nomUsuel;
    private String prenom;
    private String prenomUsuel;
    private String login;
    private String matricule;
    private String codeAffectationRH;
    private String affectationRH;
    private String codeAffectationTerrain;
    private String affectationTerrain;
    private Long idActeur;
    private Long idDroit;
    private String type;
    private String profilsString;
    private String descriptionProfilsString;
    private String fonction;
    private String finHisto;

    public ActeurHabiliteDto(final ActeurVue acteurVue) {
        this.nom = acteurVue.getNom();
        this.prenom = acteurVue.getPrenom();
        this.type = acteurVue.getTypeActeur();
        this.idActeur = acteurVue.getIdActeur();
        this.nomUsuel = acteurVue.getNomUsuel();
        this.prenomUsuel = acteurVue.getPrenomUsuel();
        this.login = acteurVue.getLogin();
        if (acteurVue.getIdta() != null && acteurVue.getIdtn() != null) {
            this.matricule = acteurVue.getIdta() + acteurVue.getIdtn();
        }
        this.codeAffectationRH = acteurVue.getCellule();
        this.codeAffectationTerrain = acteurVue.getCelluleTerrain();
    }


    public ActeurHabiliteDto(final String nom, final String prenom, final Long idActeur, final String type,
                             final String profilsString, final String prenomUsuel, final String nomUsuel,
                             final String idTa, final String idTn, final String login, final String fonction,
                             final String cellule, final String celluleTerrain, final Instant finHisto) {
        this.nom = nom;
        this.prenom = prenom;
        this.idActeur = idActeur;
        this.type = type;
        this.profilsString = profilsString != null ? profilsString.substring(3, profilsString.indexOf(",ou")) : null;
        this.prenomUsuel = prenomUsuel;
        this.nomUsuel = nomUsuel;
        if(idTa != null && idTn != null) {
            this.matricule = idTa + idTn;
        }
        this.login = login;
        this.fonction = fonction;
        this.codeAffectationRH = cellule;
        this.codeAffectationTerrain = celluleTerrain;
        DateTimeFormatter formatHistDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.finHisto = finHisto != null ? finHisto.atZone(ZoneId.systemDefault()).toLocalDate().format(formatHistDate) : null;
    }


}
