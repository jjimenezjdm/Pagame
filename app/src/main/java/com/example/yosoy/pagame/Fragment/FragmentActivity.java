package com.example.yosoy.pagame.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yosoy.pagame.Item.BillItem;
import com.example.yosoy.pagame.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentActivity extends Fragment {

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<BillItem, BillItemViewHolder> mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private static String dateString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fActividad = inflater.inflate(R.layout.fragment_content_actividad, container, false);
        //Obtenemos la referencia a la RecyclerView
        mRecyclerView = (RecyclerView) fActividad.findViewById(R.id.RecyclerViewActividad);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Obtenemos la referencia al user de Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Inicializamos la referencia a la BD
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //Creamos el adaptador para la RV, en este caso, queremos mostrar cuentas, por lo que será de este tipo.
        mFirebaseAdapter = new FirebaseRecyclerAdapter<BillItem, BillItemViewHolder>(
                BillItem.class, R.layout.activity_gestion_cuenta, BillItemViewHolder.class,
                mFirebaseDatabaseReference.child(user.getUid()).child("Cuentas").child("Actividad")) {
            //Se llamará al metodo tantas veces como cuentas tengamos en nuestra BD
            @Override
            protected void populateViewHolder(BillItemViewHolder viewHolder, BillItem model, int position) {
                Color C = new Color();
                //Le ponemos valor a los TextView con la informacion que obtenemos de Firebase
                viewHolder.cuentaName.setText(model.getNombre());
                viewHolder.cuentaMoney.setText(model.getMoney() + " €");
                viewHolder.cuentaEstado.setText(model.getDescripcion() + " deuda");
                viewHolder.cuentaDebes.setText(model.getDebe());
                if (!model.getDate().equalsIgnoreCase("")) {
                    Long dat = Long.parseLong(model.getDate());
                    Date d = new Date(dat);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = formatter.format(d);
                    viewHolder.cuentaDate.setText(formattedDate);
                }//Coloreamos los datos dependiendo de las acciones que hayamos hecho
                if (model.getDescripcion().equalsIgnoreCase("Añadiste ")) {
                    viewHolder.cuentaEstado.setTextColor(getResources().getColor(R.color.TEDEBEN));
                } else if (model.getDescripcion().equalsIgnoreCase("Liquidaste ")) {
                    viewHolder.cuentaEstado.setTextColor(C.RED);
                } else if (model.getDescripcion().equalsIgnoreCase("Editaste ")) {
                    viewHolder.cuentaEstado.setTextColor(C.BLUE);
                }
            }

        };

        //Le introducimos el adaptador a la RecyclerView
        mRecyclerView.setAdapter(mFirebaseAdapter);
        return fActividad;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Clase que nos servirá de ayuda para mostrar el contenido en la RV
     */
    public static class BillItemViewHolder extends RecyclerView.ViewHolder {
        //Obtenemos los TextView
        TextView cuentaName;
        TextView cuentaMoney;
        TextView cuentaDebes;
        TextView cuentaEstado;
        TextView cuentaDate;
        View mview;

        public BillItemViewHolder(View v) {
            super(v);
            cuentaName = (TextView) itemView.findViewById(R.id.nombre);
            cuentaMoney = (TextView) itemView.findViewById(R.id.money);
            cuentaDebes = (TextView) itemView.findViewById(R.id.debes);
            cuentaEstado = (TextView) itemView.findViewById(R.id.descripcion);
            cuentaDate = (TextView) itemView.findViewById(R.id.date);
            mview = v;
        }
    }

}
