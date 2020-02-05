package com.pagatodo.notifications;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.Arrays;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private List<AbstractDialogFragment> fragmentList = Arrays.asList(new ListaNotificacionesFragment(), new ListMensajesFragment());
    private List<String> stringList = Arrays.asList("Notificaciones", "Afiliaciones");

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }
}
