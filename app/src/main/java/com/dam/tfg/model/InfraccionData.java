package com.dam.tfg.model;

public class InfraccionData {
    private String fecha;
    private String longitud;
    private String latitud;
    private String velocidad;
    private String velocidadMaxima;


    public InfraccionData(String fecha, String longitud, String latitud, String velocidad, String velocidadMaxima) {
        this.fecha = fecha;
        this.longitud = longitud;
        this.latitud = latitud;
        this.velocidad = velocidad;
        this.velocidadMaxima = velocidadMaxima;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(String velocidad) {
        this.velocidad = velocidad;
    }

    public String getVelocidadMaxima() {
        return velocidadMaxima;
    }

    public void setVelocidadMaxima(String velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
    }
}
