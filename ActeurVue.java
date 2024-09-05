package fr.vdm.referentiel.refadmin.model;

import fr.vdm.referentiel.refadmin.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "RFAVACTEUR")
public class ActeurVue extends AbstractBaseEntity {
    @Id
    @Column(name = "IDACTEUR", nullable = false, precision = 0)
    private Long idActeur;
    
    @Column(name = "TYPEACTEUR", nullable = true, length = 1)
    private String typeActeur;
    
    @Column(name = "TYPECOMPTE", nullable = true, precision = 0)
    private Long typeCompte;
    
    @Column(name = "LIBELLETYEP", nullable = true, length = 255)
    private String libelleType;
    
    @Column(name = "CODETYPE", nullable = true, length = 30)
    private String codeType;
    
    @Column(name = "LOGIN", nullable = false, length = 60)
    private String login;
    
    @Column(name = "NOM", nullable = false, length = 60)
    private String nom;
    
    @Column(name = "IDEMAIL", nullable = true, precision = 0)
    private Long idEmail;
    
    @Column(name = "COLL", nullable = true, length = 3)
    private String coll;
    
    @Column(name = "SSCOLL", nullable = true, length = 3)
    private String ssColl;
    
    @Column(name = "IDTA", nullable = true, length = 4)
    private String idta;
    
    @Column(name = "IDTN", nullable = true, length = 4)
    private String idtn;
    
    @Column(name = "NOMMARITAL", nullable = true, length = 60)
    private String nomMarital;
    
    @Column(name = "PRENOM", nullable = true, length = 60)
    private String prenom;
    
    @Column(name = "CELLULE", nullable = true, length = 10)
    private String cellule;
    
    @Column(name = "CELLULEDET", nullable = true, length = 10)
    private String celluleDet;
    
    @Column(name = "TSAFFECT", nullable = true)
    private Instant tsAffect;
    
    @Column(name = "FONCTION", nullable = true, length = 60)
    private String fonction;
    
    @Column(name = "NOMUSUEL", nullable = true, length = 60)
    private String nomUsuel;
    
    @Column(name = "PRENOMUSUEL", nullable = true, length = 60)
    private String prenomUsuel;
    
    @Column(name = "COLLTERRAIN", nullable = true, length = 3)
    private String collTerrain;
    
    @Column(name = "SSCOLLTERRAIN", nullable = true, length = 3)
    private String ssCollTerrain;
    
    @Column(name = "CELLULETERRAIN", nullable = true, length = 10)
    private String celluleTerrain;
    
    @Column(name = "TSTAFFECTTERRAIN", nullable = true)
    private Instant tsAffectTerrain;

    
    @Column(name = "TSSORTIE", nullable = true)
    private Instant tsSortie;
    
    @Column(name = "TOPREAFFECTER", nullable = true, precision = 0)
    private Long topReaffecter;
    
    @Column(name = "DESCRIPTION", nullable = true, length = 1024)
    private String description;
    
    @Column(name = "TSSORTIEPREV", nullable = true)
    private Instant tsSortiePrev;
    
    @Column(name = "IDORGANISATION", nullable = true, precision = 0)
    private Long idOrganisation;
    
    @Column(name = "CODEEQUFONC", nullable = true, precision = 0)
    private Long codeEquFonc;
    
    @Column(name = "CODEEQUFONCTERRAIN", nullable = true, precision = 0)
    private Long codeEquFoncTerrain;
    
    @Column(name = "BOOCONSENTPHOTO", nullable = true, precision = 0)
    private Boolean booConsentPhoto;

    @Column(name = "BOOCONSENTCOORDPERSO", nullable = true, precision = 0)
    private Boolean booConsentCoordPerso;

    @Column(name = "TSCONSENTPHOTO", nullable = true)
    private Instant tsConsentPhoto;

    @Column(name = "TSCONSENTCOORDPERSO", nullable = true)
    private Instant tsConsentCoordPerso;

    @Column(name = "EMAIL")
    private String email;


    /**
     * Retourne la concaténation du prenom principal avec le nom principal de
     * l'acteur
     *
     * @return String
     */
    public String getNomComplet() {
        if (getNomPrincipal() != null) {
            if (getPrenomPrincipal() != null) {
                return getPrenomPrincipal() + " " + getNomPrincipal();
            } else {
                return getPrenomPrincipal();
            }
        } else if (getPrenomPrincipal() == null) {
            return getNomPrincipal();
        } else {
            return "";
        }
    }

    /**
     * Retourne le nom principal d'un acteur.
     *
     * @return String
     */
    public String getNomPrincipal() {
        if (StringUtil.isNotBlank(this.nomUsuel)) {
            return this.nomUsuel;
        } else if (StringUtil.isNotBlank(this.nomMarital)) {
            return this.nomMarital;
        } else {
            return this.nom;
        }
    }

    /**
     * Retourne le prenom principal d'un acteur.
     *
     * @return String
     */
    public String getPrenomPrincipal() {
        if (StringUtil.isNotBlank(this.prenomUsuel)) {
            return this.prenomUsuel;
        } else {
            return this.prenom;
        }
    }

}