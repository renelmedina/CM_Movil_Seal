package com.recamedi.comunicaciondispersa;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Renel01 on 16/02/2018.
 */

public class Servicios extends IntentService {
    private Activity act;
    public Servicios(Activity act2) {
        super("holas");
        act=act2;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeoLocation geoLocation= new GeoLocation(act);
        geoLocation.IniciarServicio();
        geoLocation.muestraPosicionActual();
    }
}
