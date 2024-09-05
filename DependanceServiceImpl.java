package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.Dependance;
import fr.vdm.referentiel.refadmin.repository.DependanceRepository;
import fr.vdm.referentiel.refadmin.service.DependanceService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Log4j2
@Service
public class DependanceServiceImpl implements DependanceService {
    private final DependanceRepository dependanceRepository;

    public DependanceServiceImpl(DependanceRepository dependanceRepository) {
        this.dependanceRepository = dependanceRepository;
    }

    //  25/11/2015 – MMN - EVT 10758 :renvoi la liste des dépendances ayant pour dépendance celle passés en paramètre
    @Override
    public List<Long> findOffresByDependance(final Long idDependance) {
        List<Long> result = new ArrayList<Long>();
        List<Dependance> dependances = dependanceRepository.findDependancesByIdOffreSecond(idDependance);
        if (dependances != null){
            for (Dependance dep : dependances){
                result.add(dep.getIdOffrePrinc());
            }
        }
        return result;
    }
}
