package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.model.Offre;
import fr.vdm.referentiel.refadmin.model.ParametrageLdap;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OffreService {
    List<OffreDto> findAllOffres();

    List<QuestionDto> findQuestionsByIdOffre(Long idOffre);

    List<ProfilDto> findProfilsByIdOffre(Long idOffre);

    OffreDto saveOffre(OffreRequestDto offreRequestDto) throws ServiceException;

    Page<OffreDto> getOffresByFilters(OffreRequestDto offreRequestDto, Pageable page);

    void delete(List<Long> offresIds);
    public List<GroupeApplicatifDto> getGroupApplicatifList();
    public List<GroupeApplicatifDto> getGroupApplicatifListByFilters(GroupeApplicatifRequestDto groupeApplicatifRequestDto);

    void deleteById(Long offreId);

    OffreDto findOffreByCodeOffre(String codeOffreRfa);

    OffreDto getOffre(Long idOffre);

    ParametrageLdap findParametrageLdapByIdOffre(Long idOffre);

    RfsGroupeApplicationRequestDto findGroupePrincipalByIdOffre(Long idOffre);

    List<RfsGroupeApplicationRequestDto> findGroupesSecondairesByIdOffre(Long idOffre);

    void deleteAllById(List<Long> offresIdList);

    /**
     * Cette méthode permet de retourner un objet {@link Offre} si son paramètre {@param idOffre} est different de {@code NULL}
     * sinon elle retourne {@code NULL}
     *
     * @param idOffre id de l'offre
     * @return retourne une offre
     */
    Offre getOffreByIdOffreIfIdIsNotNull(Long idOffre);

    OffreDto getOffreDtoByIdOffre(Long idOffre);

    public List<Offre> findDependances(final Long idOffre);
    byte[] getExportOffre();
}