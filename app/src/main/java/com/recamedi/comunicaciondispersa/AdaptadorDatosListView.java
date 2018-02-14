package com.recamedi.comunicaciondispersa;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Renel01 on 30/01/2018.
 */

public class AdaptadorDatosListView extends BaseAdapter {
    Context contexto;
    List<DatosListview> ListaObjetos;

    public AdaptadorDatosListView(Context contexto, List<DatosListview> listaObjetos) {
        this.contexto = contexto;
        ListaObjetos = listaObjetos;
    }

    @Override
    public int getCount() {
        return ListaObjetos.size();//retorna la cantidad de elementos de la lista
    }

    @Override
    public Object getItem(int position) {
        return ListaObjetos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ListaObjetos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista=convertView;
        LayoutInflater inflate=LayoutInflater.from(contexto);
        vista=inflate.inflate(R.layout.itemslistview,null);

        ImageView imagen=(ImageView)vista.findViewById(R.id.ivFoto);
        TextView tvTitulo=(TextView)vista.findViewById(R.id.tvTitulo);
        TextView tvDescripcion=(TextView)vista.findViewById(R.id.tvDescripcion);

        tvTitulo.setText(ListaObjetos.get(position).getTitulo().toString());
        tvDescripcion.setText(ListaObjetos.get(position).getDetalle().toString());
        imagen.setImageResource(ListaObjetos.get(position).getImagen());


        return vista;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
