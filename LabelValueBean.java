package fr.vdm.referentiel.refadmin.utils;

import lombok.Data;

@Data

public class LabelValueBean {

    private String label = "";

    private String value = "";

    public LabelValueBean(String label, String value) {
        this.label = label;
        this.value = value;

    }
}
