package com.example.yosoy.pagame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yosoy.pagame.Item.BillItem;
import com.example.yosoy.pagame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * Created by Judit Jiménez on 06/12/2016.
 * Clase para mostrar en detalle una cuenta
 */

public class DetailsBillActivity extends AppCompatActivity {
    private static final String TAG = "DETAILS CUENTA";
    //Variables de los TextView donde mostraremos la informacion
    private TextView mNombre, mTelefono, mMoney, mDescripcion, mDebes;
    //Referencia a la BD de Firebase
    private DatabaseReference mDatabaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_cuenta);
        //Obtenemos la referencia a la BD
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Obtenemos los valores de los TextView
        mTelefono = (TextView) findViewById(R.id.Telefono);
        mNombre = (TextView) findViewById(R.id.Nombre);
        mDescripcion = (TextView) findViewById(R.id.Descripcion);
        mMoney = (TextView) findViewById(R.id.Cuenta);
        mDebes = (TextView) findViewById(R.id.debesTeDeben);
        //Obtenemos el ID de la cuenta que estamos detallando
        Bundle b = getIntent().getExtras();
        final String id = b.getString("ID");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Para la cuenta que detallamos, vamos a coger los datos almacenados en la BD
        mDatabaseReference.child(user.getUid()).child("Cuentas").child("Amigos").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Obtenemos la cuenta y mostramos los datos cargandolos en los TextView
                final BillItem cuenta = dataSnapshot.getValue(BillItem.class);
                if(cuenta!=null) {
                    Log.i(TAG,"LA CUENTA ES: "+cuenta.toString());
                    final List<String> telefono = cuenta.getmTelefono();
                    mTelefono.setText(telefono.get(0));
                    mNombre.setText(cuenta.getNombre());
                    mMoney.setText(cuenta.getMoney());
                    mDescripcion.setText(cuenta.getDescripcion());
                    mDebes.setText(cuenta.getDebe());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "No tengo permiso para cargar la BD, no hay user conectado");

            }
        });
        //Accion del boton borrar
        final Button deleteCuenta = (Button) findViewById(R.id.liquidar);
        deleteCuenta.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null) {
                    //Obtenemos los datos de la cuenta antes de borrarla, ya que tenemos que añadirla primero a nuestros movimientos
                    mDatabaseReference.child(user.getUid()).child("Cuentas").child("Amigos").child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final BillItem cuenta = dataSnapshot.getValue(BillItem.class);
                            if(cuenta!=null) {
                                Log.i(TAG,"LA CUENTA ES: "+cuenta.toString());
                                cuenta.setDescripcion("Liquidaste ");
                                mDatabaseReference.child(user.getUid()).child("Cuentas").child("Actividad").push().setValue(cuenta);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "No tengo permiso para cargar la BD, no hay user conectado");

                        }
                });
                    //Borramos el valor de la BD con removeValue
                    Log.i("--------","El id de la cuenta que quiero eliminar es: "+id);
                    mDatabaseReference.child(user.getUid()).child("Cuentas").child("Amigos").child(id).removeValue();
                    Intent i = new Intent (getApplication(),DashboardActivity.class);
                    startActivity(i);
                    }

            }
        }));

        //Accion del boton editar. Obtiene los datos nuevos del TextView e inserta la nueva cuenta
        final Button editarCuenta = (Button) findViewById(R.id.editar);
        editarCuenta.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null) {
                    Intent editCuenta = new Intent(getApplication(),EditBillActivity.class);
                    editCuenta.putExtra("telefono",mTelefono.getText());
                    editCuenta.putExtra("dinero",mMoney.getText());
                    editCuenta.putExtra("descripcion",mDescripcion.getText());
                    editCuenta.putExtra("debe",mDebes.getText());
                    editCuenta.putExtra("nombre",mNombre.getText());
                    editCuenta.putExtra("ID",id);
                    startActivityForResult(editCuenta,RESULT_OK);
                }

            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


}
