package com.example.trabalhofinalprog;

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
import com.example.trabalhofinalprog.controller.DisciplinaController;
import com.example.trabalhofinalprog.util.Sessao;
import  com.example.trabalhofinalprog.model.Disciplina;

public class Disciplinas extends AppCompatActivity {

    private DisciplinaController disciplinaController;
    private Disciplina disciplina;
    private EditText nomeDisciplina;
    private Button btSalvar;
    private ListView listaDisciplinas;
    private ArrayAdapter<Disciplina> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplinas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.desciplinas), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nomeDisciplina = findViewById(R.id.etNomeDisciplina);
        btSalvar = findViewById(R.id.btnSalvarDisciplina);
        listaDisciplinas = findViewById(R.id.lvDisciplinas);
        disciplinaController = new DisciplinaController(this);
        long id = Sessao.getUsuarioId(this);

        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                disciplinaController.listarPorProfessor(id)
        );

        listaDisciplinas.setAdapter(adapter);


        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDisciplina();
            }
        });

    }
    private void salvarDisciplina(){
        String nome = nomeDisciplina.getText().toString();
        long id = Sessao.getUsuarioId(this);
        if(TextUtils.isEmpty(nome)){
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        disciplina = new Disciplina(nome, id);
        if (disciplinaController.inserir(disciplina) < 0) {
            Toast.makeText(getApplicationContext(), "Disciplina inválida ou já cadastrada!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Disciplina cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
        nomeDisciplina.setText("");
        adapter.clear();
        adapter.addAll(disciplinaController.listarPorProfessor(id));
    }

}