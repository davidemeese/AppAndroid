package com.dam.tfg.interfaces;

import com.dam.tfg.model.InfraccionData;

import java.util.List;

public interface InfraccionCallback {
    void onInfraccionesObtenidas(List<InfraccionData> infracciones);
    void onError(String mensajeError);
}
