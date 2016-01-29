package damp_2.utad.actividad7_fernando_sanchez_martinez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.HashMap;

import damp_2.utad.qblibreria.QBAdmin;
import damp_2.utad.qblibreria.QBAdminIdioma;
import damp_2.utad.qblibreria.QBAdminIdiomaListener;
import damp_2.utad.qblibreria.QBAdminListener;
import damp_2.utad.qblibreria.QBUsersLogin;
import damp_2.utad.qblibreria.QBUsersLoginListener;


/**
 * CLASE MAIN ACTIVITY: Es la clase principal de la app, encargada de mostrar la representación gráfica de el login, y ,
 * a su vez, gestionar los datos que el usuario introduce.
 * Implementa todas la clases e interfaces gestionadoras de QuickBlox.
 * Toda la información está almacenada de forma jerárquica al igual que la herencia con las distintas librerias.
 */
public class MainActivity extends AppCompatActivity implements QBAdminListener, QBUsersLoginListener, QBAdminIdiomaListener {
    /**
     * Se declaran todas las variables necesarias
     */
    private QBAdmin qbAdmin;
    private QBAdminIdioma qbAdminIdioma;
    private QBUsersLogin qbUsersLogin;
    private ArrayList<QBCustomObject> dataLang;
    private TextView tv1;
    private TextView tv2;
    public EditText et1;
    public EditText et2;
    private Button botonLogin;
    private String idioma = "1";
    private String nombre;
    private String password;
    private CheckBox radioCred;
    buttonListener botonListener;

    /**
     * Se introducen las creedenciales de nuestra app de QuickBlox
     */
    // Datos para logearse al QBX
    private String apId = "33666";
    private String apKey = "Z4RtXJ8ND2-HkNa";
    private String apSecret = "M2jw43vOOOB7ju3";

    //SharedPreferences settings = getSharedPreferences("actividad6", 0);

    /**
     * Método que se ejecuta y crea el Main Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// se le asigna el layout que le dara su vista LOGIN

        botonListener = new buttonListener(this);

        /**
         * Se buscan por id y asignan las variables a los txt y lbl de la app
         */
        tv1 = (TextView) findViewById(R.id.lbl_usuario);
        tv2 = (TextView) findViewById(R.id.lbl_pass);
        et1 = (EditText) findViewById(R.id.txt_user);
        et2 = (EditText) findViewById(R.id.txt_pass);

        radioCred = (CheckBox) findViewById(R.id.cb_creedenciales);

        botonLogin = (Button) findViewById(R.id.btn_Login);
        botonLogin.setOnClickListener(botonListener);

        /**
         * Se inicializan los listeners y el administrador de QB con las creedenciales anteriormente declaradas.
         */
        qbAdmin = new QBAdmin(apId, apKey, apSecret);
        qbUsersLogin = new QBUsersLogin();
        qbAdminIdioma = new QBAdminIdioma();

        qbUsersLogin.addQBUserLoginListener(this);
        qbAdminIdioma.addIdiomaListener(this);

        qbAdmin.addQBAdminListener(this);
        qbAdmin.sessionSimple();

    }

    /**
     * Método que extrae la info del usuario y la envia para hacer el logeo
     *
     * @param nombre
     * @param password
     */
    public void logear(String nombre, String password) {

        nombre = String.valueOf(et1.getText());
        password = String.valueOf(et2.getText());

        qbUsersLogin.loginUsuario(nombre, password);
    }

   /* public void generarHash(){
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("gebulot.pmdm_actividad7_sol", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }*/

    /**
     * Método que nos avisa si la sesión ha sido creada y en que estado.
     * A su vez, almacena los datos en la memoria interna en caso de que el usuario de a pp lo decida y marque el checkBox
     *
     * @param esCreada
     */
    @Override
    public void sessionCreada(boolean esCreada) {

        SharedPreferences settings = getSharedPreferences("actividad6", 0);
        String usuario = settings.getString("USUARIO", null);
        String pass = settings.getString("PASS", null);

        if (esCreada == true) {
            /**
             * Si existe algun dato almacenado anteriormente, logear directamente sin pasar por el Login
             */
            if (usuario != null && pass != null) {
                logear(usuario, pass);
                Log.v("sessionCreada", " session creada " + esCreada);
            } else {
                Log.v("sessionCreada", " session creada " + esCreada);
            }
        } else {
            Log.v("sessionCreada", " session NO creada " + esCreada);
        }
    }

    /**
     * Método que obtiene el idioma seleccionado
     *
     * @param arrCustomObjects
     */
    @Override
    public void getIdioma(ArrayList<QBCustomObject> arrCustomObjects) {
        ArrayList<String> arrValor = new ArrayList<>();
        this.dataLang = arrCustomObjects;
        if (arrCustomObjects != null) {


            Log.v("MainActivity", "cargando datos");
            for (int i = 0; i < dataLang.size(); i++) {
                HashMap<String, Object> fields = dataLang.get(i).getFields();

                Log.v("MainActivity", "Los datos son: " + fields.get("valor"));
                arrValor.add(fields.get("valor").toString());

            }
        }
        /**
         * Se modifican con el texto en el idioma correspondiente los distintos elementos de nuesta pantalla de login
         */
        tv1.setText(arrValor.get(0).toString());
        tv2.setText(arrValor.get(1).toString());
        botonLogin.setText(arrValor.get(2).toString());

    }

    /**
     * Método que lanza la otra actividad del mapa si el logeo ha sido exitoso, y en caso de que el usuario haya
     * seleccionado el checkBox, almacenará las creedenciales en la memoria interna para fururos usos de app y
     * comodidad del usuario.
     *
     * @param logeado
     */
    @Override
    public void login(boolean logeado) {
        //SharedPreferences settings = getSharedPreferences("actividad6", 0);
        if (logeado == true) {

            if (radioCred.isChecked()) {
                SharedPreferences settings = getSharedPreferences("actividad6", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("USUARIO", nombre);
                editor.putString("PASS", password);
                editor.commit();
                lanzar1();// se lanza la nueva activity
            } else {
                lanzar1();// se lanza la nueva activity
            }

            //lanzar1();// se lanza la nueva activity

            Toast.makeText(MainActivity.this, et1.getText() + " Ha logueado con éxito ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, " Usuario o contraseña inválidos ", Toast.LENGTH_SHORT).show();
        }

    }

    public void lanzar1() {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    /**
     * Gestiona el clickeo correcto y sin multiples opciones del selector de idioma
     *
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton:
                if (checked)
                    idioma = "1";
                break;
            case R.id.radioButton2:
                if (checked)
                    idioma = "2";
                break;

        }
        /**
         * Se obtiene el idioma seleccionado
         */
        if (idioma.equals("1")) {
            qbAdminIdioma.getData(idioma);
        } else if (idioma.equals("2")) {
            qbAdminIdioma.getData(idioma);
        }
    }

}
