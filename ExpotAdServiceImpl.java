package fr.vdm.referentiel.refadmin.service.impl;

import com.opencsv.CSVWriter;
import fr.vdm.referentiel.refadmin.dto.*;
import fr.vdm.referentiel.refadmin.mapper.ActeurADMapper;
import fr.vdm.referentiel.refadmin.model.ad.ActeurAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteApplicatifAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteRessourceAD;
import fr.vdm.referentiel.refadmin.model.ad.CompteServiceAD;
import fr.vdm.referentiel.refadmin.model.enums.Operator;
import fr.vdm.referentiel.refadmin.service.ExpotAdService;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class ExpotAdServiceImpl implements ExpotAdService {

    private final LdapTemplate ldapTemplate;

    private static final ActeurADMapper acteurADMapper = ActeurADMapper.INSTANCE;

    public ExpotAdServiceImpl(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }


    @Override
    public RechercheResponse getActeursADByFilters(RechercheRequest rechercheRequest) {
        List<ActeurADDto> acteurADDtoList = new ArrayList<>();

        if (rechercheRequest == null || (rechercheRequest.getEmployeeTypes().isEmpty() && rechercheRequest.getFilterRequestList().isEmpty())) return null;


        if (rechercheRequest.getEmployeeTypes().contains("agent")){
            ContainerCriteria criteriaAgent = this.queryForFirter("OU=Utilisateurs", "A");
            for (FilterRequest filterRequest : rechercheRequest.getFilterRequestList()) {
                criteriaAgent = addFilter(criteriaAgent, filterRequest);
            }
            List<ActeurAD> acteurADAgentList = ldapTemplate.find(criteriaAgent, ActeurAD.class);
            acteurADDtoList.addAll(acteurADMapper.acteurADToActeurADDtoList(acteurADAgentList));
        }

        if (rechercheRequest.getEmployeeTypes().contains("externe")){
            ContainerCriteria criteriaAgentExterne = this.queryForFirter("OU=Utilisateurs", "E");
            for (FilterRequest filterRequest : rechercheRequest.getFilterRequestList()) {
                criteriaAgentExterne = addFilter(criteriaAgentExterne, filterRequest);
            }
            List<ActeurAD> acteurADExterneList = ldapTemplate.find(criteriaAgentExterne, ActeurAD.class);
            acteurADDtoList.addAll(acteurADMapper.acteurADToActeurADDtoList(acteurADExterneList));
        }

        if (rechercheRequest.getEmployeeTypes().contains("ressources")){
            ContainerCriteria criteriaRessources = this.queryForFirter("OU=Ressources");
            for (FilterRequest filterRequest : rechercheRequest.getFilterRequestList()) {
                criteriaRessources = addFilter(criteriaRessources, filterRequest);
            }
            List<CompteRessourceAD> compteRessourceADSList = ldapTemplate.find(criteriaRessources, CompteRessourceAD.class);
            acteurADDtoList.addAll(acteurADMapper.compteRessourceADToActeurADDtoList(compteRessourceADSList));
        }

        if (rechercheRequest.getEmployeeTypes().contains("services")){
            ContainerCriteria criteriaServices = this.queryForFirter("OU=Services");
            for (FilterRequest filterRequest : rechercheRequest.getFilterRequestList()) {
                criteriaServices = addFilter(criteriaServices, filterRequest);
            }
            List<CompteServiceAD> compteServiceADSList = ldapTemplate.find(criteriaServices, CompteServiceAD.class);
            acteurADDtoList.addAll(acteurADMapper.compteServiceADToActeurADDtoList(compteServiceADSList));
        }

        if (rechercheRequest.getEmployeeTypes().contains("applications")){
            ContainerCriteria criteriaCompte= this.queryForFirter("OU=Comptes");
            ContainerCriteria criteriaApplicatifs= criteriaCompte.and(this.queryForFirter("OU=Applications"));

            for (FilterRequest filterRequest : rechercheRequest.getFilterRequestList()) {
                criteriaApplicatifs = addFilter(criteriaApplicatifs, filterRequest);
            }
            List<CompteApplicatifAD> compteApplicatifADSList = ldapTemplate.find(criteriaApplicatifs, CompteApplicatifAD.class);
            acteurADDtoList.addAll(acteurADMapper.compteApplicatifADToActeurADDtoList(compteApplicatifADSList));
        }

        ldapTemplate.setIgnorePartialResultException(true);

        return RechercheResponse.builder()
                .acteurADDtoList(acteurADDtoList)
                .totalActeurs(acteurADDtoList.size())
                .build();
    }

    private ContainerCriteria queryForFirter(String baseOU){
        return query()
                .base(baseOU)
                .countLimit(Integer.MAX_VALUE)
                .where("objectClass").is("user").and("objectClass").is("person");
    }
    private ContainerCriteria queryForFirterApplication(String baseOU1, String baseOU2){
        return query()
                .base(baseOU1).base(baseOU2)
                .countLimit(Integer.MAX_VALUE)
                .where("objectClass").is("user").and("objectClass").is("person");
    }

    private ContainerCriteria queryForFirter(String baseOU, String employeeType){
        return query()
                .base(baseOU)
                .countLimit(Integer.MAX_VALUE)
                .where("objectClass").is("user").and("objectClass").is("person")
                .and("employeeType").is(employeeType);
    }

    private ContainerCriteria addFilter(ContainerCriteria criteria,FilterRequest filterRequest) {

        ContainerCriteria filterCriteria = query()
                .where(filterRequest.getFilter().getField()).like(filterRequest.getFilter().getValue());

        if (filterRequest.getOperateur().equals(Operator.AND)) {
            return criteria.and(filterCriteria);
        }
        return criteria.or(filterCriteria);
    }


    @Override
    public byte[] exportCsvFile(ExportFIleDto exportFileDto) {
        try {
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);
            ExportFileChosenAttributesDto chosenAttributes = exportFileDto.getChosenAttributes();

            RechercheResponse rechercheResponse = getActeursADByFilters(exportFileDto.getRechercheRequest());

            String[] csvHeader = generateHeaders(chosenAttributes);


            csvWriter.writeNext(csvHeader);

            rechercheResponse.getActeurADDtoList().forEach(acteurADDto -> {
                List<String> data = new ArrayList<>();

                if (chosenAttributes.isDn()) {
                    data.add(acteurADDto.getDn());

                }

                if (chosenAttributes.isIdentifiant()) {
                    data.add(acteurADDto.getIdentifiant());
                }

                if (chosenAttributes.isCn()) {
                    data.add(acteurADDto.getCn());
                }

                if (chosenAttributes.isEmployeeNumber()) {
                    data.add(acteurADDto.getMatricule());
                }

                if (chosenAttributes.isUserPrincipalName()) {
                    data.add(acteurADDto.getUserPrincipalName());
                }

                if (chosenAttributes.isVdmNomPatronymique()) {
                    data.add(acteurADDto.getVdmNomPatronymique());
                }

                if (chosenAttributes.isGivenName()) {
                    data.add(acteurADDto.getGivenName());
                }

                if (chosenAttributes.isSn()) {
                    data.add(acteurADDto.getSn());
                }

                if (chosenAttributes.isTelephoneMobilePerso()) {
                    data.add(acteurADDto.getTelephoneMobilePerso());
                }

                if (chosenAttributes.isMailPerso()) {
                    data.add(acteurADDto.getMailPerso());
                }

                if (chosenAttributes.isTelephoneFixePerso()) {
                    data.add(acteurADDto.getTelephoneFixePerso());
                }

                if (chosenAttributes.isTelephoneMobilePro()) {
                    data.add(acteurADDto.getTelephoneMobilePro());
                }

                if (chosenAttributes.isTelephoneFixePro()) {
                    data.add(acteurADDto.getTelephoneFixePro());
                }

                if (chosenAttributes.isIndicatifsNumTelPerso()) {
                    data.add(String.join(", ", acteurADDto.getIndicatifsNumTelPerso()));
                }

                if (chosenAttributes.isNumBureau()) {
                    data.add(acteurADDto.getNumBureau());
                }

                if (chosenAttributes.isAffectationTerrain()) {
                    data.add(acteurADDto.getAffectationTerrain());
                }

                if (chosenAttributes.isDescription()) {
                    data.add(acteurADDto.getDescription());
                }

                if (chosenAttributes.isEmployeeType()) {
                    data.add(acteurADDto.getEmployeeType());

                }

                if (chosenAttributes.isUserAccountControl()) {
                    data.add(acteurADDto.getUserAccountControl());

                }

                if (chosenAttributes.isEmail()) {
                    data.add(acteurADDto.getEmail());
                }

                csvWriter.writeNext(data.toArray(new String[0]));
            });

            csvWriter.close();

            return stringWriter.toString().getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    private String[] generateHeaders(ExportFileChosenAttributesDto chosenAttributesDto) {
        List<String> headerList = new ArrayList<>();

        if (chosenAttributesDto.isDn()) {
            headerList.add("DN");
        }

        if (chosenAttributesDto.isIdentifiant()) {
            headerList.add("Identifiant");
        }

        if (chosenAttributesDto.isCn()) {
            headerList.add("CN");
        }

        if (chosenAttributesDto.isEmployeeNumber()) {
            headerList.add("Matricule");
        }

        if (chosenAttributesDto.isUserPrincipalName()) {
            headerList.add("User Principal Name");
        }

        if (chosenAttributesDto.isVdmNomPatronymique()) {
            headerList.add("VDM Nom Patronymique");
        }

        if (chosenAttributesDto.isGivenName()) {
            headerList.add("Given Name");
        }

        if (chosenAttributesDto.isSn()) {
            headerList.add("SN");
        }

        if (chosenAttributesDto.isTelephoneMobilePerso()) {
            headerList.add("Mobile Personnel");
        }

        if (chosenAttributesDto.isMailPerso()) {
            headerList.add("Mail Personnel");
        }

        if (chosenAttributesDto.isTelephoneFixePerso()) {
            headerList.add("Fixe Personnel");
        }

        if (chosenAttributesDto.isTelephoneMobilePro()) {
            headerList.add("Mobile Professionnel");
        }

        if (chosenAttributesDto.isTelephoneFixePro()) {
            headerList.add("Fixe Professionnel");
        }

        if (chosenAttributesDto.isIndicatifsNumTelPerso()) {
            headerList.add("Indicatifs Num Tel Perso");
        }

        if (chosenAttributesDto.isNumBureau()) {
            headerList.add("Num Bureau");
        }

        if (chosenAttributesDto.isAffectationTerrain()) {
            headerList.add("Affectation Terrain");
        }

        if (chosenAttributesDto.isDescription()) {
            headerList.add("Description");
        }

        if (chosenAttributesDto.isEmployeeType()) {
            headerList.add("Employee Type");
        }

        if (chosenAttributesDto.isUserAccountControl()) {
            headerList.add("User Account Control");
        }


        if (chosenAttributesDto.isEmail()) {
            headerList.add("Email");
        }

        return headerList.toArray(new String[0]);
    }




}
