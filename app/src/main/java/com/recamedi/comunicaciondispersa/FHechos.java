package com.recamedi.comunicaciondispersa;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FHechos extends Fragment {
    ListView listadatos;
    ArrayList<DatosListview> Lista;
    List<DatosListview> lstDocumentos=new ArrayList<>();
    DataBaseHelper db;

    public FHechos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fhechos, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listadatos=(ListView)getView().findViewById(R.id.lstDocumentosHechos);
        db= new DataBaseHelper(getView().getContext());
        Lista=new ArrayList<DatosListview>();
        mostrarDatos();

    }
    private void mostrarDatos(){
        lstDocumentos=db.getAllDocumentosHechos();
        /*AdaptadorDatosListView adapter=new AdaptadorDatosListView(DocumentosPendientes.this,lstDocumentos);
        listadatos.setAdapter(adapter);*/
        AdaptadorDatosListView miAdaptador= new AdaptadorDatosListView(getView().getContext(),lstDocumentos);
        listadatos.setAdapter(miAdaptador);
//        listadatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
//                DatosListview objclick=(DatosListview) adapterView.getItemAtPosition(posicion);
//                Intent acDocumentosDetalle=new Intent(getView().getContext(),DetalleDocumento.class);
//                acDocumentosDetalle.putExtra("objeto",(Serializable)objclick);
//                startActivity(acDocumentosDetalle);
//                //finish();//Cierra este Intent(formulario)
//            }
//        });

    }
}
