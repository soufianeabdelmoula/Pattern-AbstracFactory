package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.LienActeurGrpFonc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LienActeurGroupeFonctionnelRepository extends JpaRepository<LienActeurGrpFonc, Long> {
  List<LienActeurGrpFonc> findAllByIdGrp(Long idGrp);

  @Modifying
  @Query(value = "delete from LienActeurGrpFonc lien where lien.idActeur =:idActeur and lien.idGrp =:idGrpFonc")
  void deleteByIdActeurAndIdGrp(@Param("idGrpFonc") Long idGrpFonc, @Param("idActeur") Long idActeur);

  @Modifying
  @Query(value = "delete from LienActeurGrpFonc lien where lien.idActeur =:idActeur")
  void deleteByIdActeur(@Param("idActeur") Long idActeur);


  List<LienActeurGrpFonc> findAllByIdActeur(Long idActeur);

  LienActeurGrpFonc findByIdActeurAndIdGrp(Long idActeur, Long idGrp);

}
