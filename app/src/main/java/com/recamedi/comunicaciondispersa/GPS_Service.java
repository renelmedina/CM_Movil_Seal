package com.recamedi.comunicaciondispersa;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

/**
 * Created by Renel01 on 17/02/2018.
 */

public class GPS_Service extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private BroadcastReceiver broadcastReceiver;
    private Context ConextoServicio;

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

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i=new Intent("location_update");
                i.putExtra("coordinates",location.getLongitude()+" "+location.getLatitude());
                sendBroadcast(i);
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
}
