package com.example.trabalhofinalprog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
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

import com.example.trabalhofinalprog.model.Disciplina;
import com.example.trabalhofinalprog.util.Sessao;
import com.example.trabalhofinalprog.model.Atendimento;
import com.example.trabalhofinalprog.controller.AtendimentoController;


public class MeusAtendimentos extends AppCompatActivity {

    private AtendimentoController atendimentoController;
    private Atendimento atendimento;
    private Spinner filtro;
    private ListView listar;
    private ArrayAdapter<Atendimento> adapterListar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_atendimentos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MeusAtendimentos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        atendimentoController = new AtendimentoController(this);

        filtro = findViewById(R.id.spFiltroAtendimentos);
        listar = findViewById(R.id.lvAtendimentos);

        adapterListar = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<Atendimento>());
        listar.setAdapter(adapterListar);

        listar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Atendimento atendimentoSelecionado = adapterListar.getItem(position);

                Intent intent = new Intent(MeusAtendimentos.this, DetalheAtendimento.class);

                intent.putExtra("idAtendimento", atendimentoSelecionado.getId());

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        long id = Sessao.getUsuarioId(this);
        List<Atendimento> lista = Usuario.ALUNO.equals(Sessao.getTipo(this))
                ? atendimentoController.listarPorAluno(id, null)
                : atendimentoController.listarPorProfessor(id, null);
        adapterListar.clear();
        adapterListar.addAll(lista);
    }
}
