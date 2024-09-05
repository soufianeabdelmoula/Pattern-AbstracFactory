package fr.vdm.referentiel.refadmin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name="RFDTAGNT")
@IdClass(AgentRHId.class)
public class AgentRH {

    @Column(name = "COLL", nullable = false, length = 3)
    private String coll;

    @Column(name = "SSCOLL", nullable = false, length = 3)
    private String sscoll;

    @Id
    @Column(name = "IDTA", nullable = false)
    private String idta;

    @Id
    @Column(name = "IDTN", nullable = false)
    private String idtn;

    @Column(name = "NOM", nullable = false, length = 40)
    private String nom;

    @Column(name = "NOM_USUEL", length = 40)
    private String nomUsuel;

    @Column(name = "PRENOM", nullable = false, length = 40)
    private String prenom;

    @Column(name = "LIB_FONCTION", nullable = false, length = 60)
    private String libFonction;

    @Column(name = "TSSORTIE")
    private Instant tsSortie;

    @Column(name = "CELLULEDET", nullable = false, length = 10)
    private String celluleDet;

    @Column(name = "TSCREAT")
    private Instant tsCreat;

    @Column(name = "TSMODIF")
    private Instant tsModif;

    @Column(name = "INTERVMODIF", length = 60)
    private String intervModif;

    @Column(name = "INTERVCREAT", length = 60)
    private String intervCreat;

    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "CELLULE", length = 10)
    private String cellule;

    @Column(name = "COMPTE_PREEMBAUCHE", length = 1)
    private String comptePreembauche;

    @Column(name = "DATE_ENVOI_IDENTIFIANTS")
    private Instant dateEnvoiIdentifiants;

    @Column(name = "IDENTIFIANTS_ENVOYER", length = 40)
    private String identifiantsEnvoyer;

    @Column(name = "LOGIN", length = 40)
    private String login;

}
