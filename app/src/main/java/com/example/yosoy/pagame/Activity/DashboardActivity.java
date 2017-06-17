package com.example.yosoy.pagame.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yosoy.pagame.Fragment.FragmentActivity;
import com.example.yosoy.pagame.Fragment.FragmentFriends;
import com.example.yosoy.pagame.Fragment.FragmentGroup;
import com.example.yosoy.pagame.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Judit Jiménez on 05/11/2016.
 * <p>
 * Actividad principal de la aplicación, desde esta tendremos acceso a las demás
 */
public class DashboardActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //TAG para poder saber en el LOG dónde estamos
    private static final String TAG = "CuentaInterface";
    //Variable de Firebase para controlar la autentificación de un usuario
    private FirebaseAuth mFirebaseAuth;
    //Variable de Firebase para controlar el usuario
    private FirebaseUser mFirebaseUser;
    //Nombre del Usuario autentificado
    private String mUsername;
    //Guardar las preferencias de usuario
    private SharedPreferences mSharedPreferences;
    //Usaremos esta variable para partir del hecho de que el user es anonimo (no hay)
    public static final String ANONYMOUS = "anonymous";
    // IDs for menu items (LogOut)
    private static final int MENU_LOGOUT = Menu.FIRST;
    //Variable de GoogleApiClient para poder hacer login
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_dashboard);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        // Inicializamos las variables de Firebase de la autentificacion
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //Comprobamos si el usuario está a null o no
        if (mFirebaseUser == null) {
            // No hay usuario autentificado, nos volvemos al Login
            startActivity(new Intent(this, LoginActivity.class));
            //Acabamos
            finish();
            return;
        } else {
            //Hay usuario autentificado, guardamos las credenciales
            mUsername = mFirebaseUser.getDisplayName();

        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        //Obtenemos el TabHost de activity_dashboard_content
        FragmentTabHost tabHost = (FragmentTabHost) findViewById(R.id.tabHost);
        //la añadimos a la principal
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        //Tab amigos
        tabHost.addTab(tabHost.newTabSpec("AMIGOS").setIndicator("AMIGOS"), FragmentFriends.class, null);
        //Tab grupo
        tabHost.addTab(tabHost.newTabSpec("GRUPO").setIndicator("GRUPO"), FragmentGroup.class, null);
        //Tab amigos
        tabHost.addTab(tabHost.newTabSpec("ACTIVIDAD").setIndicator("ACTIVIDAD"), FragmentActivity.class, null);
        tabHost.addTab(tabHost.newTabSpec("ACTIVIDAD").setIndicator("ACTIVIDAD"), FragmentActivity.class, null);

        //Por defecto iniciamos en el tabhost "amigos"
        tabHost.setCurrentTab(0);
    }

    /**
     * Creamos las opciones para el menú de Logut
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_LOGOUT, Menu.NONE, "Cerrar Sesión");
        return true;
    }

    /**
     * Dependiendo del valor, hacemos una accion u otra
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_LOGOUT:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


}