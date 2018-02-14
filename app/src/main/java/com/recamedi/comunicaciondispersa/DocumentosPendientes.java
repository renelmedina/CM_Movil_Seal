package com.recamedi.comunicaciondispersa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentosPendientes extends AppCompatActivity {


    private HandleXml obj;

    ListView listadatos;
    ArrayList<DatosListview> Lista;
    List<DatosListview> lstDocumentos=new ArrayList<>();
    DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentos_pendientes);

        listadatos=(ListView)findViewById(R.id.lstDatos);
        Lista=new ArrayList<DatosListview>();

//        Generalidades general=(Generalidades)getApplication();
//        String url=general.getCadena()+"login.php";
//        obj=new HandleXml(url);
//        obj.fetchXML();
//        Toast.makeText(getApplicationContext(),"Sincronizando...", Toast.LENGTH_SHORT).show();
//        while (obj.parsingCompete){
//        }
//        Toast.makeText(getApplicationContext(),"Sincronizacion terminada", Toast.LENGTH_SHORT).show();
//
        db= new DataBaseHelper(this);

        /*txt1.setText(obj.getLstDatos().get(0).get(0));
        txt2.setText(obj.getLstDatos().get(1).get(0));
        txt3.setText(obj.getLstDatos().get(2).get(0));*/
        /*Toast.makeText(getApplicationContext(),"Tamaño: "+obj.getLstDatos().size(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"Tamaño 1: "+obj.getLstDatos().get(0).size(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"Tamaño 2: "+obj.getLstDatos().get(1).size(), Toast.LENGTH_SHORT).show();*/



        mostrarDatos();
    }
    private void mostrarDatos(){
        lstDocumentos=db.getAllDocumentos();
        /*AdaptadorDatosListView adapter=new AdaptadorDatosListView(DocumentosPendientes.this,lstDocumentos);
        listadatos.setAdapter(adapter);*/
        AdaptadorDatosListView miAdaptador= new AdaptadorDatosListView(getApplicationContext(),lstDocumentos);
        listadatos.setAdapter(miAdaptador);
        listadatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
                DatosListview objclick=(DatosListview) adapterView.getItemAtPosition(posicion);
                Intent acDocumentosDetalle=new Intent(getApplicationContext(),DetalleDocumento.class);
                acDocumentosDetalle.putExtra("objeto",(Serializable)objclick);
                startActivity(acDocumentosDetalle);
                finish();//Cierra este Intent(formulario)
            }
        });

    }
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




        return super.onOptionsItemSelected(opcion_menu);
    }
    private class ListarDocumentos extends AsyncTask<String,Void,String>{
        //Antes de iniciar el proceso en segundo plano
        @Override
        protected void onPreExecute(){

        }
        //Ejecutando en segundo plano
        @Override
        protected String doInBackground(String... rutaAplicacion) {
            return null;
        }
        //Despues de terminar la ejecucion en segundo plano y mostrar los resultados o algun aviso del mismo
        @Override
        protected void onPostExecute(String result){

        }
    }

}

//////////////////////////////////////////////Originalmente era asi/////////////////////////////////
//
//
//        package com.recamedi.comunicaciondispersa;
//
//        import android.content.Intent;
//        import android.os.Environment;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.util.Xml;
//        import android.view.View;
//        import android.widget.AdapterView;
//        import android.widget.Button;
//        import android.widget.EditText;
//        import android.widget.ListView;
//        import android.widget.Toast;
//
//        import org.xmlpull.v1.XmlSerializer;
//
//        import java.io.File;
//        import java.io.FileOutputStream;
//        import java.io.IOException;
//        import java.io.OutputStreamWriter;
//        import java.io.Serializable;
//        import java.net.MalformedURLException;
//        import java.util.ArrayList;
//        import java.util.List;
//
//public class DocumentosPendientes extends AppCompatActivity {
//
//
//    private HandleXml obj;
//
//    ListView listadatos;
//    ArrayList<DatosListview> Lista;
//    List<DatosListview> lstDocumentos=new ArrayList<>();
//    DataBaseHelper db;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_documentos_pendientes);
//        listadatos=(ListView)findViewById(R.id.lstDatos);
//        Lista=new ArrayList<DatosListview>();
//
//        Generalidades general=(Generalidades)getApplication();
//        String url=general.getCadena()+"login.php";
//        obj=new HandleXml(url);
//        obj.fetchXML();
//        while (obj.parsingCompete);
//
//        db= new DataBaseHelper(this);
//
//        /*txt1.setText(obj.getLstDatos().get(0).get(0));
//        txt2.setText(obj.getLstDatos().get(1).get(0));
//        txt3.setText(obj.getLstDatos().get(2).get(0));*/
//        /*Toast.makeText(getApplicationContext(),"Tamaño: "+obj.getLstDatos().size(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),"Tamaño 1: "+obj.getLstDatos().get(0).size(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),"Tamaño 2: "+obj.getLstDatos().get(1).size(), Toast.LENGTH_SHORT).show();*/
//        String textodetalle="";
//        Integer codigodocumento;
//        String nrosuministro;
//        String codtipodoc;
//        String nombretipodoc;
//        String codigobarras;
//        String latitud;
//        String longitud;
//        String fechaasignacion;
//        String dnicliente;
//        String nombrecliente;
//        //Crear directorio
//        File CarpetaPrincipal = new File(Environment.getExternalStorageDirectory() + "/recamedi");
//        // Comprobamos si la carpeta está ya creada
//        // Si la carpeta no está creada, la creamos.
//        //File myNewFolder = null;
//        if(!CarpetaPrincipal.isDirectory()) {
//
//            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//            CarpetaPrincipal = new File(extStorageDirectory,"recamedi");//recamedi es el nombre de la Carpeta que vamos a crear
//            CarpetaPrincipal.mkdir(); //creamos la carpeta
//            Log.d("CARPETAS","Se volvio a crear "+CarpetaPrincipal);
//            Toast.makeText(getApplicationContext(),"Se volvio a crear "+CarpetaPrincipal, Toast.LENGTH_SHORT).show();
//        }else{
//            Log.d("CARPETAS","La carpeta ya estaba creada");
//        }
//        try{
//            //Creamos el serializer
//            XmlSerializer ser = Xml.newSerializer();
//            String varArchivo="_pen";
//            //Creamos un fichero en memoria interna
//            //File ruta_sd = Environment.getExternalStorageDirectory();
//            File f = new File(CarpetaPrincipal, "prueba_sdxdxd"+varArchivo+".xml");
//            OutputStreamWriter fout;
//            //verificamos la existencia del archivo
//            if (f.isFile()){
//                fout =
//                        new OutputStreamWriter(
//                                new FileOutputStream(f,true));//Agregamos mas contenido al archivo existente
//            }else{
//                fout =
//                        new OutputStreamWriter(
//                                new FileOutputStream(f));//creamos el archivo de cero.
//            }
//            //Asignamos el resultado del serializer al fichero
//            ser.setOutput(fout);
//            ser.startTag("", "datos");
//            //Asignamos lo capturado de internet a variables tipo array
//            for (int i=0; i<obj.getLstDatos().size();i++){
//                codigodocumento=Integer.parseInt(obj.getLstDatos().get(i).get(0).toString());//codigodoc de <codigos codigodoc="254" nrosuministro="321321"/>
//                nrosuministro=obj.getLstDatos().get(i).get(1);//nrosuministro de <codigos codigodoc="254" nrosuministro="321321"/>
//                codtipodoc=obj.getLstDatos().get(i).get(2);
//                nombretipodoc=obj.getLstDatos().get(i).get(3);//nombretipodoc de <tipodoc codigo="1" nombretipodoc="Nombre Del tipo de Doc1"/>
//                codigobarras=obj.getLstDatos().get(i).get(4);//codigobarra de <codigobarra>2018-11-13158-1</codigobarra>
//                latitud=obj.getLstDatos().get(i).get(5);//lat de <coordenadas lat="-153.255" long="15.358"/>
//                longitud=obj.getLstDatos().get(i).get(6);//long de <coordenadas lat="-153.255" long="15.358"/>
//                fechaasignacion=obj.getLstDatos().get(i).get(7);//fechaasig de <fechaasig>2015-06-01</fechaasig>
//                dnicliente=obj.getLstDatos().get(i).get(8);//dni de <cliente dni="246" nombrecliente="Carlos Peres SA" />
//                nombrecliente=obj.getLstDatos().get(i).get(9);//nombrecliente de <cliente dni="246" nombrecliente="Carlos Peres SA" />
//                //textodetalle="CodigoDoc: "+codigodocumento+"\n";
//                textodetalle="Suministro Nro:" + nrosuministro +"\n";
//                textodetalle+="Tipo Doc:" +nombretipodoc+"\n";
//                textodetalle+="Cod. Barra:" +codigobarras+"\n";
//                /*textodetalle+="Lat:" +latitud+"\n";
//                textodetalle+="Long:" +longitud+"\n";*/
//                textodetalle+="Posicion:"+latitud+","+longitud;
//                /*textodetalle+="Fecha Asignado:" +fechaasignacion+"\n";
//                textodetalle+="DNI Cliente:" +dnicliente+"\n";
//                textodetalle+="Nombre Cliente:" +nombrecliente;*/
//                Lista.add(
//                        new DatosListview(
//                                codigodocumento,
//                                codigodocumento.toString(),
//                                textodetalle,
//                                R.drawable.arequipaaccsac,
//                                nrosuministro,
//                                codtipodoc,
//                                nombretipodoc,
//                                codigobarras,
//                                latitud,
//                                longitud,
//                                fechaasignacion,
//                                dnicliente,
//                                nombrecliente
//                        )
//                );
//                db.addDocumento(new DatosListview(
//                        codigodocumento,
//                        codigodocumento.toString(),
//                        textodetalle,
//                        R.drawable.arequipaaccsac,
//                        nrosuministro,
//                        codtipodoc,
//                        nombretipodoc,
//                        codigobarras,
//                        latitud,
//                        longitud,
//                        fechaasignacion,
//                        dnicliente,
//                        nombrecliente
//                ));
//
//
//                //DatosListview(int id, String titulo, String detalle, int imagen, String nroSuministro, String codTipoCod, String nombreTipoDoc, String codigoBarra, String latitud, String longitud, String fechaAsigando, String clienteDNI, String clienteNombre)
//                //Toast.makeText(getApplicationContext(),textodetalle, Toast.LENGTH_SHORT).show();
//
//                //Construimos el XML para trabajar en modo offline
//                ser.startTag("", "comunicaciones");
//                ser.startTag("", "codigos");
//                ser.attribute(null,"codigodoc",codigodocumento.toString());
//                ser.attribute(null,"nrosuministro",nrosuministro);
//                //ser.text("Usuario1");
//                ser.endTag("", "codigos");
//                //<tipodoc codigo="3" nombretipodoc="Nombre Del tipo de Doc3"/>
//                ser.startTag("", "tipodoc");
//                ser.attribute(null,"codigo",codtipodoc);
//                ser.attribute(null,"nombretipodoc",nombretipodoc);
//                ser.endTag("", "tipodoc");
//                //<codigobarra>2018-11-13158-3</codigobarra>
//                ser.startTag("", "codigobarra");
//                ser.text(codigobarras);
//                ser.endTag("", "codigobarra");
//                //<coordenadas lat="-153.255" long="15.358"/>
//                ser.startTag("", "coordenadas");
//                ser.attribute(null,"lat",latitud);
//                ser.attribute(null,"long",longitud);
//                ser.endTag("", "coordenadas");
//                //<codigobarra>2018-11-13158-3</codigobarra>
//                ser.startTag("", "fechaasig");
//                ser.text(fechaasignacion);
//                ser.endTag("", "fechaasig");
//                // <cliente dni="246" nombrecliente="Carlos Peres SA" />
//                ser.startTag("", "cliente");
//                ser.attribute(null,"dni",dnicliente);
//                ser.attribute(null,"nombrecliente",nombrecliente);
//                ser.endTag("", "cliente");
//
//                ser.endTag("", "comunicaciones");
//            }
//            ser.endTag("", "datos");
//
//
//            ser.endDocument();
//            fout.close();
//
//            Toast.makeText(this, "Archivo guardado "+fout, Toast.LENGTH_SHORT).show();
//
//
//            /*File archivotxt = new File(CarpetaPrincipal, "miXml.xml");
//            StringBuilder sb = new StringBuilder();
//
//            //Construimos el XML
//            sb.append("");
//            sb.append("<Usuario1>" + "Usuario1" + "</Usuario1> \r");
//            sb.append("<Usuario1>" + "ApellidosUsuario1" + "</Usuario1> \r");
//            sb.append("");
//
//            FileWriter writer = new FileWriter(archivotxt);
//            //writer.append("Este es el contenido de mi archivo de texto.");
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
//            Toast.makeText(this, "Archivo guardado "+archivotxt, Toast.LENGTH_SHORT).show();*/
//        }
//        catch(IOException e){
//            //mostrar en el Logcat el error
//            Log.d("Error al crear archivo",e.getStackTrace().toString());
//        }
//
//
//
//        //for (int i=0; i<obj.getLstDatos().size())
//
//
//
//
//        AdaptadorDatosListView miAdaptador= new AdaptadorDatosListView(getApplicationContext(),Lista);
//        listadatos.setAdapter(miAdaptador);
//        listadatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
//                DatosListview objclick=(DatosListview) adapterView.getItemAtPosition(posicion);
//                Intent acDocumentosDetalle=new Intent(getApplicationContext(),DetalleDocumento.class);
//                acDocumentosDetalle.putExtra("objeto",(Serializable)objclick);
//                startActivity(acDocumentosDetalle);
//            }
//        });
//        /*txt1=findViewById(R.id.etTextoBuscar);
//        txt2=findViewById(R.id.etCurrency);
//        txt3=findViewById(R.id.etTemperatura);
//        txt4 = findViewById(R.id.etHumedad);
//
//
//
//
//        btnBuscar=findViewById(R.id.btnBuscar);*/
//        /*btnBuscar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //http://localhost/clientes/accsac.com/Aplicaciones/seal/comunicaciondispersa/login.php
//                //String url="http://192.168.1.34/clientes/accsac.com/Aplicaciones/seal/comunicaciondispersa/login.php";
//
//                Generalidades general=(Generalidades)getApplication();
//                String url=general.getCadena()+"login.php";
//                obj=new HandleXml(url);
//                obj.fetchXML();
//                while (obj.parsingCompete);
//                //txt1.setText(obj.getCountry());
//                //txt2.setText(obj.getHumidity());
//                //txt3.setText(obj.getTemperature());
//                //List listaDatos= obj.getLstDatos();
//
//                txt1.setText(obj.getLstDatos().get(0).get(0));
//                txt2.setText(obj.getLstDatos().get(1).get(0));
//                txt3.setText(obj.getLstDatos().get(2).get(0));
//                obj.getLstDatos().size();
//            }
//        });*/
//        mostrarDatos();
//    }
//    private void mostrarDatos(){
//        lstDocumentos=db.getAllDocumentos();
//        AdaptadorDatosListView adapter=new AdaptadorDatosListView(DocumentosPendientes.this,lstDocumentos);
//        listadatos.setAdapter(adapter);
//
//    }
//}

