package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.repository.ActeurVueRepository;
import fr.vdm.referentiel.refadmin.service.ActeurVueService;
import org.springframework.stereotype.Service;

@Service
public class ActeurVueServiceImpl implements ActeurVueService {

    private final ActeurVueRepository acteurVueRepository;

    public ActeurVueServiceImpl(ActeurVueRepository acteurVueRepository) {
        this.acteurVueRepository = acteurVueRepository;
    }

    /** {@inheritDoc}*/
    @Override
    public ActeurVue getActeurVueByLogin(String login) {
        return this.acteurVueRepository.findActeurVueByLogin(login);
    }

    /** {@inheritDoc}*/
    @Override
    public Boolean existsActeurVueByLogin(String login) {
        return this.acteurVueRepository.existsActeurVueByLogin(login);
    }

    /** {@inheritDoc}*/
    @Override
    public ActeurVue getActeurVueByIdActeurIfIdIsNotNull(Long idActeur) {
        return idActeur != null? this.acteurVueRepository.findById(idActeur).orElse(null): null;
    }
}
