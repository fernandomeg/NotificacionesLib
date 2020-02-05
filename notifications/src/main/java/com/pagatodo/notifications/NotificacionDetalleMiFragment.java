package com.pagatodo.notifications;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.pagatodo.notifications.databinding.FragmentLibNotificacionDetalleMiBinding;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificacionDetalleMiFragment extends AbstractDialogFragment {

    private static final String NOTIFICATION_KEY= "notification_key";
    private Notificacion notificacion;
    private SimpleExoPlayer simpleExoPlayer;
    private FragmentLibNotificacionDetalleMiBinding binding;

    public NotificacionDetalleMiFragment() {
        // Required empty public constructor
    }

    public static NotificacionDetalleMiFragment newInstance(final Notificacion notificacion){
        final Bundle args = new Bundle();
        args.putSerializable(NOTIFICATION_KEY, notificacion);
        final NotificacionDetalleMiFragment notificacionDetalleFragment = new NotificacionDetalleMiFragment();
        notificacionDetalleFragment.setArguments(args);
        return notificacionDetalleFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args!=null) {
            notificacion = (Notificacion) args.getSerializable(NOTIFICATION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lib_notificacion_detalle_mi, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvMensaje.setText(notificacion.getMensaje());
        binding.svContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                PreferenceManager.putNotificationTimestamp(getActivity());
                return false;
            }
        });
        binding.btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPlayer();
                ((NotificacionMiFragment) getParentFragment()).loadFragmentLista();
            }
        });
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

    private boolean hasVideo(){
        return notificacion.getEnlace()!=null && !notificacion.getEnlace().isEmpty();
    }

    private boolean hasImage(){
        return notificacion.getImagen()!=null && !notificacion.getImagen().isEmpty();
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
        if (Util.SDK_INT <= 23 ) {
            initPlayer();
        }
        if(hasImage()){
            loadImage(binding.ivImagen, notificacion.getImagen());
        }else{
            binding.ivImagen.setVisibility(View.GONE);
            binding.pbNotificacionImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 ) {
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
