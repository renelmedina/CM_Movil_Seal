package com.recamedi.comunicaciondispersa;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Renel01 on 25/01/2018.
 */

/*public class Generalidades extends Activity {

    SharedPreferences prefs = getSharedPreferences("pConfiguracionesApp", Context.MODE_PRIVATE);
    private String cadena =prefs.getString("rutawebapp","recamedi.com");
    //private String cadena="http://wwww.recamedi.com";

    public String getCadena() {
        return cadena;
    }
    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

}*/

public class Generalidades extends Application {

    private String RutawebAppGeneralidades;



    private int TiempoConexion;
    private int TiempoLectura;

    private String UsuarioActual;



    private String PasswordActual;
    //private String cadena="holas";
    public Generalidades(){

    }
    public String getCadena() {
        return RutawebAppGeneralidades;
    }
    public void setCadena(String cadena) {
        this.RutawebAppGeneralidades = cadena;
    }
    public int getTiempoConexion() {
        return TiempoConexion;
    }

    public void setTiempoConexion(int tiempoConexion) {
        TiempoConexion = tiempoConexion;
    }

    public int getTiempoLectura() {
        return TiempoLectura;
    }

    public void setTiempoLectura(int tiempoLectura) {
        TiempoLectura = tiempoLectura;
    }
    public String getUsuarioActual() {
        return UsuarioActual;
    }

    public void setUsuarioActual(String usuarioActual) {
        UsuarioActual = usuarioActual;
    }

    public String getPasswordActual() {
        return PasswordActual;
    }

    public void setPasswordActual(String passwordActual) {
        PasswordActual = passwordActual;
    }
}