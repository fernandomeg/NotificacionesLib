package com.pagatodo.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodo.notifications.databinding.LibNotificacionItemBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class AdaptadorNotificaciones extends RecyclerView.Adapter<AdaptadorNotificaciones.NotificacionesViewHolder> {

    private final List<Notificacion> notificaciones = new ArrayList<>();
    private final NotificacionItemListener notificacionItemListener;
    private final Set<String> notificacionLeidaSet;
    public final DateFormat notiDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private final DateFormat notiDateFormatmili = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    private static final Comparator DATE_COMPARATOR = new Comparator<Notificacion>() {
        @Override
        public int compare(final Notificacion notificacion, final Notificacion noti) {
            return noti.getFechaEnvio().compareTo(notificacion.getFechaEnvio());
        }
    };

    public AdaptadorNotificaciones(Context context, final NotificacionItemListener notificacionItemListener) {
        this.notificacionItemListener = notificacionItemListener;
        this.notificacionLeidaSet = PreferenceManager.getNotificaciones(context);
    }

    private void addNotificacionId(Context context, final String notificacionId) {
        PreferenceManager.addNotificaciones(notificacionId, context);
        notificacionLeidaSet.add(notificacionId);
    }

    @Override
    public NotificacionesViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LibNotificacionItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.lib_notificacion_item, parent, false);
        return new NotificacionesViewHolder(binding);
    }

    public void add(final Notificacion notificacion) {
        if (!notificaciones.contains(notificacion)) {
            notificaciones.add(notificacion);
            sortNotificaciones();
            notifyItemChanged(notificaciones.indexOf(notificacion));
            notifyDataSetChanged();
        }
    }

    public void update(final Notificacion notificacion) {
        if (notificaciones.contains(notificacion)) {
            final int itemIndex = notificaciones.indexOf(notificacion);
            notificaciones.set(itemIndex, notificacion);
            notifyItemChanged(itemIndex);
        }
    }

    public void remove(final Notificacion notificacion) {
        if (notificaciones.contains(notificacion)) {
            final int itemIndex = notificaciones.indexOf(notificacion);
            notificaciones.remove(itemIndex);
            notifyItemRemoved(itemIndex);
            notifyDataSetChanged();
        }
    }

    private void sortNotificaciones() {
        Collections.sort(notificaciones, DATE_COMPARATOR);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final NotificacionesViewHolder holder, final int position) {

        final Notificacion notificacion = notificaciones.get(position);

        holder.binding.rlNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                notificacionItemListener.onNotificacionSelected(notificacion, notificacion.getId());
                addNotificacionId(holder.binding.getRoot().getContext(), notificacion.getId());
                //notifyItemChanged(position);
            }
        });

        holder.binding.tvListaTituloNotificacion.setText(notificacion.getTitulo());
        holder.binding.tvListaMensajeNotificacion.setText(notificacion.getMensaje());

        if (notificacion.isLeida()) {
            holder.binding.dNotification.setImageResource(R.drawable.icono_sistema_sistema);
//            holder.binding.ivNotificacion.setVisibility(View.VISIBLE);
            holder.binding.tvListaTituloNotificacion.setTextColor(Color.GRAY);
            holder.binding.tvListaMensajeNotificacion.setTextColor(Color.GRAY);
            holder.binding.rlNotificacion.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.binding.cardview.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.binding.cardview.setAlpha(0.5f);
        } else {
            holder.binding.dNotification.setImageResource(R.drawable.icono_sistema);
//            holder.binding.ivNotificacion.setVisibility(View.GONE);
            holder.binding.tvListaTituloNotificacion.setTextColor(Color.parseColor("#3C4C57"));
            holder.binding.tvListaMensajeNotificacion.setTextColor(Color.parseColor("#00C2E2"));
            holder.binding.rlNotificacion.setBackgroundColor(Color.WHITE);
            holder.binding.cardview.setBackgroundColor(Color.WHITE);
            holder.binding.cardview.setAlpha(1f);
        }
    }

    public Date parseDate(final String fecha) {
        try {

            notiDateFormatmili.setTimeZone(TimeZone.getTimeZone("UTC"));

            return notiDateFormatmili.parse(fecha);
        } catch (final ParseException exc) {

            return new Date();
        }
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    class NotificacionesViewHolder extends RecyclerView.ViewHolder {
        LibNotificacionItemBinding binding;

        NotificacionesViewHolder(final LibNotificacionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface NotificacionItemListener {
        void onNotificacionSelected(Notificacion notificacion, String idLeida);
    }
}
