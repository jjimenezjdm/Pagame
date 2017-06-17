package com.example.yosoy.pagame.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yosoy.pagame.Activity.AddBillActivity;
import com.example.yosoy.pagame.Activity.DetailsGroupActivity;
import com.example.yosoy.pagame.Item.BillItem;
import com.example.yosoy.pagame.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FragmentGroup extends Fragment implements View.OnClickListener {

    // Add a BillItem Request Code
    private static final int ADD_CUENTA_ITEM_REQUEST = 0;

    private static final String TAG = "FragmentGroup";
    //Referenciamos la BD
    private DatabaseReference mFirebaseDatabaseReference;
    //Nos creamos el adaptador para ver los datos, en este caso de tipo BillItem
    private FirebaseRecyclerAdapter<BillItem, BillItemViewHolder> mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fGrupo =  inflater.inflate(R.layout.fragment_content_grupo,container,false);
        FloatingActionButton fab = (FloatingActionButton) fGrupo.findViewById(R.id.buttonAdd);
        fab.setOnClickListener(this);

        mRecyclerView = (RecyclerView) fGrupo.findViewById(R.id.RecyclerViewGrupo);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Referencia del usuario logueado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Inicializamos la BD
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<BillItem, BillItemViewHolder>(
                BillItem.class, R.layout.activity_cuenta_item, BillItemViewHolder.class,
                mFirebaseDatabaseReference.child(user.getUid()).child("Cuentas").child("Grupo")) {
            //Metodo que se ejecuta una vez por caada cuenta que tengamos en la BD de Firebase
            @Override
            protected void populateViewHolder(BillItemViewHolder viewHolder, BillItem model,final int position) {
                Color C = new Color();
                //Obtiene los datos guardados en Firebase y los introduce en los diferentes campos de nuesto layout
                viewHolder.cuentaName.setText(model.getNombre());
                viewHolder.cuentaMoney.setText(model.getMoney() + " €");
                List<String> tel = model.getmTelefono();
                viewHolder.cuentaTelephone.setText(tel.get(0));
                viewHolder.cuentaDebes.setText(model.getDebe());
                if (model.getDebe().equalsIgnoreCase(" Te debe ")) {
                    viewHolder.cuentaDebes.setTextColor(getResources().getColor(R.color.TEDEBEN));
                    viewHolder.cuentaMoney.setTextColor(getResources().getColor(R.color.TEDEBEN));
                } else if (model.getDebe().equalsIgnoreCase(" Le debes ")) {
                    viewHolder.cuentaDebes.setTextColor(C.RED);
                    viewHolder.cuentaMoney.setTextColor(C.RED);
                }

                viewHolder.mview.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent details = new Intent(getActivity(), DetailsGroupActivity.class);
                        final String ID = getRef(position).getKey();
                        details.putExtra("ID",ID);
                        startActivity(details);
                    }
                });

            }

        };


        mRecyclerView.setAdapter(mFirebaseAdapter);

        return fGrupo;
    }

    /**
     * Método para cuando pulsemos sobre el boton add irnos a la actividad correspondiente a add
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent act2 = new Intent(getActivity(), AddBillActivity.class);
        act2.putExtra("TabHost","Grupo");
        startActivityForResult(act2, ADD_CUENTA_ITEM_REQUEST);
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
     * Método que nos ayuda a mostrar los datos en la RV
     */
    public static class BillItemViewHolder extends RecyclerView.ViewHolder {
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
