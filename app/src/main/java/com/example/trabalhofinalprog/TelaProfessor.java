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

public class TelaProfessor extends AppCompatActivity {
    TextView texto;
    Button btGerenciar, btSolicitacoes, btDisponibilidade, btAgenda;

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