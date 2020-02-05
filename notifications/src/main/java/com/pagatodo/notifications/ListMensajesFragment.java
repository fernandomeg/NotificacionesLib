package com.pagatodo.notifications;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pagatodo.notifications.databinding.FragmentListMensajesBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListMensajesFragment extends AbstractDialogFragment {

    private FragmentListMensajesBinding binding;
    private int numNotificacionesFirestore;
    private AdaptadorMensajes adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initUI(inflater,container);
        return binding.getRoot();
    }

    private void initUI(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_mensajes, container, false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL,false));
        adaptador = new AdaptadorMensajes();
        numNotificacionesFirestore = 0;

        initNotificacionListener(getString(
                R.string.firestore_mensajes,
                applicationId,
                tpv));
        binding.recycler.setAdapter(adaptador);
    }

    private void initNotificacionListener(final String path) {//NOSONAR complejo
        final FirebaseFirestore databasefb = FirebaseFirestore.getInstance();
        final Query query = databasefb.collection(path);
        //final Query query = databasefb.collection("/notification/com.pagatodo.yawallet/CO/00002568/Inbox/");


        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final @Nullable QuerySnapshot snapshots,
                                final @Nullable FirebaseFirestoreException firestoreException) {
                binding.loadingBar.setVisibility(View.GONE);
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
                    binding.loadingBar.setVisibility(View.GONE);
                } else {
                    binding.tvAvisoNotificaciones.setVisibility(View.GONE);
                }
            }
        });

    }

}
