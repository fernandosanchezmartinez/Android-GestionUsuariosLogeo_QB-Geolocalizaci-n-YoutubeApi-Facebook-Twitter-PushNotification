package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import damp_2.utad.pushnotification_lib.Consts;
import damp_2.utad.pushnotification_lib.PushNotificationAdmin;
import damp_2.utad.pushnotification_lib.PushNotificationsAdminListener;

/**
 * Clase encargada de la gestion de los datos, en éste caso le pasamos el apid y será
 * la encargada de iniciar el push notification admin que posteriormente hará el registro
 * al GCM para que le retorne su número
 */
public class DataHolder implements PushNotificationsAdminListener{

    private String apId = "33666";

    public final static DataHolder instance=new DataHolder();
    public final String TAG="DataHolder";

    /**
     * Declaramos una variable propia que hara referencia al pushnotificationsadmin
     */
    public PushNotificationAdmin pushNotificationAdmin;

    public DataHolder(){
        //contructor de data holder
    }

    /**
     * MÉTDO ENCARGADO DE CREAR EL PUSHNOTIFICATION ADMIN, PASANDOLE POR PARÁMETRO EL NÚMERO DE REGISTRO
     * DE LA APLICACION (apId) Y LA ACTIVITY. SE LE AÑADE EL LISTENER
     * @param activity
     * @param aid
     */
    public void initPushNotificationsAdmin(Activity activity,String aid){
        pushNotificationAdmin=new PushNotificationAdmin(activity,aid);
        pushNotificationAdmin.addListener(this);
        /**
         * SE REGISTRA EL RECIVER PROPIO CREADO EN ÉSTA CLASE QUE RECIBIRÁ LOS MENSAJES
         * (ACTIVIDAD 8)
         */
        LocalBroadcastManager.getInstance(activity).registerReceiver(mPushReceiver,
                new IntentFilter(Consts.NEW_PUSH_EVENT));
    }

    /**
     * Método boleano qyue nos retorna true o false si el registro se ha efectuado correctamente
     * @param blRegistered
     * @return
     */
    @Override
    public boolean pushNotificationsRegistered(boolean blRegistered) {

        return blRegistered;
    }

    /**
     * PROCESAMIENTO DE MENSAJES (ACTIVIDAD8)
     */
    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra(Consts.EXTRA_MESSAGE);
            String qbcid = intent.getStringExtra("QBCID");

            Log.v(TAG, "Receiving event " + Consts.NEW_PUSH_EVENT + " with data: " + message);


        }
    };
}
