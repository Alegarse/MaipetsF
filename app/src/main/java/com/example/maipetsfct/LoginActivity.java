package com.example.maipetsfct;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instanciamos los campos de login
        email = findViewById(R.id.userEmailLogin);
        password = findViewById(R.id.userPassLogin);

        login = findViewById(R.id.btnLogin);
        cancel = findViewById(R.id.btnCancel);
    }
}
