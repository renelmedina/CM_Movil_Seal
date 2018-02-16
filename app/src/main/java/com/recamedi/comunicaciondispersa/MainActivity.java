package com.recamedi.comunicaciondispersa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView ivSincronizarDatos;
    Generalidades gen;

    private HandleXml obj;
    public String xmlTexto;
    List<List<String>>  lstDatos=null;
    DataBaseHelper db;

    String usuario="";
    String password="";
    private ProgressBar pbEstadoSincronizacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ProgressBarr
        pbEstadoSincronizacion = (ProgressBar) findViewById(R.id.pbEstadoSincronizacion);
        pbEstadoSincronizacion.setMax(5000);
        pbEstadoSincronizacion.setVisibility(View.INVISIBLE);
        TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
        tvEjemplo.setVisibility(View.INVISIBLE);

        //Base de Datos
        db= new DataBaseHelper(this);
        gen= (Generalidades)this.getApplication();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //EjecutarLogin();

        /*Cargando Preferencias*/
        SharedPreferences prefs = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        Generalidades gen= (Generalidades)this.getApplication();
        gen.setCadena(prefs.getString("rutawebapp","recamedi.com"));
        //Toast.makeText(this, gen.getCadena(), Toast.LENGTH_SHORT).show();
        //Se captura los datos del activity anterior
//        Intent intent=getIntent();
//        Bundle extras=intent.getExtras();
//        Boolean hayExtras=intent.hasExtra("acLoginUser");
//        if (hayExtras){
//            usuario=extras.getString("acLoginUser");
//            password=extras.getString("acLoginPassword");
//        }
        usuario=gen.getUsuarioActual();
        password=gen.getPasswordActual();
        ivSincronizarDatos=(ImageView)findViewById(R.id.ivSincronizarDatos);
        ivSincronizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Generalidades gen= (Generalidades)getApplication();
                //Toast.makeText(getApplicationContext(), gen.getCadena(), Toast.LENGTH_SHORT).show();

                SincronizarDatos(usuario,password);

            }
        });

        GeoLocation geoLocation=new GeoLocation(this);
        geoLocation.IniciarServicio();
        geoLocation.muestraPosicionActual();
        Toast.makeText(getApplicationContext(), "Latitud: "+geoLocation.getLatitud()+" Longitud: "+geoLocation.getLongitud(), Toast.LENGTH_SHORT).show();


    }

    private void SincronizarDatos(String usuario,String password) {
        Generalidades gen= (Generalidades)this.getApplication();

        Toast.makeText(getApplicationContext(),usuario +""+password,Toast.LENGTH_SHORT).show();
        new ListarDocumentos().execute(gen.getCadena()+"webservices/sincronizardatos.php?usuario="+usuario+"&password="+password);


    }
    private class ListarDocumentos extends AsyncTask<String,Integer,String> {
        //Antes de iniciar el proceso en segundo plano
        @Override
        protected void onPreExecute(){
            TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            tvEjemplo.setVisibility(View.VISIBLE);
            pbEstadoSincronizacion.setVisibility(View.VISIBLE);
            pbEstadoSincronizacion.setProgress(0);
            //sToast.makeText(getApplicationContext(),"Sincronizando Datos...",Toast.LENGTH_SHORT).show();
            ivSincronizarDatos.setEnabled(false);
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
                xmlTexto=null;
                return "-3";
            }
        }
        //

        //Despues de terminar la ejecucion en segundo plano y mostrar los resultados o algun aviso del mismo
        @Override
        protected void onPostExecute(String result){
            xmlTexto=result;
            TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            //tvEjemplo.setText(""+result.toString());
            //tvEstadoSession.setText("Usuario Aceptado");
            DataBaseHelper db;
            db= new DataBaseHelper(getApplicationContext());
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
                Snackbar.make(tvEjemplo, "Agregados: "+i+" , Duplicados eliminados: "+FilasEliminadas, Snackbar.LENGTH_LONG).show();
            }else if (Estadorespuesta.equals("-3")){
                Snackbar.make(tvEjemplo, "Sin Conexion. Verifica tu conexion a internet(cod:"+xmlTexto+")", Snackbar.LENGTH_LONG).show();
            }else if(Estadorespuesta.equals("-2")){
                Snackbar.make(tvEjemplo, "Sin Conexion. Verifica tu conexion a internet(cod:"+xmlTexto+")", Snackbar.LENGTH_LONG).show();
            }else if(Estadorespuesta.equals("-1")){
                Snackbar.make(tvEjemplo, "Sin Conexion. Verifica tu conexion a internet(cod:"+xmlTexto+")", Snackbar.LENGTH_LONG).show();
            }else {

            }
            tvEjemplo.setVisibility(View.INVISIBLE);
            pbEstadoSincronizacion.setVisibility(View.INVISIBLE);
            ivSincronizarDatos.setEnabled(true);
            //tvEjemplo.setText("Sinconizacion Finalizada");
        }
        @Override
        protected void onProgressUpdate(Integer... valor){
            TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            tvEjemplo.setText("Sincronizando "+valor[0]+" Registros...");
            pbEstadoSincronizacion.setProgress(valor[0]);
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
    //Leers XMLrecibido
    public String LeerXml(String XMLtextoParsear){//Esta funcion no se usa, pero funciona bien, solo para ver el progressbarr debe estar dentro del mismo doInBackground
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
            xpp.setInput(new StringReader(XMLtextoParsear));
            String texto = null;
            int eventType=xpp.getEventType();
            int indice=0;
            DataBaseHelper db;
            db= new DataBaseHelper(getApplicationContext());
            while (eventType!= XmlPullParser.END_DOCUMENT){
                String name=xpp.getName();

                switch (eventType){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        texto=xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("codigos")){//Nombre del tag del archivo xml
                            /*Obtiene de la etiqueta <codigos />*/
                            //codigos=myParser.getAttributeValue(null,"codigodoc");

                            /*Boolean Existe=db.VerificarRegistroRedundante(xpp.getAttributeValue(null,"codigodoc"));
                            if (Existe){
                                break;
                            }*/
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

    }
    /*Para abrir activities*/
    public void EjecutarLogin(){
        Intent actLogin = new Intent(this,LoginClase.class);
        startActivity(actLogin);

    }
    public void EjecutarConfiguraciones(){
        Intent actConfiguracione = new Intent(this,Configuraciones.class);
        startActivity(actConfiguracione);

    }
    public void EjecutarPendientes(){
        Intent acPendientes= new Intent(this,DocumentosPendientes.class);
        startActivity(acPendientes);
    }
    public void MenuVisual(){
        Intent actMenuVisual = new Intent(this,MenuVisual.class);
        startActivity(actMenuVisual);

    }
    /*metodos creados por android por defecto*/

    //El Menu
    @Override
    public boolean onCreateOptionsMenu(Menu mimenu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menuprincipal,mimenu);
        return true;
    }

    //El evento que desencadenara cada option del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = opcion_menu.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.itEnviarDatos) {
            return true;
        }
        if (id == R.id.itReenviarDatos) {
            return true;
        }

        if (id == R.id.itCapturarDatos) {
            EjecutarPendientes();
            return true;
        }
        if (id == R.id.itConfiguracion) {
            EjecutarConfiguraciones();
            return true;
        }
        if (id == R.id.itCerrarSession) {
            EjecutarLogin();
            return true;
        }
        if (id == R.id.itSalirPrograma) {
            return true;
        }
        if (id == R.id.itAcercaDe) {
            MenuVisual();
            return true;
        }



        return super.onOptionsItemSelected(opcion_menu);
    }
}
