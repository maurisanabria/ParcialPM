package com.example.parcialpm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListaRegistrosActivity extends AppCompatActivity {

    ListView listRegistros;
    DBHelper dbHelper;
    ArrayAdapter<String> adapter;
    ArrayList<String> listaRegistros; // [ ] texto / [✓] texto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_registros);

        listRegistros = findViewById(R.id.listRegistros);
        dbHelper = new DBHelper(this);

        cargarLista();

        // Al tocar una tarea: menú con opciones
        listRegistros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemSeleccionado = listaRegistros.get(position);
                boolean estaCompletado = itemSeleccionado.startsWith("[✓]");
                String textoOriginal = itemSeleccionado.substring(4); // saltea "[ ] " o "[✓] "
                mostrarMenuAcciones(textoOriginal, estaCompletado);
            }
        });
    }

    private void cargarLista() {
        List<String> registrosDB = dbHelper.obtenerTodosLosRegistros();
        listaRegistros = new ArrayList<>(registrosDB);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaRegistros
        );

        listRegistros.setAdapter(adapter);
    }

    private void mostrarMenuAcciones(String textoOriginal, boolean estaCompletado) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acción sobre la tarea");

        String opcionEstado = estaCompletado ? "Marcar como pendiente" : "Marcar como completada";
        String[] opciones = new String[] {
                opcionEstado,
                "Editar texto",
                "Eliminar",
                "Cancelar"
        };

        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0: // cambiar estado
                    int nuevoEstado = estaCompletado ? 0 : 1;
                    boolean okEstado = dbHelper.actualizarEstadoPorTexto(textoOriginal, nuevoEstado);
                    if (okEstado) {
                        Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                        cargarLista();
                    } else {
                        Toast.makeText(this, "No se pudo actualizar el estado", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 1: // editar texto
                    mostrarDialogoEdicion(textoOriginal);
                    break;

                case 2: // eliminar
                    boolean borrado = dbHelper.borrarRegistroPorTexto(textoOriginal);
                    if (borrado) {
                        Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                        cargarLista();
                    } else {
                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 3: // cancelar
                    dialog.dismiss();
                    break;
            }
        });

        builder.show();
    }

    private void mostrarDialogoEdicion(String textoOriginal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar tarea");

        final EditText input = new EditText(this);
        input.setText(textoOriginal);
        input.setSelection(input.getText().length());

        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoTexto = input.getText().toString().trim();

            if (nuevoTexto.isEmpty()) {
                Toast.makeText(this, "El texto no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = dbHelper.actualizarTextoPorTexto(textoOriginal, nuevoTexto);
            if (ok) {
                Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                cargarLista();
            } else {
                Toast.makeText(this, "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
