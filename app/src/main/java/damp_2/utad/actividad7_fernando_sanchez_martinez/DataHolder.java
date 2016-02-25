package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import damp_2.utad.pushnotification_lib.Consts;
import damp_2.utad.pushnotification_lib.PushNotificationAdmin;
import damp_2.utad.pushnotification_lib.PushNotificationsAdminListener;
import damp_2.utad.qblibreria.QBAdmin;


public class DataHolder implements PushNotificationsAdminListener{

    private String apId = "33666";
    private String apKey = "Z4RtXJ8ND2-HkNa";
    private String apSecret = "M2jw43vOOOB7ju3";

    public final static DataHolder instance=new DataHolder();
    public final String TAG="DataHolder";


    public QBAdmin qbAdmin;
    public PushNotificationAdmin pushNotificationAdmin;

    public DataHolder(){

    }

    public void initQbAdmin(Context context){
        qbAdmin = new QBAdmin(apId, apKey, apSecret);
    }

    public void initPushNotificationsAdmin(Activity activity,String aid){
        pushNotificationAdmin=new PushNotificationAdmin(activity,aid);
        pushNotificationAdmin.addListener(this);


        LocalBroadcastManager.getInstance(activity).registerReceiver(mPushReceiver,
                new IntentFilter(Consts.NEW_PUSH_EVENT));
    }

    @Override
    public void pushNotificationsRegistered(boolean blRegistered) {

    }

    // ESTE ES EL ULTIMO PASO QUE HARA EL MENSAJE RECIBIDO. AQUI ES DONDE EJECUTAMOS LO QUE NOS INTERESE EJECUTAR
    //AL RECIBIR UN MENSAJE. EN CASO DE RECIBIR EL MENSAJE CUANDO ESTAMOS DENTRO DE LA APP, O SI ESTAMOS FUERA DE LA APP
    //AQUI ES DONDE LLEGA EL MENSAJE. TODAS LAS ACCIONES QUE HAGAMOS CON EL MENSAJE SE HARAN AQUI.
    //
    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            String message = intent.getStringExtra(Consts.EXTRA_MESSAGE);
            String qbcid = intent.getStringExtra("QBCID");

            //Log.v(TAG, "Receiving event " + Consts.NEW_PUSH_EVENT + " with data: " + message);

            //AQUI INSERTAREMOS EL CODIGO QUE EJECUTAREMOS CUANDO LLEGUE EL MENSAJE.
        }
    };
}
