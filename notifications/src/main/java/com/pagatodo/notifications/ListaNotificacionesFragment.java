package com.pagatodo.notifications;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pagatodo.notifications.databinding.FragmentLibNotificacionListBinding;

public class ListaNotificacionesFragment extends AbstractDialogFragment {

    private AdaptadorNotificaciones adaptador;
    private FragmentLibNotificacionListBinding binding;
    private int numNotificacionesFirestore;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        initUI(inflater,container);

        return binding.getRoot();
    }

    private void initUI(final LayoutInflater inflater, final ViewGroup container) {

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lib_notificacion_list, container, false);


        binding.rvNotificaciones.setLayoutManager(layoutManager);
        adaptador = new AdaptadorNotificaciones(getActivity(), new AdaptadorNotificaciones.NotificacionItemListener() {
            @Override
            public void onNotificacionSelected(final Notificacion notificacion, final String notiLeida) {
                changeStatusNotification(notificacion,getString(
                        R.string.firestore_notificacion,
                        applicationId,
                        tpv));
                if (getParentFragment() instanceof NotificacionesDialogFragment) {
                    ((NotificacionesDialogFragment) getParentFragment()).seleccionaNotificacion(notificacion);
                }
            }
        });
        binding.rvNotificaciones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                PreferenceManager.putNotificationTimestamp(getActivity());
                return false;
            }
        });



        numNotificacionesFirestore=0;

        initNotificacionListener(getString(
                R.string.firestore_notificacion,
                applicationId,
                "all"));
        initNotificacionListener(getString(
                R.string.firestore_notificacion,
                applicationId,
                tpv));
        binding.rvNotificaciones.setAdapter(adaptador);
    }

    private void changeStatusNotification(Notificacion notificacion, String path){
        final FirebaseFirestore databasefb = FirebaseFirestore.getInstance();

        //Obtener el documento que se va a modificar y cambiar el estatus a leido
        DocumentReference notificationReference = databasefb.collection(path).document(notificacion.getId());
        notificationReference.update("leida",true);
    }

    private void initNotificacionListener(final String path) {//NOSONAR complejo
        final FirebaseFirestore databasefb = FirebaseFirestore.getInstance();
        final Query query = databasefb.collection(path);
        //final Query query = databasefb.collection("/notification/com.pagatodo.yawallet/CO/00002568/Mensajes/");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final @Nullable QuerySnapshot snapshots,
                                final @Nullable FirebaseFirestoreException firestoreException) {
                binding.pbNotificaciones.setVisibility(View.GONE);
                if (firestoreException != null) {
                    return;
                }
                numNotificacionesFirestore += snapshots.getDocuments().size();

                mostrarAvisoSinNotificaciones(numNotificacionesFirestore);
                if (!snapshots.getDocumentChanges().isEmpty()) {
                    Notificacion notificacion;
                    for (final DocumentChange documentChange : snapshots.getDocumentChanges()) {
                        notificacion = parseNotificacion(documentChange);
                        final DocumentChange.Type type = documentChange.getType();
                        if (type == DocumentChange.Type.ADDED) {
                            adaptador.add(notificacion);
                        }else if (type == DocumentChange.Type.MODIFIED) {
                            adaptador.update(notificacion);
                            if (getParentFragment() instanceof NotificacionesDialogFragment) {
                                ((NotificacionesDialogFragment) getParentFragment()).actualizarDetalleNotificacion(notificacion);
                            }
                        }else if (type == DocumentChange.Type.REMOVED) {
                            adaptador.remove(notificacion);
                        }
                    }
                }
            }

            private Notificacion parseNotificacion(final DocumentChange documentChange){
                final Notificacion notificacion = documentChange.getDocument().toObject(Notificacion.class);
                notificacion.setId(documentChange.getDocument().getId());
                return notificacion;
            }

            private void mostrarAvisoSinNotificaciones(final int numNotificacionesFirestore){
                if (numNotificacionesFirestore==0){
                    binding.tvAvisoNotificaciones.setVisibility(View.VISIBLE);
                    binding.pbNotificaciones.setVisibility(View.GONE);
                } else {
                    binding.tvAvisoNotificaciones.setVisibility(View.GONE);
                }
            }
        });

    }

}
