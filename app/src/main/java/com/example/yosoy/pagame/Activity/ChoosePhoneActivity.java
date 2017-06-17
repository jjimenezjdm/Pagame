package com.example.yosoy.pagame.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;

import android.content.pm.PackageManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.yosoy.pagame.R;

public class ChoosePhoneActivity extends ListActivity {

    final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 42;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        setTitle("ELEGIR CONTACTO");

        //Compruebo la versión del movil para pedir los permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }else {
                  proceedWithContactRead();
            }
        }
    }

    /**
     * Método principal que se encargará de leer los contactos de mi agenda
     */
    private void proceedWithContactRead() {

        // Get the ContentResolver (proxy para conectarse)
        ContentResolver contentResolver = getContentResolver();

        // Query: contacts with phone shorted by name
        Cursor mCursor = getContentResolver().query(
                Data.CONTENT_URI,
                new String[]{Data._ID, Data.DISPLAY_NAME, Phone.NUMBER,
                        Phone.TYPE},
                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' AND "
                        + Phone.NUMBER + " IS NOT NULL", null,
                Data.DISPLAY_NAME + " ASC");

        Log.i("ContactListExample", "Num contactos: " + mCursor.getCount());

        // Setup the list
        ListAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                mCursor,
                new String[]{Data.DISPLAY_NAME, Phone.NUMBER},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        setListAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    proceedWithContactRead();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    CharSequence text = "Ups! I need your permission :-/";
                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(this, text, duration).show();
                }
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent result = new Intent();

        // Obtiene el dato en cuestión
        Cursor c = (Cursor) getListAdapter().getItem(position);
        //Guarda el número de teléfono y el nombre
        int colIdx = c.getColumnIndex(Phone.NUMBER);
        String telefono = c.getString(colIdx);
        String nombre = c.getString(c.getColumnIndex(Data.DISPLAY_NAME));

        // Guarda el telefono
        result.putExtra("telefono", telefono);
        setResult(Activity.RESULT_OK, result);

        //Guardar el nombre del contacto
        result.putExtra("nombre", nombre);
        setResult(Activity.RESULT_OK,result);

        // Cierra la actividad
        finish();
    }
}