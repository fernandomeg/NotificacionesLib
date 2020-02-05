package com.pagatodo.notifications;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class AbstractDialogFragment extends DialogFragment {

    public static Handler handler;
    public static Runnable runnableCode;

    public static String applicationId;
    public static String tpv;

    public static void initIdentity(String applicationId, String tpv) {
        AbstractDialogFragment.applicationId = applicationId;
        AbstractDialogFragment.tpv = tpv;
    }

    public void cargarFragment(FragmentManager fragmentManager, final Fragment fragment, final @IdRes int containerId) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public void cargarFragmentConReturn(FragmentManager fragmentManager, final Fragment fragment, final @IdRes int containerId){
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public boolean isLandscape() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
