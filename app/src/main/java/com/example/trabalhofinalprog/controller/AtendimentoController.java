package com.example.trabalhofinalprog.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.trabalhofinalprog.database.ConectaDB;
import com.example.trabalhofinalprog.model.Atendimento;
import com.example.trabalhofinalprog.model.Presenca;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Atendimentos agendados, pedidos de alteração/cancelamento e registro de resultado com presença. */
public class AtendimentoController {

    private static final String SELECT =
            "SELECT t.id_atendimento, t.id_solicitacao, t.id_professor, t.id_disciplina, t.tipo_atendimento, " +
                    "t.data, t.hora_inicio, t.hora_fim, t.modalidade, t.descricao, t.status, " +
                    "t.conteudo_tratado, t.observacoes, p.nome, d.nome " +
                    "FROM atendimento t " +
                    "JOIN usuario p ON p.id_usuario = t.id_professor " +
                    "LEFT JOIN disciplina d ON d.id_disciplina = t.id_disciplina ";

    private final ConectaDB banco;

    public AtendimentoController(Context contexto) {
        this.banco = ConectaDB.getInstance(contexto);
    }

    public Atendimento buscarPorId(long id) {
        List<Atendimento> l = consultar(SELECT + "WHERE t.id_atendimento = ?", new String[]{String.valueOf(id)});
        return l.isEmpty() ? null : l.get(0);
    }

    /** Agenda do professor (statusFiltro nulo = todos), em ordem de data. */
    public List<Atendimento> listarPorProfessor(long idProfessor, String statusFiltro) {
        return listar("t.id_professor = ?", idProfessor, statusFiltro);
    }

    /** Atendimentos em que o aluno participa (statusFiltro nulo = todos). */
    public List<Atendimento> listarPorAluno(long idAluno, String statusFiltro) {
        return listar("t.id_atendimento IN (SELECT id_atendimento FROM presenca WHERE id_aluno = ?)",
                idAluno, statusFiltro);
    }

    /** Próximos atendimentos AGENDADOS do aluno (de hoje em diante). */
    public List<Atendimento> proximosDoAluno(long idAluno, int limite) {
        return consultar(SELECT +
                        "WHERE t.status = 'AGENDADO' AND t.data >= ? " +
                        "AND t.id_atendimento IN (SELECT id_atendimento FROM presenca WHERE id_aluno = ?) " +
                        "ORDER BY t.data, t.hora_inicio LIMIT " + Math.max(1, limite),
                new String[]{hoje(), String.valueOf(idAluno)});
    }

    /** Aluno pede alteração: o atendimento fica sinalizado para o professor decidir. */
    public boolean solicitarAlteracao(long idAtendimento) {
        return mudarStatus(idAtendimento, Atendimento.ALTERACAO_SOLICITADA, Atendimento.AGENDADO);
    }

    /** Aluno pede cancelamento: o atendimento fica sinalizado para o professor confirmar. */
    public boolean solicitarCancelamento(long idAtendimento) {
        return mudarStatus(idAtendimento, Atendimento.CANCELAMENTO_SOLICITADO, Atendimento.AGENDADO);
    }

    /** Professor cancela de fato um atendimento ainda não realizado. */
    public boolean cancelar(long idAtendimento) {
        return mudarStatus(idAtendimento, Atendimento.CANCELADO,
                Atendimento.AGENDADO, Atendimento.ALTERACAO_SOLICITADA, Atendimento.CANCELAMENTO_SOLICITADO);
    }

    /** Professor reagenda (também volta o status para AGENDADO). */
    public boolean reagendar(long idAtendimento, String novaData, String novaHoraInicio, String novaHoraFim) {
        if (!DisponibilidadeController.horarioValido(novaHoraInicio)) return false;
        ContentValues v = new ContentValues();
        v.put("data", novaData);
        v.put("hora_inicio", novaHoraInicio);
        v.put("hora_fim", novaHoraFim);
        v.put("status", Atendimento.AGENDADO);
        return banco.getWritableDatabase().update("atendimento", v,
                "id_atendimento = ? AND status IN ('AGENDADO','ALTERACAO_SOLICITADA','CANCELAMENTO_SOLICITADO')",
                new String[]{String.valueOf(idAtendimento)}) > 0;
    }

    /** Adiciona outro aluno ao atendimento (ex.: atendimento em grupo). */
    public boolean adicionarAluno(long idAtendimento, long idAluno) {
        ContentValues v = new ContentValues();
        v.put("id_atendimento", idAtendimento);
        v.put("id_aluno", idAluno);
        v.put("presente", 0);
        return banco.getWritableDatabase().insertWithOnConflict("presenca", null, v,
                SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    public List<Presenca> listarPresencas(long idAtendimento) {
        List<Presenca> lista = new ArrayList<>();
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT p.id_atendimento, p.id_aluno, p.presente, u.nome FROM presenca p " +
                        "JOIN usuario u ON u.id_usuario = p.id_aluno " +
                        "WHERE p.id_atendimento = ? ORDER BY u.nome COLLATE NOCASE",
                new String[]{String.valueOf(idAtendimento)})) {
            while (c.moveToNext()) {
                Presenca p = new Presenca(c.getLong(0), c.getLong(1), c.getInt(2) == 1);
                p.setNomeAluno(c.getString(3));
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Registra o resultado (tela "Registrar atendimento"): situação (REALIZADO, FALTA ou CANCELADO),
     * conteúdo tratado, observações e a presença de cada aluno. Tudo na mesma transação.
     */
    public boolean registrarResultado(long idAtendimento, String situacao, String conteudo,
                                      String observacoes, List<Presenca> presencas) {
        SQLiteDatabase db = banco.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues v = new ContentValues();
            v.put("status", situacao);
            v.put("conteudo_tratado", conteudo);
            v.put("observacoes", observacoes);
            int linhas = db.update("atendimento", v, "id_atendimento = ?",
                    new String[]{String.valueOf(idAtendimento)});
            if (linhas == 0) return false;

            if (presencas != null) {
                for (Presenca p : presencas) {
                    ContentValues pv = new ContentValues();
                    pv.put("presente", p.isPresente() ? 1 : 0);
                    db.update("presenca", pv, "id_atendimento = ? AND id_aluno = ?",
                            new String[]{String.valueOf(idAtendimento), String.valueOf(p.getIdAluno())});
                }
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    // ------------------------------------------------- números do dashboard do professor

    public int contarHoje(long idProfessor) {
        return contar("SELECT COUNT(*) FROM atendimento WHERE id_professor = ? AND data = ? AND status <> 'CANCELADO'",
                String.valueOf(idProfessor), hoje());
    }

    public int contarPorStatus(long idProfessor, String status) {
        return contar("SELECT COUNT(*) FROM atendimento WHERE id_professor = ? AND status = ?",
                String.valueOf(idProfessor), status);
    }

    // ---------------------------------------------------------------- helpers

    private int contar(String sql, String... args) {
        try (Cursor c = banco.getReadableDatabase().rawQuery(sql, args)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    private boolean mudarStatus(long id, String novoStatus, String... statusPermitidos) {
        StringBuilder in = new StringBuilder();
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(id));
        for (String s : statusPermitidos) {
            if (in.length() > 0) in.append(",");
            in.append("?");
            args.add(s);
        }
        ContentValues v = new ContentValues();
        v.put("status", novoStatus);
        return banco.getWritableDatabase().update("atendimento", v,
                "id_atendimento = ? AND status IN (" + in + ")", args.toArray(new String[0])) > 0;
    }

    private List<Atendimento> listar(String condicao, long id, String statusFiltro) {
        String sql = SELECT + "WHERE " + condicao;
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(id));
        if (statusFiltro != null && !statusFiltro.isEmpty()) {
            sql += " AND t.status = ?";
            args.add(statusFiltro);
        }
        return consultar(sql + " ORDER BY t.data, t.hora_inicio", args.toArray(new String[0]));
    }

    private List<Atendimento> consultar(String sql, String[] args) {
        List<Atendimento> lista = new ArrayList<>();
        try (Cursor c = banco.getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                Atendimento a = new Atendimento();
                a.setId(c.getLong(0));
                a.setIdSolicitacao(c.isNull(1) ? 0 : c.getLong(1));
                a.setIdProfessor(c.getLong(2));
                a.setIdDisciplina(c.isNull(3) ? 0 : c.getLong(3));
                a.setTipoAtendimento(c.getString(4));
                a.setData(c.getString(5));
                a.setHoraInicio(c.getString(6));
                a.setHoraFim(c.getString(7));
                a.setModalidade(c.getString(8));
                a.setDescricao(c.getString(9));
                a.setStatus(c.getString(10));
                a.setConteudoTratado(c.getString(11));
                a.setObservacoes(c.getString(12));
                a.setNomeProfessor(c.getString(13));
                a.setNomeDisciplina(c.getString(14));
                lista.add(a);
            }
        }
        return lista;
    }

    private static String hoje() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }
}
