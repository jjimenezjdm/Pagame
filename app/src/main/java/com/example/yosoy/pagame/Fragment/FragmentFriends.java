package com.example.yosoy.pagame.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yosoy.pagame.Activity.AddBillActivity;
import com.example.yosoy.pagame.Activity.DetailsBillActivity;
import com.example.yosoy.pagame.Item.BillItem;
import com.example.yosoy.pagame.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentFriends extends Fragment implements View.OnClickListener {
    // Add a BillItem Request Code
    private static final int ADD_CUENTA_ITEM_REQUEST = 0;
    //Referencia a la BD de Firebase
    private DatabaseReference mFirebaseDatabaseReference;
    private static final String TAG = "FragmentFriends";
    //Float que contará nuestro saldo actual
    private Float total=0f;
    //RV
    private RecyclerView mRecyclerView;
    //Adaptador de Firebase para la RecyclerView
    private FirebaseRecyclerAdapter<BillItem, BillItemViewHolder> mFirebaseAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fAmigos = inflater.inflate(R.layout.fragment_content_amigos, container, false);
        //Obtenemos los datos del layout
        FloatingActionButton fab = (FloatingActionButton) fAmigos.findViewById(R.id.buttonAdd);
        fab.setOnClickListener(this);

        //Obtenemos los datos para incializar la cv con la información del usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final CardView cv = (CardView) fAmigos.findViewById(R.id.UserInfo);
        final TextView finalDinero = (TextView) cv.findViewById(R.id.debeDineros);
        if(user!=null) {
            TextView email = (TextView) cv.findViewById(R.id.nombre);
            email.setText(user.getEmail());
            ImageView im = (ImageView) cv.findViewById(R.id.photoUser);
            //Mostramos la imagen con Glide
            Glide.with(FragmentFriends.this)
                    .load(user.getPhotoUrl())
                    .into(im);
        }
        //Obtenemos la RV
        mRecyclerView = (RecyclerView) fAmigos.findViewById(R.id.RecyclerViewAmigos);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //Inicializamos la BD
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        total = 0f;
        mFirebaseAdapter = new FirebaseRecyclerAdapter<BillItem, BillItemViewHolder>(
                BillItem.class, R.layout.activity_cuenta_item, BillItemViewHolder.class,
                mFirebaseDatabaseReference.child(user.getUid()).child("Cuentas").child("Amigos")) {
            //Método que se ejecutara tantas veces como cuentas tengamos en nuestra BD
            @Override
            protected void populateViewHolder(final BillItemViewHolder viewHolder, BillItem model, final int position) {
                Color C = new Color();
                //Obtiene los valores de Firebase y los introduce en los textView correspondientes
                viewHolder.cuentaName.setText(model.getNombre());
                viewHolder.cuentaMoney.setText(model.getMoney() + " €");
                List<String> tel = model.getmTelefono();
                viewHolder.cuentaTelephone.setText(tel.get(0));
                viewHolder.cuentaDebes.setText(model.getDebe());
                Long dat = Long.parseLong(model.getDate());
                Date d = new Date(dat);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = formatter.format(d);
                viewHolder.cuentaDate.setText(formattedDate);
                //Coloreamos los textView dependiendo de los valores indicados
                if (model.getDebe().equalsIgnoreCase(" Te debe ")) {
                    viewHolder.cuentaDebes.setTextColor(getResources().getColor(R.color.TEDEBEN));
                    viewHolder.cuentaMoney.setTextColor(getResources().getColor(R.color.TEDEBEN));
                    total+= Float.parseFloat(model.getMoney());
                } else if (model.getDebe().equalsIgnoreCase(" Le debes ")) {
                    viewHolder.cuentaDebes.setTextColor(C.RED);
                    viewHolder.cuentaMoney.setTextColor(C.RED);
                    total-= Float.parseFloat(model.getMoney());
                }

                if(total<0)
                    finalDinero.setTextColor(C.RED);
                else if (total>0)
                    finalDinero.setTextColor(getResources().getColor(R.color.TEDEBEN));
                finalDinero.setText(total.toString());
                finalDinero.setText(finalDinero.getText()+" €");
                viewHolder.mview.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent details = new Intent(getActivity(), DetailsBillActivity.class);
                        final String ID = getRef(position).getKey();
                        details.putExtra("ID",ID);
                        startActivity(details);
                    }
                });
            }
        };

        //Le introducimos el adaptador a la RecyclerView
        mRecyclerView.setAdapter(mFirebaseAdapter);
        return fAmigos;
    }

    /**
     * Método para cuando se pulsa en el botón add
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent act2 = new Intent(getActivity(), AddBillActivity.class);
        act2.putExtra("TabHost","Amigos");
        startActivityForResult(act2, ADD_CUENTA_ITEM_REQUEST);
    }

    /**
     * Método para comprobar que lo que nos devuelven es correcto
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        log("Entered onActivityResult()");

        //Check result code and request code.
        if (requestCode == ADD_CUENTA_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                //Create a BillItem from data and add it to the adapter
                BillItem item = new BillItem(data);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void log(String msg) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, msg);
    }
    /**
     * Clase que nos servirá de ayuda para mostrar el contenido en la RV
     */
    public static class BillItemViewHolder extends RecyclerView.ViewHolder {
        //Obtenemos los TextView
        TextView cuentaName;
        TextView cuentaMoney;
        TextView cuentaDebes;
        TextView cuentaTelephone;
        TextView cuentaDate;
        View mview;

        public BillItemViewHolder(View v) {
            super(v);

            cuentaName = (TextView) itemView.findViewById(R.id.nombre);
            cuentaMoney = (TextView) itemView.findViewById(R.id.money);
            cuentaDebes = (TextView) itemView.findViewById(R.id.debes);
            cuentaTelephone = (TextView) itemView.findViewById(R.id.telephone);
            cuentaDate = (TextView) itemView.findViewById(R.id.date);
            mview = v;
        }


    }



}
