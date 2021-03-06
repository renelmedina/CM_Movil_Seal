package com.recamedi.comunicaciondispersa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Configuraciones extends AppCompatActivity {

    EditText etRutaAppWeb;
    EditText etC_TiempoConexion;
    EditText etC_TiempoLectura;
    //String prUsuario;//preferencias usuario
    //String prPassword;//preferencias password
    ArrayAdapter<CharSequence> aaModo;
    Spinner spConfiguraciones_Modo;
    boolean configuracionesGuardadas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);
        etRutaAppWeb=(EditText)findViewById(R.id.etRutaAppWeb);
        etC_TiempoConexion=(EditText)findViewById(R.id.etC_TiempoConexion);
        etC_TiempoLectura=(EditText)findViewById(R.id.etC_TiempoLectura);

        spConfiguraciones_Modo=(Spinner)findViewById(R.id.spConfiguraciones_Modo);
        aaModo = ArrayAdapter.createFromResource(this, R.array.saModoInicio, android.R.layout.simple_spinner_item);
        aaModo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConfiguraciones_Modo.setAdapter(aaModo);

        Button btnGuardarConfiguraciones=(Button)findViewById(R.id.btnGuardarConfiguraciones);
        btnGuardarConfiguraciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPreferencias();
            }
        });

    }
    // al cerrar la aplicación, guardamos preferencias
    @Override
    public void onDestroy(){
        super.onDestroy();
        guardarPreferencias();
    }
    // al abrir la aplicación, cargamos preferencias
    @Override
    protected void onStart() {
        super.onStart();
        cargarPreferencias();
// para ver el funcionamiento, imprimimos preferencias si existen
        String mensaje = "";
        if (this.configuracionesGuardadas) {
            mensaje = "Las preferencias fueron guardadas ya";
        } else {
            mensaje = "Las preferencias todavia no se guardaron";
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
    //guardar configuración aplicación Android usando SharedPreferences
    public void guardarPreferencias(){
        int indiceEstado=spConfiguraciones_Modo.getSelectedItemPosition();
        String ModoInicio="";
        switch (indiceEstado){
            case 0:
                ModoInicio="1";//1. Clasico
                break;
            case 1:
                ModoInicio="2";//1. Modo Volanteo
                break;
        }
        SharedPreferences prefs = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("preferenciasGuardadas", true);
        editor.putString("rutawebapp", etRutaAppWeb.getText().toString());
        editor.putInt("TiempoConexion",Integer.parseInt(etC_TiempoConexion.getText().toString()));
        editor.putInt("TiempoLectura",Integer.parseInt(etC_TiempoLectura.getText().toString()));
        editor.putInt("InicioModo",Integer.parseInt(ModoInicio));

        //editor.putString("password", etPassword.getText().toString());
        editor.commit();
        Toast.makeText(this, "Guardando Configuraciones", Toast.LENGTH_SHORT).show();
        /*Registrando en la clase global*/
        Generalidades gen= (Generalidades)this.getApplication();
        gen.setCadena(prefs.getString("rutawebapp","http://accsac.com/sistemas/seal/comunicaciondispersa/"));
        gen.setTiempoConexion(prefs.getInt("TiempoConexion",10000));
        gen.setTiempoLectura(prefs.getInt("TiempoLectura",10000));
    }

    //cargar configuración aplicación Android usando SharedPreferences
    public void cargarPreferencias(){
        SharedPreferences prefs = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        //this.prUsuario = prefs.getString("usuario", "valor por defecto");
        //this.prPassword = prefs.getString("password", "valor por defecto");
        this.configuracionesGuardadas = prefs.getBoolean("preferenciasGuardadas", true);

        etRutaAppWeb.setText(prefs.getString("rutawebapp","http://accsac.com/sistemas/seal/comunicaciondispersa/"));
        etC_TiempoConexion.setText(""+prefs.getInt("TiempoConexion",10000));
        etC_TiempoLectura.setText(""+prefs.getInt("TiempoLectura",10000));

        spConfiguraciones_Modo.setSelection((prefs.getInt("InicioModo",1))-1);
        //etPassword.setText(this.prPassword);
        //chkGuardasCredenciales.setChecked(true);

    }
}
