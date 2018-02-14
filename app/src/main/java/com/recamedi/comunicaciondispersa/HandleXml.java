package com.recamedi.comunicaciondispersa;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renel01 on 28/01/2018.
 */

public class HandleXml{
    private String country="country";
    private String temperature="temperature";
    private String humidity="humidity";
    private String pressure="pressure";

    private String EstadoEnvioDocumento="0";
    //Inicio Variables de login
    private String codigoestadologin;
    private String textoestadologin;
    private String codigopersonallogin;
    private String nombrepersonal;
    private String textorespuesta;

    public HandleXml(String[] rutaAplicacion) {
        this.codigoestadologin = rutaAplicacion[0];
    }

    //Fin Variables de login
    public String getCodigoestadologin() {
        return codigoestadologin;
    }

    public void setCodigoestadologin(String codigoestadologin) {
        this.codigoestadologin = codigoestadologin;
    }

    public String getTextoestadologin() {
        return textoestadologin;
    }

    public void setTextoestadologin(String textoestadologin) {
        this.textoestadologin = textoestadologin;
    }

    public String getCodigopersonallogin() {
        return codigopersonallogin;
    }

    public void setCodigopersonallogin(String codigopersonallogin) {
        this.codigopersonallogin = codigopersonallogin;
    }

    public String getNombrepersonal() {
        return nombrepersonal;
    }

    public void setNombrepersonal(String nombrepersonal) {
        this.nombrepersonal = nombrepersonal;
    }

    public String getTextorespuesta() {
        return textorespuesta;
    }

    public void setTextorespuesta(String textorespuesta) {
        this.textorespuesta = textorespuesta;
    }


    public String getEstadoEnvioDocumento() {
        return EstadoEnvioDocumento;
    }

    public void setEstadoEnvioDocumento(String estadoEnvioDocumento) {
        EstadoEnvioDocumento = estadoEnvioDocumento;
    }
    private String urlString=null;
    /*variables tipo array*/
    //String [][] strDatos;
    List<List<String>>  lstDatos=null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingCompete=true;
    public HandleXml(String url){
        this.urlString=url;
    }

    public String getCountry() {
        return country;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public List<List<String>> getLstDatos() {
        return lstDatos;
    }

    public void parseXMLAndStorelt(XmlPullParser myParser){
        int event;
        String text=null;
        try {
            event=myParser.getEventType();
            while (event!=XmlPullParser.END_DOCUMENT){
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text=myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("respuesta")){//Nombre del tag del archivo xml
                            //country=text;
                            EstadoEnvioDocumento=myParser.getAttributeValue(null,"codigo");//<respuesta codigo="200"/>
                        }/*else if (name.equals("humidity")){
                            humidity=myParser.getAttributeValue(null,"value");
                            humidity+=myParser.getAttributeValue(null,"unit");
                        }else if (name.equals("pressure")){
                            pressure=myParser.getAttributeValue(null,"value");

                        }else if(name.equals("temperature")){
                            temperature=myParser.getAttributeValue(null,"value");
                        }*/else{
                            //aun nada
                        }
                        break;
                }
                event=myParser.next();
            }
            parsingCompete=false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*Usado para listar todo el documento */
    public void parseXMLAndStoreV(XmlPullParser myParser){
        int event;
        String texto=null;
        String codigos=null;
        lstDatos=new ArrayList<List<String>>();
        try {
            event=myParser.getEventType();
            int indice=0;

            while (event!=XmlPullParser.END_DOCUMENT){
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        texto=myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("codigos")){//Nombre del tag del archivo xml
                            /*Obtiene de la etiqueta <codigos />*/
                            //codigos=myParser.getAttributeValue(null,"codigodoc");
                            lstDatos.add(new ArrayList<String>());
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"codigodoc"));
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"nrosuministro"));
                            //myParser.getAttributeCount();
                        }else if (name.equals("tipodoc")){
                            /*Obtiene de la etiqueta <tipodoc></tipodoc>*/
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"codigo"));
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"nombretipodoc"));

                        }else if (name.equals("codigobarra")){
                            //pressure=myParser.getAttributeValue(null,"value");
                            lstDatos.get(indice).add(texto);

                        }else if(name.equals("coordenadas")){
                            //temperature=myParser.getAttributeValue(null,"value");
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"lat"));
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"long"));
                        }else if(name.equals("fechaasig")){
                            //temperature=myParser.getAttributeValue(null,"value");
                            lstDatos.get(indice).add(texto);
                        }else if (name.equals("cliente")){
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"dni"));
                            lstDatos.get(indice).add(myParser.getAttributeValue(null,"nombrecliente"));
                            //la etiqueta <cliente /> es el ultimo, asi que denotamos que es el fin de este registro y se va al siguiente registro
                            indice+=1;
                        }else{
                            //aun nada
                        }
                        break;
                }
                event=myParser.next();

            }
            //txt2.setText(listaDatos.get(0).get(2));
            //txt3.setText(listaDatos.get(0).get(3));
            parsingCompete=false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void fetchXML(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL(urlString);
                    HttpURLConnection connect=(HttpURLConnection)url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.setDoInput(true);
                    connect.connect();

                    InputStream stream=connect.getInputStream();
                    xmlFactoryObject=XmlPullParserFactory.newInstance();
                    XmlPullParser myparser=xmlFactoryObject.newPullParser();
                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                    myparser.setInput(stream,null);
                    parseXMLAndStoreV(myparser);
                    //parseXMLAndStorelt(myparser);
                    stream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void fetchXML(int Operador){
        final int operador=Operador;
        //EstadoEnvioDocumento+=operador;

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connect=null;
                try {
                    URL url=new URL(urlString);
                    connect=(HttpURLConnection)url.openConnection();
                    connect.setReadTimeout(1000);
                    connect.setConnectTimeout(1000);
                    connect.setRequestMethod("GET");
                    connect.setDoInput(true);
                    connect.connect();
                    //Boolean conestado=(Boolean)connect.connect();
                    InputStream stream=connect.getInputStream();
                    xmlFactoryObject=XmlPullParserFactory.newInstance();
                    XmlPullParser myparser=xmlFactoryObject.newPullParser();
                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                    myparser.setInput(stream,null);
                    switch (operador){
                        case 0:
                            parseXMLAndStoreV(myparser);
                            break;
                        case 1:
                            parseXMLAndStorelt(myparser);
                            break;
                        case 2:
                            parseXMLLogin(myparser);
                            break;
                    }
                    stream.close();


                    //EstadoEnvioDocumento+=operador;
                    //parseXMLAndStoreV(myparser);
                    //parseXMLAndStorelt(myparser);
                }catch (Exception e){
                    e.printStackTrace();
                    connect.disconnect();
                    parsingCompete=false;
                    codigoestadologin="0";//Servidor desconectado,desconocido
                }finally {
                    connect.disconnect();
                    parsingCompete=false;
                    codigoestadologin="0";//Servidor desconectado,desconocido
                }
            }
        });
        thread.start();
    }

    public void parseXMLLogin(XmlPullParser myParser){
        int event;
        String text=null;
        try {
            event=myParser.getEventType();
            while (event!=XmlPullParser.END_DOCUMENT){
                String name=myParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text=myParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("estadologin")){//Nombre del tag del archivo xml
                            //country=text;
                            codigoestadologin=myParser.getAttributeValue(null,"codigoestado");//<estadologin codigoestado="1" etiquetaestado="Acceso Correcto"/>
                            textoestadologin=myParser.getAttributeValue(null,"textoestado");//<estadologin codigoestado="1" etiquetaestado="Acceso Correcto"/>

                        }else if (name.equals("usuario")){
                            codigopersonallogin=myParser.getAttributeValue(null,"codigopersonal");
                            nombrepersonal=myParser.getAttributeValue(null,"nombrepersonal");;

                        }else if (name.equals("textorespuesta")){
                            pressure=myParser.getAttributeValue(null,"value");
                            textorespuesta=text;
                        }/*else if(name.equals("temperature")){
                            temperature=myParser.getAttributeValue(null,"value");
                        }*/else{
                            //aun nada
                        }
                        break;
                }
                event=myParser.next();
            }
            parsingCompete=false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
