package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.icu.text.UnicodeSetSpanner;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetalleDocumento extends AppCompatActivity {
    LocationManager locationManager;
    DatosListview objDetalle;
    TextView tvCodDoc;
    TextView tvNroSuministro;
    TextView tvTipoDoc;
    TextView tvCodBarra;
    Spinner spEstado;
    Spinner spParentesco;
    ArrayAdapter<CharSequence> aaEstado;
    ArrayAdapter<CharSequence> aaParentesco;
    EditText etDni;
    EditText etLecturaMedidor;
    Button btnCamara;
    TextView tvFotosTomadas;
    Button btnEntrgado;
    Button btnRezagado;
    ImageView ivFoto;
    DataBaseHelper db;
    Double Latitud = 0.0;
    Double Longitud = 0.0;

    Generalidades gen;
    String usuario="";
    String password="";

    //Parametros de a foto
    private Uri output;
    private String foto;
    private File file;
    Fotografias tomarfoto;
    private int cantidadFotos=0;
    private boolean registrarfotoEntregado=false;
    private boolean registrarfotoRezagado=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_documento);

        //Credenciales usuario actual
        gen=(Generalidades)getApplication();
        usuario=gen.getUsuarioActual();
        password=gen.getPasswordActual();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tomarfoto=new Fotografias(output,foto,file,this,this);
        tomarfoto.verificarPermisosEscritura();
        if (!isLocationEnabled())
            showAlert();
        tvCodDoc = (TextView) findViewById(R.id.tvCodDoc);
        tvNroSuministro = (TextView) findViewById(R.id.tvNroSuministro);
        tvTipoDoc = (TextView) findViewById(R.id.tvTipoDoc);
        tvCodBarra = (TextView) findViewById(R.id.tvCodBarra);
        etDni = (EditText) findViewById(R.id.etDNI);
        etLecturaMedidor = (EditText) findViewById(R.id.etLecturaMedidor);
        tvFotosTomadas=(TextView)findViewById(R.id.tvFotosTomadas);
        spEstado = (Spinner) findViewById(R.id.spEstado);
        spParentesco = (Spinner) findViewById(R.id.spParentesco);

        aaEstado = ArrayAdapter.createFromResource(this, R.array.saEstados, android.R.layout.simple_spinner_item);
        aaEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(aaEstado);
        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long id) {
                //Toast.makeText(getBaseContext(),adapterView.getItemAtPosition(posicion)+ " Selecionado",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        aaParentesco = ArrayAdapter.createFromResource(this, R.array.saParentesco, android.R.layout.simple_spinner_item);
        aaParentesco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spParentesco.setAdapter(aaParentesco);
        spParentesco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {
                //Toast.makeText(getBaseContext(),adapterView.getItemAtPosition(posicion)+ " Selecionado",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db = new DataBaseHelper(this);

        objDetalle = (DatosListview) getIntent().getExtras().getSerializable("objeto");
        tvCodDoc.setText(objDetalle.getTitulo());
        tvNroSuministro.setText(objDetalle.getNroSuministro());
        tvTipoDoc.setText(objDetalle.getNombreTipoDoc());
        tvCodBarra.setText(objDetalle.getCodigoBarra()+" "+objDetalle.getClienteNombre());

        btnRezagado = (Button) findViewById(R.id.btn_ddRezagado);
        btnRezagado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //miUbicacion();
                //RegistrarDocumentoTrabajo(objDetalle,"3");//3=Rezagado

                registrarfotoRezagado=true;
                if (cantidadFotos<1){
                    TomarFotografia();
                }
                if (cantidadFotos>0){
                    miUbicacion();
                    RegistrarDocumentoTrabajo(objDetalle,"3");//3=Rezagado
                }
                //registrarfotoRezagado

            }
        });
        btnCamara=(Button)findViewById(R.id.btnCamara);
        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TomarFotografia();
            }
        });
        btnEntrgado = (Button) findViewById(R.id.btn_ddEntregado);
        btnEntrgado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarfotoEntregado=true;
                if (cantidadFotos<1){
                    TomarFotografia();
                }
                if (cantidadFotos>0){
                    miUbicacion();
                    RegistrarDocumentoTrabajo(objDetalle,"4");//4=Entregado A cliente
                }


            }
        });
        ivFoto=(ImageView)findViewById(R.id.ivFoto);

    }

    private void TomarFotografia() {
        SimpleDateFormat fechaFormatoFoto = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss", Locale.getDefault());
        Date fechaEjecutadoFoto = new Date();
        String sFechaEjecutadoFoto = fechaFormatoFoto.format(fechaEjecutadoFoto);
        //Se toma foto
        String NombreFoto=objDetalle.getId()+"_"+objDetalle.getNroSuministro()+"_"+sFechaEjecutadoFoto;
        tomarfoto.ObtenerCamara(NombreFoto);
    }

    /*Ubicacion de GPS*/
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Active su ubicacion")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "para usar esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Longitud = location.getLongitude();
            Latitud = location.getLatitude();
            //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        if (!checkLocation()){
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location!=null){
            Longitud = location.getLongitude();
            Latitud = location.getLatitude();
        }
        Toast.makeText(getApplicationContext(),"Longitud: "+Longitud+" Latitud: "+Latitud,Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locListener);
    }
    private void RegistrarDocumentoTrabajo(DatosListview objDetalle, String EstadoEntrega) {
        int indiceEstado=spEstado.getSelectedItemPosition();
        String ValorEstado="";
        switch (indiceEstado){
            case 0:
                ValorEstado="10";//10. Con firma
                break;
            case 1:
                ValorEstado="11";//11. Sin firma<
                break;
            case 2:
                ValorEstado="12";//12. Ausente
                break;
            case 3:
                ValorEstado="13";//13. No Ubicado
                break;
            case 4:
                ValorEstado="14";//14. Rechazado
                break;
            case 5:
                ValorEstado="15";//15. Terreno baldio
                break;
            case 6:
                ValorEstado="16";//16. NIS no corresponde
                break;
            case 7:
                ValorEstado="17";//17. Construccion paralizada
                break;
        }
        int indiceParentesco=spParentesco.getSelectedItemPosition();
        String ValorParentesco="";
        switch (indiceParentesco){
            case 0:
                ValorParentesco="00";//00. Destinatario
                break;
            case 1:
                ValorParentesco="01";//01. Trabajador del hogar
                break;
            case 2:
                ValorParentesco="02";//02. Padre
                break;
            case 3:
                ValorParentesco="03";//03. Madre
                break;
            case 4:
                ValorParentesco="04";//04. Hijo(a)
                break;
            case 5:
                ValorParentesco="05";//05. Hermano(a)
                break;
            case 6:
                ValorParentesco="06";//06. Conyuge
                break;
            case 7:
                ValorParentesco="07";//07. Conviviente
                break;
            case 8:
                ValorParentesco="08";//08. Suegro(a)
                break;
            case 9:
                ValorParentesco="09";//09. Otro pariente
                break;
            case 10:
                ValorParentesco="20";//20. Trabajador(a)
                break;
            case 11:
                ValorParentesco="22";//22. Vigilante
                break;
            case 12:
                ValorParentesco="40";//40. Vecino(a)
                break;
            case 13:
                ValorParentesco="99";//99. No informado
                break;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);
        String DniRecepion="0";
        String LecturaMedidor="0";
        if (!etDni.getText().equals("")){
            DniRecepion=etDni.getText().toString();
        }
        if (!etLecturaMedidor.getText().equals("")){
            LecturaMedidor=etLecturaMedidor.getText().toString();
        }

        int cantidadActualizado= db.updateDocumento(
                objDetalle.getId(),
                ""+EstadoEntrega,//3=Doc. Rezagado,4=Doc. E. Cliente, segun la BD en Mysql
                ""+ValorEstado,//Las comillas simples no tienen ningun valor, solo es para conservar el tip de ayuda de android studio
                ""+ValorParentesco,
                ""+DniRecepion,
                ""+LecturaMedidor,
                ""+fecha,
                ""+Latitud,
                ""+Longitud
        );
        if (cantidadActualizado>0){
            Toast.makeText(getApplicationContext(),"Se guardo el archivo correctamente",Toast.LENGTH_SHORT).show();
            //Se envia datos al servidor
            SimpleDateFormat fechaFormato = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
            Date fechaEjecutado = new Date();
            String sFechaEjecutado = fechaFormato.format(fechaEjecutado);
            //Generalidades gen=(Generalidades)getApplication();
            new RegistrarDatosServidor().execute(gen.getCadena()+"webservices/guardarlectura.php?"
                    +"usuario="+usuario+"&password="+password+"&"
                    +"DocumentosTrabajoID="+objDetalle.getId()+"&"
                    +"FechaAsignado="+objDetalle.getFechaAsigando()+"&"
                    +"FechaEjecutado="+sFechaEjecutado+"&"
                    +"Estado="+EstadoEntrega+"&"
                    +"EstadoSeal="+ValorEstado+"&"
                    +"NombreRecepcionador=&"
                    +"DNIRecepcionador="+etDni.getText()+"&"
                    +"Parentesco="+ValorParentesco+"&"
                    +"LecturaMedidor="+LecturaMedidor+"&"
                    +"LatitudVisita="+Latitud+"&"
                    +"LongitudVisita="+Longitud+"&"
                    +"Observaciones="
            );
            //Aqui debe de abrir nuevamente la lista de documentos pendientes

        }else{
            AlertDialog.Builder mensajitoalusurio=new AlertDialog.Builder(this);
            mensajitoalusurio.setTitle("No se registro");
            mensajitoalusurio.setMessage("No se registro esta documentacion, intenta nuevamente");
            mensajitoalusurio.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Se hace algo para evitar que pase a la siguiente ventana, para que permanesca aqui hasta que se complete la elctura
                }
            });
        }
    }

    private class RegistrarDatosServidor extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute(){
            //Se desactivan el boton hasta que culmine el proceso
            //btnEntrgado.setEnabled(false);
            //btnRezagado.setEnabled(false);
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                publishProgress(0);
                String TextoRecuperadoDeServidor=downloadUrl(urls[0]);
                //String Estadorespuesta=LeerXml(""+TextoRecuperadoDeServidor);
                //Toast.makeText(getApplicationContext(),"url: "+urls[0],Toast.LENGTH_SHORT).show();

                XmlPullParserFactory factory;
                String respuestaparaaccion=null;
                try{
                    //StringBuilder fis=new StringBuilder();
                    factory=XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp=factory.newPullParser();
                    //Toast.makeText(getApplicationContext(),"-"+xmlTexto,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),":)"+XMLtextoParsear,Toast.LENGTH_SHORT).show();
                    xpp.setInput(new StringReader(TextoRecuperadoDeServidor));
                    String texto = null;
                    int eventType=xpp.getEventType();
                    int indice=0;
                    DataBaseHelper db;
                    db= new DataBaseHelper(getApplicationContext());
                    while (eventType!= XmlPullParser.END_DOCUMENT){
                        String name=xpp.getName();
                        publishProgress(indice);
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                break;
                            case XmlPullParser.TEXT:
                                texto=xpp.getText();
                                break;
                            case XmlPullParser.END_TAG:
                                if (name.equals("cantidad")){//Nombre del tag del archivo xml
                                    respuestaparaaccion=texto;
                                }else{
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

                //return Estadorespuesta;
                //return downloadUrl(urls[0]);
            } catch (IOException e) {
                //tvEstadoSession.setText("Parameros Incorrectos !!!");
                //Toast.makeText(getApplicationContext(),"Error de Conexion:"+e,Toast.LENGTH_SHORT).show();

                return "-3";
            }

        }
        @Override
        protected void onPostExecute(String result){
            //se activa los botones una vez culminado el proceso
            //btnEntrgado.setEnabled(false);
            //btnRezagado.setEnabled(false);
            TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            //tvEjemplo.setText(""+result.toString());
            //tvEstadoSession.setText("Usuario Aceptado");
            DataBaseHelper db;
            db= new DataBaseHelper(getApplicationContext());
            //String Estadorespuesta=LeerXml(""+xmlTexto);
            String Estadorespuesta=result;
            DatosListview objDetallito = (DatosListview) getIntent().getExtras().getSerializable("objeto");
            if (Integer.parseInt(Estadorespuesta)>0){
                Snackbar.make(btnEntrgado, "Guardado Correctamente("+Estadorespuesta+")", Snackbar.LENGTH_SHORT).show();
                db.updateDocumento(
                        objDetallito.getId(),
                        "1"//1= Enviado
                );
            }else if (Estadorespuesta.equals("-2")){//problemas de lectura y escritura en android
                //Toast.makeText(getApplicationContext(),"Agregados: "+i+" , Duplicados eliminados: "+FilasEliminadas,Toast.LENGTH_SHORT).show();
                db.updateDocumento(
                        objDetallito.getId(),
                        "0"//0= No enviado
                );
                Snackbar.make(btnEntrgado, "Usuario y contraeña incorrectos("+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
            }else if (Estadorespuesta.equals("-3")){//que probablemete no se leyo bien los datos recibidos o no hay conexion a internet o servidor equivocado
                db.updateDocumento(
                        objDetallito.getId(),
                        "0"//0= No enviado
                );
                Snackbar.make(btnEntrgado, "Sin Conexion. Verifica tu conexion a internet(cod:"+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
            }else if(Estadorespuesta.equals("-1")){//que hubo algun problema leyendo el archivo xml devuelto por elservidor
                db.updateDocumento(
                        objDetallito.getId(),
                        "0"//0= No enviado
                );
                Snackbar.make(btnEntrgado, "Error al leer respuesta del servidor(cod:"+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
            }else if (Estadorespuesta.equals("0")){//Usuario y contraseñas incorrectos, no se registro Online
                db.updateDocumento(
                        objDetallito.getId(),
                        "0"//0= No enviado
                );
                Toast.makeText(getApplicationContext(),"Usuario y Contraseña incorrectos(cod:"+Estadorespuesta+")",Toast.LENGTH_SHORT).show();
                //Snackbar.make(btnEntrgado, "Usuario y Contraseña incorrectos(cod:"+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
            }

        }
        @Override
        protected void onProgressUpdate(Integer... valor){
            /*TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            tvEjemplo.setText("Sincronizando "+valor[0]+" Registros...");
            pbEstadoSincronizacion.setProgress(valor[0]);*/
        }
    }
    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(gen.getTiempoLectura());
            conn.setConnectTimeout(gen.getTiempoConexion());
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();
            //conn.getInputStream().read();


            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }

        }
    }
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        /*Aqui se leera el XML*/
        //xmlTexto =new String(buffer);
        DataInputStream dis = new DataInputStream(stream);
        String inputLine;
        String xmlDelServidor="";
        while ((inputLine = dis.readLine()) != null) {
            //Log.d("XML",inputLine);
            xmlDelServidor +=inputLine;
        }
        return new String(xmlDelServidor);
    }


    //Recibe los datos(en este caso las fotografias), para procesarlas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {

            try {
                cantidadFotos+=1;
                tomarfoto.loadImageFromFile(ivFoto);
                //String subirfoto=postArchivo(tomarfoto.getFile().toString());
                new Enviarfotos().execute(tomarfoto.getFile().toString());
                tvFotosTomadas.setText("Cantidad de Fotos: "+cantidadFotos);
                //tvFotosTomadas.append(tomarfoto.getFoto().toString()+"\n");
                //Aqui se registra en la BD Offline
                InsertarFotoOffline(tomarfoto.getFile().toString());
                if(registrarfotoEntregado==true){
                    miUbicacion();
                    RegistrarDocumentoTrabajo(objDetalle,"4");//4=Entregado A cliente
                }
                if (registrarfotoRezagado=true){
                    miUbicacion();
                    RegistrarDocumentoTrabajo(objDetalle,"3");//3=Rezagado
                }
                //loadImageFromFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void InsertarFotoOffline(String rutafoto) {
        SimpleDateFormat fechaFormatoFoto = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Date fechaEjecutadoFoto = new Date();
        String sFechaEjecutadoFoto = fechaFormatoFoto.format(fechaEjecutadoFoto);
        long cantidadRegistrado= db.AgregarFoto(
                ""+objDetalle.getId(),
                ""+rutafoto,
                "",
                ""+sFechaEjecutadoFoto
        );
        if (cantidadRegistrado!=-1){//-1 significa que que hubo problemas al registrar esta foto en BD
            if (cantidadRegistrado>0){//cantidadRegistrado, tomara el ultimo ID registrado
                Toast.makeText(getApplicationContext(),"Fotos regitradas: "+cantidadRegistrado,Toast.LENGTH_LONG).show();
            }else {
                //Toast.makeText(getApplicationContext(),"Fotos NO regitradas: "+cantidadRegistrado,Toast.LENGTH_LONG).show();

            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sin Foto");
            builder.setMessage("No se registro la foto, vuelve a tomar la foto.("+cantidadRegistrado+")")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    TomarFotografia();
                }
            }).show();
        }
    }

    //Alguna Accion cuando el esta habilitado los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    private class Enviarfotos extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            //String respuesta=postArchivo(strings[0]);
            SubirArchivos sa=new SubirArchivos(strings[0],gen.getCadena()+"webservices/guardarfoto.php");
            String respuesta=sa.postArchivo();
            return respuesta;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String subirfoto= s;
            Toast.makeText(getApplicationContext(),""+subirfoto,Toast.LENGTH_LONG).show();
        }
    }
}