package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Renel01 on 17/02/2018.
 */

public class GPS_Service extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiver;
    private Context ConextoServicio;
    private Generalidades gen=(Generalidades)getApplication();
    //private static Generalidades gen;
    //static final String CONSTANT_4 = "etc"+gen.getCadena();
    //private static Generalidades gene=gen;
    //final String RutaApp=gen.getCadena() + "webservices/insertarruta.php";

    //static final String RutaApp="http://accsac.com/sistemas/seal/comunicaciondispersa/webservices/insertarruta.php";
    String RutaApp="";//=gen.getCadena();
    String CodigoPersonal="";
    //static String dfads=
    private DatabaseGPS dbrutas;//=new DatabaseGPS(this);

    public GPS_Service() {
    }

    public Context getConextoServicio() {
        return ConextoServicio;
    }

    public void setConextoServicio(Context conextoServicio) {
        ConextoServicio = conextoServicio;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        SharedPreferences prefsGen = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
        RutaApp=prefsGen.getString("rutawebapp","http://www.recamedi.com/")+"webservices/insertarruta.php";
        SharedPreferences prefsApp = getSharedPreferences("preferenciasMiApp", Context.MODE_PRIVATE);
        //RutaApp=prefsGen.getString("rutawebapp","http://www.recamedi.com/")+"webservices/insertarruta.php";
        //Toast.makeText(getApplicationContext(),"RUTA: "+RutaApp,Toast.LENGTH_SHORT).show();
        CodigoPersonal=prefsApp.getString("codper","");
        //String s = gen.getCadena() + "webservices/insertarruta.php";
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i=new Intent("location_update");
                i.putExtra("coordinates",location.getLongitude()+" "+location.getLatitude());
                sendBroadcast(i);

                //Toast.makeText(getApplicationContext(),"RUTA: "+RutaApp,Toast.LENGTH_SHORT).show();


                SimpleDateFormat fechaFormato = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());
                Date fechaEjecutado = new Date();
                String sFechaEjecutado = fechaFormato.format(fechaEjecutado);
                dbrutas=new DatabaseGPS(getApplicationContext());
                long insertados=dbrutas.AgregarRuta(
                        ""+sFechaEjecutado,
                        ""+location.getLatitude(),
                        ""+location.getLongitude(),
                        ""+CodigoPersonal
                );
                if (insertados>0){
                    Toast.makeText(getApplicationContext(),"LAT: +"+location.getLatitude()+ ", LONG: "+location.getLongitude(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"NADA("+insertados+")----LAT: +"+location.getLatitude()+ ", LONG: "+location.getLongitude(),Toast.LENGTH_SHORT).show();

                }
                String DireccionUbicacion="";
                try{
                    Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> list=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if (!list.isEmpty()){
                        Address direccion=list.get(0);
                        DireccionUbicacion=direccion.getAddressLine(0);
                    }
                }catch (IOException e){
                    DireccionUbicacion=""+e;
                }
                new RegistrarRutaServidor().execute(""+CodigoPersonal,location.getLatitude()+"",location.getLongitude()+"",DireccionUbicacion+"",sFechaEjecutado);
                //Generalidades gen=(Generalidades)getApplication();
                //Aqui tengo que poner la base de datos para registrar las incidencias



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i=new Intent("gps_desactivado");
                i.putExtra("Valor","desactivado");
                sendBroadcast(i);


                /*Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);*/

            }
        };
        //Minimo tiempo para updates en Milisegundos
        final long MIN_TIEMPO_ENTRE_UPDATES = 3000;//Segundos * 60 * 1; // 1 minuto
        //Minima distancia para updates en metros.
        final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = 1; // 1.5 metros
        locationManager=(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIEMPO_ENTRE_UPDATES,MIN_CAMBIO_DISTANCIA_PARA_UPDATES,listener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager!=null){
            locationManager.removeUpdates(listener);
        }
    }
    private class RegistrarRutaServidor extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            Generalidades gen=(Generalidades)getApplication();
            String charset = "UTF-8";
            //String requestURL = gen.getCadena()+"webservices/insertarruta.php";//?Idfotovisitascampo="+IdObtenidoServidor;//,strings[1];*/
            String requestURL = RutaApp;//?Idfotovisitascampo="+IdObtenidoServidor;//,strings[1];*/

            Log.e("URL",requestURL);
            MultipartUtility multipart = null;
            String response=null;
            try {
                multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("idpersonal", ""+(strings[0]));
                multipart.addFormField("latitud", ""+(strings[1]));
                multipart.addFormField("longitud", ""+(strings[2]));
                multipart.addFormField("direccion", ""+(strings[3]));
                multipart.addFormField("fechahora", ""+(strings[4]));
                //multipart.addFilePart("uploadedfile", new File(RutaFoto));
                //response = multipart.finish(); // response from server.
                String respuestaServidor=multipart.finish();
                response=respuestaServidor;
                //Luego se registra modo Online
            } catch (IOException e) {
                response="Error_url:"+requestURL;
                e.printStackTrace();
            }

            //return respuesta;
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Servidor respuesta(+"+s+ ")",Toast.LENGTH_SHORT).show();

        }
    }

}
