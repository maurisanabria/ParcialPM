package com.example.parcialpm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "parcial.db";
    private static final int DB_VERSION = 1;  // dejamos versión 1 para no complicar

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla usuarios
        db.execSQL("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT, " +
                "clave TEXT)");

        // Usuario por defecto para el login
        db.execSQL("INSERT INTO usuarios (usuario, clave) VALUES ('admin', '1234')");

        // Tabla registros tipo agenda: texto + estado completado
        db.execSQL("CREATE TABLE registros (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "texto TEXT, " +
                "completado INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si alguna vez subís de versión, esto borra y recrea todo
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS registros");
        onCreate(db);
    }

    // ---------- LOGIN ----------
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

    // ---------- REGISTROS / AGENDA ----------

    // Insertar registro (siempre arranca como pendiente)
    public boolean insertarRegistro(String texto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("texto", texto);
        valores.put("completado", 0); // 0 = pendiente

        long resultado = db.insert("registros", null, valores);
        db.close();
        return resultado != -1;
    }

    // Obtener último registro (solo el texto)
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

    // Contar registros
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

    // Borrar TODOS los registros
    public boolean borrarTodosLosRegistros() {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("registros", null, null);
        db.close();
        return filas > 0;
    }

    // Obtener todos los registros formateados como agenda: [ ] texto / [✓] texto
    public List<String> obtenerTodosLosRegistros() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT texto, completado FROM registros ORDER BY id DESC", null);

        List<String> lista = new ArrayList<>();

        while (cursor.moveToNext()) {
            String texto = cursor.getString(0);
            int completado = cursor.getInt(1);

            String prefijo = (completado == 1) ? "[✓] " : "[ ] ";
            lista.add(prefijo + texto);
        }

        cursor.close();
        db.close();
        return lista;
    }

    // Actualizar estado (0 = pendiente, 1 = completado)
    public boolean actualizarEstadoPorTexto(String texto, int nuevoEstado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("completado", nuevoEstado);

        int filas = db.update("registros", valores, "texto = ?", new String[]{texto});
        db.close();
        return filas > 0;
    }

    // Actualizar el texto de una tarea
    public boolean actualizarTextoPorTexto(String textoViejo, String textoNuevo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("texto", textoNuevo);

        int filas = db.update("registros", valores, "texto = ?", new String[]{textoViejo});
        db.close();
        return filas > 0;
    }

    // Borrar un solo registro por texto
    public boolean borrarRegistroPorTexto(String texto) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("registros", "texto = ?", new String[]{texto});
        db.close();
        return filas > 0;
    }
}
