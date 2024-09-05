package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.ResponsableGrpFoncDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface ResponsableGrpFonctionnelService {
    Page<ResponsableGrpFoncDto> findAll(Pageable pageable);
    Page<ResponsableGrpFoncDto> filterGrpFonc(String nom, String responsable, Pageable pageable);
}
