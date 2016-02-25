package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.HashMap;

import damp_2.utad.qblibreria.QBAdminLocalizaciones;
import damp_2.utad.qblibreria.QBAdminLocalizacionesListener;

/**
 * Se importa el sdkFacebook
 */

/**
 * CLASE QUE SE ENCARGA DE LA GESTIÓN DE LOS PAPAS EN NUESTRA APP
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, QBAdminLocalizacionesListener, FragmentoFBTW.OnFragmentInteractionListener {

    public GoogleMap mMap;
    ArrayList<String> arrValor;
    private MainActivity ma;

    private ArrayList<QBCustomObject> dataLang;
    public QBAdminLocalizaciones qbAdminLocalizaciones;
    //buttonListener botonListener;

    public LocationManager LocManager;
    String locationProvider = LocationManager.GPS_PROVIDER;
    Marker mkCentro;

    /*public void initFragments() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
          mapFragment.isVisible();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // listener = new LocationListener(this);

        // generarHash();

        //initFragments();
        /**
         * se inicializa antes de ejecutar ninguna operacion en el oncreate de nuestra actiivity
         */

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // botonListener = new buttonListener(this);

        LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        LocManager.requestLocationUpdates(locationProvider, 0, 0, this);

        qbAdminLocalizaciones = new QBAdminLocalizaciones();

        qbAdminLocalizaciones.addLocalizacionesListener(this);




        // IDENTIFICADOR DE PROYECTO:   861311781554
        DataHolder.instance.initPushNotificationsAdmin(this, "861311781554");
        Toast.makeText(this, " REGISTRADO EN GCM APLICACIÓN... ", Toast.LENGTH_SHORT).show();
        DataHolder.instance.pushNotificationAdmin.registerToNotification();
        Toast.makeText(this, " REGISTRO COMPLETADO SATISFACTORIAMENTE EN EL SERVICIO DE GOOGLE CLOUD MESSAGING", Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       /* mMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Hello world"));*/


        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

    }

    @Override
    public void onLocationChanged(Location location) {

        double latitudActual = location.getLatitude();
        double longitudActual = location.getLongitude();

        Toast toast1 =
                Toast.makeText(getApplicationContext(),
                        "TU LONGITUD ES:" + longitudActual + " \nTU LATITUD ES:" + latitudActual, Toast.LENGTH_SHORT);

        toast1.show();

        if (mkCentro != null) {
            mkCentro.remove();
        }

        LatLng posActual = new LatLng(latitudActual, longitudActual);

        mkCentro = mMap.addMarker(new MarkerOptions().position(posActual).title("ÉSTA ES LA POSICIÓN ACTUAL").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posActual));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }


    @Override
    public void getLocalizaciones(ArrayList<QBCustomObject> datos) {
        ArrayList<String> arrValor = new ArrayList<>();
        this.dataLang = datos;
        if (datos != null) {

            Log.v("MainActivity", "cargando datos");
            for (int i = 0; i < dataLang.size(); i++) {
                HashMap<String, Object> fields = dataLang.get(i).getFields();

                Log.v("MainActivity", "cargando los datos de localizaciones");
                arrValor.add(fields.toString());

                String titulo = (fields.get("nombre")).toString();
                String latitud = (fields.get("latitud")).toString();
                String longitud = (fields.get("longitud")).toString();

                LatLng marcador = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                mMap.addMarker(new MarkerOptions().position(marcador).title(titulo));

            }

        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onDescargarCoordClick(View v) {
        Log.v("MapsActivity", "boton descargar coordenadas pulsado");
        qbAdminLocalizaciones.getData();
        Toast.makeText(this, " deescargando coordenadas... ", Toast.LENGTH_SHORT).show();

    }
}

