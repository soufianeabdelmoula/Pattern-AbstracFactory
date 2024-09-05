package fr.vdm.referentiel.refadmin.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@Data
public class AffectationDto {



    /**
     * Les différents niveaux de l'affectation
     */
    private List<String> affectation = Arrays.asList(new String[15]);

    /**
     * Le code equipement
     */
    private Integer equipement;

    /**
     * Retourne l'affectation de plus bas niveau
     * @return le code de la cellule de plus bas niveau a laquelle est affectée
     *         l'acteur
     */
    public String getCodeAffectation() {

        for (int i = this.affectation.size()-1; i >= 0; i--) {
            if (StringUtils.isNotBlank(this.affectation.get(i))) {
                return this.affectation.get(i);
            }
        }
        return "";
    }

    /**
     * Retourne le niveau correspondant à l'affectation
     * @return le niveau de l'affectation ou -1 si aucune affectation n'est
     *         selectionnée
     */
    public int getNiveauAffectation() {

        for (int i = this.affectation.size()-1; i >= 0; i--) {
            if (StringUtils.isNotBlank(this.affectation.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public String getAffectation(final int index) {
        try {
            return this.affectation.get(index);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

}
