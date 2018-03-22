package com.recamedi.comunicaciondispersa;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Renel01 on 22/01/2018.
 */

public class LoginClase extends AppCompatActivity {
    //Datos de Session
    ArrayList<String> DatosSessionOnline;

    //Objetos
    TextView tvEstadoSession;
    EditText etUsuario,etPassword;
    CheckBox chkGuardasCredenciales;
    String prUsuario;//preferencias usuario
    String prPassword;//preferencias password
    Button btnIniciarSession;
    ImageView ivConfiguraciones;
    boolean preferenciasGuardadas;


    public String xmlTexto;


    public String NombreCompleto;
    public String CodigoTrabajador;

    private HandleXml obj;

    //Obteniendo Datos
    Generalidades gen;//= (Generalidades)this.getApplication();

    String CodigoPersonal="";


    int ModoInicial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginusuario);
        gen= (Generalidades)this.getApplication();
        /*Cargando Preferencias*/

        //cargarPreferencias();
        //SharedPreferences prefs = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);

        //gen.setCadena(prefs.getString("rutawebapp","recamedi.com"));
        //Toast.makeText(this, gen.getCadena(), Toast.LENGTH_SHORT).show();
        tvEstadoSession=(TextView)findViewById(R.id.tvEstadoSession___);
        etUsuario=(EditText)findViewById(R.id.etUsuario);
        etPassword=(EditText)findViewById(R.id.etPassword);

        ivConfiguraciones=(ImageView)findViewById(R.id.ivConfiguraciones);
        ivConfiguraciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Guardando preferencias", Toast.LENGTH_SHORT).show();
                Intent acConfiguraciones =new Intent(getApplicationContext(),Configuraciones.class);
                startActivity(acConfiguraciones);
                //finish();
            }
        });

        btnIniciarSession=(Button)findViewById(R.id.btnIniciar);
        btnIniciarSession.setOnClickListener(new View.OnClickListener(){




            @Override
            public void onClick(View view) {
                IniciarSession(null);

            }
        });

    }
    // al abrir la aplicación, guardamos preferencias
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
    }
    //guardar configuración aplicación Android usando SharedPreferences
    public void guardarPreferencias(){
        SharedPreferences prefs = getSharedPreferences("preferenciasMiApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("preferenciasGuardadas", true);
        editor.putString("usuario", etUsuario.getText().toString());
        editor.putString("password", etPassword.getText().toString());
        if (!CodigoPersonal.isEmpty()){
            editor.putString("codper", CodigoPersonal);
        }
        editor.commit();
        Toast.makeText(this, "Guardando preferencias", Toast.LENGTH_SHORT).show();
    }

    //cargar configuración aplicación Android usando SharedPreferences
        public void cargarPreferencias(){
        SharedPreferences prefs = getSharedPreferences("preferenciasMiApp", Context.MODE_PRIVATE);
        this.prUsuario = prefs.getString("usuario", "");
        this.prPassword = prefs.getString("password", "");
        this.preferenciasGuardadas = prefs.getBoolean("preferenciasGuardadas", true);

        etUsuario.setText(this.prUsuario);
        etPassword.setText(this.prPassword);

        SharedPreferences prefsGen = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        gen.setCadena(prefsGen.getString("rutawebapp","http://accsac.com/sistemas/seal/comunicaciondispersa/"));
        gen.setTiempoLectura(prefsGen.getInt("TiempoLectura",10000));
        gen.setTiempoConexion(prefsGen.getInt("TiempoConexion",10000));
        ModoInicial=prefsGen.getInt("InicioModo",1);
            //chkGuardasCredenciales.setChecked(true);


    }

    /*Para Grabar Archivos*/
    private boolean existe(String[] archivos, String archbusca) {
        for (int f = 0; f < archivos.length; f++)
            if (archbusca.equals(archivos[f])) {
                //tvEstadoSession.setText(getExternalFilesDir(archivos[f]).toString());
                /*Toast t = Toast.makeText(this, getExternalFilesDir(archivos[f]).toString(),
                        Toast.LENGTH_LONG);*/
                return true;
            }
        return false;
    }



    public void IniciarSession(View vista){
        Generalidades gen= (Generalidades)this.getApplication();
        //Toast.makeText(getApplicationContext(),gen.getCadena(),Toast.LENGTH_SHORT).show();
        tvEstadoSession.setText("Ruta: "+gen.getCadena());
        DataBaseHelper db;
        db= new DataBaseHelper(getApplicationContext());
        Boolean AccesoCorrecto=db.LoginSistemaOffline(""+etUsuario.getText(),""+etPassword.getText());
        /*Intent acMainActivity=new Intent(getApplicationContext(),MainActivity.class);
        acMainActivity.putExtra("acLoginUser",etUsuario.getText().toString());
        acMainActivity.putExtra("acLoginPassword",etPassword.getText().toString());*/

        Intent actprincipal=new Intent(getApplicationContext(),MenuLateralActivity.class);
        Intent actVolanteo=new Intent(getApplicationContext(),VisitaCampoMarcador.class);

        //startActivity(actprincipal);
        if (AccesoCorrecto){
            switch (ModoInicial){
                case 1:
                    startActivity(actprincipal);
                    break;
                case 2:
                    startActivity(actVolanteo);
                    break;
                default:
                    startActivity(actprincipal);
            }
            Intent i = new Intent(getApplicationContext(),GPS_Service.class);
            startService(i);
            finish();
        }else {
            new ConsultarDatos().execute(gen.getCadena()+"webservices/logincelulares.php?usuario="+etUsuario.getText()+"&password="+etPassword.getText());
        }
        gen.setUsuarioActual(etUsuario.getText().toString());
        gen.setPasswordActual(etPassword.getText().toString());
        chkGuardasCredenciales=(CheckBox)findViewById(R.id.chkRecordarCredenciales);
        if (chkGuardasCredenciales.isChecked()){
            guardarPreferencias();
        }


    }
    /*Leer archivos o string del formato XML*/

    public String LeerXml(View view,String XMLtextoParsear){
        XmlPullParserFactory factory;
        String respuestaparaaccion=null;
        try{
            //StringBuilder fis=new StringBuilder();
            factory=XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp=factory.newPullParser();
            //Toast.makeText(getApplicationContext(),"-"+xmlTexto,Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(),":)"+XMLtextoParsear,Toast.LENGTH_SHORT).show();
            xpp.setInput(new StringReader(XMLtextoParsear));
            String text = null;
            int eventType=xpp.getEventType();
            DatosSessionOnline = new ArrayList<String>();
            while (eventType!= XmlPullParser.END_DOCUMENT){
                String name=xpp.getName();

                switch (eventType){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text=xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        /*DatosSessionOnline.add(xpp.getAttributeValue(null,"codigopersonal"));
                        DatosSessionOnline.add(etUsuario.getText().toString());
                        DatosSessionOnline.add(etPassword.getText().toString());
                        DatosSessionOnline.add(xpp.getAttributeValue(null,"nombrepersonal"));
                        SimpleDateFormat dateFormato = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
                        Date date = new Date();
                        String fechaactual = dateFormato.format(date);
                        DatosSessionOnline.add(4,fechaactual)*/
                        if (name.equals("estadologin")){//Nombre del tag del archivo xml
                            //country=text;
                            //codigoestadologin=xpp.getAttributeValue(null,"codigoestado");//<estadologin codigoestado="1" etiquetaestado="Acceso Correcto"/>
                            //textoestadologin=xpp.getAttributeValue(null,"textoestado");//<estadologin codigoestado="1" etiquetaestado="Acceso Correcto"/>
                            respuestaparaaccion=xpp.getAttributeValue(null,"codigoestado");
                            if (respuestaparaaccion.equals("1")){//Significa que todo fue correcto
                                DatosSessionOnline = new ArrayList<String>();
                                DatosSessionOnline.add(etUsuario.getText().toString());
                                DatosSessionOnline.add(etPassword.getText().toString());
                                SimpleDateFormat dateFormato = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
                                Date date = new Date();
                                String fechaactual = dateFormato.format(date);
                                DatosSessionOnline.add(fechaactual);

                            }
                        }else if (name.equals("usuario")){
                            DatosSessionOnline.add(xpp.getAttributeValue(null,"codigopersonal"));

                            DatosSessionOnline.add(xpp.getAttributeValue(null,"nombrepersonal"));
                            //codigopersonallogin=xpp.getAttributeValue(null,"codigopersonal");
                            //nombrepersonal=xpp.getAttributeValue(null,"nombrepersonal");;

                        }else if (name.equals("textorespuesta")){
                            //pressure=xpp.getAttributeValue(null,"value");
                            
                            tvEstadoSession.setText(text);
                        }/*else if(name.equals("temperature")){
                            temperature=myParser.getAttributeValue(null,"value");
                        }*/else{
                            //aun nada
                        }
                        break;
                }
                eventType=xpp.next();
            }
            return respuestaparaaccion;
            //tvEstadoSession.setText(NombreCompleto);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            respuestaparaaccion="-1";
            return respuestaparaaccion;
        } catch (IOException e) {
            e.printStackTrace();
            respuestaparaaccion="-2";
            return respuestaparaaccion;
        }

    }






    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            /*TV_mensaje.setText("ANTES de EMPEZAR la descarga. Hilo PRINCIPAL");
            Log.v(TAG_LOG, "ANTES de EMPEZAR la descarga. Hilo PRINCIPAL");

            miBarraDeProgreso = (ProgressBar) findViewById(R.id.progressBar_indicador);*/
            tvEstadoSession.setText("Iniciando Session...");
            btnIniciarSession.setEnabled(false);
        }
        // Lo que ocurre en segundo plano
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                //tvEstadoSession.setText("Parameros Incorrectos !!!");
                //Toast.makeText(getApplicationContext(),"Error de Conexion:"+e,Toast.LENGTH_SHORT).show();
                xmlTexto=null;
                return "-3";
            }
        }
        // Cuando termino todo el proceso del segundo plano, muestra los resultados
        @Override
        protected void onPostExecute(String result) {
            //tvEstadoSession.setText(result.toString());
            xmlTexto=result;

            //tvEstadoSession.setText("Usuario Aceptado");
            btnIniciarSession.setEnabled(true);
            DataBaseHelper db;
            db= new DataBaseHelper(getApplicationContext());
            Boolean AccesoCorrecto=db.LoginSistemaOffline(""+etUsuario.getText(),""+etPassword.getText());
            //LeerXml(null);
            String Estadorespuesta=LeerXml(null,""+xmlTexto);
            String textomensaje;
            //Intent actprincipal=new Intent(getApplicationContext(),MenuLateralActivity.class);
            Intent acMainActivity;//=new Intent(getApplicationContext(),MenuLateralActivity.class);
            if (ModoInicial==2){
                acMainActivity=new Intent(getApplicationContext(),VisitaCampoMarcador.class);
            }else {
                acMainActivity=new Intent(getApplicationContext(),MenuLateralActivity.class);
            }
            acMainActivity.putExtra("acLoginUser",etUsuario.getText().toString());
            acMainActivity.putExtra("acLoginPassword",etPassword.getText().toString());
            switch (Estadorespuesta){
                case "-3":
                    textomensaje="Parameros Incorrectos !!!. Intentando modo Offline";
                    if (AccesoCorrecto){
                        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                        startService(i);
                        startActivity(acMainActivity);
                        finish();
                    }else {
                        textomensaje+="\n - No existe este usuario en modo offline";
                    }
                    tvEstadoSession.setText(textomensaje);
                    break;
                case "-2":
                    textomensaje="Error de Lectura de Datos de Session. Intentando modo Offline";
                    tvEstadoSession.setText("Error de Lectura de Datos de Session. Intentando modo Offline");
                    if (AccesoCorrecto){
                        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                        startService(i);
                        startActivity(acMainActivity);
                        finish();
                    }else {
                        textomensaje+="\n No existe este usuario en modo offline";
                    }
                    tvEstadoSession.setText(textomensaje);
                    break;
                case "-1":
                    textomensaje="Error de conexion. Error En formato XML. Intentando modo Offline";
                    if (AccesoCorrecto){
                        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                        startService(i);
                        startActivity(acMainActivity);
                        finish();
                    }else {
                        textomensaje+="\n No existe este usuario en modo offline";
                    }
                    tvEstadoSession.setText(textomensaje);
                    //tvEstadoSession.setText("Error En formato XML");
                    break;
                case "1"://Significa que el webservices a validado correctamente el acceso y le permite el acceso al sistema
                    if (!AccesoCorrecto){
                        db.AgregarUsuario(
                                ""+DatosSessionOnline.get(3),
                                ""+DatosSessionOnline.get(0),
                                ""+DatosSessionOnline.get(1),
                                ""+DatosSessionOnline.get(4),
                                ""+DatosSessionOnline.get(2)
                        );
                        CodigoPersonal=DatosSessionOnline.get(3);
                        guardarPreferencias();
                    }
                    Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                    startService(i);
                    startActivity(acMainActivity);
                    finish();
                    break;
            }

        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(10000 /* milliseconds */);

            conn.setReadTimeout(gen.getTiempoLectura());
            //conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(gen.getTiempoConexion());

            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);


            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }

        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        /*Aqui se leera el XML*/
        xmlTexto =new String(buffer);

        return new String(buffer);
    }
}
