package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Renel01 on 16/02/2018.
 */

public class GeoLocation implements LocationListener {
    private Boolean activado;
    private Double Longitud;
    private Double Latitud;
    private String DireccionUbicacion;
    public LocationManager handle;
    private String provide;
    private Activity act;
    private Context contexto;

    public Activity getAct() {
        return act;
    }

    public void setAct(Activity act) {
        this.act = act;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public GeoLocation(Activity act, Context contextito) {
        this.act = act;
        this.contexto=contextito;
    }

    public Boolean getActivado() {
        return activado;
    }

    public void setActivado(Boolean activado) {
        this.activado = activado;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public String getDireccionUbicacion() {
        return DireccionUbicacion;
    }

    public void setDireccionUbicacion(String direccionUbicacion) {
        DireccionUbicacion = direccionUbicacion;
    }

    public LocationManager getHandle() {
        return handle;
    }

    public void setHandle(LocationManager handle) {
        this.handle = handle;
    }

    public String getProvide() {
        return provide;
    }

    public void setProvide(String provide) {
        this.provide = provide;
    }

    public void IniciarServicio() {
        handle = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        provide = handle.getBestProvider(c, true);
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        handle.requestLocationUpdates(provide, 10000, 1, this);
    }

    public void muestraPosicionActual() {
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = handle.getLastKnownLocation(provide);
        if (location==null){
            Latitud=0.0;
            Longitud=0.0;
        }else {
            Latitud=location.getLatitude();
            Longitud=location.getLongitude();
        }
        obtenerDireccion(location);
        Toast.makeText(act.getApplicationContext(), "Latitud: "+Latitud+" Longitud: "+Longitud, Toast.LENGTH_SHORT).show();

    }
    public void obtenerDireccion(Location loc){
        if (loc!=null){
            if (loc.getLatitude()!=0.0 && loc.getLongitude()!=0.0){
                try{
                    Geocoder geocoder=new Geocoder(act, Locale.getDefault());
                    List<Address> list=geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                    if (!list.isEmpty()){
                        Address direccion=list.get(0);
                        DireccionUbicacion=direccion.getAddressLine(0);
                    }
                }catch (IOException e){
                    DireccionUbicacion=""+e;
                }
            }
        }
    }
    public void paraServicio(){
        handle.removeUpdates(this);
        Latitud=null;
        Longitud=null;
        DireccionUbicacion="Ubicacion desconectada";
    }
    @Override
    public void onLocationChanged(Location location) {
        muestraPosicionActual();
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


    /*Algunas configuraciones para mejora visual*/
    public boolean VerificarGPSActivo() {
        return handle.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                handle.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    public boolean checkLocation() {
        if (!VerificarGPSActivo()){
            return VerificarGPSActivo();
        }else {
            return false;
        }
        //showAlert();
    }


}
