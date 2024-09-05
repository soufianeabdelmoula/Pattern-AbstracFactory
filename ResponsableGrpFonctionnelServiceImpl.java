package fr.vdm.referentiel.refadmin.service.impl;
import fr.vdm.referentiel.refadmin.dto.ResponsableGrpFoncDto;
import fr.vdm.referentiel.refadmin.mapper.ResponsableGrpFoncMapper;
import fr.vdm.referentiel.refadmin.model.GroupeFonctionnel;
import fr.vdm.referentiel.refadmin.model.ResponsableGrpFonc;
import fr.vdm.referentiel.refadmin.repository.GroupeFonctionnelRepository;
import fr.vdm.referentiel.refadmin.repository.ResponsableGrpFoncRepository;
import fr.vdm.referentiel.refadmin.service.ResponsableGrpFonctionnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

@Service
public class ResponsableGrpFonctionnelServiceImpl implements ResponsableGrpFonctionnelService {
    @Autowired
    private  final ResponsableGrpFoncRepository responsableGrpFonctionnelRepository;
    private final ResponsableGrpFoncMapper responsableGrpFoncMapper= ResponsableGrpFoncMapper.INSTANCE;
    public ResponsableGrpFonctionnelServiceImpl(ResponsableGrpFoncRepository responsableGrpFonctionnelRepository, ResponsableGrpFoncMapper responsableGrpFoncMapper, GroupeFonctionnelRepository groupeFonctionnelRepository) {
        this.responsableGrpFonctionnelRepository = responsableGrpFonctionnelRepository;
    }
    @Override
    public Page<ResponsableGrpFoncDto> findAll(Pageable pageable) {
        return this.responsableGrpFonctionnelRepository.findAll(pageable).map(ResponsableGrpFoncMapper.INSTANCE::responsableGrpFoncToDto);
    }

    @Override
    public Page<ResponsableGrpFoncDto> filterGrpFonc(String nom, String responsable, Pageable pageable) {
        Specification<ResponsableGrpFonc> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (nom!= null && !nom.isEmpty()) {
                Join<ResponsableGrpFonc, GroupeFonctionnel> groupeFonctionnelJoin = root.join("grpFonc");
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(groupeFonctionnelJoin.get("libLdapGrFonc")),
                                "%" + nom.toLowerCase() + "%")
                        ));}

            if (responsable != null && !responsable.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("login")),
                                "%" + responsable.toLowerCase() + "%"));}
            return predicate;};
        System.out.println("hi");
        return this.responsableGrpFonctionnelRepository.findAll(spec,pageable).map(ResponsableGrpFoncMapper.INSTANCE::responsableGrpFoncToDto);

}


}
