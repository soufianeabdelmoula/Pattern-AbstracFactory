package fr.vdm.referentiel.refadmin.service.impl;

import fr.vdm.referentiel.refadmin.service.PasswordService;
import fr.vdm.referentiel.refadmin.utils.ConstanteActeur;
import fr.vdm.referentiel.refadmin.utils.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final String dictionnaireMaj = "BCDFGHJKLMNPQRSTVWXZ";
    private final String dictionnaireMin = "bcdfghjklmnpqrstvwxz";
    private final String dictionnaireNombre = "0123456789";

    private final Random rand = new SecureRandom();

    @Value("${pwd.service.format}")
    String pwdServiceFormat;

    @Value("${pwd.acteur.format}")
    String pwdActeurFormat;

    @Value("${pwd.applicatif.format}")
    String pwdApplicatifFormat;

    private String generateBlocByDictionary(String dictionnaire, int count) {
        String bloc = "";
        int doublon = 0;
        char c;
        do {
            c = dictionnaire.charAt(rand.nextInt(dictionnaire.length()));
            //Un bloc de 4 Char ou plus peut admettre 1 seul doublon
            if (StringUtils.countMatches(bloc, String.valueOf(c)) >= 1) {
                if (count >= 4 && doublon < 1) {
                    doublon++;
                    bloc = bloc.concat(String.valueOf(c));
                }
            } else {
                bloc = bloc.concat(String.valueOf(c));
            }
        } while (bloc.length() < count);

        return bloc;
    }

    private String genererBloc(String type, int length) throws ServiceException {


        switch (type) {
            case "M":
                return generateBlocByDictionary(this.dictionnaireMaj, length);

            case "m":
                return generateBlocByDictionary(this.dictionnaireMin, length);

            case "c":
                return generateBlocByDictionary(this.dictionnaireNombre, length);

            default:
                throw new ServiceException();
        }

    }

    private String getFormatPassword(String typeActeur) {
        switch (typeActeur) {
            case ConstanteActeur.TYPE_SERVICE:
                return this.pwdServiceFormat;
            case ConstanteActeur.TYPE_APPLICATIF:
                return this.pwdApplicatifFormat;
            default:
                return this.pwdActeurFormat;
        }

    }

    /**
     * Genere un Mot de passe en fonction du type Acteur
     * Properties à renseigner pour 3 typologie de comptes
     * pwd.service.format=M2%m2%c6%m2%M2
     * pwd.acteur.format=M4-m4-c4
     * pwd.applicatif.format=M4_c2_m4_c2
     * Le format des properties suit la Régle:
     * M pour Majuscule
     * m pour minuscule
     * c pour chiffre
     * le nombre suivant M/m/c correspond au nombre d'occurence
     * chaque groupe de M/m/c doit être séparé par un séparateur au choix
     *
     * @param typeActeur
     * @return
     */
    public String genererPassword(String typeActeur) {
        String password = "";
        String str = this.getFormatPassword(typeActeur);

        try {
            ArrayList<String> splitStr = new ArrayList<>(Arrays.asList(str.split("(?<=\\G...)")));

            for (String split : splitStr) {
                switch (split.length()) {
                    case 3:
                        password = password.concat(genererBloc(String.valueOf(split.charAt(0)), Character.getNumericValue(split.charAt(1))));
                        password = password.concat(String.valueOf(split.charAt(2)));
                        break;
                    case 2:
                        password = password.concat(genererBloc(String.valueOf(split.charAt(0)), Character.getNumericValue(split.charAt(1))));
                        break;
                    default:
                        throw new ServiceException();

                }

            }


        } catch (ServiceException e) {
            //En cas d'erreur on génère un mot de passe au format par défaut : MMMM-9999-mmmm
            password = "";
            password = password.concat(generateBlocByDictionary(this.dictionnaireMaj, 4));
            password = password.concat("-");
            password = password.concat(generateBlocByDictionary(this.dictionnaireNombre, 4));
            password = password.concat("-");
            password = password.concat(generateBlocByDictionary(this.dictionnaireMin, 4));
            return password;
        }
        return password;
    }

}