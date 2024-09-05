package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.AgentRH;
import fr.vdm.referentiel.refadmin.model.AgentRHId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActeurRhRepository extends JpaRepository<AgentRH, AgentRHId> {

    @Query(value = "select a from AgentRH a where (upper(a.nom) like upper(concat('%', :nom, '%')) or upper(a.nomUsuel) like upper(concat('%', :nom, '%')) or :nom is null or :nom = '') " +
            "and (upper(a.prenom) like upper(concat('%', :prenom, '%')) or :prenom is null or :prenom = '')" +
            "and (upper(a.login) like upper(concat('%', :login, '%')) or :login is null or :login = '') " +
            "and (a.cellule like concat('%', :codeCellule, '%') or a.celluleDet like concat('%', :codeCellule, '%') or :codeCellule is null or :codeCellule = '')")
    Page<AgentRH> findByFiltre(@Param("nom") String nom, @Param("prenom") String prenom, @Param("login") String login, @Param("codeCellule") String codeCellule, Pageable pageable);


    @Query(value = "select a from AgentRH a where concat(a.idta, a.idtn) like concat('%',:input, '%') ")
    List<AgentRH> findByInput(@Param("input") String input);
}