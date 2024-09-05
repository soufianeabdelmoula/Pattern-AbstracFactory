package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.dto.GroupeADDto;
import fr.vdm.referentiel.refadmin.mapper.GroupeADMapper;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.ad.GroupeAD;
import fr.vdm.referentiel.refadmin.repository.ad.GroupeADRepository;
import fr.vdm.referentiel.refadmin.service.GroupeADService;
import fr.vdm.referentiel.refadmin.service.PropagationLDAPService;
import fr.vdm.referentiel.refadmin.utils.DnUtils;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropagationLDAPServiceImpl implements PropagationLDAPService {

    private final GroupeADService groupeADService;
    private final GroupeADRepository groupeADRepository;

    public PropagationLDAPServiceImpl(GroupeADService groupeADService, GroupeADRepository groupeADRepository) {
        this.groupeADService = groupeADService;
        this.groupeADRepository = groupeADRepository;
    }

    @Override
    public void propagerSauvegardeDroitActeur(ActeurVue acteurVue, List<String> dnProfilsActeur) throws ServiceException {

        List<GroupeADDto> listeGroupes = new ArrayList<>();
        for (String dn: dnProfilsActeur){
            GroupeAD groupeAD = this.groupeADRepository.findByCommonName(DnUtils.extraireValeurAttributFromDn(dn, "cn"));
            // On vérifie que le groupe existe dans le LDAP
            if (groupeAD != null){
                GroupeADDto groupeADDto = GroupeADMapper.INSTANCE.groupeADToGroupeADDto(groupeAD);
                listeGroupes.add(groupeADDto);
            } else {
                throw new ServiceException(String.format("Le groupe applicatif %s n'existe pas dans l'annuaire", DnUtils.extraireValeurAttributFromDn(dn, "cn")));
            }
        }

        if (!listeGroupes.isEmpty()){
            this.groupeADService.affecterActeur(acteurVue.getLogin(), listeGroupes, acteurVue.getTypeActeur());
        } else {
            throw new ServiceException("Vous devez choisir au moin un groupe applicatif");
        }
    }
}
