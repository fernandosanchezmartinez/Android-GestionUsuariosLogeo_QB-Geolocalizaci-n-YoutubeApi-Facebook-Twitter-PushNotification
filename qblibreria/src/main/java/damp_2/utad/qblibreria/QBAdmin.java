package damp_2.utad.qblibreria;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;

import java.util.List;

/**
 * CLASE ENCARGADA DE LA GESTION DE Qb
 */
public class QBAdmin {

    private boolean sessionCreada;
    private QBAdminListener listener;


    /**
     * Se pasan las creedenciales
     * @param id
     * @param key
     * @param secret
     */
    public QBAdmin(String id, String key, String secret) {

        QBSettings.getInstance().fastConfigInit(id, key, secret);

    }

    /**
     * Se añade el listener
     * @param list
     */
    public void addQBAdminListener(QBAdminListener list) {
        listener = list;
    }

    /**
     * Se crea la sesion. Se gestiona los dos estados/resultados posibles: Satisfactorio y Error
     */
    public void sessionSimple() {
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                sessionCreada = true;
                listener.sessionCreada(sessionCreada);
                Log.v("QBADMIN", "DEBERIA EN LA SESION");
            }

            @Override
            public void onError(List<String> errors) {
                // errors
                sessionCreada = false;
                listener.sessionCreada(sessionCreada);

            }
        });
    }

}
