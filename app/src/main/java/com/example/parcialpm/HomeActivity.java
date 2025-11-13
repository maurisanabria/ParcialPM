package com.example.parcialpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    EditText etTexto;
    Button btnGuardar, btnBorrar, btnCerrarSesion;
    TextView tvUltimo, tvCantidad;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etTexto = findViewById(R.id.etTexto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvUltimo = findViewById(R.id.tvUltimoRegistro);
        tvCantidad = findViewById(R.id.tvCantidad);

        dbHelper = new DBHelper(this);

        // Cargar último registro y cantidad al entrar
        actualizarUltimoYCantidad();

        // Guardar registro
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String texto = etTexto.getText().toString().trim();

                if (texto.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Ingrese un texto", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean guardado = dbHelper.insertarRegistro(texto);

                if (guardado) {
                    Toast.makeText(HomeActivity.this, "Registro guardado", Toast.LENGTH_SHORT).show();
                    etTexto.setText("");
                    actualizarUltimoYCantidad();
                } else {
                    Toast.makeText(HomeActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Borrar todos los registros
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean borrado = dbHelper.borrarTodosLosRegistros();

                if (borrado) {
                    Toast.makeText(HomeActivity.this, "Registros borrados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "No había registros para borrar", Toast.LENGTH_SHORT).show();
                }

                actualizarUltimoYCantidad();
            }
        });

        // Cerrar sesión (volver al login)
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
                finish(); // cierra la pantalla actual
            }
        });
    }

    private void actualizarUltimoYCantidad() {
        String ultimo = dbHelper.obtenerUltimoRegistro();
        int cantidad = dbHelper.contarRegistros();

        if (!ultimo.isEmpty()) {
            tvUltimo.setText("Último registro: " + ultimo);
        } else {
            tvUltimo.setText("Último registro: (no hay registros)");
        }

        tvCantidad.setText("Total de registros: " + cantidad);
    }
}

