package com.example.trabalhofinalprog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastroActivity extends AppCompatActivity {
    private EditText nome, email, matricula, senha, confirmaSenha;
    private RadioButton opAluno, opProfessor;
    private Button cadastroUsuario;
    private RadioGroup rg;

    private String nomePreenchido, emailPreenchido, senhaPreenchido, confimaSenhaPreenchido, matr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nome = findViewById(R.id.etNome);
        email = findViewById(R.id.etEmailCadastro);
        matricula = findViewById(R.id.etMatricula);
        senha = findViewById(R.id.etSenhaCadastro);
        confirmaSenha = findViewById(R.id.etConfirmarSenha);
        cadastroUsuario = findViewById(R.id.btnCadastrarUsuario);
        opAluno = findViewById(R.id.rbAluno);
        opProfessor = findViewById(R.id.rbProfessor);
        rg = findViewById(R.id.rgTipoUsuario);

        nomePreenchido = nome.getText().toString();
        emailPreenchido = email.getText().toString();
        senhaPreenchido = senha.getText().toString();
        confimaSenhaPreenchido = confirmaSenha.getText().toString();
        matr = matricula.getText().toString();

        cadastroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrar();
            }
        });
    }

    private void cadastrar() {
        nomePreenchido = nome.getText().toString().trim();
        emailPreenchido = email.getText().toString().trim();
        senhaPreenchido = senha.getText().toString();
        confimaSenhaPreenchido = confirmaSenha.getText().toString();
        matr = matricula.getText().toString().trim();

        int idSelecionado = rg.getCheckedRadioButtonId();
        RadioButton radioButtonSelecionado = findViewById(idSelecionado);
        String textoSelecionado = radioButtonSelecionado.getText().toString();

        if (!verificaPreencimento()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!!!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (textoSelecionado.equals("Aluno") && TextUtils.isEmpty(matr) && validaMatricula(matr)) {
            Toast.makeText(getApplicationContext(), "Preencha a matricula corretamente!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        if (!validaEmail(emailPreenchido)) {
            Toast.makeText(getApplicationContext(), "Preencha o email corretamente!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validaSenha(senhaPreenchido, confimaSenhaPreenchido)){
            Toast.makeText(getApplicationContext(), "As senhas não são iguais!!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(getApplicationContext(), "Usuário cadastrado!!!", Toast.LENGTH_SHORT).show();
    }

    private boolean verificaPreencimento() {
        int idSelecionado = rg.getCheckedRadioButtonId();

        return idSelecionado != -1 && !TextUtils.isEmpty(nomePreenchido) && !TextUtils.isEmpty(emailPreenchido)
                && !TextUtils.isEmpty(senhaPreenchido) &&
                !TextUtils.isEmpty(confimaSenhaPreenchido);
    }

    private boolean validaEmail(String email) {
        String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validaMatricula(String matricula) {
        String MATRICULA_REGEX = "^[0-9]{6,}$";
        Pattern pattern = Pattern.compile(MATRICULA_REGEX);
        Matcher matcher = pattern.matcher(matricula);
        return matcher.matches();
    }

    private boolean validaSenha(String senha1, String senha2){
        return senha1.equals(senha2);
    }
}
