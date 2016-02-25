package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * CLASE QUE GESTIONA LA MUESTRA DE LOS MENSAJES CUANDO SON RECIBIDOS POR EL BRODCASTER RECIVER
 */
public class MiIntentService extends IntentService {

    /**
     * Se declaran los parámetros del gcm_intent_service
     */
    public static final String GCM_INTENT_SERVICE = "GcmIntentService";
    public static final String GCM_SEND_ERROR = "Send error: ";
    public static final String GCM_DELETED_MESSAGE = "Deleted messages on server: ";
    public static final String GCM_RECEIVED = "Received: ";
    public static final String EXTRA_MESSAGE = "message";

   // public static final int NOTIFICATION_ID = 1;

    private static final String TAG = MiIntentService.class.getSimpleName();

   // private NotificationManager notificationManager;

    /**
     * constructor de la clase que hereda con un super los atributos del GCM_IntentService
     */
    public MiIntentService(){
        super(GCM_INTENT_SERVICE);
    }

    /**
     * Metodo que se ejecuta cuando se lanza el intent, en éste caso nuestra clase gestionadora
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "new push");

        Bundle extras = intent.getExtras();
        //OBTENEMOS LA INSANCIA DEL GCM
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        /**
         * El mensaje que se recive (getMessageType) es el intent que se le pasa desde el MiBroadcastreciver
         */
        String messageType = googleCloudMessaging.getMessageType(intent);

        if (!extras.isEmpty()) {  // Siempre que no esté vacio
            /**
             * Se hace un filtrado de los tipos de mensaje que pùeden ser recividos
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                processNotification(GCM_SEND_ERROR, extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                processNotification(GCM_DELETED_MESSAGE, extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                processNotification(GCM_RECEIVED, extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }

        MiWakefullBroadcastReceiver.completeWakefulIntent(intent);//Se completa la comprobacion del lanzado

    }


    private void processNotification(String type, Bundle extras) {

//AQUI SE PROCESARIAN LOS MENSAJES CUANDO SON PULSADOS( ACTIVIDAD8)

    }
}
