package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.LienProfilPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LienProfilPermissionRepository extends JpaRepository<LienProfilPermission, Long> {

    List<LienProfilPermission> findAllByProfil(String profil);
    List<LienProfilPermission> findAll();
}