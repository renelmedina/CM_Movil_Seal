package com.recamedi.comunicaciondispersa;

import java.io.Serializable;

/**
 * Created by Renel01 on 30/01/2018.
 */

public class DatosListview implements Serializable {
    private int Id;//codigo del documento
    private String Titulo;
    private String Detalle;
    private int Imagen;
    private String NroSuministro;
    private String CodTipoCod;
    private String NombreTipoDoc;



    private String NroDocumentoSeal;
    private String CodigoBarra;
    private String Latitud;
    private String Longitud;
    private String FechaAsigando;
    private String ClienteDNI;
    private String ClienteNombre;

    public DatosListview(int id, String titulo, String detalle, int imagen, String nroSuministro, String codTipoCod, String nombreTipoDoc, String codigoBarra, String latitud, String longitud, String fechaAsigando, String clienteDNI, String clienteNombre) {
        Id = id;
        Titulo = titulo;
        Detalle = detalle;
        Imagen = imagen;
        NroSuministro = nroSuministro;
        CodTipoCod = codTipoCod;
        NombreTipoDoc = nombreTipoDoc;
        CodigoBarra = codigoBarra;
        Latitud = latitud;
        Longitud = longitud;
        FechaAsigando = fechaAsigando;
        ClienteDNI = clienteDNI;
        ClienteNombre = clienteNombre;
    }

    public DatosListview(int id, String titulo, String detalle, int imagen, String nroSuministro, String codTipoCod, String nombreTipoDoc, String nrodocumentoseal, String codigoBarra, String latitud, String longitud, String fechaAsigando, String clienteDNI, String clienteNombre) {
        Id = id;
        Titulo = titulo;
        Detalle = detalle;
        Imagen = imagen;
        NroSuministro = nroSuministro;
        CodTipoCod = codTipoCod;
        NombreTipoDoc = nombreTipoDoc;
        NroDocumentoSeal = nrodocumentoseal;
        CodigoBarra = codigoBarra;
        Latitud = latitud;
        Longitud = longitud;
        FechaAsigando = fechaAsigando;
        ClienteDNI = clienteDNI;
        ClienteNombre = clienteNombre;
    }

    public DatosListview() {

    }

    public String getNroDocumentoSeal() {
        return NroDocumentoSeal;
    }

    public void setNroDocumentoSeal(String nroDocumentoSeal) {
        NroDocumentoSeal = nroDocumentoSeal;
    }
    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getNroSuministro() {
        return NroSuministro;
    }

    public void setNroSuministro(String nroSuministro) {
        NroSuministro = nroSuministro;
    }

    public String getCodTipoCod() {
        return CodTipoCod;
    }

    public void setCodTipoCod(String codTipoCod) {
        CodTipoCod = codTipoCod;
    }

    public String getNombreTipoDoc() {
        return NombreTipoDoc;
    }

    public void setNombreTipoDoc(String nombreTipoDoc) {
        NombreTipoDoc = nombreTipoDoc;
    }

    public String getCodigoBarra() {
        return CodigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        CodigoBarra = codigoBarra;
    }

    public String getLatitud() {
        return Latitud;
    }

    public void setLatitud(String latitud) {
        Latitud = latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public void setLongitud(String longitud) {
        Longitud = longitud;
    }

    public String getFechaAsigando() {
        return FechaAsigando;
    }

    public void setFechaAsigando(String fechaAsigando) {
        FechaAsigando = fechaAsigando;
    }

    public String getClienteDNI() {
        return ClienteDNI;
    }

    public void setClienteDNI(String clienteDNI) {
        ClienteDNI = clienteDNI;
    }

    public String getClienteNombre() {
        return ClienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        ClienteNombre = clienteNombre;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitutlo(String titutlo) {
        Titulo = titutlo;
    }

    public String getDetalle() {
        return Detalle;
    }

    public void setDetalle(String detalle) {
        Detalle = detalle;
    }

    public int getImagen() {
        return Imagen;
    }

    public void setImagen(int imagen) {
        Imagen = imagen;
    }
}
