package fr.vdm.referentiel.refadmin.repository;

import fr.vdm.referentiel.refadmin.model.Cellule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CelluleRepository  extends JpaRepository<Cellule,Long> {
    Cellule findByCollAndSsCollAndCode(String coll, String ssColl, String code);

    Cellule findByLibLdapGrCel(String libLdapGrCel);


    @Query(value = "Select * from RFSTGRCEL c where c.COLL = 'MAR' " +
            "AND (c.FINRH is null OR c.FINRH > sysdate)" +
            "START WITH c.COLL = 'MAR' AND trim(c.CELLULE) = :codeCellule " +
            "connect by c.CELPERE = prior c.CELLULE " +
            "ORDER BY c.NIVEAU", nativeQuery = true)
    List<Cellule> findAllfilsActiveInclus(@Param("codeCellule") String codeCellule);

    @Query(value = "Select * from RFSTGRCEL c where c.COLL = 'MAR' " +
            "AND (c.FINRH is null OR c.FINRH > sysdate)" +
            "START WITH c.COLL = 'MAR' AND trim(c.CELLULE) = :codeCellule  " +
            "connect by c.CELLULE = prior c.CELPERE " +
            "ORDER BY c.NIVEAU", nativeQuery = true)
    List<Cellule> findAllParentsActiveInclus(@Param("codeCellule") String codeCellule);

    @Query(value = "select c from Cellule c where trim(c.code) = trim(:code) and (c.finRh is null or c.finRh > CURRENT_DATE )")
    Cellule findCelluleActiveByCode(@Param("code")String code);

    @Query(value = "select c from Cellule c where trim(c.code) = trim(:code) ")
    Cellule findCelluleByCode(@Param("code")String code);

    @Query("select c from Cellule c where c.niveau = :niveau and (c.finRh is null or c.finRh > CURRENT_DATE )")
    List<Cellule> findCelluleActiveByNiveau(@Param("niveau") Long niveau);

    @Query(value = "select c from Cellule c where trim(c.codeCellulePere) = :codePere and (c.finRh is null or c.finRh > CURRENT_DATE )")
    List<Cellule> findAllFilsActiveDirects(@Param("codePere") String codePere);


    @Query(value = "select c from Cellule c where " +
            "upper(c.libAbrgrCellule) like upper(concat('%', :input, '%'))" +
            " or upper(c.libLongGrCellule) like upper(concat('%', :input, '%'))" +
            " or upper(c.libLdapGrCel) like upper(concat('%', :input, '%'))" +
            " or upper(c.code) like upper(concat('%', :input, '%')) and (c.finRh is null or c.finRh > CURRENT_DATE )")
    List<Cellule> findCellulesActiveByInput(@Param("input") String input);

    @Query(value = "SELECT c.* " +
            "FROM RFSTGRCEL c " +
            "WHERE c.COLL = :coll AND c.SSCOLL = :ssColl " +
            "AND c.CODE = :code " +
            "START WITH c.COLL = :coll AND c.SSCOLL = :ssColl AND c.CODE = :code " +
            "CONNECT BY PRIOR c.CODE = c.CODE_CELLULE_PERE " +
            "ORDER BY c.NIVEAU", nativeQuery = true)
    List<Cellule> findPeresByCollAndSsCollAndCode(@Param("coll") String coll, @Param("ssColl") String ssColl, @Param("code") String code);

    @Query(value = "SELECT DISTINCT c.COLL, c.SSCOLL, c.CELLULE, c.NIVEAU " +
            "FROM RFSTGRCEL c " +
            "WHERE c.COLL = :coll AND c.SSCOLL = :ssColl " +
            "START WITH c.COLL = :coll AND c.SSCOLL = :ssColl AND c.CELLULE = :code " +
            "CONNECT BY PRIOR c.CELLULE = c.CELPERE " +
            "ORDER BY c.NIVEAU", nativeQuery = true)
    List<Object[]> findClePeres(@Param("coll") String coll, @Param("ssColl") String ssColl, @Param("code") String code);


    @Query(value = "select c.id from Cellule c " +
            "where trim(c.code) = trim(:code) " +
            "and c.coll = 'MAR'")
    Long findIdCelluleCollMarByCode(@Param("code")String code);

    @Query(value = "select c from Cellule c " +
            "where trim(c.code) = trim(:code) " +
            "and c.coll = 'MAR'")
    Cellule findCelluleCollMarByCode(@Param("code")String code);
}