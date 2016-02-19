package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.view.View;
import android.widget.Button;

/**
 * Clase encargada de la gestión de los click en el boton de logear. Es el listener de los botones
 */
public class buttonListener implements View.OnClickListener{

    MainActivity mainActivity;// Se crea una referencia a MainActivity


    /**
     * Constructor
     * @param ma1
     */
    public buttonListener(MainActivity ma1){
        mainActivity=ma1;
    }

    /**
     * Implementacion del onClick de "Logear"
     * @param v
     */
    @Override
    public void onClick(View v) {

        Button boton = (Button)v;
        if(boton.getId()==(R.id.btn_Login))
        {
            mainActivity.logear(mainActivity.et1.getText().toString(),mainActivity.et2.getText().toString());
        }/*else if(boton.getId()==(R.id.btn_descargarCoord)){
            mapsActivity.qbAdminLocalizaciones.getData();

        }*/
    }
}
