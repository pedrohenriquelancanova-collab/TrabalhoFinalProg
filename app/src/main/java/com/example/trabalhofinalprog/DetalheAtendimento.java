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
import com.example.trabalhofinalprog.model.Atendimento;
import com.example.trabalhofinalprog.controller.AtendimentoController;
import com.example.trabalhofinalprog.util.Sessao;

public class DetalheAtendimento extends AppCompatActivity {

    private AtendimentoController atendimentoController;
    private Atendimento atendimento;
    private TextView tvDetalheProfessor, tvDetalheDisciplina, tvDetalheData, tvDetalheHorario, tvDetalheModalidade, tvDetalheStatus, tvDetalheDescricao;
    private Button btAlterarAtendimento, btCancelarAtendimento, btRegistrarAtendimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_atendimento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.DetalheAtendimento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        atendimento = new Atendimento();
        atendimentoController = new AtendimentoController(this);
        tvDetalheProfessor = findViewById(R.id.tvDetalheProfessor);
        tvDetalheDisciplina = findViewById(R.id.tvDetalheDisciplina);
        tvDetalheData = findViewById(R.id.tvDetalheData);
        tvDetalheHorario = findViewById(R.id.tvDetalheHorario);
        tvDetalheModalidade = findViewById(R.id.tvDetalheModalidade);
        tvDetalheStatus = findViewById(R.id.tvDetalheStatus);
        tvDetalheDescricao = findViewById(R.id.tvDetalheDescricao);
        btAlterarAtendimento = findViewById(R.id.btnAlterarAtendimento);
        btCancelarAtendimento = findViewById(R.id.btnCancelarAtendimento);
        btRegistrarAtendimento = findViewById(R.id.btnRegistrarResultado);
        long idAtendimento = getIntent().getLongExtra("idAtendimento", -1);
        atendimento = atendimentoController.buscarPorId(idAtendimento);
        carregarInformacoes();

        btAlterarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarAtendimento();
            }
        });

        btRegistrarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalheAtendimento.this, RegistroAtendimento.class);
                intent.putExtra("idAtendimento", atendimento.getId());
                startActivity(intent);
            }
        });

    }

    private void alterarAtendimento(){

    }

    private void carregarInformacoes(){

        tvDetalheProfessor.setText(atendimento.getNomeProfessor());
        tvDetalheDisciplina.setText(atendimento.getNomeDisciplina());
        tvDetalheData.setText(atendimento.getData());
        tvDetalheHorario.setText(atendimento.getHoraInicio() + " às " + atendimento.getHoraFim());
        tvDetalheModalidade.setText(atendimento.getModalidade());
        tvDetalheStatus.setText(atendimento.getStatus());
        tvDetalheDescricao.setText(atendimento.getDescricao());
    }
}