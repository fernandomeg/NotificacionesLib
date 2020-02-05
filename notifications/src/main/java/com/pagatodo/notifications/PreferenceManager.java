package com.pagatodo.notifications;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class PreferenceManager {

    public static final String NOTIFICACIONES_ARRAY = "NOTIFICACIONES_ARRAY";
    public static final String PREFERENCE_SETTINGS = "PREFERENCE_SETTINGS";
    public static final String NOTIFICACIONES_TIMESTAMP = "NOTIFICACIONES_TIMESTAMP";



    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
    }

    public static Set<String> getNotificaciones(Context context) {
        return getPreference(context).getStringSet(NOTIFICACIONES_ARRAY, new HashSet<String>());
    }

    public static void addNotificaciones(final String notificacionId, Context context) {
        final Set<String> notificacionIdSet = getPreference(context).getStringSet(NOTIFICACIONES_ARRAY, new HashSet<String>());
        final Set<String> inNotification = new HashSet<>(notificacionIdSet);
        inNotification.add(notificacionId);
        getPreference(context).edit().putStringSet(NOTIFICACIONES_ARRAY, inNotification).apply();
//        getPreference(context).edit().putInt(NOTIFICACIONES_COUNT, 123).commit();
    }

    public static void deleteidNotificacion(final String notificacionId, Context context) {
        final Set<String> leidas = getNotificaciones(context);
        leidas.remove(notificacionId);
    }

    public static void putNotificationTimestamp(Context context){
        getPreference(context).edit().putLong(NOTIFICACIONES_TIMESTAMP, Calendar.getInstance().getTimeInMillis()).apply();
    }

    public static Long getNotificationTimestamp(Context context){
        return getPreference(context).getLong(NOTIFICACIONES_TIMESTAMP, Calendar.getInstance().getTimeInMillis());
    }

    public static void addNotificacionesLeidasListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener){
        getPreference(context).registerOnSharedPreferenceChangeListener(listener);
    }

}
