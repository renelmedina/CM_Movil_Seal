package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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

    LocationManager locationManager;

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


    Button btnEntrgado;
    Button btnRezagado;

    DataBaseHelper db;

    private HandleXml obj;

    Double Latitud = 0.0;
    Double Longitud = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_documento);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled())
            showAlert();
        tvCodDoc = (TextView) findViewById(R.id.tvCodDoc);
        tvNroSuministro = (TextView) findViewById(R.id.tvNroSuministro);
        tvTipoDoc = (TextView) findViewById(R.id.tvTipoDoc);
        tvCodBarra = (TextView) findViewById(R.id.tvCodBarra);
        etDni = (EditText) findViewById(R.id.etDNI);
        etLecturaMedidor = (EditText) findViewById(R.id.etLecturaMedidor);

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

        final DatosListview objDetalle = (DatosListview) getIntent().getExtras().getSerializable("objeto");
        tvCodDoc.setText(objDetalle.getTitulo());
        tvNroSuministro.setText(objDetalle.getNroSuministro());
        tvTipoDoc.setText(objDetalle.getNombreTipoDoc());
        tvCodBarra.setText(objDetalle.getCodigoBarra());

        btnRezagado = (Button) findViewById(R.id.btn_ddRezagado);

        btnEntrgado = (Button) findViewById(R.id.btn_ddEntregado);
        btnEntrgado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RegistrarDocumentoTrabajo(objDetalle);
                miUbicacion();
            }
        });


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
                        "usa esta app")
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
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
    private void RegistrarDocumentoTrabajo(DatosListview objDetalle) {
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

    private class RegistrarDatosServidor extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute(){
            //Se desactivan el boton hasta que culmine el proceso
            btnEntrgado.setEnabled(false);
            btnRezagado.setEnabled(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            //se activa los botones una vez culminado el proceso
            btnEntrgado.setEnabled(false);
            btnRezagado.setEnabled(false);
        }
        @Override
        protected void onProgressUpdate(Integer... valor){
            /*TextView tvEjemplo=(TextView)findViewById(R.id.tvEjemplo);
            tvEjemplo.setText("Sincronizando "+valor[0]+" Registros...");
            pbEstadoSincronizacion.setProgress(valor[0]);*/
        }
    }
}
