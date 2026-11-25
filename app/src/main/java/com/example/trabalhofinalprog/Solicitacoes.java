package com.example.trabalhofinalprog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.trabalhofinalprog.model.Usuario;
import java.util.List;
import java.util.ArrayList;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.trabalhofinalprog.controller.SolicitacaoController;
import com.example.trabalhofinalprog.model.Disponibilidade;
import com.example.trabalhofinalprog.model.Solicitacao;
import com.example.trabalhofinalprog.util.Sessao;


public class Solicitacoes extends AppCompatActivity {

    private SolicitacaoController solicitacaoController;
    private Spinner spFiltroSolicitacoes;
    private ListView lvListarSolicitacoes;
    private ArrayAdapter<Solicitacao> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacoes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.solicitacoes), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        solicitacaoController = new SolicitacaoController(this);
        spFiltroSolicitacoes = findViewById(R.id.spFiltroSolicitacoes);
        lvListarSolicitacoes = findViewById(R.id.lvSolicitacoes);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<Solicitacao>());
        lvListarSolicitacoes.setAdapter(adapter);

        lvListarSolicitacoes.setOnItemClickListener((parent, view, position, idItem) -> {

            Solicitacao solicitacaoSelecionada =
                    (Solicitacao) parent.getItemAtPosition(position);

            Intent intent = new Intent(Solicitacoes.this, ItemSolicitacao.class);

            intent.putExtra("idSolicitacao", solicitacaoSelecionada.getId());
            intent.putExtra("nomeSolicitacao", solicitacaoSelecionada.getNomeAluno());
            intent.putExtra("disciplinaSolicitacao", solicitacaoSelecionada.getNomeDisciplina());
            intent.putExtra("dataSolicitacao", solicitacaoSelecionada.getDataDesejada());
            intent.putExtra("statusSolicitacao", solicitacaoSelecionada.getStatus());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        long id = Sessao.getUsuarioId(this);
        // Aluno vê as solicitações que fez; professor vê as que recebeu.
        List<Solicitacao> lista = Usuario.ALUNO.equals(Sessao.getTipo(this))
                ? solicitacaoController.listarPorAluno(id, null)
                : solicitacaoController.listarPorProfessorSemFiltro(id);
        adapter.clear();
        adapter.addAll(lista);
    }
}
