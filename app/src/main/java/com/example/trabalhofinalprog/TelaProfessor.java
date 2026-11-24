package com.example.trabalhofinalprog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.trabalhofinalprog.controller.AtendimentoController;
import com.example.trabalhofinalprog.controller.SolicitacaoController;
import com.example.trabalhofinalprog.model.Usuario;
import com.example.trabalhofinalprog.util.Sessao;

public class TelaProfessor extends AppCompatActivity {
    TextView texto;
    TextView tvQuantPendente;
    TextView tvQuantRealizados;
    TextView tvQuantCancelados;
    TextView tvQuantHoje;
    Button btGerenciar, btSolicitacoes, btDisponibilidade, btAgenda, btAdicionarDisciplina;

    private AtendimentoController atendimentoController;
    private SolicitacaoController solicitacaoController;
    private Usuario usuario;




    // Atualiza os contadores toda vez que a tela volta ao primeiro plano.
    @Override
    protected void onResume() {
        super.onResume();
        long id = Sessao.getUsuarioId(this);
        tvQuantHoje.setText(String.valueOf(atendimentoController.contarHoje(id)));
        // "Solicitações" = pedidos de alunos ainda pendentes de resposta
        tvQuantPendente.setText(String.valueOf(solicitacaoController.contarPendentes(id)));
        tvQuantRealizados.setText(String.valueOf(atendimentoController.contarPorStatus(id, "REALIZADO")));
        tvQuantCancelados.setText(String.valueOf(atendimentoController.contarPorStatus(id, "CANCELADO")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_professor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.TelaProfessor), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        texto = findViewById(R.id.tvSaudacaoProfessor);
        btGerenciar = findViewById(R.id.btnMenuAlunos);
        btDisponibilidade = findViewById(R.id.btnMenuDisponibilidade);
        btSolicitacoes = findViewById(R.id.btnMenuSolicitacoes);
        btAgenda = findViewById(R.id.btnMenuAtendimentos);
        btAdicionarDisciplina = findViewById(R.id.btnAdicionarDisciplinas);
        tvQuantCancelados = findViewById(R.id.tvQtdCancelados);
        tvQuantHoje = findViewById(R.id.tvQtdHoje);
        tvQuantPendente = findViewById(R.id.tvQtdPendentes);
        tvQuantRealizados = findViewById(R.id.tvQtdRealizados);
        atendimentoController = new AtendimentoController(this);
        solicitacaoController = new SolicitacaoController(this);

        long id = Sessao.getUsuarioId(this);
        String nome = Sessao.getNome(this);

        texto.setText(nome);

        btAdicionarDisciplina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaProfessor.this, Disciplinas.class);
                startActivity(intent);
            }
        });

        btGerenciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaProfessor.this, ListaAlunos.class);
                startActivity(intent);
            }
        });

        btDisponibilidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaProfessor.this, Disbonibilidade.class);
                startActivity(intent);
            }
        });

        btSolicitacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaProfessor.this, Solicitacoes.class);
                startActivity(intent);
            }
        });

        btAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaProfessor.this, MeusAtendimentos.class);
                startActivity(intent);
            }
        });
    }
}