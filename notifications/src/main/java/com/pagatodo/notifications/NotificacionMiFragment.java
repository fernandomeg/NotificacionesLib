package com.pagatodo.notifications;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodo.notifications.databinding.FragmentLibNotificationMiBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificacionMiFragment extends AbstractDialogFragment {

    private FragmentLibNotificationMiBinding binding;
    private ListaNotficacionesMiFragment fragmentLista;

    public NotificacionMiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lib_notification_mi, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentLista = new ListaNotficacionesMiFragment();
        cargarFragment(getChildFragmentManager(), fragmentLista, binding.messagesContainer.getId());
    }

    public void seleccionaNotificacion(final Notificacion notificacion) {
        binding.tituloMensaje.setText(notificacion.getTitulo());
        cargarFragment(getChildFragmentManager(), NotificacionDetalleMiFragment.newInstance(notificacion), binding.messagesContainer.getId());
    }

    public void actualizarDetalleNotificacion(final Notificacion notificacion) {
        binding.tituloMensaje.setText(notificacion.getTitulo());
        final Fragment fragment = getChildFragmentManager().findFragmentByTag(NotificacionDetalleMiFragment.class.getSimpleName());
        if (fragment != null
                && fragment.isVisible()
                && ((NotificacionDetalleMiFragment) fragment).getNotificationId().equals(notificacion.getId())) {
            cargarFragment(getChildFragmentManager(), NotificacionDetalleMiFragment.newInstance(notificacion), binding.messagesContainer.getId());
        }
    }

    public void loadFragmentLista() {
        binding.tituloMensaje.setText(getString(R.string.titulo_mensajes_mi));
        cargarFragment(getChildFragmentManager(), fragmentLista, binding.messagesContainer.getId());
    }
}
