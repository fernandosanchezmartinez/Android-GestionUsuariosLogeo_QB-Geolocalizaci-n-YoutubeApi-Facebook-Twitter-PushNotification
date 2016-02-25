package damp_2.utad.pushnotification_lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * CLASE PUSH NOTIFICATION ADMIN. FUNDAMENTAL EN EL MANEJO DE MENSAJES SE ENCARGA DEL REGISTRO
 * EN EL GCM Y DE LA CONEXION CON QUIKBLOX(COMO SERVER)
 */
public class PushNotificationAdmin{

    private static final String TAG = "PushNotificationAdmin";
    //private static final String LOG_TAG="PushNotificationAdmin";

    /**
     * SE DECLARA EL GCM
     */
    private GoogleCloudMessaging googleCloudMessaging;
    private String regId;// VARIABLE QUE CONTENDRA EL NUMERO DE REGISTRO DE NUETRA APP
    private Activity activity;// VARIABLE ACTIVITY
    private String sProjectNumber="";// VARIABLE QUE CONTENDRÁ EL ID DE NUESTRO PROYECTO

    private ArrayList<PushNotificationsAdminListener> listeners=new ArrayList<PushNotificationsAdminListener>();

    /**
     * CONSTRUCTOR DEL PUSH NOTIFICATION ADMIN SE IGUALAN LA ACTIVITY Y EL IDENTIFICADOR DE
     * NUETSTRO PROYECTO.
     * @param activity
     * @param sProjectNumber
     */
    public PushNotificationAdmin(Activity activity,String sProjectNumber){
        this.activity=activity;
        this.sProjectNumber=sProjectNumber;

    }

    /**
     * MÉTODO QUE SE ENCARGA DEL REGISTRO EN EL GCM, ES UN MÉTODO FUNDAMENTAL Y SE PODRIA
     * LLAMAR EN EL ON CREATE DE NUETRA APLICACIÓN TRAS EL LOGEO EN QB O SIMPLEMENTE LLAMARLO
     * EN EL ON CLIK DE UN BOTON EN LA ACTIVIDAD DEL MAPA
     */
    public void registerToNotification(){
        checkPlayService();
    }


    public void addListener(PushNotificationsAdminListener listener){
        listeners.add(listener);
    }

    public void removeListener(PushNotificationsAdminListener listener){
        listeners.remove(listener);
    }


//-----------------------------------------------------------------------------------------------------------//
    /**
     * métodos de chekeo de servicios, se comprueba que el id de registro sea el correcto y en ese caso llama
     * al método de subscripción.
     */
//-----------------------------------------------------------------------------------------------------------//
    private void checkPlayService() {
        Log.v(TAG, "checkPlayService ");
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);// se obtiene la instancia del GCM
            regId = getRegistrationId();// Se obtiene el id de registro
            Log.v(TAG, "checkPlayService "+regId);
            if (regId.isEmpty()) {// en el caso que el registro esté vacio, se llama a un método q lo registrará en 2 plano.
                registerInBackground();
            }
            else{
                /**
                 * LLAMAD A MÉTODO QUE SUBSCRIBE NUESTRA APP AL SERVICIO DE PUSH NOTIFICATIONS
                 * SE LE PASA EL ID DE REGISTRO
                 */
                subscribeToPushNotifications(regId);
            }
        } else {
            Log.v(TAG, "No valid Google Play Services APK found.");
        }
    }
    /**
     * CHEKEA SI LA VERSION DEL APK DEL DISPOSITIVO ES COMPATIBLE CON EL SERVICIO DE MENSAJERÍA DE GOOGLE GCM
     * (se usa en el método superior para antes de realizar las comprobaciones del id de registro, se tenga claro
     *  que el dispositivo es compatible)
     * @return
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, Consts.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.v(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
//-----------------------------------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------------------------------//

    /**
     * MÉTODO QUE OBTIENE EL ID DE REGISTRO
     * GESTIONA EL MANEJO Y PERDIDA DE ID DE REGISTRO EN CASO DE ACTUALIZACION DE APP
     * @return
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(Consts.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Consts.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.v(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * MÉTODO QUE OBTIENE LA VERSION DE LA APP
     * @return
     */
    public int getAppVersion() {
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    /**
     * MÉTODO QUE REGISTRA NUESTRA APLICACIÓN EN EL SERVICIO DE GCM. LO REALIZA EN SEGUNDO PLANO
     * DE FORMA ASINCRONA Y ALMACENA DICHO ID EN EL SAHREDPREFERENCES.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
                    }
                    regId = googleCloudMessaging.register(sProjectNumber);
                    msg = "Device registered, registration ID=" + regId;// QUEDA REGISTRADA LA APLICACIÓN

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    Handler h = new Handler(activity.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            subscribeToPushNotifications(regId);
                        }
                    });

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * ALMACENA EL ID DE REGISTRO DE NUESTRA APP EN EL DISPOSITIVO(sharedPreferences)
     * @return
     */
    private SharedPreferences getGCMPreferences() {

        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * MÉTODO ENCARGADO DE CREAR EL TOKEN CON EL ID DE REGISTRO DE ANDROID Y COMPLETAR EL REGISTRO EN GCM
     * @param regId registration ID
     */
    private void subscribeToPushNotifications(String regId) {

        Log.v(TAG, "subscribing...");

        String deviceId;
        // SE OBTIENE EL ID COMPROBANDO QUE TIPO DE DISPOSITIVO ES (MOVIL, TABLET)
        final TelephonyManager mTelephony = (TelephonyManager) activity.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
        } else {
            deviceId = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        /**
         * SE HACE LA SUBSCRIPCION PASANDOLE TODOS LOS PARÁMETROS NECESARIOS Y USANDO EL MÉTODO HEREDADO DEL LISTENER PUSH
         * NOTIFICATION REGISTERED, NOTIFICANDO EL ESTADO DEL REGISTRO EN CUALQUIER LUGAR DE NUESTRA APP.
         */

        QBMessages.subscribeToPushNotificationsTask(regId, deviceId, QBEnvironment.DEVELOPMENT, new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {
                Log.v(TAG, "subscribed");
                for(int i=0;i<listeners.size();i++){
                    listeners.get(i).pushNotificationsRegistered(true);
                }
            }

            @Override
            public void onError(List<String> strings) {
                for(int i=0;i<listeners.size();i++){
                    listeners.get(i).pushNotificationsRegistered(false);
                }
            }
        });
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        Toast.makeText(activity, " GUARDANDO DATOS EN MEMORIA INTERNA: ID_REGISTRO Y VERSION DE APP ", Toast.LENGTH_SHORT).show();
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion();
        Log.v(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Consts.PROPERTY_REG_ID, regId);
        editor.putInt(Consts.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
