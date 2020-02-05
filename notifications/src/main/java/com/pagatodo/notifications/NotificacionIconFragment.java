package com.pagatodo.notifications;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pagatodo.notifications.databinding.FragmentLibNotificacionIconBinding;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

public class NotificacionIconFragment extends AbstractDialogFragment {

    private static final String TAG = NotificacionIconFragment.class.getName();
    private FragmentLibNotificacionIconBinding binding;
    private final Set<String> notificationIds = new TreeSet<>();
    private Activity context;

    public static NotificacionIconFragment newInstance() {
        final NotificacionIconFragment fragment = new NotificacionIconFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        context = getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lib_notificacion_icon, container, false);
        binding.btnNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final NotificacionesDialogFragment notificaciones = new NotificacionesDialogFragment();
                notificaciones.setNotificacionIconFragment(NotificacionIconFragment.this);
                notificaciones.show(getFragmentManager(), "");
            }
        });

        final NotificacionesProvider.NotificacionCallback listener = new NotificacionesProvider.NotificacionCallback(){

            @Override
            public void onUpdate(Integer notificacionesCount) {
                actualizarNotificaciones(notificacionesCount);
            }
        };


        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initNotificationsReminder();
    }

    private void actualizarNotificaciones(final int noLeidasCount) {
//        MposApplication.getInstance().setNotisNoLeidas(noLeidasCount);

        binding.tvNumeroNotificaciones.setText(Integer.toString(noLeidasCount));
        binding.tvNumeroNotificaciones.setVisibility(noLeidasCount<=0?View.INVISIBLE:View.VISIBLE);
    }

    public void initNotificationsReminder() {

        PreferenceManager.putNotificationTimestamp(context);

        final long timelapse = 5 * 60 * 1000;

        if ( handler == null ) {
            handler = new Handler();
        }

        if ( runnableCode == null ) {
            runnableCode = new Runnable() {
                @Override
                public void run() {
                    Log.e("","");
                    Long timestamp = PreferenceManager.getNotificationTimestamp(context);

                    if ((timestamp + timelapse) < Calendar.getInstance().getTimeInMillis()) {
                        if (context instanceof OnNotificacionInteraction) {
                            ((OnNotificacionInteraction) context).notificationReminder(
                                    getString(R.string.Cabecera_notificacion_aviso),
                                    getString(R.string.Cuerpo_notificacion_aviso)
                            );
                        }else{
                            Toast.makeText(context, "La libreria notificaciones requiere que la actividad donde se ocupe implemente OnNotificacionInteraction", Toast.LENGTH_SHORT).show();
                        }
                        PreferenceManager.putNotificationTimestamp(context);
                    }
                    handler.postDelayed(this, 10000);

                }
            };
        }

        handler.post(runnableCode);

    }

    @Override
    public void onResume() {
        super.onResume();
        initNotificationsReminder();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnableCode);
    }

}
