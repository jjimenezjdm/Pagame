package com.example.yosoy.pagame.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yosoy.pagame.Item.BillItem;
import com.example.yosoy.pagame.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Muestra los datos de un grupo
 * Created by Judit Jiménez on 18/12/2016.
 */

public class DetailsGroupActivity extends AppCompatActivity {



    //Variable para controlar el resultado de un item
    private static final int REQUEST_CHOOSE_PHONE = 1;
    //TAG al mostrar un LOG podamos saber dónde estamos
    private static final String TAG = "ADD CUENTA";
    //Creamos los campos EditText que hay en el formulario para manipularlos
    private TextView mNombre, mTelefono, mMoney, mDescripcion,mDebes;
    private static String debe;
    private static String dateString;

    private List<String> telefonos = new ArrayList<String>();
    //Creamos una variable DatabaseReference referencia a nuestra BD en firebase
    private DatabaseReference mDatabaseReference;
    //Creamos la referencia al usuario logueado
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //Creamos la variable de la recyclerView para mostrar el telefono
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<String, DetailsGroupActivity.TelefonoItemViewHolder> mFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_details);

        //Obtenemos el valor de la cuenta de tipo grupo que estamos mirando
        Bundle b = getIntent().getExtras();
        final String IDG = b.getString("ID");

        //Obtenemos la referencia de la base de datos de Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Obtenemos la referencia a los diferentes TextView de grupo_details
        mNombre = (TextView) findViewById(R.id.Nombre);
        mDescripcion = (TextView) findViewById(R.id.Descripcion);
        mMoney = (TextView) findViewById(R.id.Cuenta);
        mTelefono = (TextView) findViewById(R.id.Telefono);
        mDebes = (TextView) findViewById(R.id.debesTeDeben);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerViewTelefono);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsGroupActivity.this));

        //Inicializamos el adaptador, en este caso de String, para los diferentes telefonos almacenados en nuestra BD
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, DetailsGroupActivity.TelefonoItemViewHolder>(
                String.class, R.layout.activity_telefonoitem, DetailsGroupActivity.TelefonoItemViewHolder.class,
                mDatabaseReference.child(user.getUid()).child("Cuentas").child("Grupo").child(IDG).child("mTelefono")) {
            @Override
            protected void populateViewHolder(TelefonoItemViewHolder viewHolder, String model, int position) {
                Log.i("DetailsGrupo","Los telefonos son: "+model);
                viewHolder.cuentaTelephone.setText(model);
            }
        };
        //Le añadimos el adaptador a la RV
        mRecyclerView.setAdapter(mFirebaseAdapter);
        //Volvemos a obtener los datos de la cuenta y los mostramos en el TextView que le corresonda
        mDatabaseReference.child(user.getUid()).child("Cuentas").child("Grupo").child(IDG).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BillItem cuenta = dataSnapshot.getValue(BillItem.class);
                if(cuenta!=null) {
                    Log.i(TAG,"LA CUENTA ES: "+cuenta.toString());
                    mNombre.setText(cuenta.getNombre());
                    mMoney.setText(cuenta.getMoney());
                    mDescripcion.setText(cuenta.getDescripcion());
                    mDebes.setText(cuenta.getDebe());
                    debe = cuenta.getDebe();
                    dateString = cuenta.getDate();
                    List<String> telefonos = cuenta.getmTelefono();
                    for(int j = 0; j<telefonos.size(); j++)
                        Log.i("DetailsGrupo","TELEFONO: "+telefonos.get(j));

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "No tengo permiso para cargar la BD, no hay user conectado");

            }
        });


        //Boton añadir un número de teléfono
        Button contactButton = (Button) findViewById(R.id.añadir);
        contactButton.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsGroupActivity.this,ChoosePhoneActivity.class);
                intent.putExtra("ID", IDG);
                startActivityForResult(intent, REQUEST_CHOOSE_PHONE);
            }
        }) );


        //Accion para borrar una cuenta de tipo grupo
        final Button deleteCuenta = (Button) findViewById(R.id.liquidar);
        deleteCuenta.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null) {
                    mDatabaseReference.child(user.getUid()).child("Cuentas").child("Grupo").child(IDG).addValueEventListener(new ValueEventListener() {
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

                    Log.i("DetailsGroupActivity","El id de la cuenta que quiero eliminar es: "+IDG);
                    mDatabaseReference.child(user.getUid()).child("Cuentas").child("Grupo").child(IDG).removeValue();
                    Intent i = new Intent (getApplication(),DashboardActivity.class);
                    startActivity(i);
                }

            }
        }));



    }

    @SuppressLint("InlinedApi")
    private void addChild(String telefono) {
        //Mira el ID de la cuenta que tenemos
        Bundle b = getIntent().getExtras();
        final String idF = b.getString("ID");

        Log.i("DetailsGroupActivity","EL ID ES: "+idF);

        //Preparamos la BD para la insercción de la nueva cuenta (con los contactos añadidos)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            telefonos.add(telefono);
            BillItem c = new BillItem(mNombre.getText().toString(),mDescripcion.getText().toString(),telefonos,mMoney.getText().toString(),debe,dateString);
            //Introducimos los valores en la base de datos de firebase
            Log.i("DetailsGroupActivity","EL ID ES: "+idF);
            mDatabaseReference.child(user.getUid()).child("Cuentas").child("Grupo").child(idF).setValue(c);
            //Cambio la descripcion para que me sirva en el tab de "Actividad"
            c.setDescripcion("Añadido " + telefono);
            mDatabaseReference.child(user.getUid()).child("Cuentas").child("Actividad").push().setValue(c);

        }
    }
    /**
     * OnActivityResoult del método, comprueba que el resultado de los ITEMS es el correcto
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
                addChild(telefono);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clase que sirve de ayuda para mostrar el texto en la RV
     */
    public static class TelefonoItemViewHolder extends RecyclerView.ViewHolder {

        TextView cuentaTelephone;
        View mview;

        public TelefonoItemViewHolder(View v) {
            super(v);
            cuentaTelephone = (TextView) itemView.findViewById(R.id.Telefono);
            mview = v;
        }


    }

}