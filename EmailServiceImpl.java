package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.model.Email;
import fr.vdm.referentiel.refadmin.model.HistoriqueEmail;
import fr.vdm.referentiel.refadmin.model.TypeMail;
import fr.vdm.referentiel.refadmin.repository.EmailRepository;
import fr.vdm.referentiel.refadmin.repository.HistoriqueEmailRepository;
import fr.vdm.referentiel.refadmin.repository.TypeMailRepository;
import fr.vdm.referentiel.refadmin.service.EmailService;
import fr.vdm.referentiel.refadmin.utils.ConstanteActeur;
import fr.vdm.referentiel.refadmin.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class EmailServiceImpl implements EmailService {
    private final TypeMailRepository typeMailRepository;

    private final HistoriqueEmailRepository historiqueEmailRepository;
    private final EmailRepository emailRepository;

    public EmailServiceImpl(HistoriqueEmailRepository historiqueEmailRepository,
                            EmailRepository emailRepository,
                            TypeMailRepository typeMailRepository) {
        this.historiqueEmailRepository = historiqueEmailRepository;
        this.emailRepository = emailRepository;
        this.typeMailRepository = typeMailRepository;
    }


    /**
     * Compare 2 emails de la table Email.
     *
     * @param idEmail1   : l'id de l'email1 a comparer
     * @param dateDebut1
     * @param idEmail2   : l'id de l'historique l'email2 a comparer
     * @param dateDebut2
     * @return true si les deux mails sont differents, false sinon
     */
    @Override
    public boolean isHistoriqueDifferent(Long idEmail1, Instant dateDebut1, Long idEmail2, Instant dateDebut2) {
        boolean different = true;
        String stringMail1 = null;
        HistoriqueEmail historiqueEmail1 =
                this.historiqueEmailRepository.findHistoriqueEmailByIdEmailAndDebHisto(idEmail1, dateDebut1);
        if (historiqueEmail1 != null) {
            stringMail1 = historiqueEmail1.getEmail();
        }
        String stringMail2 = null;
        HistoriqueEmail historiqueEmail2 =
                this.historiqueEmailRepository.findHistoriqueEmailByIdEmailAndDebHisto(idEmail2, dateDebut2);
        if (historiqueEmail2 != null) {
            stringMail2 = historiqueEmail2.getEmail();
        }
        if (stringMail1 == null && stringMail2 == null || stringMail1 != null
                && stringMail1.equals(stringMail2)) {
            different = false;
        }
        return different;    }

    /**
     * Compare 2 emails de la Table HistoriqueEmail
     *
     * @param idEmail1   : l'id de l'email1 a comparer
     * @param dateDebut1
     * @param idEmail2   : l'id de l'historique l'email2 a comparer
     * @return true si les deux mails sont differents, false sinon
     */
    @Override
    public boolean isDifferent(Long idEmail1, Instant dateDebut1, Long idEmail2) {
        boolean different = true;
        String stringMail1 = null;
        String stringMail2 = null;
        HistoriqueEmail historiqueEmail1 =
                this.historiqueEmailRepository.findHistoriqueEmailByIdEmailAndDebHisto(idEmail1, dateDebut1);
        if (historiqueEmail1 != null) {
            stringMail1 = historiqueEmail1.getEmail();
        }
        Email email2 = this.getEmailByIdEmail(idEmail2);
        if (email2 != null) {
            stringMail2 = email2.getEmail();
        }
        if (stringMail1 == null && stringMail2 == null || stringMail1 != null
                && stringMail1.equals(stringMail2)) {
            different = false;
        }
        return different;
    }

    /**
     * @param idEmail
     * @return
     */
    @Override
    public Email getEmailByIdEmail(Long idEmail) {
        return emailRepository.findById(idEmail).orElse(null);
    }


    /**
     * Création ou mise à jour du mail associé à un acteur
     *
     * @param idActeur l'identifiant de l'acteur
     * @param adresse  l'adresse email
     * @return l'email .
     */
    public Email saveMail(final long idActeur, final String adresse) {
        return this.saveMail(idActeur, adresse, false);
    }


    /**
     * Création ou mise à jour du mail associé à un acteur
     *
     * @param idActeur l'identifiant de l'acteur
     * @param adresse  l'adresse email
     * @param externe  indique si il s'agit d'un mail externe (true) ou interne
     *                 (false)
     * @return l'email .
     */
    public Email saveMail(final long idActeur, final String adresse,
                          final boolean externe) {

        Email oldEmail = null;
        if (idActeur != 0) {
            oldEmail = emailRepository.selectByActeur(idActeur);
            log.info("Sauvegarde de la messagerie  de l'acteur " + idActeur);
        }

        // Cas ou l'ancien email existe.
        if (oldEmail != null) {
            // si le nouvel email est nul on supprime l'objet.
            if (StringUtil.isBlank(adresse)) {
                this.emailRepository.delete(oldEmail);
                return null;
            }
            // Sinon on modifie l'adresse.
            else {
                oldEmail.setEmail(adresse);
                emailRepository.save(oldEmail);
                return oldEmail;
            }
        } else {
            // Cas ou l'ancien email n'existe pas
            if (!StringUtil.isBlank(adresse)) {
                Email email = new Email();
                TypeMail typeMail;
                // On attribue le code du mail externe / interne
                if (externe) {
                    typeMail =
                            typeMailRepository.findByCode(ConstanteActeur.CODE_TYPE_MAIL_EXTERNE);
                } else {
                    typeMail =
                            typeMailRepository.findByCode(ConstanteActeur.CODE_TYPE_MAIL_INTERNE);
                }
                email.setEmail(adresse);
                email.setTypeMail(typeMail);
                emailRepository.save(email);
                return email;
            }
            return null;
        }
    }


}
