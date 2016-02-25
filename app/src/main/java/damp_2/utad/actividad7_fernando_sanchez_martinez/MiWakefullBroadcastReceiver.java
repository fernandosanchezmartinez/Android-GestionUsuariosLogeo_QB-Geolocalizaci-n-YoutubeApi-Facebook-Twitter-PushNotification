package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * CLASE ENCARGADA DE RECIVIR LOS MENSAJES EN NUESTRA APLICACIÓN ANDROID
 */
public class MiWakefullBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.v("GcmBroadcastReceiver", "HE RECIBIDO!!!!");
        // Explicitly specify that GcmIntentService will handle the intent.
        /**
         * Se le pasa el mensaje como intent a la clase gestionadora de mensajes que hereda de inten service
         */
        ComponentName comp = new ComponentName(context.getPackageName(), MiIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
