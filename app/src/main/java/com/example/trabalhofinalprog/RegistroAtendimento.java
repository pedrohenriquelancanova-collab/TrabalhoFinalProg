package com.example.trabalhofinalprog;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.trabalhofinalprog.model.Atendimento;
import com.example.trabalhofinalprog.controller.AtendimentoController;
import com.example.trabalhofinalprog.model.Solicitacao;

import java.security.SecurityPermission;
import java.util.ArrayList;

public class RegistroAtendimento extends AppCompatActivity {

    private Spinner spSituacaoAtendimento;
    private EditText etConteudoTratado, etObservacoesAtendimento;
    private ListView lvListaAlunos;
    private Button btSalvar;
    private Atendimento atendimento;
    private AtendimentoController atendimentoController;
    private ArrayAdapter<Atendimento> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_atendimento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RegistroAtendimento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        atendimentoController = new AtendimentoController(this);
        spSituacaoAtendimento = findViewById(R.id.spSituacaoAtendimento);
        etConteudoTratado = findViewById(R.id.etConteudoTratado);
        etObservacoesAtendimento = findViewById(R.id.etObservacoesAtendimento);
        lvListaAlunos = findViewById(R.id.lvPresencaAlunos);
        btSalvar = findViewById(R.id.btnSalvarRegistro);
        long idSelecionado = getIntent().getLongExtra("idAtendimento", -1);
        atendimento = atendimentoController.buscarPorId(idSelecionado);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<Atendimento>());
        lvListaAlunos.setAdapter(adapter);
        carregarDados();

    }
    private void carregarDados(){
        etConteudoTratado.setText(atendimento.getConteudoTratado());
        etObservacoesAtendimento.setText(atendimento.getObservacoes());
    }
}