package fr.vdm.referentiel.refadmin.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdGroupDto {
    private OffreGroupeDto groupePrincipal;
    private List<String> objectClassList;
    private List<OffreGroupeDto> groupesSecondaires;
}
