package com.recamedi.comunicaciondispersa;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Renel01 on 15/03/2018.
 */

public class EnviarDocumentos {
    private Context contexto;
    private String usuario;
    private String password;
    private String RutaApp;
    private String RutaFotos;


    public Context getContexto() {
        return contexto;
    }
    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRutaApp() {
        return RutaApp;
    }
    public void setRutaApp(String rutaApp) {
        RutaApp = rutaApp;
    }
    public String getRutaFotos() {
        return RutaFotos;
    }
    public void setRutaFotos(String rutaFotos) {
        RutaFotos = rutaFotos;
    }
    public void enviarDatosServidor(String RuttaAPP){
        /*Cargando Preferencias*/
        /*SharedPreferences prefs = get getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        String Ruta=prefs.getString("rutawebapp","recamedi.com")+"webservices/sincronizardatos.php?usuario="+usuario+"&password="+password;*/

        new EnviarDatosServidor().execute(RuttaAPP);
    }
    public void enviarDatosServidor(){
        new EnviarDatosServidor().execute();
    }
    private class EnviarDatosServidor extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            //strings[0]=Direccion
            publishProgress(0);
            //String respuesta=postArchivo(strings[0]);
            /*SubirArchivos sa=new SubirArchivos(strings[0],gen.getCadena()+"webservices/guardarfoto.php?Idfotovisitascampo="+IdObtenidoServidor,strings[1]);
            String respuesta=sa.postArchivo();
            */

            DataBaseHelper db;
            db= new DataBaseHelper(contexto);
            String response="-1000";


            //Primero se recupera el listado de fotografias sel BD offline para enviar.
            ArrayList<ArrayList> listadoDocEntregado=db.ListarDocHechosOffLine();

            if (listadoDocEntregado.size() <1){
                response="-6";
            }
            for (int i = 0; i<listadoDocEntregado.size(); i++){
                String CodigoDocInternoOffline=listadoDocEntregado.get(i).get(0).toString();
                String CodigoDocumento=listadoDocEntregado.get(i).get(1).toString();
                String FechaAsignacion=listadoDocEntregado.get(i).get(2).toString();
                String FechaVisita=listadoDocEntregado.get(i).get(3).toString();
                String EstadoEntrega=listadoDocEntregado.get(i).get(4).toString();
                String EstadoFirma=listadoDocEntregado.get(i).get(5).toString();
                String DNIRecepcion=listadoDocEntregado.get(i).get(6).toString();
                String Parentesco=listadoDocEntregado.get(i).get(7).toString();
                String LecturaMedidor=listadoDocEntregado.get(i).get(8).toString();
                String VisitaLatitud=listadoDocEntregado.get(i).get(9).toString();
                String VisitaLongitud=listadoDocEntregado.get(i).get(10).toString();
                String EstadoEnvioServidor=listadoDocEntregado.get(i).get(11).toString();


                String contentAsString;//respuesta en formato XMl del servidor
                String respuestaparaaccion=null;
                int IdObtenidoServidor=0;

                //String myurl= general.getCadena()+"webservices/guardarlectura.php";
                String myurl= RutaApp;// strings[0];

                try {
                    publishProgress(0);
                    Log.i("URL",""+RutaApp);
                    myurl = myurl.replace(" ","%20");
                    InputStream is = null;
                    // Only display the first 500 characters of the retrieved
                    // web page content.
                    MultipartUtility multipart = null;
                    String charset2 = "UTF-8";
                    //response = multipart.finish(); // response from server.

                    String xmlDelServidor="";

                    try {
                        multipart = new MultipartUtility(myurl, charset2);
                        multipart.addFormField("usuario", "" + usuario);
                        multipart.addFormField("password", "" + password);
                        multipart.addFormField("DocumentosTrabajoID", "" + CodigoDocumento);
                        multipart.addFormField("FechaAsignado", "" + FechaAsignacion);
                        multipart.addFormField("FechaEjecutado", "" + FechaVisita);
                        multipart.addFormField("Estado", "" + EstadoEntrega);
                        multipart.addFormField("EstadoSeal", "" + EstadoFirma);
                        multipart.addFormField("NombreRecepcionador", "");
                        multipart.addFormField("DNIRecepcionador", "" + DNIRecepcion);
                        multipart.addFormField("Parentesco", "" + Parentesco);
                        multipart.addFormField("LecturaMedidor", "" + LecturaMedidor);
                        multipart.addFormField("LatitudVisita", "" + VisitaLatitud);
                        multipart.addFormField("LongitudVisita", "" + VisitaLongitud);
                        multipart.addFormField("Observaciones", "");
                        String respuestaServidor2 = multipart.finish();

                        //multipart.addFormField("param_name_3", "param_value");
                        //multipart.addFilePart("uploadedfile", new File(RutaFoto));
                        contentAsString = respuestaServidor2;


                        //return contentAsString;
                        // Makes sure that the InputStream is closed after the app is
                        // finished using it.
                    } catch (IOException e){
                        contentAsString="-10";
                    } finally {
                        if (is != null) {
                            is.close();
                        }

                    }



                    String TextoRecuperadoDeServidor=contentAsString;
                    //String Estadorespuesta=LeerXml(""+TextoRecuperadoDeServidor);
                    //Toast.makeText(getApplicationContext(),"url: "+urls[0],Toast.LENGTH_SHORT).show();

                    XmlPullParserFactory factory;

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
                                    }else if (name.equals("IdObtenido")){
                                        IdObtenidoServidor=Integer.parseInt(texto);
                                    }else{
                                        //Aun nada
                                    }
                                    break;
                            }
                            eventType=xpp.next();
                        }
                        //return respuestaparaaccion;
                        //tvEstadoSession.setText(NombreCompleto);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        respuestaparaaccion="-1";
                        //return respuestaparaaccion;
                    } catch (IOException e) {
                        e.printStackTrace();
                        respuestaparaaccion="-2";
                        //return respuestaparaaccion;
                    }

                    //return Estadorespuesta;
                    //return downloadUrl(urls[0]);
                } catch (IOException e) {
                    //tvEstadoSession.setText("Parameros Incorrectos !!!");
                    //Toast.makeText(getApplicationContext(),"Error de Conexion:"+e,Toast.LENGTH_SHORT).show();
                    respuestaparaaccion="-3";
                    //return "-3";
                }
                DataBaseHelper databaseoffline;
                databaseoffline= new DataBaseHelper(contexto);
                //String Estadorespuesta=LeerXml(""+xmlTexto);
                String Estadorespuesta=respuestaparaaccion;
                int CodigoDocumentito = Integer.parseInt(CodigoDocumento);
                if (Integer.parseInt(Estadorespuesta)>0){
                    publishProgress(i);
                    response=i+""+Estadorespuesta;
                    //Envia las fotos a servidor
                    if (IdObtenidoServidor>0){

                        new Enviarfotos().execute(IdObtenidoServidor+"",CodigoDocumentito+"");//CodigodevuelltServidir, CodigoDoc
                    }else {

                    }


                    databaseoffline.updateDocumento(
                            CodigoDocumentito,
                            "1"//1= Enviado
                    );
                }else if (Estadorespuesta.equals("-2")){//problemas de lectura y escritura en android
                    response=Estadorespuesta;
                    databaseoffline.updateDocumento(
                            CodigoDocumentito,
                            "0"//0= No enviado
                    );


                }else if (Estadorespuesta.equals("-3")){//que probablemete no se leyo bien los datos recibidos o no hay conexion a internet o servidor equivocado
                    response=Estadorespuesta;
                    databaseoffline.updateDocumento(
                            CodigoDocumentito,
                            "0"//0= No enviado
                    );


                }else if(Estadorespuesta.equals("-1")){//que hubo algun problema leyendo el archivo xml devuelto por elservidor
                    response=Estadorespuesta;
                    databaseoffline.updateDocumento(
                            CodigoDocumentito,
                            "0"//0= No enviado
                    );


                }else if (Estadorespuesta.equals("0")){//Usuario y contraseñas incorrectos, no se registro Online
                    response=Estadorespuesta;
                    databaseoffline.updateDocumento(
                            CodigoDocumentito,
                            "0"//0= No enviado
                    );

                }
            }



            //return respuesta;
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(Integer.parseInt(s)>0){
                Toast.makeText(contexto,"Se envio "+s+" al servidor",Toast.LENGTH_LONG).show();
            }else if(s.equals("-6")) {
                Toast.makeText(contexto,"No hay documentos para enviar("+s+")",Toast.LENGTH_LONG).show();
            }
        }
    }
    private class Enviarfotos extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            //String respuesta=postArchivo(strings[0]);
            /*SubirArchivos sa=new SubirArchivos(strings[0],gen.getCadena()+"webservices/guardarfoto.php?Idfotovisitascampo="+IdObtenidoServidor,strings[1]);
            String respuesta=sa.postArchivo();
            */

            //gen=(Generalidades)getApplication();
            //usuario=gen.getUsuarioActual();
            //password=gen.getPasswordActual();
            String charset = "UTF-8";
            String requestURL = RutaFotos;// gen.getCadena()+"webservices/guardarfoto.php";//?Idfotovisitascampo="+IdObtenidoServidor;//,strings[1];*/
            Log.i("URL",requestURL);
            DataBaseHelper db;
            db= new DataBaseHelper(contexto);
            MultipartUtility multipart = null;

            String response=null;
            try {


                //Primero se recupera el listado de fotografias sel BD offline para enviar.
                ArrayList<ArrayList> listadodefotos=db.ListarFotosSinIdVisitasCampoOnline(strings[1]+"");
                    /*Iterator<ArrayList> iterator=listadodefotos.iterator();
                    while (iterator.hasNext()){
                        listadodefotos.get(0).get(1);

                    }*/
                //response=listadodefotos.size()+"-"+strings[1];
                for (int i = 0; i<listadodefotos.size(); i++){
                    String CodigoFotos=listadodefotos.get(i).get(0).toString();
                    String CodigoDocumento=listadodefotos.get(i).get(1).toString();
                    String RutaFoto=listadodefotos.get(i).get(2).toString();
                    String EstadoFoto=listadodefotos.get(i).get(3).toString();
                    String FechaFotoTomada=listadodefotos.get(i).get(4).toString();
                    //Toast.makeText(getApplicationContext(),"FotosEncontradas: "+listadodefotos.size(),Toast.LENGTH_SHORT).show();
                    //new Enviarfotos().execute(tomarfoto.getFile().toString(),IdObtenidoServidor+"");

                    //verificaos que tenemos un IDvisitas campo correcto
                    if (Integer.parseInt(strings[0])>0){
                        //Actulizamos el campo (visitascampoonline) de la bd offline(codigo interno tabla foto,codigo obtenido del bd Online)
                        int CantidadFotosModificadas=db.updateFotosModoOnline(Integer.parseInt(CodigoFotos),Integer.parseInt(strings[0]));
                        if (CantidadFotosModificadas>0){//se copuerba que se hizo el cambio
                            //Luego se envia las fotos
                            multipart = new MultipartUtility(requestURL, charset);
                            multipart.addFormField("Idfotovisitascampo", ""+(strings[0]));
                            multipart.addFormField("fechatiempo", FechaFotoTomada+"");
                            //multipart.addFormField("param_name_3", "param_value");
                            multipart.addFilePart("uploadedfile", new File(RutaFoto));
                            //response = multipart.finish(); // response from server.
                            String respuestaServidor=multipart.finish();
                            if (respuestaServidor.equals("si")){//si el servidor responde "si"
                                //significa que guardo en la BD online y se subio  la foto. Se registra el cambio en la BD Offline
                                int CantidadRegistradosOnline=db.updateFotosEstado(Integer.parseInt(CodigoFotos),"1");//1=foto enviado
                                if ((CantidadRegistradosOnline>0)){
                                    //Foto online(archivo y BD) y Offline(BD)
                                    response="3";
                                }
                            }else{
                                int CantidadRegistradosOnline=db.updateFotosEstado(Integer.parseInt(CodigoFotos),"0");//0=Foto no enviado
                                if ((CantidadRegistradosOnline>0)){
                                    //Foto NO registradoonline(archivo y BD),
                                    response="2";
                                }
                            }
                        }else{
                            response="1";//No se actualizado con los IdVisitaCampo en la BD offline, por tanto no se sube el archivo
                        }
                    }else{
                        response="0";//No se obtuvo el ID del servidor
                    }
                }

                //Luego se registra modo Online
            } catch (IOException e) {
                response="Error";
                e.printStackTrace();
            }

            //return respuesta;
            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String subirfoto= s;
            Toast.makeText(contexto,"respuesta: "+subirfoto,Toast.LENGTH_LONG).show();
        }
    }
}
