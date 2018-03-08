package com.recamedi.comunicaciondispersa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renel01 on 6/03/2018.
 */

public class DatabaseGPS extends SQLiteOpenHelper {
    //Base de datos
    private static final int DATABASE_VER=3;
    private static final String DATABASE_NAME="GPSTRACK";//

    //Tabla DE lecturas
    private static final String TABLE_NAME="rutas";
    private static final String KEY_ID="Id";
    private static final String KEY_FechaHora="FechaHora";
    private static final String KEY_Latitud="Latitud";
    private static final String KEY_longitud="Longitud";
    private static final String KEY_Idpersonal="IdPersonal";
    private static final String KEY_EstadoEnvioServidor="EstadoEnvioServidor";

    public DatabaseGPS(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"//0
                +KEY_FechaHora+" TEXT,"//1
                +KEY_Latitud+" TEXT,"//2
                +KEY_longitud+" TEXT,"//3
                +KEY_Idpersonal+" TEXT,"//4
                +KEY_EstadoEnvioServidor+" TEXT"//5
                +");";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);


    }
    public long AgregarRuta(String FechaHora,String Latitud,String Longitud,String Idpersonal){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_FechaHora,FechaHora);
        values.put(KEY_Latitud,Latitud);
        values.put(KEY_longitud,Longitud);
        values.put(KEY_Idpersonal,Idpersonal);
        //values.put(KEY_EstadoEnvioServidor,EstadoFoto);
        long CantidadRutaRegistrado= db.insert(TABLE_NAME,null,values);
        //db.close();
        if (CantidadRutaRegistrado==-1){
            String sqlsentencia="INSERT INTO "+TABLE_NAME+"("
                    +KEY_FechaHora+","
                    +KEY_Latitud+","
                    +KEY_longitud+","
                    +KEY_Idpersonal
                    +")VALUES('"
                    +FechaHora+"','"
                    +Latitud+"','"
                    +Longitud+"','"
                    +Idpersonal+"'"
                    +")";
            Log.e("SQLSEN",sqlsentencia);
            db.rawQuery(sqlsentencia,null);
        }else{
            //Aqui podriamos enviar a la BDONLINE
        }
        return CantidadRutaRegistrado;

    }

    public ArrayList<ArrayList> ListarRutaXEstadoEnvioServidor(String EstadoServidor){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{
                KEY_ID,
                KEY_FechaHora,//Codigo de documento
                KEY_Latitud,//fecha de asignacion
                KEY_longitud,
                KEY_Idpersonal,
                KEY_EstadoEnvioServidor
        },KEY_EstadoEnvioServidor+"=? ",new String[]{EstadoServidor},null,null,null,null);
        //String selectquery= "SELECT *, cast("+KEY_EstadoEntrega+" as int) as estadito FROM "+TABLE_NAME+" WHERE estadito > 2 and "+KEY_EstadoEnvio+" <1";//0, doc sin enviar al servidor

        ArrayList<ArrayList> lstDocEntregadosOffline=new ArrayList<>();

        int Contador=0;
        if (cursor.moveToFirst()){
            do{
                Contador+=1;
                ArrayList<String> datosListview=new ArrayList<String>();
                datosListview.add(cursor.getString(0));
                datosListview.add(cursor.getString(1));
                datosListview.add(cursor.getString(2));
                datosListview.add(cursor.getString(3));
                datosListview.add(cursor.getString(4));
                datosListview.add(cursor.getString(5));
                lstDocEntregadosOffline.add(datosListview);
            }while (cursor.moveToNext());
        }
        return lstDocEntregadosOffline;
    }

}
