package com.example.parcialpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    EditText etTexto;
    Button btnGuardar, btnVerReg, btnBorrar, btnCerrarSesion;
    TextView tvUltimoRegistro, tvCantidad;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // IDs EXACTOS de tu XML
        etTexto = findViewById(R.id.etTexto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVerReg = findViewById(R.id.btnVerReg);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvUltimoRegistro = findViewById(R.id.tvUltimoRegistro);
        tvCantidad = findViewById(R.id.tvCantidad);

        dbHelper = new DBHelper(this);

        // GUARDAR
        btnGuardar.setOnClickListener(v -> {
            String texto = etTexto.getText().toString().trim();

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingrese una tarea para guardar", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean guardado = dbHelper.insertarRegistro(texto);

            if (guardado) {
                Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show();
                etTexto.setText("");
                actualizarInfo();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });

        // VER REGISTROS
        btnVerReg.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ListaRegistrosActivity.class));
        });

        // BORRAR TODOS
        btnBorrar.setOnClickListener(v -> {
            boolean hubo = dbHelper.borrarTodosLosRegistros();

            if (hubo) {
                Toast.makeText(this, "Registros eliminados", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No hay registros para borrar", Toast.LENGTH_SHORT).show();
            }

            actualizarInfo();
        });

        // CERRAR SESIÓN
        btnCerrarSesion.setOnClickListener(v -> {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        });

        actualizarInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarInfo();
    }

    private void actualizarInfo() {
        String ultimo = dbHelper.obtenerUltimoRegistro();
        int cantidad = dbHelper.contarRegistros();

        if (ultimo == null || ultimo.isEmpty()) {
            tvUltimoRegistro.setText("Última tarea registrada: (no hay registros)");
        } else {
            tvUltimoRegistro.setText("Última tarea registrada: " + ultimo);
        }

        tvCantidad.setText("Total de tareas registradas: " + cantidad);
    }
}
