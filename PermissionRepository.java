package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.LienProfilPermission;
import fr.vdm.referentiel.refadmin.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Permission findPermissionById(Long id);
}