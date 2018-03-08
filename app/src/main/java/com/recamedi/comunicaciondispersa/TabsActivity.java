package com.recamedi.comunicaciondispersa;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TabsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    FPendientes fp;
    FHechos fh;
    FEnviados fe;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Documentos");

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aqui se pone para enviar todos los trabajos a modo online

                new EnviarDatosServidor().execute("");

                Snackbar.make(view, "Enviando Datos...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabs, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    fp=new FPendientes();
                    return fp;
                case 1:
                    fh=new FHechos();
                    return fh;
                case 2:
                    fe=new FEnviados();
                    return fe;
            }
            return PlaceholderFragment.newInstance(position + 1);
            //return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    private class EnviarDatosServidor extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {
            publishProgress(0);
            //String respuesta=postArchivo(strings[0]);
            /*SubirArchivos sa=new SubirArchivos(strings[0],gen.getCadena()+"webservices/guardarfoto.php?Idfotovisitascampo="+IdObtenidoServidor,strings[1]);
            String respuesta=sa.postArchivo();
            */
            Generalidades gen=(Generalidades)getApplication();
            String charset = "UTF-8";
            String requestURL = gen.getCadena()+"webservices/guardarfoto.php";//?Idfotovisitascampo="+IdObtenidoServidor;//,strings[1];*/
            Log.e("URL",requestURL);
            DataBaseHelper db;
            db= new DataBaseHelper(getApplicationContext());
            String response="0";


            //Primero se recupera el listado de fotografias sel BD offline para enviar.
            ArrayList<ArrayList> listadoDocEntregado=db.ListarDocHechosOffLine();
                    /*Iterator<ArrayList> iterator=listadodefotos.iterator();
                    while (iterator.hasNext()){
                        listadodefotos.get(0).get(1);

                    }*/
            //response=listadodefotos.size()+"-"+strings[1];
            if (listadoDocEntregado.size() <1){
                //Toast.makeText(getApplicationContext(),"No hay nada para enviar",Toast.LENGTH_SHORT).show();
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

                Generalidades general;
                general=(Generalidades)getApplication();
                String usuario=gen.getUsuarioActual();
                String password=gen.getPasswordActual();
                String contentAsString;//respuesta en formato XMl del servidor
                String respuestaparaaccion=null;
                int IdObtenidoServidor=0;

                String myurl= general.getCadena()+"webservices/guardarlectura.php?"
                        +"usuario="+usuario+"&password="+password+"&"
                        +"DocumentosTrabajoID="+CodigoDocumento+"&"
                        +"FechaAsignado="+FechaAsignacion+"&"
                        +"FechaEjecutado="+FechaVisita+"&"
                        +"Estado="+EstadoEntrega+"&"
                        +"EstadoSeal="+EstadoFirma+"&"
                        +"NombreRecepcionador=&"
                        +"DNIRecepcionador="+DNIRecepcion+"&"
                        +"Parentesco="+Parentesco+"&"
                        +"LecturaMedidor="+LecturaMedidor+"&"
                        +"LatitudVisita="+VisitaLatitud+"&"
                        +"LongitudVisita="+VisitaLongitud+"&"
                        +"Observaciones=";
                try {
                    publishProgress(0);
                    Log.i("URL",""+strings[0]);
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
                        int respuesta = conn.getResponseCode();
                        Log.d("respuesta", "The response is: " + respuesta);
                        is = conn.getInputStream();
                        //conn.getInputStream().read();
                        // Convert the InputStream into a string
                        //String contentAsString = readIt(is);
                        DataInputStream dis = new DataInputStream(is);
                        String inputLine;
                        String xmlDelServidor="";
                        while ((inputLine = dis.readLine()) != null) {
                            //Log.d("XML",inputLine);
                            xmlDelServidor +=inputLine;
                        }
                        contentAsString = xmlDelServidor;


                        //return contentAsString;
                        // Makes sure that the InputStream is closed after the app is
                        // finished using it.
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
                databaseoffline= new DataBaseHelper(getApplicationContext());
                //String Estadorespuesta=LeerXml(""+xmlTexto);
                String Estadorespuesta=respuestaparaaccion;
                int CodigoDocumentito = Integer.parseInt(CodigoDocumento);
                if (Integer.parseInt(Estadorespuesta)>0){
                    publishProgress(i);
                    response=i+"";
                    //Envia las fotos a servidor
                    if (IdObtenidoServidor>0){

                        new Enviarfotos().execute(IdObtenidoServidor+"",CodigoDocumentito+"");//CodigodevuelltServidir, CodigoDoc
                    }else {
                        Toast.makeText(getApplicationContext(),"ID del registro"+IdObtenidoServidor,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"Se envio "+s+" al servidor",Toast.LENGTH_LONG).show();
            }else if(s.equals("-6")) {
                Toast.makeText(getApplicationContext(),"No hay documentos para enviar("+s+")",Toast.LENGTH_LONG).show();
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
            Generalidades gen;
            gen=(Generalidades)getApplication();
            //usuario=gen.getUsuarioActual();
            //password=gen.getPasswordActual();
            String charset = "UTF-8";
            String requestURL = gen.getCadena()+"webservices/guardarfoto.php";//?Idfotovisitascampo="+IdObtenidoServidor;//,strings[1];*/
            Log.e("URL",requestURL);
            DataBaseHelper db;
            db= new DataBaseHelper(getApplicationContext());
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
            Toast.makeText(getApplicationContext(),"resputa: "+subirfoto,Toast.LENGTH_LONG).show();
        }
    }
}
