package fr.vdm.referentiel.refadmin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActeurADDto {


    private String dn;

    private String identifiant;


    private String cn;

    private String matricule;

    private String userPrincipalName;

    private String vdmNomPatronymique;

    private String givenName;

    private String sn;

    private String telephoneMobilePerso;

    private String mailPerso;

    private String telephoneFixePerso;

    private byte[] profilePicture;

    private String telephoneMobilePro;

    private String telephoneFixePro;

    private List<String> indicatifsNumTelPerso;

    private String numBureau;

    private String affectationTerrain;
    private String affectationOfficielle;

    private String description;

    private String email;

    private String userAccountControl;
    private String employeeType;

    private String title;

    private String departmentNumber;

    private String displayName;

    private String streetAddress;

    private String postalCode;

    private String vdmDepartmentTexte;

}