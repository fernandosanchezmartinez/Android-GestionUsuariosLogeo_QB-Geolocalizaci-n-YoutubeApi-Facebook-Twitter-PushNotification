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
 * Clase que se encarga de la gestión del idioma
 */
public class QBAdminIdioma {

    private QBAdminIdiomaListener listener;

    public void addIdiomaListener(QBAdminIdiomaListener Listener) {
        this.listener = Listener;
    }

    /**
     * Obtiene los datos según el idioma
     */
    public void getData(String idioma) {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();

        requestBuilder.eq("idi", idioma);// coincide con lo puesto en nuestra tabla de QB


        QBCustomObjects.getObjects("idioma", requestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
            /**
             * SATISFACTORIAMENTE
             * @param arrCustomObjects
             * @param params
             */
            @Override
            public void onSuccess(ArrayList<QBCustomObject> arrCustomObjects, Bundle params) {
                Log.v("QBAdminTabla", "Conx. Data correcto " + arrCustomObjects);
                listener.getIdioma(arrCustomObjects);//Se pasa el idioma por array, obtenido de QB

            }

            /**
             * EN ERROR
             * @param errors
             */
            @Override
            public void onError(List<String> errors) {
                Log.v("QBAdminTabla", "Error Conx. Data " + errors);
                listener.getIdioma(null);

            }
        });
    }
}
