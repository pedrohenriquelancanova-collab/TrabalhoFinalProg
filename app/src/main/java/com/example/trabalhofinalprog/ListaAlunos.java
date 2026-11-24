package com.example.trabalhofinalprog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.trabalhofinalprog.controller.UsuarioController;
import com.example.trabalhofinalprog.model.Disciplina;
import com.example.trabalhofinalprog.util.Sessao;
import com.example.trabalhofinalprog.model.Usuario;

public class ListaAlunos extends AppCompatActivity {

    private Usuario usuario;
    private UsuarioController usuarioController;
    private Button cadastrarAluno;
    private ListView lvAlunos;
    private ArrayAdapter<Usuario> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_alunos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ListaAlunos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usuario = new Usuario();
        usuarioController = new UsuarioController(this);
        cadastrarAluno = findViewById(R.id.btnNovoAluno);
        lvAlunos = findViewById(R.id.lvAlunos);
        long id = Sessao.getUsuarioId(this);

        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                usuarioController.listarAlunosSemFiltro()
        );

        lvAlunos.setAdapter(adapter);

        cadastrarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaAlunos.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.addAll(usuarioController.listarAlunosSemFiltro());
    }

}