package com.recamedi.comunicaciondispersa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MenuVisual extends AppCompatActivity {
    TextView tvEstados_MenuVisual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_visual);
        tvEstados_MenuVisual=(TextView)findViewById(R.id.tvEstados_MenuVisual);

        Generalidades gen= (Generalidades)this.getApplication();
        tvEstados_MenuVisual.setText(gen.getCadena());
        //Generalidades.class.get


        /*ClaseGlobal global=(ClaseGlobal) getActivity().getApplicationContext();
        global.ponPaciente(paciente);*/
    }
}
