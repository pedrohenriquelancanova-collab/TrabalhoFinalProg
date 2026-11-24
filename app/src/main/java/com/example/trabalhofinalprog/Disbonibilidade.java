package com.example.trabalhofinalprog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.trabalhofinalprog.controller.DisponibilidadeController;
import com.example.trabalhofinalprog.model.Disponibilidade;
import com.example.trabalhofinalprog.util.Sessao;

public class Disbonibilidade extends AppCompatActivity {
    private Spinner diaSemana;
    private EditText horaInicial, horaFinal;
    private Button btAdicionar;
    private ListView disponibilidades;
    private ArrayAdapter<Disponibilidade> adapterLista;

    private DisponibilidadeController disponibilidadeController;
    private Disponibilidade disponibilidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponibilidade);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.disbonibilidade), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        disponibilidadeController = new DisponibilidadeController(this);
        disponibilidade = new Disponibilidade();
        long id = Sessao.getUsuarioId(this);
        diaSemana = findViewById(R.id.spDiaSemana);
        horaFinal = findViewById(R.id.etHorarioFim);
        horaInicial = findViewById(R.id.etHorarioInicio);
        btAdicionar = findViewById(R.id.btnAdicionarDisponibilidade);
        disponibilidades = findViewById(R.id.lvDisponibilidades);

        String[] opcoes = {
                "Domingo",
                "Segunda-Feira",
                "Terça-Feira",
                "Quarta-Feira",
                "Quinta-Feira",
                "Sexta-Feira",
                "Sábado"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );

        adapterLista = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                disponibilidadeController.listarPorProfessor(id)
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        diaSemana.setAdapter(adapter);

        disponibilidades.setAdapter(adapterLista);

        btAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarDisponibilidade();
            }
        });
    }

    private void cadastrarDisponibilidade() {
        String inicio = normalizaHora(horaInicial.getText().toString());
        String fina = normalizaHora(horaFinal.getText().toString());

        if(TextUtils.isEmpty(inicio) || TextUtils.isEmpty(fina)){
            Toast.makeText(getApplicationContext(), "Preencha todos os campos!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        disponibilidadeController = new DisponibilidadeController(this);
        long id = Sessao.getUsuarioId(this);
        int opcao = diaSemana.getSelectedItemPosition() + 1;
        disponibilidade = new Disponibilidade(id, opcao, inicio, fina);
        long resultado = disponibilidadeController.inserir(disponibilidade);

        if (resultado == DisponibilidadeController.HORARIO_INVALIDO) {
            Toast.makeText(getApplicationContext(),
                    "Horário inválido! Use HH:mm (ex.: 08:00) e o fim depois do início.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultado == DisponibilidadeController.CONFLITO) {
            Toast.makeText(getApplicationContext(),
                    "Esse horário se sobrepõe a outro já cadastrado neste dia!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (resultado < 0) {
            Toast.makeText(getApplicationContext(), "Erro ao cadastrar disponibilidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Disponibilidade cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
        horaInicial.setText("");
        horaFinal.setText("");
        adapterLista.clear();
        adapterLista.addAll(disponibilidadeController.listarPorProfessor(id));
    }

    private String normalizaHora(String hora) {
        hora = hora.trim();
        return hora.matches("[0-9]:[0-5][0-9]") ? "0" + hora : hora;
    }

}