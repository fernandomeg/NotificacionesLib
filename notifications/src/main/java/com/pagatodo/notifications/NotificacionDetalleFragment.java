package com.pagatodo.notifications;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pagatodo.notifications.databinding.FragmentLibNotificacionDetalleBinding;
import com.squareup.picasso.Picasso;

public class NotificacionDetalleFragment extends Fragment {//GOD CLASS

    private static final String NOTIFICATION_KEY = "notification_key";
    private Notificacion notificacion;
    private SimpleExoPlayer simpleExoPlayer;
    private FragmentLibNotificacionDetalleBinding binding;

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public NotificacionDetalleFragment() {
        // Required empty public constructor
    }

    public static NotificacionDetalleFragment newInstance(final Notificacion notificacion) {
        final Bundle args = new Bundle();
        args.putSerializable(NOTIFICATION_KEY, notificacion);

        final NotificacionDetalleFragment notificacionDetalleFragment = new NotificacionDetalleFragment();
        notificacionDetalleFragment.setArguments(args);
        return notificacionDetalleFragment;
    }

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            notificacion = (Notificacion) args.getSerializable(NOTIFICATION_KEY);
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        ImageView imageClose;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lib_notificacion_detalle, container, false);
        binding.tvTitulo.setText(notificacion.getTitulo());
        binding.tvMensaje.setText(notificacion.getMensaje());
        imageClose = binding.ivCerrar;
        imageClose.setClickable(true);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                returnToNotificationsList();
            }
        });
        binding.svContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                PreferenceManager.putNotificationTimestamp(getActivity());
                return false;
            }
        });

        binding.getRoot().setFocusableInTouchMode(true);
        binding.getRoot().requestFocus();
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if( keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP)
                {
                    returnToNotificationsList();
                    return true;
                }
                return false;
            }
        });

        return binding.getRoot();
    }



    public void returnToNotificationsList() {
        if (getParentFragment() instanceof NotificacionesDialogFragment) {
            binding.pvPlayer.setVisibility(View.GONE);
            clearPlayer();
            ocultarNotificacionDetalle();
            ((NotificacionesDialogFragment) getParentFragment()).loadFragmentLista();
        }
    }

    private void ocultarNotificacionDetalle() {
        binding.cvNotificacionDetalle.setVisibility(View.GONE);
    }

    private void initPlayer() {
        if (hasVideo()) {
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), new DefaultTrackSelector());
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.prepare(buildMediaSource(notificacion.getEnlace()));
            binding.pvPlayer.setPlayer(simpleExoPlayer);
            simpleExoPlayer.addListener(new BakingEventListener());
        } else {
            binding.pvPlayer.setVisibility(View.GONE);
            binding.pbNotificacionVideo.setVisibility(View.GONE);
        }
    }

    private void clearPlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    private ExtractorMediaSource buildMediaSource(final String url) {

        final DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(
                        getActivity(),
                        Util.getUserAgent(getActivity(), "exo_promo"));

        return new ExtractorMediaSource.Factory(dataSourceFactory).
                createMediaSource(Uri.parse(url));
    }

    private void loadImage(final ImageView imageView, final String url) {
        binding.pbNotificacionImage.setVisibility(View.VISIBLE);
        Picasso.with(imageView.getContext()).load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        binding.pbNotificacionImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        binding.pbNotificacionImage.setVisibility(View.GONE);
                    }
                });
        setImageNotificationListener(imageView, url);
    }

    private void setImageNotificationListener(final ImageView imageView, final String url) {
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                FullScreenDialogFragment.newInstance(url).show(getFragmentManager(), "fullscreenimage");
                return false;
            }
        });
    }

    private boolean hasVideo() {
        return notificacion.getEnlace() != null && !notificacion.getEnlace().isEmpty();
    }

    private boolean hasImage() {
        return notificacion.getImagen() != null && !notificacion.getImagen().isEmpty();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            clearPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            clearPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) {
            initPlayer();
        }
        if (hasImage()) {
            loadImage(binding.ivImagen, notificacion.getImagen());
        } else {
            binding.ivImagen.setVisibility(View.GONE);
            binding.pbNotificacionImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initPlayer();
        }
    }

    public void onFailure(final Throwable throwable) {
        //NO ACTION
    }

    class BakingEventListener implements Player.EventListener {

        @Override
        public void onTimelineChanged(final Timeline timeline, final Object manifest, final int reason) {

            //NO ACTION
        }

        @Override
        public void onTracksChanged(final TrackGroupArray trackGroups, final TrackSelectionArray trackSelections) {
            //NO ACTION

        }

        @Override
        public void onLoadingChanged(final boolean isLoading) {
            //NO ACTION

        }

        @Override
        public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
            if (playbackState == Player.STATE_READY) {
                binding.pbNotificacionVideo.setVisibility(View.GONE);
            }
        }

        @Override
        public void onRepeatModeChanged(final int repeatMode) {
            //NO ACTION

        }

        @Override
        public void onShuffleModeEnabledChanged(final boolean shuffleModeEnabled) {

            //NO ACTION
        }

        @Override
        public void onPlayerError(final ExoPlaybackException error) {
            //NO ACTION
        }

        @Override
        public void onPositionDiscontinuity(final int reason) {
            //NO ACTION

        }

        @Override
        public void onPlaybackParametersChanged(final PlaybackParameters playbackParameters) {
            //NO ACTION

        }

        @Override
        public void onSeekProcessed() {
            //NO ACTION

        }
    }

    public String getNotificationId() {
        return notificacion != null ? notificacion.getId() : "";
    }
}
