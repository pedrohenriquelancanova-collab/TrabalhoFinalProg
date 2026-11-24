package com.example.trabalhofinalprog.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.trabalhofinalprog.database.ConectaDB;
import com.example.trabalhofinalprog.model.Disponibilidade;

import java.util.ArrayList;
import java.util.List;

/** Horários em que cada professor atende. */
public class DisponibilidadeController {

    public static final long ERRO = -1;
    public static final long HORARIO_INVALIDO = -2; // formato errado ou fim <= início
    public static final long CONFLITO = -3;         // sobrepõe outro horário do mesmo dia

    private final ConectaDB banco;

    public DisponibilidadeController(Context contexto) {
        this.banco = ConectaDB.getInstance(contexto);
    }

    /** Retorna o id gravado ou um dos códigos negativos acima. */
    public long inserir(Disponibilidade d) {
        if (!horarioValido(d.getHoraInicio()) || !horarioValido(d.getHoraFim())
                || d.getHoraInicio().compareTo(d.getHoraFim()) >= 0
                || d.getDiaSemana() < 1 || d.getDiaSemana() > 7) {
            return HORARIO_INVALIDO;
        }
        // Dois intervalos se sobrepõem quando inicio < fim_existente E fim > inicio_existente.
        // (HH:mm com zero à esquerda compara corretamente como texto.)
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT 1 FROM disponibilidade WHERE id_professor = ? AND dia_semana = ? " +
                        "AND hora_inicio < ? AND hora_fim > ?",
                new String[]{String.valueOf(d.getIdProfessor()), String.valueOf(d.getDiaSemana()),
                        d.getHoraFim(), d.getHoraInicio()})) {
            if (c.moveToFirst()) return CONFLITO;
        }
        ContentValues v = new ContentValues();
        v.put("id_professor", d.getIdProfessor());
        v.put("dia_semana", d.getDiaSemana());
        v.put("hora_inicio", d.getHoraInicio());
        v.put("hora_fim", d.getHoraFim());
        return banco.getWritableDatabase().insert("disponibilidade", null, v);
    }

    public boolean excluir(long id) {
        return banco.getWritableDatabase().delete("disponibilidade", "id_disponibilidade = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public List<Disponibilidade> listarPorProfessor(long idProfessor) {
        return consultar("WHERE id_professor = ?", new String[]{String.valueOf(idProfessor)});
    }

    /** Horários do professor em um dia da semana (Calendar.DAY_OF_WEEK) — usado no spinner "Horário disponível". */
    public List<Disponibilidade> listarPorProfessorEDia(long idProfessor, int diaSemana) {
        return consultar("WHERE id_professor = ? AND dia_semana = ?",
                new String[]{String.valueOf(idProfessor), String.valueOf(diaSemana)});
    }

    private List<Disponibilidade> consultar(String where, String[] args) {
        List<Disponibilidade> lista = new ArrayList<>();
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT id_disponibilidade, id_professor, dia_semana, hora_inicio, hora_fim " +
                        "FROM disponibilidade " + where + " ORDER BY dia_semana, hora_inicio", args)) {
            while (c.moveToNext()) {
                Disponibilidade d = new Disponibilidade();
                d.setId(c.getLong(0));
                d.setIdProfessor(c.getLong(1));
                d.setDiaSemana(c.getInt(2));
                d.setHoraInicio(c.getString(3));
                d.setHoraFim(c.getString(4));
                lista.add(d);
            }
        }
        return lista;
    }

    /** Aceita somente HH:mm (00:00 a 23:59). */
    public static boolean horarioValido(String h) {
        return h != null && h.matches("([01][0-9]|2[0-3]):[0-5][0-9]");
    }
}
