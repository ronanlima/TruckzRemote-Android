package com.br4dev.foodtruck;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;

import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.br4dev.foodtruck.servidor.PonteComunicacaoServidor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

@SuppressWarnings("ResourceType")
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
    private static final String TAG = "FoodTruck";
    private Button btnLocal;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location localizacao;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int INTERVALO_ATUALIZACAO = 1000 * 7;
    private static int INTERVALOR_RAPIDO = 1000 * 2;
    private static int DISTANCIA = 30; // 30 metros
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo_foodtruck);
        //setSupportActionBar(toolbar);

        btnLocal = (Button) findViewById(R.id.btn_local);
        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPlayServices()){
                    if (locationRequest != null && googleApiClient != null){
                        getLocation();
                        if (latitude != 0.0 && longitude != 0.0){
                            PonteComunicacaoServidor.callWsToMarkLocaleUser(2, latitude, longitude);
                        }
                    }
                }
            }
        });

        if (checkPlayServices()){
            createLocationRequest();
            buildGoogleApiClient();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVALO_ATUALIZACAO);
        locationRequest.setFastestInterval(INTERVALOR_RAPIDO);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISTANCIA);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultado = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultado != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultado)){
                GooglePlayServicesUtil.getErrorDialog(resultado, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "Este aparelho não suporta os serviços do Google Play Services.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void getLocation() {
        localizacao = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (localizacao != null){
            latitude = localizacao.getLatitude();
            longitude = localizacao.getLongitude();
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("GPS necessário");
            alertDialog.setMessage("O GPS não está habilitado. Solicitamos sua ativação.");
            alertDialog.setPositiveButton("Configurações", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getBaseContext().startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        //getLocation();
        Log.i(TAG, "Nao faz nada. O usuario precisa clicar no botao de marcar localizacao para o " +
                "sistema conseguir a latitude e longitude.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.SERVICE_MISSING){
            Log.i(TAG, connectionResult.getErrorMessage());
        } else if (connectionResult.getErrorCode() == ConnectionResult.SERVICE_DISABLED){
            Log.i(TAG, "O GPS está desligado.");
        } else if (connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Log.i(TAG, "É necessário atualizar a biblioteca do Google Play Services!");
        }
        Log.i(TAG,"Conexão falhou: ConnectionResult.getErrorCode = "+connectionResult.getErrorCode());
    }
}
