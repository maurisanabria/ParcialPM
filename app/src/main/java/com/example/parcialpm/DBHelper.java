package com.example.parcialpm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "parcial.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT, clave TEXT)");
        db.execSQL("INSERT INTO usuarios (usuario, clave) VALUES ('admin', '1234')");

        db.execSQL("CREATE TABLE registros (id INTEGER PRIMARY KEY AUTOINCREMENT, texto TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS registros");
        onCreate(db);
    }

    // INSERTAR REGISTRO
    public boolean insertarRegistro(String texto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("texto", texto);

        long resultado = db.insert("registros", null, valores);
        db.close();
        return resultado != -1;
    }

    // OBTENER ÚLTIMO REGISTRO
    public String obtenerUltimoRegistro() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT texto FROM registros ORDER BY id DESC LIMIT 1", null);

        String texto = "";
        if (cursor.moveToFirst()) {
            texto = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return texto;
    }

    // CONTAR REGISTROS
    public int contarRegistros() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM registros", null);

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    // BORRAR TODOS LOS REGISTROS
    public boolean borrarTodosLosRegistros() {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("registros", null, null);
        db.close();
        return filas > 0;
    }

    // VALIDAR LOGIN
    public boolean validarUsuario(String usuario, String clave) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE usuario = ? AND clave = ?",
                new String[]{usuario, clave}
        );

        boolean valido = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return valido;
    }
}
