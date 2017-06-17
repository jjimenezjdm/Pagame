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
 * Created by Judit JImenez on 17/12/2016.
 */

public class EditBillActivity extends AppCompatActivity {

    //Variable para controlar el resultado de un item
    private static final int REQUEST_CHOOSE_PHONE = 1;
    //TAG al mostrar un LOG podamos saber dónde estamos
    private static final String TAG = "EDIT CUENTA";
    //Creamos los campos EditText que hay en el formulario para manipularlos
    private EditText mNombre, mTelefono, mMoney, mDescripcion;
    private RadioGroup grupo;
    //Creamos una variable DatabaseReference referencia a nuestra BD en firebase
    private DatabaseReference mDatabaseReference;
    private static String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cuenta);

        //Obtengo el String para ver el ID de la cuenta que quiero editar
        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("ID");

        //Obtenemos los diferentes campos del formulario
        grupo = (RadioGroup) findViewById(R.id.opciones_cuenta);
        mNombre = (EditText) findViewById(R.id.nombre);
        mDescripcion = (EditText) findViewById(R.id.desc);
        mTelefono = (EditText) findViewById(R.id.numTelef);
        mMoney = (EditText) findViewById(R.id.Cantmoney);

        //Autocompletamos los campos del formulario
        mNombre.setText( bundle.getString("nombre"));
        mDescripcion.setText(bundle.getString("descripcion"));
        mTelefono.setText(bundle.getString("telefono"));
        mMoney.setText(bundle.getString("dinero"));


        //Obtenemos la referencia de la base de datos de Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Boton añadir un número de teléfono
        Button contactButton = (Button) findViewById(R.id.ChooseButton);
        contactButton.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditBillActivity.this, ChoosePhoneActivity.class);
                startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
            }
        }));


        //Accion para el boton de editarCuenta
        final Button submitButton = (Button) findViewById(R.id.editarCuenta);
        submitButton.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String debes = "";

                //Obtenemos el valor del formulario
                String nombre = mNombre.getText().toString();
                String telefono = mTelefono.getText().toString();
                String descripcion = mDescripcion.getText().toString();
                String dinero = mMoney.getText().toString();

                if (grupo.getCheckedRadioButtonId() == R.id.bDebes) {
                    debes = " Le debes ";
                } else {
                    debes = " Te debe ";
                }

                //Creo una cuenta con esos datos
                Intent data = new Intent();
                BillItem.packageIntent(data, nombre, descripcion, telefono, dinero, debes,dateString);
                log("Los datos llegan a EditBillActivity: Nombre " + nombre + " Telefono " + telefono + " descripcion " + descripcion + " DINERO " + dinero + " " + debes);

                //Formateo la fecha
                Date d = new Date();
                dateString = String.valueOf(d.getTime());

                List<String> telef = new ArrayList<String>();
                telef.add(telefono);
                setResult(RESULT_OK, data);
                //Me creo una cuenta con esos datos para añadirla a la BD
                BillItem c = new BillItem(nombre, descripcion, telef, dinero, debes, dateString);

                //Preparamos la BD para la insercción de la nueva cuenta
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if((telefono.equalsIgnoreCase("")) || dinero.equalsIgnoreCase("")) {
                        Toast.makeText(EditBillActivity.this, "Hay campos vacios en el formulario",
                                Toast.LENGTH_LONG).show();
                        Intent a = new Intent(EditBillActivity.this, EditBillActivity.class);
                        a.putExtra("ID",id);
                        startActivity(a);
                    }else {
                        //Introducimos los valores en la base de datos de firebase
                        mDatabaseReference.child(user.getUid()).child("Cuentas").child("Amigos").child(id).setValue(c);
                        c.setDescripcion("Editaste ");
                        mDatabaseReference.child(user.getUid()).child("Cuentas").child("Actividad").push().setValue(c);
                        //He acabado de editar la cuenta, me iria a la actividad Dashboard
                        startActivity(new Intent(EditBillActivity.this, DashboardActivity.class));
                    }
                } else {
                    //Si no hay un usuario registrado, volvemos a Login
                    startActivity(new Intent(EditBillActivity.this, LoginActivity.class));
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