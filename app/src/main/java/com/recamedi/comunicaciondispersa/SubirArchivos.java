package com.recamedi.comunicaciondispersa;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Renel01 on 22/02/2018.
 */

public class SubirArchivos {
    private String ArchivoSubir;
    private String RutaAplicacion;

    public SubirArchivos(String archivoSubir, String rutaAplicacion) {
        ArchivoSubir = archivoSubir;
        RutaAplicacion = rutaAplicacion;
    }

    public String getRutaAplicacion() {
        return RutaAplicacion;
    }

    public void setRutaAplicacion(String rutaAplicacion) {
        RutaAplicacion = rutaAplicacion;
    }
    public String getArchivoSubir() {
        return ArchivoSubir;
    }

    public void setArchivoSubir(String archivoSubir) {
        ArchivoSubir = archivoSubir;
    }

    public String postArchivo() {
        HttpURLConnection conn = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte [] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(ArchivoSubir));
            URL url = new URL(RutaAplicacion);//"http://192.168.1.3/clientes/accsac.com/Aplicaciones/seal/comunicaciondispersa/webservices/guardarfoto.php");
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                    + ArchivoSubir + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while(bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        /*String serverResponseCode = "Conexion: "+conn.getResponseCode();
        String serverResponseMessage = conn.getResponseMessage();*/
            Log.d("Enviado: ","Enviado");
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            String inputLine;
            String xmlDelServidor="";
            while ((inputLine = dis.readLine()) != null) {
                //Log.d("XML",inputLine);
                xmlDelServidor +=inputLine;
            }
            return new String(xmlDelServidor);
            //return respost;
        }catch(Exception e) {
            return "Error: " + e;
        }finally {
            if(conn != null)
                conn.disconnect();
        }
    }
}
