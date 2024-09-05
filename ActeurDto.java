package fr.vdm.referentiel.refadmin.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ActeurDto {

    private Long idActeur;

    private String login;

    private String nom;

    private Long idEmail;

    private Instant tsCreat;

    private String intervCreat;

    private int version;

    private Instant tsModif;

    private String intervModif;

    private Boolean consentPhoto;

    private Boolean consentCoordPerso;

    private Instant tsConsentPhoto;

    private Instant tsConsentCoordPerso;
}
