package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.AgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentInfoRepository extends JpaRepository<AgentInfo, Long> {
}