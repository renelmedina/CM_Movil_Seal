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
    private static final int DATABASE_VER=9;
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
    private static final String KEY_EstadoEnvio="estadoenvioserv";//Si ya se envio(1) al servidor o aun no(0). para trabajar en modo offline

    //Tabla de logins
    private static final String TABLE_NAME_LOGIN="Logins";
    private static final String KEY_TNL_ID="IdLogins";
    private static final String KEY_TNL_CODIGOPERSONAL="CodigoPersonal";
    private static final String KEY_TNL_USUARIOPERSONAL="UsuarioPersonal";
    private static final String KEY_TNL_PASSWORDPERSONAL="PaswwordPersonal";
    private static final String KEY_TNL_NOMBREPERSONAL="NombrePersonal";
    private static final String KEY_TNL_FECHAACCESO="FechaAcceso";

    //Tabla de fotografias
    private static final String TABLE_NAME_FOTOS="Fotos";
    private static final String KEY_TNF_ID="IdFotos";
    private static final String KEY_TNF_ID_FotosOnnline="IdFotosOnline";
    private static final String KEY_TNL_ID_TNF="IdDocumento";
    private static final String KEY_TNF_RUTAFOTO="RutaFoto";
    private static final String KEY_TNF_ESTADOFOTO="EstadoSubidaFoto";
    private static final String KEY_TNF_FECHA="FechaFoto";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"//0
                +KEY_Codigos_codigodoc+" TEXT,"//1
                +KEY_Codigos_nrosuministro+" TEXT,"//2
                +KEY_tipodoc_codigo+" TEXT,"//3
                +KEY_tipodoc_nombretipodoc+" TEXT,"//4
                +KEY_nrodocumentoseal+" TEXT,"//5
                +KEY_codigobarra+" TEXT,"//6
                +KEY_coordenadas_lat+" TEXT,"//7
                +KEY_coordenadas_long+" TEXT,"//8
                +KEY_fechaasig+" TEXT,"//9
                +KEY_cliente_dni+" TEXT,"//10
                +KEY_cliente_nombrecliente+" TEXT,"//11
                +KEY_EstadoEntrega+" TEXT,"//12
                +KEY_EstadoFirma+" TEXT,"//13
                +KEY_Parentesco+" TEXT,"//14
                +KEY_DniRecepcion+" TEXT,"//15
                +KEY_LecturaMedidor+" TEXT,"//16
                +KEY_FechaVisita+" TEXT,"//17
                +KEY_VisitaLatitud+" TEXT,"//18
                +KEY_VisitaLongitud+" TEXT,"//19
                +KEY_EstadoEnvio+" TEXT"//20
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
        //Creando la Tabla de Fotos

        CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME_FOTOS+" ("
                +KEY_TNF_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +KEY_TNL_ID_TNF+" TEXT,"
                +KEY_TNF_ID_FotosOnnline+" TEXT,"
                +KEY_TNF_RUTAFOTO+" TEXT,"
                +KEY_TNF_ESTADOFOTO+" TEXT,"
                +KEY_TNF_FECHA+" TEXT"
                +")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_LOGIN);
        onCreate(sqLiteDatabase);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_FOTOS);
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
    public long AgregarFoto(String CodDocumento,String RutaFotoCelular,String EstadoFoto, String FechaFoto){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(KEY_TNL_ID_TNF,CodDocumento);
        values.put(KEY_TNF_RUTAFOTO,RutaFotoCelular);
        values.put(KEY_TNF_ESTADOFOTO,EstadoFoto);
        values.put(KEY_TNF_FECHA,FechaFoto);
        long cantidadFotosRegistrado= db.insert(TABLE_NAME_FOTOS,null,values);

        db.close();
        return cantidadFotosRegistrado;
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
    public int updateDocumento(int codDocumento,String EstadoEntrega, String EstadoFirma, String Parentesco,String DniRecepcion, String LecturaMedidor,String FechaVisita, String LatitudVisita, String LongitudVisita){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_EstadoEntrega,EstadoEntrega);
        values.put(KEY_EstadoFirma,EstadoFirma);
        values.put(KEY_Parentesco,Parentesco);
        values.put(KEY_DniRecepcion,DniRecepcion);
        values.put(KEY_LecturaMedidor,LecturaMedidor);
        values.put(KEY_FechaVisita,FechaVisita);
        values.put(KEY_VisitaLatitud,LatitudVisita);
        values.put(KEY_VisitaLongitud,LongitudVisita);

        /*return db.update(TABLE_NAME,
                values,KEY_Codigos_codigodoc+" =?",new String[]{codDocumento});*/
        /*return db.update(TABLE_NAME,
                values,KEY_Codigos_codigodoc+" ='"+codDocumento+"'",null);*/
        return db.update(TABLE_NAME,values,KEY_Codigos_codigodoc+" ="+codDocumento,null);
        //db.update("sdfsd","sdfsdfsd","asdfasdfasd");

    }
    //para actualizar el estado de envio al servidor
    public void updateDocumento(int codDocumento, String estadoEnvio){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_EstadoEnvio,estadoEnvio);
        db.update(TABLE_NAME,values,KEY_Codigos_codigodoc+" ="+codDocumento,null);
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
                textodetalle+="Posicion Visita: "+cursor.getString(18)+", "+cursor.getString(19)+ "\n";

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
                textodetalle+="Nombre Cliente: "+cursor.getString(11)+"\n";
                textodetalle+="Estado Firma: "+cursor.getString(12)+"\n";
                textodetalle+="Parentesco: "+cursor.getString(13)+"\n";
                textodetalle+="DNI Recepcion: "+cursor.getString(15)+"\n";
                textodetalle+="Lectura Medidor: "+cursor.getString(16)+"\n";
                textodetalle+="FechaVisitada: "+cursor.getString(17)+"\n";
//                if (cursor.getString(12)==null){
//                    textodetalle+="Estado: Sin visita";
//                }else{
//                    textodetalle+="Estado: "+cursor.getString(12);
//                }
                textodetalle+="Estado: "+cursor.getString(12)+"\n";
                textodetalle+="Estado Envio: "+cursor.getString(20);

                datosListview.setDetalle(textodetalle);
                datosListview.setImagen(R.drawable.arequipaaccsac);

                lstDocumentos.add(datosListview);
            }while (cursor.moveToNext());
        }
        return lstDocumentos;
    }
}
