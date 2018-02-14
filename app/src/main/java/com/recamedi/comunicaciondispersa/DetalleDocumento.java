package com.recamedi.comunicaciondispersa;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetalleDocumento extends AppCompatActivity {
    String RespuestaServidorGeneral;


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


    Button btnGrabar;

    DataBaseHelper db;

    private HandleXml obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_documento);
        tvCodDoc=(TextView)findViewById(R.id.tvCodDoc);
        tvNroSuministro=(TextView)findViewById(R.id.tvNroSuministro);
        tvTipoDoc=(TextView)findViewById(R.id.tvTipoDoc);
        tvCodBarra=(TextView)findViewById(R.id.tvCodBarra);
        etDni=(EditText)findViewById(R.id.etDNI);
        etLecturaMedidor=(EditText)findViewById(R.id.etLecturaMedidor);

        spEstado=(Spinner)findViewById(R.id.spEstado);
        spParentesco=(Spinner)findViewById(R.id.spParentesco);

        aaEstado=ArrayAdapter.createFromResource(this, R.array.saEstados,android.R.layout.simple_spinner_item);
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


        aaParentesco=ArrayAdapter.createFromResource(this,R.array.saParentesco,android.R.layout.simple_spinner_item);
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

        db= new DataBaseHelper(this);

        final DatosListview objDetalle=(DatosListview)getIntent().getExtras().getSerializable("objeto");
        tvCodDoc.setText(objDetalle.getTitulo());
        tvNroSuministro.setText(objDetalle.getNroSuministro());
        tvTipoDoc.setText(objDetalle.getNombreTipoDoc());
        tvCodBarra.setText(objDetalle.getCodigoBarra());
        btnGrabar=(Button)findViewById(R.id.btnGrabar);
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                DatosListview objDetalles=(DatosListview)getIntent().getExtras().getSerializable("objeto");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
                Date date = new Date();

                String fecha = dateFormat.format(date);
                db.updateDocumento(
                        objDetalle.getId(),
                        "1",//1 significara que ya se hizo la visita
                        ""+ValorEstado,//Las comillas simples no tienen ningun valor, solo es para conservar el tip de ayuda de android studio
                        ""+ValorParentesco,
                        ""+etDni.getText(),
                        ""+etLecturaMedidor.getText(),
                        ""+fecha
                );
                Toast.makeText(getApplicationContext(),"Se guardo el archivo correctamente",Toast.LENGTH_SHORT).show();
                /*Intent acDocumentosLista=new Intent(getApplicationContext(),DocumentosPendientes.class);
                startActivity(acDocumentosLista);
                        objDetalles.getNroSuministro(),
                        objDetalles.getCodTipoCod(),
                        objDetalles.getNombreTipoDoc(),
                        objDetalles.getCodigoBarra(),
                        objDetalles.getLatitud(),
                        objDetalles.getLongitud(),
                        objDetalles.getFechaAsigando(),
                        objDetalles.getClienteDNI(),
                        objDetalles.getClienteNombre()
                );
                db.updateDocumento(datosListview);*/

                Generalidades general=(Generalidades)getApplication();
                String url=general.getCadena()+"webservices/capturardatoscel.php";
                obj=new HandleXml(url);
                obj.fetchXML(1);
                while (obj.parsingCompete);
                String respuestaserv=obj.getEstadoEnvioDocumento();
                Toast.makeText(getApplicationContext(),""+respuestaserv,Toast.LENGTH_SHORT).show();

                if (respuestaserv.trim().equals("200")){
                    Intent acDocumentosPendientes=new Intent(getApplicationContext(),DocumentosPendientes.class);
                    startActivity(acDocumentosPendientes);
                    finish();//Cierra este Intent(formulario)
                }
            }
        });


    }


    private void crearXml() throws IOException {
        //Crear directorio
        File CarpetaPrincipal = new File(Environment.getExternalStorageDirectory() + "/recamedi");
        // Comprobamos si la carpeta está ya creada
        // Si la carpeta no está creada, la creamos.
        //File myNewFolder = null;
        if(!CarpetaPrincipal.isDirectory()) {

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            CarpetaPrincipal = new File(extStorageDirectory,"recamedi");//recamedi es el nombre de la Carpeta que vamos a crear
            CarpetaPrincipal.mkdir(); //creamos la carpeta
            Log.d("CARPETAS","Se volvio a crear "+CarpetaPrincipal);
            Toast.makeText(getApplicationContext(),"Se volvio a crear "+CarpetaPrincipal, Toast.LENGTH_SHORT).show();
        }else{
            Log.d("CARPETAS","La carpeta ya estaba creada");
        }
        try{
            //Creamos el serializer
            XmlSerializer ser = Xml.newSerializer();
            //Creamos un fichero en memoria interna
            //File ruta_sd = Environment.getExternalStorageDirectory();
            File f = new File(CarpetaPrincipal, "prueba_sd.xml");
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            new FileOutputStream(f));
            //Asignamos el resultado del serializer al fichero
            ser.setOutput(fout);
            //Construimos el XML
            ser.startTag("", "comunicaciones");

            ser.startTag("", "codigos");
            ser.attribute(null,"codigodoc","256");
            ser.attribute(null,"nrosuministro","321323");
            //ser.text("Usuario1");
            ser.endTag("", "codigos");
            //<tipodoc codigo="3" nombretipodoc="Nombre Del tipo de Doc3"/>
            ser.startTag("", "tipodoc");
            ser.attribute(null,"codigo","3");
            ser.attribute(null,"nombretipodoc","Nombre Del tipo de Doc3");
            ser.endTag("", "tipodoc");
            //<codigobarra>2018-11-13158-3</codigobarra>
            ser.startTag("", "codigobarra");
            ser.text("2018-11-13158-3");
            ser.endTag("", "codigobarra");
            //<coordenadas lat="-153.255" long="15.358"/>
            ser.startTag("", "coordenadas");
            ser.attribute(null,"lat","-153.255");
            ser.attribute(null,"long","15.358");
            ser.endTag("", "coordenadas");
            //<codigobarra>2018-11-13158-3</codigobarra>
            ser.startTag("", "fechaasig");
            ser.text("2015-06-03");
            ser.endTag("", "fechaasig");
            // <cliente dni="246" nombrecliente="Carlos Peres SA" />
            ser.startTag("", "cliente");
            ser.attribute(null,"dni","246");
            ser.attribute(null,"nombrecliente","Carlos Peres SA");
            ser.endTag("", "cliente");

            ser.endTag("", "comunicaciones");

            ser.endDocument();
            fout.close();

            Toast.makeText(this, "Archivo guardado "+fout, Toast.LENGTH_SHORT).show();


            /*File archivotxt = new File(CarpetaPrincipal, "miXml.xml");
            StringBuilder sb = new StringBuilder();

            //Construimos el XML
            sb.append("");
            sb.append("<Usuario1>" + "Usuario1" + "</Usuario1> \r");
            sb.append("<Usuario1>" + "ApellidosUsuario1" + "</Usuario1> \r");
            sb.append("");

            FileWriter writer = new FileWriter(archivotxt);
            //writer.append("Este es el contenido de mi archivo de texto.");
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Archivo guardado "+archivotxt, Toast.LENGTH_SHORT).show();*/
        }
        catch(IOException e)
        {
            //mostrar en el Logcat el error
            Log.d("Error al crear archivo",e.getStackTrace().toString());
        }



        try
        {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("prueba_int.txt")));

            String texto = fin.readLine();
            Toast.makeText(getApplicationContext(),fin + " _", Toast.LENGTH_SHORT).show();
            fin.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
    }
}
