package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Renel01 on 16/02/2018.
 */

public class GeoLocation extends IntentService  implements LocationListener {
    private Boolean activado;
    private Double Longitud;
    private Double Latitud;
    private String DireccionUbicacion;
    public LocationManager handle;
    private String provide;
    private Activity act;

    public GeoLocation(Activity act) {
        this.act = act;
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
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


}
