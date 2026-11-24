package com.example.trabalhofinalprog.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.trabalhofinalprog.database.ConectaDB;
import com.example.trabalhofinalprog.model.Atendimento;
import com.example.trabalhofinalprog.model.Solicitacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Solicitações de atendimento: o aluno cria, o professor aprova ou recusa. */
public class SolicitacaoController {

    /** Códigos negativos retornados por aprovar(). Valores > 0 são o id do atendimento criado. */
    public static final long JA_TRATADA = -1;
    public static final long HORARIO_OCUPADO = -2;

    private static final String SELECT =
            "SELECT s.id_solicitacao, s.id_aluno, s.id_professor, s.id_disciplina, s.tipo_atendimento, " +
                    "s.data_desejada, s.hora_inicio, s.hora_fim, s.modalidade, s.descricao, s.status, s.criado_em, " +
                    "a.nome, p.nome, d.nome " +
                    "FROM solicitacao s " +
                    "JOIN usuario a ON a.id_usuario = s.id_aluno " +
                    "JOIN usuario p ON p.id_usuario = s.id_professor " +
                    "LEFT JOIN disciplina d ON d.id_disciplina = s.id_disciplina ";

    private final ConectaDB banco;

    public SolicitacaoController(Context contexto) {
        this.banco = ConectaDB.getInstance(contexto);
    }

    /** Cria a solicitação com status PENDENTE. Retorna o id ou -1 em caso de erro. */
    public long criar(Solicitacao s) {
        ContentValues v = new ContentValues();
        v.put("id_aluno", s.getIdAluno());
        v.put("id_professor", s.getIdProfessor());
        if (s.getIdDisciplina() > 0) v.put("id_disciplina", s.getIdDisciplina()); else v.putNull("id_disciplina");
        v.put("tipo_atendimento", s.getTipoAtendimento());
        v.put("data_desejada", s.getDataDesejada());
        v.put("hora_inicio", s.getHoraInicio());
        v.put("hora_fim", s.getHoraFim());
        v.put("modalidade", s.getModalidade());
        v.put("descricao", s.getDescricao());
        v.put("status", Solicitacao.PENDENTE);
        v.put("criado_em", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
        return banco.getWritableDatabase().insert("solicitacao", null, v);
    }

    public Solicitacao buscarPorId(long id) {
        List<Solicitacao> l = consultar(SELECT + "WHERE s.id_solicitacao = ?", new String[]{String.valueOf(id)});
        return l.isEmpty() ? null : l.get(0);
    }

    /** statusFiltro nulo = todas. Mais recentes primeiro. */
    public List<Solicitacao> listarPorAluno(long idAluno, String statusFiltro) {
        return listar("s.id_aluno = ?", idAluno, statusFiltro);
    }

    public List<Solicitacao> listarPorProfessor(long idProfessor, String statusFiltro) {
        return listar("s.id_professor = ?", idProfessor, statusFiltro);
    }

    public List<Solicitacao> listarPorProfessorSemFiltro(long idProfessor) {
        return listar("s.id_professor = ?", idProfessor, null);
    }



    public int contarPendentes(long idProfessor) {
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM solicitacao WHERE id_professor = ? AND status = 'PENDENTE'",
                new String[]{String.valueOf(idProfessor)})) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    /**
     * Aprova a solicitação e, na mesma transação, cria o atendimento AGENDADO com a presença do aluno.
     * Retorna o id do atendimento criado, JA_TRATADA se a solicitação não existe / já foi tratada,
     * ou HORARIO_OCUPADO se o professor já tem atendimento nesse horário.
     * As demais solicitações pendentes em conflito com esta são canceladas automaticamente.
     */
    public long aprovar(long idSolicitacao) {
        SQLiteDatabase db = banco.getWritableDatabase();
        db.beginTransaction();
        try {
            Solicitacao s = buscarPorId(idSolicitacao);
            if (s == null || !Solicitacao.PENDENTE.equals(s.getStatus())) return JA_TRATADA;

            // Fim do intervalo (se não houver hora_fim, usa o início).
            String fim = s.getHoraFim() != null ? s.getHoraFim() : s.getHoraInicio();

            // Já existe atendimento do professor nesse horário (aprovado antes)? Não duplica.
            try (Cursor c = db.rawQuery(
                    "SELECT 1 FROM atendimento WHERE id_professor = ? AND data = ? AND status <> 'CANCELADO' " +
                            "AND hora_inicio < ? AND COALESCE(hora_fim, hora_inicio) > ?",
                    new String[]{String.valueOf(s.getIdProfessor()), s.getDataDesejada(), fim, s.getHoraInicio()})) {
                if (c.moveToFirst()) return HORARIO_OCUPADO;
            }

            ContentValues st = new ContentValues();
            st.put("status", Solicitacao.APROVADA);
            db.update("solicitacao", st, "id_solicitacao = ?", new String[]{String.valueOf(idSolicitacao)});

            ContentValues a = new ContentValues();
            a.put("id_solicitacao", s.getId());
            a.put("id_professor", s.getIdProfessor());
            if (s.getIdDisciplina() > 0) a.put("id_disciplina", s.getIdDisciplina());
            a.put("tipo_atendimento", s.getTipoAtendimento());
            a.put("data", s.getDataDesejada());
            a.put("hora_inicio", s.getHoraInicio());
            a.put("hora_fim", s.getHoraFim());
            a.put("modalidade", s.getModalidade());
            a.put("descricao", s.getDescricao());
            a.put("status", Atendimento.AGENDADO);
            long idAtendimento = db.insertOrThrow("atendimento", null, a);

            ContentValues p = new ContentValues();
            p.put("id_atendimento", idAtendimento);
            p.put("id_aluno", s.getIdAluno());
            p.put("presente", 0);
            db.insertOrThrow("presenca", null, p);

            // Cancela automaticamente as outras solicitações PENDENTES do mesmo professor
            // cujo horário se sobrepõe ao que acabou de ser aprovado.
            ContentValues cancelada = new ContentValues();
            cancelada.put("status", Solicitacao.CANCELADA);
            db.update("solicitacao", cancelada,
                    "id_professor = ? AND data_desejada = ? AND status = 'PENDENTE' AND id_solicitacao <> ? " +
                            "AND hora_inicio < ? AND COALESCE(hora_fim, hora_inicio) > ?",
                    new String[]{String.valueOf(s.getIdProfessor()), s.getDataDesejada(),
                            String.valueOf(idSolicitacao), fim, s.getHoraInicio()});

            db.setTransactionSuccessful();
            return idAtendimento;
        } catch (Exception e) {
            return JA_TRATADA;
        } finally {
            db.endTransaction();
        }
    }

    /** Professor recusa uma solicitação pendente. */
    public boolean recusar(long idSolicitacao) {
        return mudarStatusSePendente(idSolicitacao, Solicitacao.RECUSADA);
    }

    /** Aluno cancela uma solicitação ainda pendente. */
    public boolean cancelar(long idSolicitacao) {
        return mudarStatusSePendente(idSolicitacao, Solicitacao.CANCELADA);
    }


    private boolean mudarStatusSePendente(long id, String novoStatus) {
        ContentValues v = new ContentValues();
        v.put("status", novoStatus);
        return banco.getWritableDatabase().update("solicitacao", v,
                "id_solicitacao = ? AND status = 'PENDENTE'", new String[]{String.valueOf(id)}) > 0;
    }

    private List<Solicitacao> listar(String condicao, long id, String statusFiltro) {
        String sql = SELECT + "WHERE " + condicao;
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(id));
        if (statusFiltro != null && !statusFiltro.isEmpty()) {
            sql += " AND s.status = ?";
            args.add(statusFiltro);
        }
        return consultar(sql + " ORDER BY s.criado_em DESC, s.id_solicitacao DESC", args.toArray(new String[0]));
    }

    private List<Solicitacao> consultar(String sql, String[] args) {
        List<Solicitacao> lista = new ArrayList<>();
        try (Cursor c = banco.getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                Solicitacao s = new Solicitacao();
                s.setId(c.getLong(0));
                s.setIdAluno(c.getLong(1));
                s.setIdProfessor(c.getLong(2));
                s.setIdDisciplina(c.isNull(3) ? 0 : c.getLong(3));
                s.setTipoAtendimento(c.getString(4));
                s.setDataDesejada(c.getString(5));
                s.setHoraInicio(c.getString(6));
                s.setHoraFim(c.getString(7));
                s.setModalidade(c.getString(8));
                s.setDescricao(c.getString(9));
                s.setStatus(c.getString(10));
                s.setCriadoEm(c.getString(11));
                s.setNomeAluno(c.getString(12));
                s.setNomeProfessor(c.getString(13));
                s.setNomeDisciplina(c.getString(14));
                lista.add(s);
            }
        }
        return lista;
    }
}
