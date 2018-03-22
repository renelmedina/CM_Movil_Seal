package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by Renel01 on 20/03/2018.
 */

public class VerificarPermisos {
    private Activity actividad;
    private Context contexto;

    public Activity getActividad() {
        return actividad;
    }

    public void setActividad(Activity actividad) {
        this.actividad = actividad;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public VerificarPermisos(Activity actividad, Context contexto) {
        this.actividad = actividad;
        this.contexto = contexto;
    }

    public void verificarTodosPermisos(){
        Boolean Verificador=null;
        Boolean Escritura = null;
        Boolean GPS=null;
        if (ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Escritura=false;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Escritura=true;

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(actividad,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
                //scritura=false;
            }
        }else {
            //Escritura=true;
        }
        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            //Log.e("Location", "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);

            //GPS=false;

            //return;
        }else{
            Log.i("Location", "Permisos necesarios OK!.");
            //GPS=true;
            /*gps=new GeoLocation(this,this);
            gps.IniciarServicio();*/
        }
        //Verificador=(GPS&&Escritura)?true:false;
        //return Verificador;
    }
    public Boolean permisoUbicacion(){
        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Requiere permisos para Android 6.0
            Log.e("Location", "No se tienen permisos necesarios!, se requieren.");
            ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);
            AlertDialog.Builder mensajitoalusurio=new AlertDialog.Builder(contexto);
            mensajitoalusurio.setTitle("Permiso a la ubicacion");
            mensajitoalusurio.setMessage("No se Guardo Nada, porque no esta activo el GPS");
            mensajitoalusurio.setPositiveButton("Activar GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //miUbicacion();
                    //Se hace algo para evitar que pase a la siguiente ventana, para que permanesca aqui hasta que se complete la elctura
                }
            });
            return false;
            //return;
        }else{
            Log.i("Location", "Permisos necesarios OK!.");

            return true;
        }
    }
    public Boolean permisoEscritura(){
        if (ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Escritura=false;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Escritura=true;
                return false;
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(actividad,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
                //scritura=false;
                return false;
            }
        }else {
            //Escritura=true;
            return true;
        }
    }
    public void SolicitarPermisoEscritura(){
        ActivityCompat.requestPermissions(actividad,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2);
    }
    public void SolicitarPermisoGPS(){
        ActivityCompat.requestPermissions(actividad, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 225);

    }
}
