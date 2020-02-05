package com.pagatodo.notifications;


import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;

public class Notificacion implements Serializable{
    public static final String LEIDA    = "LEIDA";
    public static final String NO_LEIDA = "NO_LEIDA";

    private String idNotificacion;
    private String fechaEnvio;
    private String titulo;
    private String mensaje;
    private String imagen;
    private String enlace;
    private String estatus = NO_LEIDA;
    private boolean leida;
    private String campo1;

    public Notificacion(){
        this.idNotificacion = "";
        this.fechaEnvio = "";
        this.titulo = "";
        this.mensaje = "";
        this.enlace = "";
        this.imagen = "";
        this.campo1 = "";
    }

    public String getId() {
        return idNotificacion;
    }

    public void setId(final String idNotif) {
        this.idNotificacion = idNotif;
    }


    public DateTime getFecha2(){
        return   ISODateTimeFormat.dateTimeParser().parseDateTime(fechaEnvio);

    }
    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(final String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(final String mensaje) {
        this.mensaje = mensaje;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(final String imagen) {
        this.imagen = imagen;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(final String enlace) {
        this.enlace = enlace;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(final String estatus) {
        this.estatus = estatus;
    }

    public String getCampo1() {
        return campo1;
    }

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    @Override
    public int hashCode() {//NOPMD
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof Notificacion){
            final Notificacion notificacion = (Notificacion) obj;
            return idNotificacion.equals(notificacion.idNotificacion);
        }else{
            return false;
        }
    }

}
