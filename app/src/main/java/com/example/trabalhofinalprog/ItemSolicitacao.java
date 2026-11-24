package com.example.trabalhofinalprog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.trabalhofinalprog.model.Solicitacao;
import com.example.trabalhofinalprog.model.Usuario;
import com.example.trabalhofinalprog.controller.SolicitacaoController;
import com.example.trabalhofinalprog.util.Sessao;

public class ItemSolicitacao extends AppCompatActivity {

    private SolicitacaoController solicitacaoController;
    private TextView tvItemSolicitante, tvItemResumoSolicitacao, tvItemStatusSolicitacao;
    private Button btAprovar, btReprovar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_solicitacao);
        String nome = getIntent().getStringExtra("nomeSolicitacao");
        String disciplina = getIntent().getStringExtra("disciplinaSolicitacao");
        String data = getIntent().getStringExtra("dataSolicitacao");
        String status = getIntent().getStringExtra("statusSolicitacao");
        long idSolicitacao = getIntent().getLongExtra("idSolicitacao", -1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.itemSolicitacao), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        solicitacaoController = new SolicitacaoController(this);
        tvItemResumoSolicitacao = findViewById(R.id.tvItemResumoSolicitacao);
        tvItemSolicitante = findViewById(R.id.tvItemSolicitante);
        tvItemStatusSolicitacao = findViewById(R.id.tvItemStatusSolicitacao);
        btAprovar = findViewById(R.id.btAprovarSolicitacao);
        btReprovar = findViewById(R.id.btRecusarSolicitacao);

        tvItemSolicitante.setText("Nome: " + nome);
        tvItemResumoSolicitacao.setText("Disciplina: " + disciplina + ", " + "Data: " + data);
        tvItemStatusSolicitacao.setText("Status: " + status);

        // Só o professor decide, e só enquanto a solicitação está pendente.
        boolean professor = Usuario.PROFESSOR.equals(Sessao.getTipo(this));
        if (!professor || !Solicitacao.PENDENTE.equals(status)) {
            btAprovar.setVisibility(View.GONE);
            btReprovar.setVisibility(View.GONE);
        }

        btAprovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long resultado = solicitacaoController.aprovar(idSolicitacao);
                if (resultado > 0) {
                    Toast.makeText(getApplicationContext(),
                            "Solicitação aprovada! Pedidos em conflito foram cancelados.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else if (resultado == SolicitacaoController.HORARIO_OCUPADO) {
                    Toast.makeText(getApplicationContext(),
                            "Você já tem um atendimento nesse horário!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Esta solicitação já foi tratada!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btReprovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (solicitacaoController.recusar(idSolicitacao)) {
                    Toast.makeText(getApplicationContext(), "Solicitação recusada!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Esta solicitação já foi tratada!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}