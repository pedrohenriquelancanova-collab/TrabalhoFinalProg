package com.example.trabalhofinalprog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trabalhofinalprog.controller.UsuarioController;
import com.example.trabalhofinalprog.model.Usuario;
import com.example.trabalhofinalprog.util.Sessao;

public class LoginActivity extends AppCompatActivity {
    private Button btLogin, btCadastro;
    private EditText textoEmail, textoSenha;
    private UsuarioController usuarioController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usuarioController = new UsuarioController(this);
        btLogin = findViewById(R.id.btnEntrar);
        btCadastro = findViewById(R.id.btCadastro);
        textoEmail = findViewById(R.id.etEmail);
        textoSenha = findViewById(R.id.etSenha);

        btCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });
    }
    private void realizarLogin(){
        String email = textoEmail.getText().toString().trim();
        String senha = textoSenha.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = usuarioController.autenticar(email, senha);
        if (usuario == null) {
            Toast.makeText(getApplicationContext(), "E-mail ou senha inválidos!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Sessao.iniciar(this, usuario);
        Class<?> destino = usuario.isProfessor() ? TelaProfessor.class : TelaAluno.class;
        startActivity(new Intent(LoginActivity.this, destino));
        finish();
    }
}
