package com.recamedi.comunicaciondispersa;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuLateralActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView ivEntregarPendientes;
    Button btnIniciarLabores;
    TextView tvEstadosincronizacion;
    ProgressBar pbSincronizando;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lateral);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comunicacion Dispersa");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                DescargarDocumentos dd=new DescargarDocumentos();
                dd.setContexto(getApplicationContext());
                dd.setPbEstadoSincronizacion(pbSincronizando);
                dd.setTxtEstado(tvEstadosincronizacion);
                Generalidades gen=(Generalidades)getApplication();
                String RutaAPP=gen.getCadena()+"webservices/sincronizardatos.php?usuario="+gen.getUsuarioActual()+"&password="+gen.getPasswordActual();
                dd.SinconizarDatos(""+RutaAPP);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pbSincronizando=(ProgressBar)findViewById(R.id.pbSincronizando);
        tvEstadosincronizacion=(TextView)findViewById(R.id.tvEstadosincronizacion);
        pbSincronizando.setVisibility(View.INVISIBLE);
        tvEstadosincronizacion.setVisibility(View.INVISIBLE);
        final Intent i = new Intent(getApplicationContext(),GPS_Service.class);

        /*final Context context=this;
        SimpleDateFormat fechaFormato = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault());
        Date fechaEjecutado = new Date();
        String sFechaEjecutado = fechaFormato.format(fechaEjecutado);
        DatabaseGPS dbrutas=new DatabaseGPS(this);
        long insertados=dbrutas.AgregarRuta(
                "-",
                "-",
                "-",
                "1"
        );
        if (insertados>0){
            Toast.makeText(getApplicationContext(),"NADA("+insertados+")",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),"NADA("+insertados+")----LAT: +",Toast.LENGTH_SHORT).show();

        }*/

        btnIniciarLabores=(Button)findViewById(R.id.btnIniciarLabores);
        btnIniciarLabores.setOnClickListener(new View.OnClickListener() {
            //Intent i = new Intent(getApplicationContext(),GPS_Service.class);
            @Override
            public void onClick(View view) {
                if (!runtine_permissions()){
                    if (btnIniciarLabores.getText().equals("Pausar Labores")){
                        btnIniciarLabores.setText("Iniciar Labores");
                        Toast.makeText(getApplicationContext(),"Pausando Labores",Toast.LENGTH_SHORT).show();
                        stopService(i);
                    }else if(btnIniciarLabores.getText().equals("Iniciar Labores")){
                        btnIniciarLabores.setText("Pausar Labores");
                        Toast.makeText(getApplicationContext(),"Iniciando Labores",Toast.LENGTH_SHORT).show();
                        startService(i);
                    }
                }


            }
        });
        ivEntregarPendientes=(ImageView)findViewById(R.id.ivEntregarPendientes);
        ivEntregarPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actPendientes=new Intent(getApplicationContext(),TabsActivity.class);
                startActivity(actPendientes);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lateral, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //android.support.v4.app.FragmentManager fm=getSupportFragmentManager();

        if (id == R.id.nav_verdocumentos) {
            // Handle the camera action
            Intent documentostabs=new Intent(getApplicationContext(),TabsActivity.class);
            startActivity(documentostabs);
            //fm1.beginTransaction().replace(R.id.escenarioprincipal,new FPendientes()).commit();

        } else if (id == R.id.nav_sinasignar) {
            Intent documentostabs=new Intent(getApplicationContext(),VisitaCampoMarcador.class);
            startActivity(documentostabs);


        //} else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_configuraciones) {
            Intent documentostabs=new Intent(getApplicationContext(),Configuraciones.class);
            startActivity(documentostabs);
        } else if (id == R.id.nav_sincronizar) {
            //Intent documentostabs=new Intent(getApplicationContext(),Configuraciones.class);
            //startActivity(documentostabs);
            DescargarDocumentos dd=new DescargarDocumentos();
            dd.setContexto(this);
            dd.setPbEstadoSincronizacion(pbSincronizando);
            dd.setTxtEstado(tvEstadosincronizacion);
            Generalidades gen=(Generalidades)getApplication();
            String RutaAPP=gen.getCadena()+"webservices/sincronizardatos.php?usuario="+gen.getUsuarioActual()+"&password="+gen.getPasswordActual();
            dd.SinconizarDatos(""+RutaAPP);

        } else if (id == R.id.nav_EnviarDatos) {
            //Intent documentostabs=new Intent(getApplicationContext(),TabsActivity.class);
            //startActivity(documentostabs);
            Generalidades gen=(Generalidades)getApplication();
            EnviarDocumentos ed=new EnviarDocumentos();
            ed.setContexto(this);
            ed.setUsuario(gen.getUsuarioActual());
            ed.setPassword(gen.getPasswordActual());
            ed.setRutaApp(gen.getCadena()+"webservices/guardarlectura.php");
            ed.setRutaFotos(gen.getCadena()+"webservices/guardarfoto.php");
            ed.enviarDatosServidor();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*Permisos de Acceso A la ubicacion*/
    private Boolean runtine_permissions(){
        if (Build.VERSION.SDK_INT>=23&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                //Activar Boton
            }else{
                runtine_permissions();
            }
        }
    }
}
