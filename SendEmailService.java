package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.dto.DemandeDroitDto;
import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.Cellule;
import fr.vdm.referentiel.refadmin.model.Offre;
import fr.vdm.referentiel.refadmin.utils.ServiceException;

import java.util.List;

public interface SendEmailService {

    /**
     * Envoie un mail
     */
    void sendMails(final String subject, final String body,
                   final String emetteur, final List<String> destinataires,
                   final List<String> destinatairesCopie);

    /**
     * Construit le mail pour les valideurs potentiels pour une demande
     * concernant un compte acteur.
     */
    void sendMailsValideursPotentiels(final String cellule, final String role, final Long idDemande);


    /**
     * Construit le mail pour les valideurs potentiels pour une demande
     * concernant une habilitation
     * @param affectation
     *            l'affectation concerné par l'envoi de mail
     * @param role
     *            le role concerné par l'envoi de mail
     * @param idOffre
     *            l'id de l'offre concerné par l'envoi de mail
     * @param idDemande
     *            l'id de la demande
     * @param idActeur
     *            l'id de l'acteur
     */
    void sendMailsValideursPotentiels(final String affectation, String role, Long idOffre, Long idDemande, Long idActeur);

    /**
     * Construit le mail quotien récapitulatif des tâches
     */
    void sendMailsRecapitulatifTaches(boolean simu);

    /**
     * Construit le mail destiné au responsable de la création de la demande
     * pour l'informer que sa demande acteur a été rejeté.
     * @param idDemande
     *            l'id de la demande concerné.
     */
    void sendMailsRefusActeur(final Long idDemande);

    /**
     * Construit et envoi le mail pour le demandeur et/ou le bénéficiaire * suite au refus ou l’acceptation d’une demande de création/suppression
     * /modification d’une habilitation à une offre de service !
     * @param demandeAcceptee
     * 			true si la demande est acceptée, false si non
     * @param idOffre
     * 			id de l'offre concernée
     * @param idActeur
     * 			id de l'acteur
     * */
    void sendMailsDemandeurBeneficiaire(final boolean demandeAcceptee, final Long idOffre, final Long idDemande, final Long idActeur);


    /**
     * Construit le mail destiné au responsable de la création de la demande
     * pour l'informer que sa demande d'habilitation a été rejeté.
     *
     * @param idDemande       l'id de la demande concerné.
     * @param demandeDroitDto la demande de droit concerné.
     */
    void sendMailsRefusHabilitation(final Long idDemande, DemandeDroitDto demandeDroitDto);


    void sendMailAlerteChangementAffectationGroupeFonctionnel(Cellule ancienneCellule, Cellule nouvelleCellule,
                                                              String groupe, ActeurVue acteur, List<String> destinataires);

    void sendMailSuppressionAutoGroupeFonctionnel(Cellule ancienneCellule, Cellule nouvelleCellule,
                                                  String groupe, ActeurVue acteur, List<String> destinataires);

    void sendMailsValideursTechniques(Offre offre, ActeurVue acteurVue);

    void sendPasswordActeur(String login, Long idDemande, String password, String nomComplet, String idta, String idtn, String loginDemandeur) throws ServiceException;
}
