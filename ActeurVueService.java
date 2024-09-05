package fr.vdm.referentiel.refadmin.service;

import fr.vdm.referentiel.refadmin.model.ActeurVue;
import fr.vdm.referentiel.refadmin.model.Droit;
import fr.vdm.referentiel.refadmin.model.Offre;

public interface ActeurVueService {

    /**
     * Cette méthode permet de trouver un objet {@link ActeurVue} dans la base de donnée
     * par son {@param login}
     *
     * @param login login le nom de l'identifiant de l'acteur (le login)
     * @return retourne un objet {@link ActeurVue}
     */
    ActeurVue getActeurVueByLogin(String login);

    /**
     * Cette methode vérifie si l'objet {@link ActeurVue} existe dans la base donnée par la valeur en paramètre : {@param idActeur}
     *
     * @param login le nom de l'identifiant de l'acteur (le login)
     * @return retourne {@code TRUE} si l'objet {@link ActeurVue} est trouvé sinon {@code FALSE}
     */
    Boolean existsActeurVueByLogin(String login);

    /**
     * Cette méthode permet de retourner un objet {@link ActeurVue} si son paramètre {@param idActeur} est different de {@code NULL}
     * sinon elle retourne {@code NULL}
     *
     * @param idActeur id de l'acteur
     * @return retourne un objet {@link ActeurVue}
     */
    ActeurVue getActeurVueByIdActeurIfIdIsNotNull(Long idActeur);
}
