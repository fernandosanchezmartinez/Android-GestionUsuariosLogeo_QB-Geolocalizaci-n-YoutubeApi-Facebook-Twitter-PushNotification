package damp_2.utad.qblibreria;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando.sanchez on 01/02/2016.
 */
public class QBAdminLocalizaciones {

    private QBAdminLocalizacionesListener listener;

    public void addLocalizacionesListener(QBAdminLocalizacionesListener Listener) {
        this.listener = Listener;
    }


    public void getData() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();

        //requestBuilder.eq("", localizacion);// coincide con lo puesto en nuestra tabla de QB


        QBCustomObjects.getObjects("localizaciones", requestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
            /**
             * SATISFACTORIAMENTE
             * @param arrCustomObjects
             * @param params
             */
            @Override
            public void onSuccess(ArrayList<QBCustomObject> arrCustomObjects, Bundle params) {
                Log.v("QBAdminTabla", "Conx. Data correcto " + arrCustomObjects);
                listener.getLocalizaciones(arrCustomObjects);

            }

            /**
             * EN ERROR
             * @param errors
             */
            @Override
            public void onError(List<String> errors) {
                Log.v("QBAdminTabla", "Error Conx. Data " + errors);
                listener.getLocalizaciones(null);

            }
        });
    }


}
