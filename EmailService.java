package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.model.Email;

import java.time.Instant;

public interface EmailService {

    /**
     * Compare 2 emails de la table Email.
     * @param idEmail1
     *            : l'id de l'email1 a comparer
     * @param idEmail2
     *            : l'id de l'historique l'email2 a comparer
     * @param dateDebut1
     * @param dateDebut2
     * @return true si les deux mails sont differents, false sinon
     */
    boolean isHistoriqueDifferent(final Long idEmail1, final Instant dateDebut1, final Long idEmail2, final Instant dateDebut2);

    /**
     * Compare 2 emails de la Table HistoriqueEmail
     * @param idEmail1
     *            : l'id de l'email1 a comparer
     * @param idEmail2
     *            : l'id de l'historique l'email2 a comparer
     * @param dateDebut1
     * @return true si les deux mails sont differents, false sinon
     */
    boolean isDifferent(final Long idEmail1, final Instant dateDebut1, final Long idEmail2);

    Email getEmailByIdEmail(Long idEmail);


    /**
     * Création ou mise à jour du mail associé à un acteur
     *
     * @param idActeur l'identifiant de l'acteur
     * @param adresse  l'adresse email
     * @return l'email .
     */
    public Email saveMail(final long idActeur, final String adresse);


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
                          final boolean externe);
}
