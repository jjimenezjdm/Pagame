package com.example.yosoy.pagame.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.yosoy.pagame.Item.BillItem;
import com.example.yosoy.pagame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase AddBillActivity, maneja el formulario activity_add_cuenta
 */
public class AddBillActivity extends AppCompatActivity {

    //Variable para controlar el resultado de un item
    private static final int REQUEST_CHOOSE_PHONE = 1;
    //TAG al mostrar un LOG podamos saber dónde estamos
    private static final String TAG = "ADD CUENTA";
    //Creamos los campos EditText que hay en el formulario para manipularlos
    private EditText mNombre, mTelefono, mMoney, mDescripcion;
    private RadioGroup grupo;
    private static String dateString;

    //Creamos una variable DatabaseReference referencia a nuestra BD en firebase
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtengo el String para ver si vengo del tabhost "Amigos" o "Grupo"
        Bundle bundle = getIntent().getExtras();
        final String extra = bundle.getString("TabHost");
        if (extra.equalsIgnoreCase("Amigos")) {
            setContentView(R.layout.activity_add_cuenta);

            //Obtenemos los diferentes campos del formulario
            grupo = (RadioGroup) findViewById(R.id.opciones_cuenta);
            mNombre = (EditText) findViewById(R.id.nombre);
            mDescripcion = (EditText) findViewById(R.id.desc);
            mTelefono = (EditText) findViewById(R.id.numTelef);
            mMoney = (EditText) findViewById(R.id.Cantmoney);

            //Boton añadir un número de teléfono
            Button contactButton = (Button) findViewById(R.id.ChooseButton);
            contactButton.setOnClickListener((new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddBillActivity.this, ChoosePhoneActivity.class);
                    startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
                }
            }));
        } else if (extra.equalsIgnoreCase(("Grupo"))) {
            setContentView(R.layout.activity_add_grupo);
            //Obtenemos los diferentes campos del formulario
            grupo = (RadioGroup) findViewById(R.id.opciones_cuenta);
            mNombre = (EditText) findViewById(R.id.nombre);
            mDescripcion = (EditText) findViewById(R.id.desc);
            mMoney = (EditText) findViewById(R.id.Cantmoney);
        }
        //Obtenemos la referencia de la base de datos de Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Código correspondiente a la propia creacion de la cuenta
        final Button submitButton = (Button) findViewById(R.id.crearCuenta);
        submitButton.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String debes = "";
                boolean debe = false;
                //Obtenemos el valor del formulario
                String nombre = mNombre.getText().toString();
                String telefono = " ";
                if (extra.equalsIgnoreCase("Amigos")) {
                    telefono = mTelefono.getText().toString();
                } else if (extra.equalsIgnoreCase("Grupo")) {
                    telefono = " ";
                }
                String descripcion = mDescripcion.getText().toString();
                String dinero = mMoney.getText().toString();
                if (grupo.getCheckedRadioButtonId() == R.id.bDebes) {
                    debes = " Le debes ";
                    debe = true;
                } else {
                    debes = " Te debe ";
                }

                //Creo una cuenta con esos datos
                Intent data = new Intent();
                BillItem.packageIntent(data, nombre, descripcion, telefono, dinero, debes, dateString);
                log("Los datos llegan a AddBillActivity: Nombre " + nombre + " Telefono " + telefono + " descripcion " + descripcion + " DINERO " + dinero + " " + debes);

                //Obtengo el String para ver si vengo del tabhost "Amigos" o "Grupo"
                Bundle bundle = getIntent().getExtras();
                String extra = bundle.getString("TabHost");
                setResult(RESULT_OK, data);
                //Damos formato a la fecha
                Date d = new Date();
                dateString = String.valueOf(d.getTime());
                List<String> telef = new ArrayList<String>();
                telef.add(telefono);
                //Me creo una cuenta con esos datos para añadirla a la BD
                BillItem c = new BillItem(nombre, descripcion, telef, dinero, debes, dateString);

                //Preparamos la BD para la insercción de la nueva cuenta
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //Introducimos los valores en la base de datos de firebase
                    Log.i(TAG, "VENGO DEL TAB " + extra);
                    if ((telefono.equalsIgnoreCase("")) || dinero.equalsIgnoreCase("")) { //Si no hay un telefono o no hay cuenta, no
                        //dejo crear la cuenta y además vuelvo a llamar a la activity
                        Toast.makeText(AddBillActivity.this, "Hay campos vacios en el formulario",
                                Toast.LENGTH_LONG).show();
                        Intent a = new Intent(AddBillActivity.this, AddBillActivity.class);
                        a.putExtra("TabHost", extra);
                        startActivity(a);
                    } else {
                        //Si todos los datos introducidos son correctos, añado la cuenta a la BD
                        mDatabaseReference.child(user.getUid()).child("Cuentas").child(extra).push().setValue(c);
                        //Cambio la descripcion para que me sirva en el tab de "Actividad"
                        c.setDescripcion("Añadiste ");
                        mDatabaseReference.child(user.getUid()).child("Cuentas").child("Actividad").push().setValue(c);
                        Toast.makeText(AddBillActivity.this, "Creada nueva cuenta.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Si no hay un usuario registrado, volvemos a Login
                    startActivity(new Intent(AddBillActivity.this, LoginActivity.class));
                }
                finish();
            }

        }));
    }

    /**
     * OnActivityResoult del método, comprueba que el resultado de los ITEMS es el correcto
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Si ha ido bien, cogemos el contacto y lo metemos en el campo mTelefono
        if ((requestCode == REQUEST_CHOOSE_PHONE)
                && (resultCode == Activity.RESULT_OK)) {
            try {
                String telefono = data.getStringExtra("telefono");
                mTelefono.setText(telefono);
                mNombre.setText(data.getStringExtra("nombre"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método que sirve para poder mostrar los mensajes por consola
     *
     * @param msg
     */
    private void log(String msg) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, msg);
    }


}
