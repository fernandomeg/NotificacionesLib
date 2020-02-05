package com.pagatodo.notifications;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pagatodo.notifications.databinding.FragmentLibDialogNotificacionesBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesDialogFragment extends AbstractDialogFragment {

    //----------UI-------------------------------------------------------
    private FragmentLibDialogNotificacionesBinding binding;
    private NotificacionIconFragment notificacionIconFragment;
    private int numberNotification=0;
    private int numberAfiliaciones=0;
    private ArrayList<Notificacion> listaMensajes;
    //----- Inst ----------------------------------------------------------

    //----- Var ----------------------------------------------------------
    int detailId;
    private boolean isLandScape;

    public void loadFragmentLista() {
        binding.configMenuDetail.setVisibility(isLandScape ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLandScape = isLandscape();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        initUI(inflater, container);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lib_dialog_notificaciones, container, false);
        detailId = isLandScape ? R.id.config_menu_detail : R.id.config_menu_items;
        binding.configMenuDetail.setVisibility(isLandScape ? View.VISIBLE : View.GONE);

        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tablayout.setupWithViewPager(binding.viewPager);

        binding.tablayout.getTabAt(0).setCustomView(R.layout.layout_custom_tab_badge);
        TextView txt1 = (TextView) binding.tablayout.getTabAt(0).getCustomView().findViewById(R.id.tab_text);
        txt1.setText("Notificaciones");

        binding.tablayout.getTabAt(1).setCustomView(R.layout.layout_custom_tab_badge);
        TextView txt3 = (TextView)binding.tablayout.getTabAt(1).getCustomView().findViewById(R.id.tab_text);
        txt3.setText("Enrolamiento");


        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        changeStatusNotification(listaMensajes, getString(
                                R.string.firestore_mensajes,
                                applicationId,
                                tpv));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        initMessagesModules();

        binding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                PreferenceManager.putNotificationTimestamp(getActivity());
                return false;
            }
        });

        binding.ivCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dismiss();
            }
        });
    }


    private void changeStatusNotification(ArrayList<Notificacion> listaMensajes, String path){
        final FirebaseFirestore databasefb = FirebaseFirestore.getInstance();
        DocumentReference notificationReference;
        for(Notificacion n : listaMensajes) {
            //Obtener el documento que se va a modificar y cambiar el estatus a leido
            if(!n.isLeida()) {
                notificationReference = databasefb.collection(path).document(n.getId());
                notificationReference.update("leida", true);
            }
        }
    }

    private void loadNumbersBadge(){
        TextView txt2 = (TextView) binding.tablayout.getTabAt(0).getCustomView().findViewById(R.id.tab_badge);
        TextView txt4 = (TextView) binding.tablayout.getTabAt(1).getCustomView().findViewById(R.id.tab_badge);

        if (numberNotification > 0){
            txt2.setVisibility(View.VISIBLE);
            txt2.setText(String.valueOf(numberNotification));
        }else{
            txt2.setVisibility(View.GONE);
            txt2.setText(String.valueOf(numberNotification));
        }

        if(numberAfiliaciones>0) {
            txt4.setVisibility(View.VISIBLE);
            txt4.setText(String.valueOf(numberAfiliaciones));
        }else{
            txt4.setVisibility(View.GONE);
            txt4.setText(String.valueOf(numberAfiliaciones));
        }

    }

    private void initMessagesModules(){

        final FirebaseFirestore databasefb = FirebaseFirestore.getInstance();

        final Query queryMensajes = databasefb.collection(getString(
                R.string.firestore_mensajes,
                applicationId,
                tpv));
        final Query queryInbox = databasefb.collection(getString(
                R.string.firestore_notificacion,
                applicationId,
                tpv));

        queryMensajes.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final @Nullable QuerySnapshot snapshots,
                                final @Nullable FirebaseFirestoreException firestoreException) {
                numberAfiliaciones=0;
                listaMensajes = null;
                listaMensajes =new ArrayList<Notificacion>();

                if(snapshots.getDocuments()!=null && !snapshots.getDocuments().isEmpty()){
                    Notificacion notificacion;

                    for(final DocumentSnapshot documentSnapshot : snapshots.getDocuments()){
                        notificacion = parseNotificacion(documentSnapshot);
                        if(!notificacion.isLeida()){
                            numberAfiliaciones += 1;
                        }
                        listaMensajes.add(notificacion);
                    }
                }
                loadNumbersBadge();
            }
            private Notificacion parseNotificacion(final DocumentSnapshot documentSnapshot){
                final Notificacion notificacion = documentSnapshot.toObject(Notificacion.class);
                notificacion.setId(documentSnapshot.getId());
                return notificacion;
            }
        });

        queryInbox.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final @Nullable QuerySnapshot snapshots,
                                final @Nullable FirebaseFirestoreException firestoreException) {

                numberNotification = 0;

                if(snapshots.getDocuments()!=null && !snapshots.getDocuments().isEmpty()){
                    Notificacion notificacion;

                    for(final DocumentSnapshot documentSnapshot : snapshots.getDocuments()){
                        notificacion = parseNotificacion(documentSnapshot);
                        if(!notificacion.isLeida()){
                            numberNotification += 1;
                        }
                    }
                }
                loadNumbersBadge();
            }
            private Notificacion parseNotificacion(final DocumentSnapshot documentSnapshot){
                final Notificacion notificacion = documentSnapshot.toObject(Notificacion.class);
                notificacion.setId(documentSnapshot.getId());
                return notificacion;
            }
        });
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onStart() {
        super.onStart();
        final Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            binding.getRoot().setLayoutParams(params);
        }
    }

    public void setNotificacionIconFragment(final NotificacionIconFragment notificacionIconFragment) {
        this.notificacionIconFragment = notificacionIconFragment;
    }

    public void seleccionaNotificacion(final Notificacion notificacion) {
        binding.configMenuDetail.setVisibility(View.VISIBLE);
        cargarFragment(getChildFragmentManager(), NotificacionDetalleFragment.newInstance(notificacion), detailId);
    }

    public void actualizarDetalleNotificacion(final Notificacion notificacion) {
        if (binding.configMenuDetail.getVisibility() == View.VISIBLE) {
            final Fragment fragment = getChildFragmentManager().findFragmentByTag(NotificacionDetalleFragment.class.getSimpleName());
            if (fragment != null
                    && fragment.isVisible()
                    && ((NotificacionDetalleFragment) fragment).getNotificationId().equals(notificacion.getId())) {
                cargarFragment(getChildFragmentManager(), NotificacionDetalleFragment.newInstance(notificacion), detailId);
            }
        }
    }
}
