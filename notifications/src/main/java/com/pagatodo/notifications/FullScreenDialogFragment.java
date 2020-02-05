package com.pagatodo.notifications;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenDialogFragment extends DialogFragment {

    private String url;

    private static final String NOTIFICATION_URL = "notification_url_dialog_fullscreen";

    public static FullScreenDialogFragment newInstance(final String url){
        final Bundle args = new Bundle();
        args.putString(NOTIFICATION_URL, url);

        final FullScreenDialogFragment fullScreenDialogFragment = new FullScreenDialogFragment();
        fullScreenDialogFragment.setArguments(args);
        return fullScreenDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        final Dialog dialog = getDialog();
        if (dialog != null)
        {
            final int width = ViewGroup.LayoutParams.MATCH_PARENT;
            final int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();

        if (args != null){
            url = args.getString(NOTIFICATION_URL);
        }

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_notification_full_screen_image, container, false);
        final ImageView imageView = view.findViewById(R.id.full_screen_image);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                dismiss();
                return true;
            }
        });

        if (url != null){
            showImage(imageView, url);
        }

        getDialog().setCanceledOnTouchOutside(true);

        return view;
    }

    private void showImage(final ImageView imageView, final String url){
        Picasso.with(imageView.getContext()).load(url)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() { //para SonarLint
                    }

                    @Override
                    public void onError() {
                        dismiss();
                    }
                });
    }
}
