package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.RoleActeur;
import fr.vdm.referentiel.refadmin.repository.RoleActeurRepository;
import fr.vdm.referentiel.refadmin.service.RoleActeurService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleActeurServiceImpl implements RoleActeurService {

    private final RoleActeurRepository roleActeurRepository;

    public RoleActeurServiceImpl(RoleActeurRepository roleActeurRepository) {
        this.roleActeurRepository = roleActeurRepository;
    }

    @Override
    public List<RoleActeur> findAll() {
        return roleActeurRepository.findAll();
    }

    @Override
    public Long findByCode(String code) {
        RoleActeur roleActeur = roleActeurRepository.findByCode(code);
        return roleActeur.getId();
    }
}
