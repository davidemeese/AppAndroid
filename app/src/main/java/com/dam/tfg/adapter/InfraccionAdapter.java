package com.dam.tfg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dam.tfg.R;
import com.dam.tfg.model.InfraccionData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InfraccionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<InfraccionData> infracciones;

    private static final int HEADER_VIEW = 0;
    private static final int ITEM_VIEW = 1;

    public InfraccionAdapter(List<InfraccionData> infracciones) {
        this.infracciones = infracciones;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_encabezado, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_infraccion, parent, false);
            return new InfraccionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InfraccionViewHolder) {
            InfraccionViewHolder viewHolder = (InfraccionViewHolder) holder;
            InfraccionData infraccion = infracciones.get(position - 1);
            viewHolder.textFecha.setText(infraccion.getFecha());
            String lat = infraccion.getLatitud();
            String lon = infraccion.getLongitud();
            if (lat.length() > 7){
                lat = lat.substring(0,7);
            }
            if (lon.length() > 7){
                lon = lon.substring(0,7);
            }
            viewHolder.textPosicion.setText(lat + ", " + lon);
            viewHolder.textVelocidad.setText(String.valueOf(infraccion.getVelocidad()));
            viewHolder.textVelocidadMaxima.setText(String.valueOf(infraccion.getVelocidadMaxima()));
        }
    }

    @Override
    public int getItemCount() {
        return infracciones.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_VIEW;
        } else {
            return ITEM_VIEW;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textFecha, textPosicion, textVelocidad, textVelocidadMaxima;

        public HeaderViewHolder(@NotNull View itemView) {
            super(itemView);
            textFecha = itemView.findViewById(R.id.textFecha);
            textPosicion = itemView.findViewById(R.id.textPosicion);
            textVelocidad = itemView.findViewById(R.id.textVelocidad);
            textVelocidadMaxima = itemView.findViewById(R.id.textVelocidadMaxima);
        }
    }

    public static class InfraccionViewHolder extends RecyclerView.ViewHolder {
        TextView textFecha, textPosicion, textVelocidad, textVelocidadMaxima;

        public InfraccionViewHolder(@NotNull View itemView) {
            super(itemView);
            textFecha = itemView.findViewById(R.id.textFecha);
            textPosicion = itemView.findViewById(R.id.textPosicion);
            textVelocidad = itemView.findViewById(R.id.textVelocidad);
            textVelocidadMaxima = itemView.findViewById(R.id.textVelocidadMaxima);
        }
    }
}


