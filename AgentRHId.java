package fr.vdm.referentiel.refadmin.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class AgentRHId implements Serializable {

    private String idta;

    private String idtn;
}
