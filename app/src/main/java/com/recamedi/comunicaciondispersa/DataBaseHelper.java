package com.recamedi.comunicaciondispersa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renel01 on 6/02/2018.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    //Base de datos
    private static final int DATABASE_VER=7;
    private static final String DATABASE_NAME="COMDIS";//comunicacion dispersa

    //Tabla DE lecturas
    private static final String TABLE_NAME="Documentos";
    private static final String KEY_ID="id";
    private static final String KEY_Codigos_codigodoc="codigodoc";
    private static final String KEY_Codigos_nrosuministro="nrosuministro";
    private static final String KEY_tipodoc_codigo="codigo";
    private static final String KEY_tipodoc_nombretipodoc="nombretipodoc";
    private static final String KEY_nrodocumentoseal="nrodocumentoseal";
    private static final String KEY_codigobarra="codigobarra";
    private static final String KEY_coordenadas_lat="lat";
    private static final String KEY_coordenadas_long="long";
    private static final String KEY_fechaasig="fechaasig";
    private static final String KEY_cliente_dni="dnicliente";
    private static final String KEY_cliente_nombrecliente="nombrecliente";
    //Datos de lectura cuando ya se hizo la visita
    private static final String KEY_EstadoEntrega="estadoentrega";
    private static final String KEY_EstadoFirma="estadofirma";
    private static final String KEY_Parentesco="parentesco";
    private static final String KEY_DniRecepcion="dnirecepcion";
    private static final String KEY_LecturaMedidor="lecturamedidor";
    private static final String KEY_FechaVisita="fechavisita";
    private static final String KEY_VisitaLatitud="visita_latitud";
    private static final String KEY_VisitaLongitud="visita_longitud";

    //Tabla de logins
    private static final String TABLE_NAME_LOGIN="Logins";
    private static final String KEY_TNL_ID="IdLogins";
    private static final String KEY_TNL_CODIGOPERSONAL="CodigoPersonal";
    private static final String KEY_TNL_USUARIOPERSONAL="UsuarioPersonal";
    private static final String KEY_TNL_PASSWORDPERSONAL="PaswwordPersonal";
    private static final String KEY_TNL_NOMBREPERSONAL="NombrePersonal";
    private static final String KEY_TNL_FECHAACCESO="FechaAcceso";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_Codigos_codigodoc+" TEXT,"
                +KEY_Codigos_nrosuministro+" TEXT,"
                +KEY_tipodoc_codigo+" TEXT,"
                +KEY_tipodoc_nombretipodoc+" TEXT,"
                +KEY_nrodocumentoseal+" TEXT,"
                +KEY_codigobarra+" TEXT,"
                +KEY_coordenadas_lat+" TEXT,"
                +KEY_coordenadas_long+" TEXT,"
                +KEY_fechaasig+" TEXT,"
                +KEY_cliente_dni+" TEXT,"
                +KEY_cliente_nombrecliente+" TEXT,"
                +KEY_EstadoEntrega+" TEXT,"
                +KEY_EstadoFirma+" TEXT,"
                +KEY_Parentesco+" TEXT,"
                +KEY_DniRecepcion+" TEXT,"
                +KEY_LecturaMedidor+" TEXT,"
                +KEY_FechaVisita+" TEXT,"
                +KEY_VisitaLatitud+" TEXT,"
                +KEY_VisitaLongitud+" TEXT"
                +");";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        //Creando la tabla de login
        CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME_LOGIN+" ("
                +KEY_TNL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_TNL_CODIGOPERSONAL+" TEXT,"
                +KEY_TNL_USUARIOPERSONAL+" TEXT,"
                +KEY_TNL_PASSWORDPERSONAL+" TEXT,"
                +KEY_TNL_NOMBREPERSONAL+" TEXT,"
                +KEY_TNL_FECHAACCESO+" TEXT"
                +")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_LOGIN);
        onCreate(sqLiteDatabase);

    }
    public Boolean LoginSistemaOffline(String Usuario, String Password){
        SQLiteDatabase db=this.getWritableDatabase();
        String[] campos = new String[] {KEY_TNL_ID,KEY_TNL_CODIGOPERSONAL, KEY_TNL_NOMBREPERSONAL,KEY_TNL_FECHAACCESO};
        String[] args = new String[] {Usuario,Password};

        Cursor c = db.query(""+TABLE_NAME_LOGIN,campos, KEY_TNL_USUARIOPERSONAL+"=? and "+KEY_TNL_PASSWORDPERSONAL+"=?", args, null, null, null);
        if (c.moveToFirst()){
            //Si hay por lo menos un registro devolvera true
            return true;
        }else{
            //Si no existe registros devuelve false
            return false;
        }
    }
    public void AgregarUsuario(String CodigoPersonal, String UsuarioPersonal, String PasswordPersonal, String NombrePersonal, String FechaAcceso){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_TNL_CODIGOPERSONAL,CodigoPersonal);
        values.put(KEY_TNL_USUARIOPERSONAL,UsuarioPersonal);
        values.put(KEY_TNL_PASSWORDPERSONAL,PasswordPersonal);
        values.put(KEY_TNL_NOMBREPERSONAL,NombrePersonal);
        values.put(KEY_TNL_FECHAACCESO,FechaAcceso);
        db.insert(TABLE_NAME_LOGIN,null,values);
        db.close();
    }
    public void addDocumento(DatosListview datosListview){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_Codigos_codigodoc,datosListview.getId());
        values.put(KEY_Codigos_nrosuministro,datosListview.getNroSuministro());
        values.put(KEY_tipodoc_codigo,datosListview.getCodTipoCod());
        values.put(KEY_tipodoc_nombretipodoc,datosListview.getNombreTipoDoc());
        values.put(KEY_nrodocumentoseal,datosListview.getNroDocumentoSeal());
        values.put(KEY_codigobarra,datosListview.getCodigoBarra());
        values.put(KEY_coordenadas_lat,datosListview.getLatitud());
        values.put(KEY_coordenadas_long,datosListview.getLongitud());
        values.put(KEY_fechaasig,datosListview.getFechaAsigando());
        values.put(KEY_cliente_dni,datosListview.getClienteDNI());
        values.put(KEY_cliente_nombrecliente,datosListview.getClienteNombre());

        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    public void addDocumento(String Iddocumento){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_Codigos_codigodoc,Iddocumento);
        values.put(KEY_Codigos_nrosuministro,Iddocumento);
        values.put(KEY_tipodoc_codigo,Iddocumento);
        values.put(KEY_tipodoc_nombretipodoc,Iddocumento);
        values.put(KEY_codigobarra,Iddocumento);
        values.put(KEY_coordenadas_lat,Iddocumento);
        values.put(KEY_coordenadas_long,Iddocumento);
        values.put(KEY_fechaasig,Iddocumento);
        values.put(KEY_cliente_dni,Iddocumento);
        values.put(KEY_cliente_nombrecliente,Iddocumento);

        db.insert(TABLE_NAME,null,values);
        db.close();
    }
    public void updateDocumento(int codDocumento,String EstadoEntrega, String EstadoFirma, String Parentesco,String DniRecepcion, String LecturaMedidor,String FechaVisita){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_EstadoEntrega,EstadoEntrega);
        values.put(KEY_EstadoFirma,EstadoFirma);
        values.put(KEY_Parentesco,Parentesco);
        values.put(KEY_DniRecepcion,DniRecepcion);
        values.put(KEY_LecturaMedidor,LecturaMedidor);
        values.put(KEY_FechaVisita,FechaVisita);

        /*return db.update(TABLE_NAME,
                values,KEY_Codigos_codigodoc+" =?",new String[]{codDocumento});*/
        /*return db.update(TABLE_NAME,
                values,KEY_Codigos_codigodoc+" ='"+codDocumento+"'",null);*/
        db.update(TABLE_NAME,values,KEY_Codigos_codigodoc+" ="+codDocumento,null);
        //db.update("sdfsd","sdfsdfsd","asdfasdfasd");

    }
    public int updateDocumento(DatosListview datosListview){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_Codigos_codigodoc,datosListview.getId());
        values.put(KEY_Codigos_nrosuministro,datosListview.getNroSuministro());
        values.put(KEY_tipodoc_codigo,datosListview.getCodTipoCod());
        values.put(KEY_tipodoc_nombretipodoc,datosListview.getNombreTipoDoc());
        values.put(KEY_codigobarra,datosListview.getCodigoBarra());
        values.put(KEY_coordenadas_lat,datosListview.getLatitud());
        values.put(KEY_coordenadas_long,datosListview.getLongitud());
        values.put(KEY_fechaasig,datosListview.getFechaAsigando());
        values.put(KEY_cliente_dni,datosListview.getClienteDNI());
        values.put(KEY_cliente_nombrecliente,datosListview.getClienteNombre());

        /*values.put(KEY_EstadoEntrega,"EstadoEntrega");
        values.put(KEY_EstadoFirma,"EstadoFirma");
        values.put(KEY_Parentesco,"Parentesco");
        values.put(KEY_DniRecepcion,"DniRecepcion");
        values.put(KEY_LecturaMedidor,"LecturaMedidor");
        values.put(KEY_FechaVisita,"FechaVisita");*/
        return db.update(TABLE_NAME,
                values,KEY_Codigos_codigodoc+" =?",new String[]{String.valueOf(datosListview.getId())});

    }
    public void deleteDocumento(DatosListview datosListview){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_Codigos_codigodoc+" -?",new String[]{String.valueOf(datosListview.getId())});
        db.close();
    }
    public DatosListview getDatosListview(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{
                KEY_Codigos_codigodoc,
                KEY_Codigos_nrosuministro,
                KEY_tipodoc_codigo,
                KEY_tipodoc_nombretipodoc,
                KEY_codigobarra,
                KEY_coordenadas_lat,
                KEY_coordenadas_long,
                KEY_fechaasig,
                KEY_cliente_dni,
                KEY_cliente_nombrecliente
        },KEY_Codigos_codigodoc+"-?",new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new DatosListview(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10),
                cursor.getString(11),
                cursor.getString(12)
        );
    }
    public boolean VerificarRegistroRedundante(String id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{
                KEY_Codigos_codigodoc,
                KEY_Codigos_nrosuministro
        },KEY_Codigos_codigodoc+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor != null) {
            return true;
        }else{
            return false;
        }
    }
    public int EliminarDuplicados(){
        int FilasEliminaadas;
        SQLiteDatabase db=this.getWritableDatabase();
        FilasEliminaadas=(int)db.delete(TABLE_NAME,"oid NOT IN (SELECT min(oid) FROM "+TABLE_NAME+" GROUP BY "+KEY_Codigos_codigodoc+")",null);
        db.close();
        return FilasEliminaadas;
    }
    public List<DatosListview> getAllDocumentos(){
        List<DatosListview> lstDocumentos=new ArrayList<>();
        String selectquery= "SELECT * FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery(selectquery,null);
        String textodetalle;
        int Contador=0;
        if (cursor.moveToFirst()){
            do{
                Contador+=1;
                DatosListview datosListview=new DatosListview();
                datosListview.setId(cursor.getInt(1));//Codigo documento
                datosListview.setNroSuministro(cursor.getString(2));
                datosListview.setCodTipoCod(cursor.getString(3));
                datosListview.setNombreTipoDoc(cursor.getString(4));
                datosListview.setNroDocumentoSeal(cursor.getString(5));
                datosListview.setCodigoBarra(cursor.getString(6));
                datosListview.setLatitud(cursor.getString(7));
                datosListview.setLongitud(cursor.getString(8));
                datosListview.setFechaAsigando(cursor.getString(9));
                datosListview.setClienteDNI(cursor.getString(10));
                datosListview.setClienteNombre(cursor.getString(11));

                datosListview.setTitulo(Contador + ".- "+cursor.getString(5));
                textodetalle="Suministro: "+cursor.getString(2)+ "\n";
                textodetalle+="Tipo Doc: "+cursor.getString(4)+ "\n";
                textodetalle+="Cod Barra: "+cursor.getString(6)+ "\n";
                textodetalle+="Posicion: "+cursor.getString(7)+", "+cursor.getString(8)+ "\n";


                /*+0+" INTEGER PRIMARY KEY,"
                        +1+" TEXT,"
                        +2+" TEXT,"
                        +3+" TEXT,"
                        +4+" TEXT,"
                        +5+" TEXT,"
                        +6+" TEXT,"
                        +7+" TEXT,"
                        +8+" TEXT,"
                        +9+" TEXT,"
                        +10+" TEXT,"
                        +11+" TEXT,"
                        +12+" TEXT,"
                        +13+" TEXT,"
                        +14+" TEXT,"
                        +15+" TEXT,"
                        +KEY_FechaVisita+" TEXT"*/
                textodetalle+="Estado Entrega: "+cursor.getString(11)+"\n";
                textodetalle+="Estado Firma: "+cursor.getString(12)+"\n";
                textodetalle+="Parentesco: "+cursor.getString(13)+"\n";
                textodetalle+="DNI Recepcion: "+cursor.getString(14)+"\n";
                textodetalle+="Lectura Medidor: "+cursor.getString(15)+"\n";
                textodetalle+="FechaVisitada: "+cursor.getString(16)+"\n";
//                if (cursor.getString(12)==null){
//                    textodetalle+="Estado: Sin visita";
//                }else{
//                    textodetalle+="Estado: "+cursor.getString(12);
//                }
                textodetalle+="Estado: "+cursor.getString(12);
                datosListview.setDetalle(textodetalle);
                datosListview.setImagen(R.drawable.arequipaaccsac);

                lstDocumentos.add(datosListview);
            }while (cursor.moveToNext());
        }
        return lstDocumentos;
    }
}
