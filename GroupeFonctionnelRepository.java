
package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.GroupeFonctionnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GroupeFonctionnelRepository extends JpaRepository<GroupeFonctionnel,Long> {
    GroupeFonctionnel findById(long id);




}

