
package com.example.trabalhofinalprog;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trabalhofinalprog.model.Disciplina;
import com.example.trabalhofinalprog.model.Solicitacao;
import com.example.trabalhofinalprog.controller.SolicitacaoController;
import com.example.trabalhofinalprog.util.Sessao;
import com.example.trabalhofinalprog.model.Usuario;
import com.example.trabalhofinalprog.controller.UsuarioController;
import com.example.trabalhofinalprog.controller.DisciplinaController;
import com.example.trabalhofinalprog.model.Disponibilidade;
import com.example.trabalhofinalprog.controller.DisponibilidadeController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SolicitacaoAtendimento extends AppCompatActivity {

    private SolicitacaoController solicitacaoController;
    private UsuarioController usuarioController;
    private DisciplinaController disciplinaController;
    private DisponibilidadeController disponibilidadeController;

    private Spinner spProfessor, spDisciplina, spAtendimento, spHorario;
    private EditText etData, etDescricao;
    private RadioGroup rg;

    private ArrayAdapter<Disciplina> adapterDisciplinas;
    private ArrayAdapter<Disponibilidade> adapterDisponibilidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_atendimento);

        spProfessor = findViewById(R.id.spProfessor);
        spDisciplina = findViewById(R.id.spDisciplina);
        spAtendimento = findViewById(R.id.spTipoAtendimento);
        spHorario = findViewById(R.id.spHorarioDisponivel);

        etData = findViewById(R.id.etDataDesejada);
        etDescricao = findViewById(R.id.etDescricaoSolicitacao);

        rg = findViewById(R.id.rgModalidade);

        solicitacaoController = new SolicitacaoController(this);
        usuarioController = new UsuarioController(this);
        disciplinaController = new DisciplinaController(this);
        disponibilidadeController = new DisponibilidadeController(this);

        carregarProfessores();
        carregarTiposAtendimento();

        // Seleção de data
        etData.setOnClickListener(v -> abrirCalendario());

        // Atualizar disciplinas e horários ao selecionar professor
        spProfessor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarDisciplinas();
                carregarHorarios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Botão de envio
        findViewById(R.id.btnEnviarSolicitacao).setOnClickListener(v -> agendar());
    }

    private void carregarProfessores() {

        ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                usuarioController.listarProfessores()
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spProfessor.setAdapter(adapter);
    }

    private void carregarTiposAtendimento() {

        String[] opcoes = {
                "Remoto",
                "Presencial"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spAtendimento.setAdapter(adapter);
    }

    private void carregarDisciplinas() {

        Usuario professor = (Usuario) spProfessor.getSelectedItem();

        if (professor == null) {
            return;
        }

        adapterDisciplinas = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                disciplinaController.listarPorProfessor(professor.getId())
        );

        adapterDisciplinas.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDisciplina.setAdapter(adapterDisciplinas);
    }

    private void abrirCalendario() {

        Calendar calendario = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, ano, mes, dia) -> {

                    String data = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dia,
                            mes + 1,
                            ano
                    );

                    etData.setText(data);

                    carregarHorarios();
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.getDatePicker().setMinDate(
                System.currentTimeMillis() - 1000
        );

        datePicker.show();
    }

    private int obterDiaSemana(String data) {

        try {
            SimpleDateFormat formato = new SimpleDateFormat(
                    "dd/MM/yyyy",
                    new Locale("pt", "BR")
            );

            formato.setLenient(false);

            Date dataConvertida = formato.parse(data);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataConvertida);

            return calendar.get(Calendar.DAY_OF_WEEK);

        } catch (ParseException e) {
            return -1;
        }
    }

    private void carregarHorarios() {

        String data = etData.getText().toString().trim();

        if (TextUtils.isEmpty(data)) {
            spHorario.setAdapter(null);
            return;
        }

        Usuario professor = (Usuario) spProfessor.getSelectedItem();

        if (professor == null) {
            return;
        }

        int diaDaSemana = obterDiaSemana(data);

        if (diaDaSemana == -1) {
            return;
        }

        adapterDisponibilidade = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                disponibilidadeController.listarPorProfessorEDia(
                        professor.getId(),
                        diaDaSemana
                )
        );

        adapterDisponibilidade.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spHorario.setAdapter(adapterDisponibilidade);
    }

    private void agendar() {

        // Validar modalidade
        int radioId = rg.getCheckedRadioButtonId();

        if (radioId == -1) {
            Toast.makeText(this, "Selecione uma modalidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioSelecionado = findViewById(radioId);

        String modalidade = radioId == R.id.rbPresencial
                ? Solicitacao.PRESENCIAL
                : Solicitacao.REMOTO;

        // Validar data
        String data = etData.getText().toString().trim();

        if (TextUtils.isEmpty(data)) {
            etData.setError("Selecione uma data!");
            return;
        }

        int diaSemana = obterDiaSemana(data);

        if (diaSemana == -1) {
            etData.setError("Data inválida!");
            return;
        }

        // Validar descrição
        String descricao = etDescricao.getText().toString().trim();

        if (TextUtils.isEmpty(descricao)) {
            etDescricao.setError("Descreva sua dúvida ou objetivo!");
            return;
        }

        // Recuperar objetos selecionados
        Usuario professor = (Usuario) spProfessor.getSelectedItem();
        Disciplina disciplina = (Disciplina) spDisciplina.getSelectedItem();
        Disponibilidade disponibilidade = (Disponibilidade) spHorario.getSelectedItem();

        if (professor == null || disciplina == null || disponibilidade == null) {
            Toast.makeText(this, "Selecione todas as opções!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spAtendimento.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione o tipo de atendimento!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoAtendimento = spAtendimento.getSelectedItem().toString();

        // Recuperar IDs
        long idAluno = Sessao.getUsuarioId(this);
        long idProfessor = professor.getId();
        long idDisciplina = disciplina.getId();

        // Recuperar horários
        String horaInicio = disponibilidade.getHoraInicio();
        String horaFim = disponibilidade.getHoraFim();

        // Converter data para yyyy-MM-dd
        String dataBanco;

        try {
            SimpleDateFormat entrada = new SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
            );

            entrada.setLenient(false);

            Date dataConvertida = entrada.parse(data);

            SimpleDateFormat saida = new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
            );

            dataBanco = saida.format(dataConvertida);

        } catch (ParseException e) {
            etData.setError("Data inválida!");
            return;
        }

        // Criar solicitação
        Solicitacao solicitacao = new Solicitacao(
                idAluno,
                idProfessor,
                idDisciplina,
                tipoAtendimento,
                dataBanco,
                horaInicio,
                horaFim,
                modalidade,
                descricao
        );

        // Salvar
        if (solicitacaoController.criar(solicitacao) < 0) {
            Toast.makeText(this, "Erro ao enviar solicitação!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(
                this,
                "Solicitação enviada!",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}
