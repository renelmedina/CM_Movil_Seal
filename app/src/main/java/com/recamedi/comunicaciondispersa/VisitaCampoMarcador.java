package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VisitaCampoMarcador extends AppCompatActivity {
    EditText etCampo_Suministro;
    EditText etCampos_CantidadFotos;
    Button btnCampo_Camara;
    Button btnCampo_Rezagado;
    Button btnCampo_Entregado;
    ImageView ivCampo_Foto;

    DataBaseHelper db;

    private Uri output;
    private String foto;
    private File file;
    Fotografias tomarfoto;


    private int cantidadFotos=0;
    private boolean registrarfotoEntregado=false;
    private boolean registrarfotoRezagado=false;
    GeoLocation gps;

    Generalidades gen=(Generalidades)getApplication();
    //ID de la tabla MYSQL VisitasCampo, Dato devuelto por el servidor cuando se registra un documento entregado, para registrarlo en la BD fotos
    private int IdObtenidoServidor;

    private VerificarPermisos vp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visita_campo_marcador);


        //vp.verificarTodosPermisos();

        etCampo_Suministro=(EditText)findViewById(R.id.etCampo_Suministro);
        etCampos_CantidadFotos=(EditText)findViewById(R.id.etCampos_CantidadFotos);
        etCampos_CantidadFotos.setEnabled(false);
        btnCampo_Camara=(Button)findViewById(R.id.btnCampo_Camara);
        btnCampo_Rezagado=(Button)findViewById(R.id.btnCampo_Rezagado);
        btnCampo_Entregado=(Button)findViewById(R.id.btnCampo_Entregado);
        ivCampo_Foto=(ImageView) findViewById(R.id.ivCampo_Foto);

        db = new DataBaseHelper(this);


        vp=new VerificarPermisos(this,this);
        gps=new GeoLocation(this,this);

        if (vp.permisoUbicacion()){
            //Tenemos Todos los permisos
            gps.IniciarServicio();
            if(gps.VerificarGPSActivo()){
                btnCampo_Rezagado.setEnabled(true);
            }else {
                btnCampo_Entregado.setEnabled(false);
            }
        }else {
            //No tenemos permisos solicitar
            vp.permisoUbicacion();
        }
        if (vp.permisoEscritura()){
            etCampo_Suministro.setEnabled(true);
        }else{
            etCampo_Suministro.setEnabled(false);
            vp.SolicitarPermisoEscritura();
        }
        tomarfoto=new Fotografias(output,foto,file,this,this);
        tomarfoto.verificarPermisosEscritura();


        //VerificarPermisos();


        gen = (Generalidades) getApplication();

        btnCampo_Entregado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarfotoEntregado=true;
                if (cantidadFotos<1){
                    TomarFotografia();
                }
                if (cantidadFotos>0){
                    try {
                        RegistrarDocumentoTrabajo(""+etCampo_Suministro.getText(),"4");//4=Entregado A cliente
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //RegistrarDocumentoTrabajo(objDetalle,"3");//3=Rezagado
                }
            }
        });
        btnCampo_Rezagado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarfotoRezagado=true;
                if (cantidadFotos<1){
                    TomarFotografia();
                }
                if (cantidadFotos>0){
                    try {
                        RegistrarDocumentoTrabajo(""+etCampo_Suministro.getText(),"3");//3=Rezagado
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //RegistrarDocumentoTrabajo(objDetalle,"3");//3=Rezagado
                }
            }
        });
        btnCampo_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TomarFotografia();
            }
        });

    }



    private void TomarFotografia() {
        String nroSuministro=etCampo_Suministro.getText().toString();
        if (!nroSuministro.isEmpty()){
            SimpleDateFormat fechaFormatoFoto = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss", Locale.getDefault());
            Date fechaEjecutadoFoto = new Date();
            String sFechaEjecutadoFoto = fechaFormatoFoto.format(fechaEjecutadoFoto);
            //Se toma foto
            String NombreFoto=nroSuministro+"_vc_"+sFechaEjecutadoFoto;
            tomarfoto.ObtenerCamara(NombreFoto);
        }
    }
    //Verifica los permisos concedido
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 225) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                gps=new GeoLocation(this,this);
                gps.IniciarServicio();
            } else {
                vp.permisoUbicacion();
            }
        }
        if (requestCode==2){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                etCampo_Suministro.setEnabled(true);
            } else {
                //Permiso Denegado
                etCampo_Suministro.setEnabled(false);
                vp.SolicitarPermisoEscritura();

            }
        }

    }
    private void VerificarPermisos(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            Log.e("Location", "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            AlertDialog.Builder mensajitoalusurio=new AlertDialog.Builder(getApplicationContext());
            mensajitoalusurio.setTitle("Permiso a la ubicacion");
            mensajitoalusurio.setMessage("No se Guardo Nada, porque no esta activo el GPS");
            mensajitoalusurio.setPositiveButton("Activar GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //miUbicacion();
                    //Se hace algo para evitar que pase a la siguiente ventana, para que permanesca aqui hasta que se complete la elctura
                }
            });
            //return;
        }else{
            Log.i("Location", "Permisos necesarios OK!.");

            gps=new GeoLocation(this,this);
            gps.IniciarServicio();
        }
    }
    //Recibe los datos(en este caso las fotografias), para procesarlas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {

            try {
                cantidadFotos+=1;
                tomarfoto.loadImageFromFile(ivCampo_Foto, etCampo_Suministro.getText().toString());//Estampa la fecha en la FOTO TOMADA y el Nro de suministro
                //String subirfoto=postArchivo(tomarfoto.getFile().toString());
                //new Enviarfotos().execute(tomarfoto.getFile().toString());

                etCampos_CantidadFotos.setText("Cantidad de Fotos: "+cantidadFotos);
                //tvFotosTomadas.append(tomarfoto.getFoto().toString()+"\n");
                //Aqui se registra en la BD Offline
                InsertarFotoOffline(tomarfoto.getFile().toString(),etCampo_Suministro.getText().toString());


                if(registrarfotoEntregado==true){
                    RegistrarDocumentoTrabajo(""+etCampo_Suministro.getText(),"4");//4=Entregado A cliente
                    registrarfotoEntregado=false;
                }
                if (registrarfotoRezagado==true){
                    RegistrarDocumentoTrabajo(""+etCampo_Suministro.getText(),"3");//3=Rezagado
                    //RegistrarDocumentoTrabajo(objDetalle,"3");//3=Rezagado
                    registrarfotoRezagado=false;
                }
                //loadImageFromFile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void RegistrarDocumentoTrabajo(String NroSuministro, String EstadoEntrega) throws IOException {
        gps.muestraPosicionActual();
        String Longitud =gps.getLongitud().toString();
        String Latitud=gps.getLatitud().toString();
        SimpleDateFormat fechaFormato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Date fechaEjecutado = new Date();
        String FechaEjecutado = fechaFormato.format(fechaEjecutado);
        long cantidadingresados=db.addDocumentoVisitaCampo(
                ""+NroSuministro,
                ""+EstadoEntrega,
                ""+FechaEjecutado,
                ""+Latitud,
                ""+Longitud

        );

        //String requestURL = gen.getCadena()+"webservices/guardarlectura.php";
        if (cantidadingresados>0){

            /*new RegistrarDatosServidor().execute(
                    requestURL,
                    gen.getUsuarioActual(),
                    gen.getPasswordActual(),
                    NroSuministro,
                    Latitud,
                    Longitud,
                    FechaEjecutado
            );*/


            EnviarDocumentos ed=new EnviarDocumentos();
            ed.setContexto(this);
            ed.setUsuario(gen.getUsuarioActual());
            ed.setPassword(gen.getPasswordActual());
            ed.setRutaApp(gen.getCadena()+"webservices/guardarlectura.php");
            ed.setRutaFotos(gen.getCadena()+"webservices/guardarfoto.php");
            //ed.enviarDatosServidor();
            //ed.EnviarDatosServidorVC();
            ed.enviarDatosServidorVolanteo();
            Intent actVisitasCampo = new Intent(getApplicationContext(),VisitaCampoMarcador.class);
            startActivity(actVisitasCampo);
            finish();
        }
    }
    private void InsertarFotoOffline(String rutafoto, String NroSuministro) {
        SimpleDateFormat fechaFormatoFoto = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Date fechaEjecutadoFoto = new Date();
        String sFechaEjecutadoFoto = fechaFormatoFoto.format(fechaEjecutadoFoto);
        /*long cantidadRegistrado= db.AgregarFoto(
                "",
                "",
                ""+rutafoto,
                "",
                ""+sFechaEjecutadoFoto
        );*/
        long cantidadRegistrado= db.AgregarFoto(
                ""+NroSuministro,
                ""+rutafoto,
                "",
                ""+sFechaEjecutadoFoto
        );
        if (cantidadRegistrado!=-1){//-1 significa que que hubo problemas al registrar esta foto en BD
            if (cantidadRegistrado>0){//cantidadRegistrado, tomara el ultimo ID registrado
                //Toast.makeText(getApplicationContext(),"Fotos regitradas: "+cantidadRegistrado,Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(),"Fotos NO regitradas: "+cantidadRegistrado,Toast.LENGTH_LONG).show();

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

//    private class RegistrarDatosServidor extends AsyncTask<String,Integer,String> {
//
//        String nroSuministroGeneral;
//        @Override
//        protected void onPreExecute(){
//            //Se desactivan el boton hasta que culmine el proceso
//            //btnEntrgado.setEnabled(false);
//            //btnRezagado.setEnabled(false);
//        }
//        @Override
//        protected String doInBackground(String... strings) {
//            nroSuministroGeneral=strings[3];
//            String charset = "UTF-8";
//
//            MultipartUtility multipart = null;
//            try {
//                publishProgress(0);
//                multipart = new MultipartUtility(strings[0], charset);
//                multipart.addFormField("usuario", ""+strings[1]);
//                multipart.addFormField("password", ""+strings[2]);
//                multipart.addFormField("NroSuministro", ""+strings[3]);
//                multipart.addFormField("LatitudVisita", ""+strings[4]);
//                multipart.addFormField("LongitudVisita", ""+strings[5]);
//                multipart.addFormField("FechaEjecutado", ""+strings[6]);
//
//                //response = multipart.finish(); // response from server.
//                String TextoRecuperadoDeServidor=multipart.finish();
//
//                //String TextoRecuperadoDeServidor=downloadUrl(urls[0]);
//                //String Estadorespuesta=LeerXml(""+TextoRecuperadoDeServidor);
//                //Toast.makeText(getApplicationContext(),"url: "+urls[0],Toast.LENGTH_SHORT).show();
//                Log.i("Repuestaserv",TextoRecuperadoDeServidor);
//                XmlPullParserFactory factory;
//                String respuestaparaaccion=null;
//                try{
//                    //StringBuilder fis=new StringBuilder();
//                    factory=XmlPullParserFactory.newInstance();
//                    XmlPullParser xpp=factory.newPullParser();
//                    factory.setNamespaceAware(true);
//                    //Toast.makeText(getApplicationContext(),"-"+xmlTexto,Toast.LENGTH_SHORT).show();
//                    //Toast.makeText(getApplicationContext(),":)"+XMLtextoParsear,Toast.LENGTH_SHORT).show();
//                    xpp.setInput(new StringReader(TextoRecuperadoDeServidor));
//                    String texto = null;
//                    int eventType=xpp.getEventType();
//                    int indice=0;
//                    DataBaseHelper db;
//                    db= new DataBaseHelper(getApplicationContext());
//                    IdObtenidoServidor=0;
//                    while (eventType!= XmlPullParser.END_DOCUMENT){
//                        Log.i("Repuestaserv","Dentro de While");
//
//                        String name=xpp.getName();
//                        publishProgress(indice);
//                        Log.i("Repuestaserv","Dentro de Whilenombre etiqueta: "+name+"="+xpp.getText());
//
//                        switch (eventType){
//                            case XmlPullParser.START_TAG:
//
//                                //break;
//                            case XmlPullParser.TEXT:
//                                texto=xpp.getText();
//                                break;
//                            case XmlPullParser.END_TAG:
//                                if (name.equals("cantidad")){//Nombre del tag del archivo xml
//                                    respuestaparaaccion=texto;
//                                }else if (name.equals("IdObtenido")){
//                                    IdObtenidoServidor=Integer.parseInt(texto);
//                                }else{
//                                    //Aun nada
//                                }
//                                break;
//                        }
//                        eventType=xpp.next();
//                    }
//                    return respuestaparaaccion;
//                    //tvEstadoSession.setText(NombreCompleto);
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                    respuestaparaaccion="-1";
//                    return respuestaparaaccion;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    respuestaparaaccion="-2";
//                    return respuestaparaaccion;
//                }
//
//                //return Estadorespuesta;
//                //return downloadUrl(urls[0]);
//            } catch (IOException e) {
//                //tvEstadoSession.setText("Parameros Incorrectos !!!");
//                //Toast.makeText(getApplicationContext(),"Error de Conexion:"+e,Toast.LENGTH_SHORT).show();
//
//                return "-3";
//            }
//
//        }
//        @Override
//        protected void onPostExecute(String result){
//            //se activa los botones una vez culminado el proceso
//            //btnEntrgado.setEnabled(false);
//            //btnRezagado.setEnabled(false);
//            TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
//            //tvEjemplo.setText(""+result.toString());
//            //tvEstadoSession.setText("Usuario Aceptado");
//            DataBaseHelper db;
//            db= new DataBaseHelper(getApplicationContext());
//            //String Estadorespuesta=LeerXml(""+xmlTexto);
//            String Estadorespuesta=result;
//            //DatosListview objDetallito = (DatosListview) getIntent().getExtras().getSerializable("objeto");
//            //Toast.makeText(getApplicationContext(),"ID del registro"+IdObtenidoServidor,Toast.LENGTH_SHORT).show();
//
//            if (Integer.parseInt(Estadorespuesta)>0){
//                //Snackbar.make(btnEntrgado, "Guardado Correctamente("+Estadorespuesta+"-"+IdObtenidoServidor+")", Snackbar.LENGTH_SHORT).show();
//                //Envia las fotos a servidor
//                if (IdObtenidoServidor>0){
//
//                    new Enviarfotos().execute(IdObtenidoServidor+"",nroSuministroGeneral);//CodigodevuelltServidir, CodigoDoc
//                }else {
//                    Toast.makeText(getApplicationContext(),"No se Obtuvo ID del Servidor: "+IdObtenidoServidor,Toast.LENGTH_SHORT).show();
//                }
//                /*db.updateDocumentoXSuministro(
//                        CodigoDocumentito,
//                        "0"//0= No enviado
//                );*/
//            }else if (Estadorespuesta.equals("-2")){//problemas de lectura y escritura en android
//                //Toast.makeText(getApplicationContext(),"Agregados: "+i+" , Duplicados eliminados: "+FilasEliminadas,Toast.LENGTH_SHORT).show();
//                /*db.updateDocumento(
//                        objDetallito.getId(),
//                        "0"//0= No enviado
//                );*/
//                Snackbar.make(btnCampo_Entregado, "Usuario y contraeña incorrectos("+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
//            }else if (Estadorespuesta.equals("-3")){//que probablemete no se leyo bien los datos recibidos o no hay conexion a internet o servidor equivocado
//                /*db.updateDocumento(
//                        objDetallito.getId(),
//                        "0"//0= No enviado
//                );*/
//                Snackbar.make(btnCampo_Entregado, "Sin Conexion. Verifica tu conexion a internet(cod:"+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
//            }else if(Estadorespuesta.equals("-1")){//que hubo algun problema leyendo el archivo xml devuelto por elservidor
//                /*db.updateDocumento(
//                        objDetallito.getId(),
//                        "0"//0= No enviado
//                );*/
//                Snackbar.make(btnCampo_Entregado, "Error al leer respuesta del servidor(cod:"+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
//            }else if (Estadorespuesta.equals("0")){//Usuario y contraseñas incorrectos, no se registro Online
//                /*db.updateDocumento(
//                        objDetallito.getId(),
//                        "0"//0= No enviado
//                );*/
//                Toast.makeText(getApplicationContext(),"Usuario y Contraseña incorrectos(cod:"+Estadorespuesta+")",Toast.LENGTH_SHORT).show();
//                //Snackbar.make(btnEntrgado, "Usuario y Contraseña incorrectos(cod:"+Estadorespuesta+")", Snackbar.LENGTH_LONG).show();
//            }
//
//        }
//        @Override
//        protected void onProgressUpdate(Integer... valor){
//            /*TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
//            tvEjemplo.setText("Sincronizando "+valor[0]+" Registros...");
//            pbEstadoSincronizacion.setProgress(valor[0]);*/
//        }
//    }
//    private class Enviarfotos extends AsyncTask<String,Integer,String>{
//
//        @Override
//        protected String doInBackground(String... strings) {
//            //String respuesta=postArchivo(strings[0]);
//            /*SubirArchivos sa=new SubirArchivos(strings[0],gen.getCadena()+"webservices/guardarfoto.php?Idfotovisitascampo="+IdObtenidoServidor,strings[1]);
//            String respuesta=sa.postArchivo();
//            */
//            Generalidades gen=(Generalidades)getApplication();
//            String charset = "UTF-8";
//            String requestURL = gen.getCadena()+"webservices/guardarfoto.php";//?Idfotovisitascampo="+IdObtenidoServidor;//,strings[1];*/
//            Log.e("URL",requestURL);
//            DataBaseHelper db;
//            db= new DataBaseHelper(getApplicationContext());
//            MultipartUtility multipart = null;
//            String response=null;
//            try {
//
//
//                //Primero se recupera el listado de fotografias sel BD offline para enviar.
//                ArrayList<ArrayList> listadodefotos=db.ListarFotosSinIdVisitasCampoOnlineXSuministro(strings[1]+"");
//                    /*Iterator<ArrayList> iterator=listadodefotos.iterator();
//                    while (iterator.hasNext()){
//                        listadodefotos.get(0).get(1);
//
//                    }*/
//                response=listadodefotos.size()+"---"+strings[1];
//                Log.i("LecturaARRAY",listadodefotos.toString());
//
//                for (int i = 0; i<listadodefotos.size(); i++){
//                    String CodigoFotos=listadodefotos.get(i).get(0).toString();
//                    //String CodigoDocumento=listadodefotos.get(i).get(1).toString();
//                    String RutaFoto=listadodefotos.get(i).get(3).toString();
//                    //String EstadoFoto=listadodefotos.get(i).get(3).toString();
//                    String FechaFotoTomada=listadodefotos.get(i).get(5).toString();
//                    //Toast.makeText(getApplicationContext(),"FotosEncontradas: "+listadodefotos.size(),Toast.LENGTH_SHORT).show();
//                    //new Enviarfotos().execute(tomarfoto.getFile().toString(),IdObtenidoServidor+"");
//
//                    //verificaos que tenemos un IDvisitas campo correcto
//                    if (Integer.parseInt(strings[0])>0){
//                        response="dentro de del IF y del FOR";
//                        //Actulizamos el campo (visitascampoonline) de la bd offline(codigo interno tabla foto,codigo obtenido del bd Online)
//                        int CantidadFotosModificadas=db.updateFotosModoOnline(Integer.parseInt(CodigoFotos),Integer.parseInt(strings[0]));
//                        if (CantidadFotosModificadas>0){//se copuerba que se hizo el cambio
//                            //Luego se envia las fotos
//                            multipart = new MultipartUtility(requestURL, charset);
//                            multipart.addFormField("Idfotovisitascampo", ""+(strings[0]));
//                            multipart.addFormField("fechatiempo", FechaFotoTomada+"");
//                            //multipart.addFormField("param_name_3", "param_value");
//                            multipart.addFilePart("uploadedfile", new File(RutaFoto));
//                            //response = multipart.finish(); // response from server.
//                            String respuestaServidor=multipart.finish();
//                            if (respuestaServidor.equals("si")){//si el servidor responde "si"
//                                //significa que guardo en la BD online y se subio  la foto. Se registra el cambio en la BD Offline
//                                int CantidadRegistradosOnline=db.updateFotosEstado(Integer.parseInt(CodigoFotos),"1");//1=foto enviado
//                                if ((CantidadRegistradosOnline>0)){
//                                    //Foto online(archivo y BD) y Offline(BD)
//                                    response="3";
//                                }
//                            }else{
//                                int CantidadRegistradosOnline=db.updateFotosEstado(Integer.parseInt(CodigoFotos),"0");//0=Foto no enviado
//                                if ((CantidadRegistradosOnline>0)){
//                                    //Foto NO registradoonline(archivo y BD),
//                                    response="2";
//                                }
//                            }
//                        }else{
//                            response="1";//No se actualizado con los IdVisitaCampo en la BD offline, por tanto no se sube el archivo
//                        }
//                    }else{
//                        response="0";//No se obtuvo el ID del servidor
//                    }
//                }
//
//                //Luego se registra modo Online
//            } catch (IOException e) {
//                response="Error";
//                e.printStackTrace();
//            }
//
//            //return respuesta;
//            return response;
//
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            String subirfoto= s;
//            Toast.makeText(getApplicationContext(),"resputa: "+subirfoto,Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private String downloadUrl(String myurl) throws IOException {
//        Log.i("URL",""+myurl);
//        myurl = myurl.replace(" ","%20");
//        InputStream is = null;
//        // Only display the first 500 characters of the retrieved
//        // web page content.
//        Generalidades gen=(Generalidades)getApplication();
//        try {
//            URL url = new URL(myurl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(gen.getTiempoLectura());
//            conn.setConnectTimeout(gen.getTiempoConexion());
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            // Starts the query
//            conn.connect();
//            int response = conn.getResponseCode();
//            Log.d("respuesta", "The response is: " + response);
//            is = conn.getInputStream();
//            //conn.getInputStream().read();
//
//
//            // Convert the InputStream into a string
//            String contentAsString = readIt(is);
//            return contentAsString;
//            // Makes sure that the InputStream is closed after the app is
//            // finished using it.
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//
//        }
//    }
//    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
//        /*Aqui se leera el XML*/
//        //xmlTexto =new String(buffer);
//        DataInputStream dis = new DataInputStream(stream);
//        String inputLine;
//        String xmlDelServidor="";
//        while ((inputLine = dis.readLine()) != null) {
//            //Log.d("XML",inputLine);
//            xmlDelServidor +=inputLine;
//        }
//        return new String(xmlDelServidor);
//    }
}
