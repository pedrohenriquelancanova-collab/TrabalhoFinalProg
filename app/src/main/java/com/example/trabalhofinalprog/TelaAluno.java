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

public class TelaAluno extends AppCompatActivity {

    TextView nome, atendimentos;

    Button solicitar, historico, minhasSolictacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.TelaAluno), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nome = findViewById(R.id.tvSaudacaoAluno);
        atendimentos = findViewById(R.id.tvEstadoProximos);
        solicitar = findViewById(R.id.btnSolicitarAtendimento);
        historico = findViewById(R.id.btnMeuHistorico);
        minhasSolictacoes = findViewById(R.id.btnMinhasSolicitacoes);

        solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaAluno.this, SolicitacaoAtendimento.class);
                startActivity(intent);
            }
        });

        minhasSolictacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaAluno.this, MeusAtendimentos.class);
                startActivity(intent);
            }
        });

        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaAluno.this, Solicitacoes.class);
                startActivity(intent);
            }
        });
    }
}