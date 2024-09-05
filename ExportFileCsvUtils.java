package fr.vdm.referentiel.refadmin.utils;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ExportFileCsvUtils {

    public static <T> byte[] exportCsvFile(List<T> exportFileDto) {
        try {
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);

            if (!exportFileDto.isEmpty()){

                // Générer l'entête du fichier
                String[] csvHeader = generateHeaders(exportFileDto.get(0));

                // Écrire l'entête du fichier
                csvWriter.writeNext(csvHeader);

                // Écrire chaque itération de la liste d'objets "ExportFileDto" sur une ligne du fichier.
                exportFileDto.forEach(export -> {
                    List<String> data;

                    try {
                        data = new ArrayList<>(convertDtoToStringList(export));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                    csvWriter.writeNext(data.toArray(new String[0]));
                });

            }

            csvWriter.close();

            return stringWriter.toString().getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static <T> List<String> convertDtoToStringList(T dto) throws IllegalAccessException {
        List<String> values = new ArrayList<>();
        for (Field field : dto.getClass().getDeclaredFields()) {
            // Si le champ est privé, on vérifie s'il possède un getter
            if (Modifier.isPrivate(field.getModifiers())) {
                String fieldName = field.getName();
                String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                try {
                    Method getter = dto.getClass().getMethod(getterName);
                    Object value = getter.invoke(dto);
                    values.add(value!=null? value.toString().trim(): "");
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    // Gérer les erreurs
                    e.printStackTrace();
                }
            }
        }
        return values;
    }


    private static <T> String[] generateHeaders(T dto) {
        List<String> headerList = new ArrayList<>();

        for (Field field : dto.getClass().getDeclaredFields()) {
            // Si le champ est privé, on vérifie s'il possède un getter
            if (Modifier.isPrivate(field.getModifiers())) {
                headerList.add(field.getName());
            }
        }

        return headerList.toArray(new String[0]);
    }
}
