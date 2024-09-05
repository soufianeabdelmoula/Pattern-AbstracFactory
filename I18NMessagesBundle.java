package fr.vdm.referentiel.refadmin.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18NMessagesBundle {

    public static final String DUMMY_MESSAGE = "model.dummyMessage";
    public static final String DATABASE_CONNECTION_ERROR = "model.error.databaseConnection";
    public static final String OBJECT_NOT_FOUND_ERROR = "model.error.objectNotFound";
    public static final String DATABASE_ERROR = "model.error.databaseError";
    public static final String SERVICE_ERROR = "model.error.serviceError";
    public static final String UNKNOWN_SERVICE_ERROR = "model.error.unknownServiceError";

    /** The bundle resource name to use. */
    private static final String BUNDLE_NAME = "com.sopragroup.utils.model.i18nMessages";

    /** Internationalization stuff. The real bundle uses. */
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Locale.FRENCH);

    /**
     * Private constructor to avoid instance creation.
     * @author lManca
     */
    private I18NMessagesBundle() {
    }

    public static String getString(final String key) {
        return BUNDLE.getString(key);
    }

    public static String getString(final String key, final Object o) {
        return I18NMessagesBundle.getString(key, new Object[] {o });
    }

    public static String getString(final String key, final Object[] args) {
        MessageFormat mf = new MessageFormat(BUNDLE.getString(key));
        return mf.format(args);
    }

}
