package com.example.yosoy.pagame.Item;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Judit Jiménez Jiménez on 09/11/2016.
 */

public class BillItem {
    private static final String TAG = "CUENTA";
    /* Variable que indica el nombre de la persona a la que debes/te deben */
    public final static String NOMBRE = "nombre";
    /* Variable que indica una breve descripcion de la cuenta */
    public final static String DESCRIPCION = "descripcion";
    /* Variable que indica si debes o te deben dinero */
    public final static String DEBE = "debe";
    /* Variable que indica el telefono de la otra persona */
    public final static String TELEFONO = "telefono";
    /* Variable que indica el dinero de la cuenta */
    public final static String MONEY = "money";
    /* Fecha en la que se creó la deuda */
    public final static String DATE = "date";


    private String mNombre = new String();
    private String mDescripcion = new String();
    private String mDebe = new String();
    private List<String> mTelefono;
    private String mMoney = new String();
    private String mDate = new String();

    /**
     * Constructor de la clase
     */
    public BillItem() {
        Calendar c = Calendar.getInstance();
        this.mNombre = "";
        this.mDebe = "";
        this.mDescripcion = "";
        this.mTelefono = new ArrayList<String>();
        this.mMoney = "";
        this.mDate = "";
    }

    /**
     * Constructor parametrizado de la clase
     * @param nombre
     *           nombre de la persona implicada en la deuda
     * @param desc
     *          descripcion de la deuda
     * @param telefono
     *          numero de telefono de la persona implicada en la deuda
     * @param money
     *          dinero que se debe
     * @param debe
     *          opcion se debe o le deben
     * @param date
     *          fecha en la que ha sido creada
     */
    public BillItem(String nombre, String desc, List<String> telefono, String money, String debe, String date) {
        this.mNombre = nombre;
        this.mDebe = debe;
        this.mDescripcion = desc;
        this.mTelefono = telefono;
        this.mMoney = money;
        this.mDate = date;
    }

    /**
     * Constructor intent de la clase
     * @param intent
     */
    public BillItem(Intent intent) {
        log("--------------------------------ESTOY EN CUENTA");
        mNombre = intent.getStringExtra(BillItem.NOMBRE);
        mDebe = intent.getStringExtra(BillItem.DEBE);
        mDescripcion = intent.getStringExtra(BillItem.DESCRIPCION);
        mMoney = intent.getStringExtra(BillItem.MONEY);
        mTelefono = intent.getStringArrayListExtra(BillItem.TELEFONO);
        mDate = intent.getStringExtra(BillItem.DATE);


    }

    //Getters y Setters de los diferentes atributos de la clase

    public List<String> getmTelefono() {
        return mTelefono;
    }

    public void setmTelefono(List<String> mTelefono) {
        this.mTelefono = mTelefono;
    }

    public String getDescripcion() {
        return mDescripcion;
    }

    public void setDescripcion(String descripcion) {
        mDescripcion = descripcion;
    }

    public String getDebe() {
        return mDebe;
    }

    public void setDebe(String debe) {
        mDebe = debe;
    }

    public String getMoney() {
        return mMoney;
    }

    public void setMoney(String money) {
        mMoney = money;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String nombre) {
        mNombre = nombre;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }


    /**
     * Método toString para mostrar todos los datos de la cuenta
     * @return
     */
    public String toString() {
        return "Nombre " + mNombre + "Descripcion " + mDescripcion + " Telefono " + mTelefono + " Dinero: " + mMoney + " " + mDebe + "Date:"
                + mDate;
    }

    // Take a set of String data values and
    // package them for transport in an Intent

    public static void packageIntent(Intent intent, String nombre, String descripcion, String telefono, String money, String debe, String date) {
        Calendar c = Calendar.getInstance();
        intent.putExtra(BillItem.NOMBRE, nombre);
        intent.putExtra(BillItem.DESCRIPCION, descripcion);
        intent.putExtra(BillItem.DEBE, debe);
        intent.putExtra(BillItem.TELEFONO, telefono);
        intent.putExtra(BillItem.MONEY, money);
        intent.putExtra(BillItem.DATE, date);

    }

    private void log(String msg) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, msg);
    }
}
