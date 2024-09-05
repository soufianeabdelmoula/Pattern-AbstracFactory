package fr.vdm.referentiel.refadmin.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "RFATACTEUR")
public class Acteur extends AbstractBaseEntity{

    @GeneratedValue(generator = "SEQ_RFATACTEUR")
    @SequenceGenerator(name="SEQ_RFATACTEUR", sequenceName = "SEQ_RFATACTEUR", allocationSize = 1 )
    @Id
    @Column(name = "IDACTEUR", nullable = false)
    private Long idActeur;
    @Basic
    @Column(name = "LOGIN", nullable = false, length = 60)
    private String login;
    @Basic
    @Column(name = "NOM", nullable = false, length = 60)
    private String nom;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "IDEMAIL")
    private Email email;

    @Basic
    @Column(name = "BOOCONSENTPHOTO")
    private Boolean consentPhoto;
    @Basic
    @Column(name = "BOOCONSENTCOORDPERSO")
    private Boolean consentCoordPerso;
    @Basic
    @Column(name = "TSCONSENTPHOTO")
    private Instant tsConsentPhoto;
    @Basic
    @Column(name = "TSCONSENTCOORDPERSO")
    private Instant tsConsentCoordPerso;


}