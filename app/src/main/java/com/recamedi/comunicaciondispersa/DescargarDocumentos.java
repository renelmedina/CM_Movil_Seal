package com.recamedi.comunicaciondispersa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renel01 on 13/03/2018.
 * Buscar documentos pendientes por hacer la lecturas
 */

public class DescargarDocumentos  {
    private TextView txtEstado;
    private ProgressBar pbEstadoSincronizacion;
    private List<List<String>>  lstDatos=null;
    private Context contexto;

    public TextView getTxtEstado() {
        return txtEstado;
    }

    public void setTxtEstado(TextView txtEstado) {
        this.txtEstado = txtEstado;
    }

    public ProgressBar getPbEstadoSincronizacion() {
        return pbEstadoSincronizacion;
    }

    public void setPbEstadoSincronizacion(ProgressBar pbEstadoSincronizacion) {
        this.pbEstadoSincronizacion = pbEstadoSincronizacion;
    }

    public List<List<String>> getLstDatos() {
        return lstDatos;
    }

    public void setLstDatos(List<List<String>> lstDatos) {
        this.lstDatos = lstDatos;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public void SinconizarDatos(String RuttaAPP){
        /*Cargando Preferencias*/
        /*SharedPreferences prefs = get getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        String Ruta=prefs.getString("rutawebapp","recamedi.com")+"webservices/sincronizardatos.php?usuario="+usuario+"&password="+password;*/

        new ListarDocumentos().execute(RuttaAPP);
    }
    private class ListarDocumentos extends AsyncTask<String,Integer,String> {
        //Antes de iniciar el proceso en segundo plano
        @Override
        protected void onPreExecute(){
            //tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            txtEstado.setVisibility(View.VISIBLE);
            pbEstadoSincronizacion.setVisibility(View.VISIBLE);
            pbEstadoSincronizacion.setProgress(0);
            //sToast.makeText(getApplicationContext(),"Sincronizando Datos...",Toast.LENGTH_SHORT).show();
            //ivSincronizarDatos.setEnabled(false);
        }
        //Ejecutando en segundo plano
        @Override
        protected String doInBackground(String... urls) {
            try {
                publishProgress(0);
                String TextoRecuperadoDeServidor=downloadUrl(urls[0]);
                //String Estadorespuesta=LeerXml(""+TextoRecuperadoDeServidor);
                XmlPullParserFactory factory;
                lstDatos=new ArrayList<List<String>>();
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
                    db= new DataBaseHelper(contexto);
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
                                if (name.equals("codigos")){//Nombre del tag del archivo xml

                                    lstDatos.add(new ArrayList<String>());
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"codigodoc"));//0
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"nrosuministro"));//1
                                    //myParser.getAttributeCount();
                                }else if (name.equals("tipodoc")){
                            /*Obtiene de la etiqueta <tipodoc></tipodoc>*/
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"codigo"));//2
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"nombretipodoc"));//3
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"nrodocumentoseal"));//4

                                }else if (name.equals("codigobarra")){
                                    //pressure=myParser.getAttributeValue(null,"value");
                                    lstDatos.get(indice).add(texto);//5

                                }else if(name.equals("coordenadas")){
                                    //temperature=myParser.getAttributeValue(null,"value");
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"lat"));//6
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"long"));//7
                                }else if(name.equals("fechaasig")){
                                    //temperature=myParser.getAttributeValue(null,"value");
                                    lstDatos.get(indice).add(texto);//8
                                }else if (name.equals("cliente")){
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"dni"));//9
                                    lstDatos.get(indice).add(xpp.getAttributeValue(null,"nombrecliente"));//10
                                    //la etiqueta <cliente /> es el ultimo, asi que denotamos que es el fin de este registro y se va al siguiente registro
                                    indice+=1;
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
                //xmlTexto=null;
                return "-3";
            }
        }
        //

        //Despues de terminar la ejecucion en segundo plano y mostrar los resultados o algun aviso del mismo
        @Override
        protected void onPostExecute(String result){
            //xmlTexto=result;
            //TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            //tvEjemplo.setText(""+result.toString());
            //tvEstadoSession.setText("Usuario Aceptado");
            DataBaseHelper db;
            db= new DataBaseHelper(contexto);
            //String Estadorespuesta=LeerXml(""+xmlTexto);
            String Estadorespuesta=result;

            if (Estadorespuesta==null){//null significa que no ha habido ningun problema con el servidor y se recibio los datos

                String textodetalle="";
                Integer codigodocumento;
                String nrosuministro;
                String codtipodoc;
                String nombretipodoc;
                String nrodocumentoseal;
                String codigobarras;
                String latitud;
                String longitud;
                String fechaasignacion;
                String dnicliente;
                String nombrecliente;
                //lstDatos.get().get()
                int i;
                for (i=0; i<lstDatos.size();i++) {
                    codigodocumento = Integer.parseInt(lstDatos.get(i).get(0).toString());//codigodoc de <codigos codigodoc="254" nrosuministro="321321"/>
                    nrosuministro = lstDatos.get(i).get(1);//nrosuministro de <codigos codigodoc="254" nrosuministro="321321"/>
                    codtipodoc = lstDatos.get(i).get(2);
                    nombretipodoc = lstDatos.get(i).get(3);//nombretipodoc de <tipodoc codigo="1" nombretipodoc="Nombre Del tipo de Doc1"/>
                    nrodocumentoseal=lstDatos.get(i).get(4);//<tipodoc codigo='2' nombretipodoc='Carta RecaudaciÃ³n/Cobranza' nrodocumentoseal='11940'/>
                    codigobarras = lstDatos.get(i).get(5);//codigobarra de <codigobarra>2018-11-13158-1</codigobarra>
                    latitud = lstDatos.get(i).get(6);//lat de <coordenadas lat="-153.255" long="15.358"/>
                    longitud = lstDatos.get(i).get(7);//long de <coordenadas lat="-153.255" long="15.358"/>
                    fechaasignacion = lstDatos.get(i).get(8);//fechaasig de <fechaasig>2015-06-01</fechaasig>
                    dnicliente = lstDatos.get(i).get(9);//dni de <cliente dni="246" nombrecliente="Carlos Peres SA" />
                    nombrecliente = lstDatos.get(i).get(10);//nombrecliente de <cliente dni="246" nombrecliente="Carlos Peres SA" />
                    //db.addDocumento(codigodocumento.toString());
                    db.addDocumento(new DatosListview(
                            codigodocumento,
                            codigodocumento.toString(),
                            textodetalle,
                            R.drawable.arequipaaccsac,
                            nrosuministro,
                            codtipodoc,
                            nombretipodoc,
                            nrodocumentoseal,
                            codigobarras,
                            latitud,
                            longitud,
                            fechaasignacion,
                            dnicliente,
                            nombrecliente
                    ));
                }

                int FilasEliminadas=db.EliminarDuplicados();
                //Toast.makeText(getApplicationContext(),"Agregados: "+i+" , Duplicados eliminados: "+FilasEliminadas,Toast.LENGTH_SHORT).show();
                Snackbar.make(txtEstado, "Agregados: "+i+" , Duplicados eliminados: "+FilasEliminadas, Snackbar.LENGTH_LONG).show();
            }else if (Estadorespuesta.equals("-3")){
                Snackbar.make(txtEstado, "Sin Conexion. Verifica tu conexion a internet(cod:"+result+")", Snackbar.LENGTH_LONG).show();
            }else if(Estadorespuesta.equals("-2")){
                Snackbar.make(txtEstado, "Sin Conexion. Verifica tu conexion a internet(cod:"+result+")", Snackbar.LENGTH_LONG).show();
            }else if(Estadorespuesta.equals("-1")){
                Snackbar.make(txtEstado, "Sin Conexion. Verifica tu conexion a internet(cod:"+result+")", Snackbar.LENGTH_LONG).show();
            }else {

            }
            txtEstado.setVisibility(View.INVISIBLE);
            pbEstadoSincronizacion.setVisibility(View.INVISIBLE);
            //ivSincronizarDatos.setEnabled(true);
            //tvEjemplo.setText("Sinconizacion Finalizada");
        }
        @Override
        protected void onProgressUpdate(Integer... valor){
            //TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            txtEstado.setText("Sincronizando "+valor[0]+" Registros...");
            pbEstadoSincronizacion.setProgress(valor[0]);
        }
    }
    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        Generalidades gen;
        try {
            gen= (Generalidades)contexto.getApplicationContext();
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(gen.getTiempoLectura());
            conn.setConnectTimeout(gen.getTiempoConexion());
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
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
}
