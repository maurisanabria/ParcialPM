package com.example.parcialpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(v -> {

            String usuario = etUser.getText().toString().trim();
            String clave = etPass.getText().toString().trim();

            if (usuario.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean valido = dbHelper.validarUsuario(usuario, clave);

            if (valido) {
                Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Usuario o clave incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
