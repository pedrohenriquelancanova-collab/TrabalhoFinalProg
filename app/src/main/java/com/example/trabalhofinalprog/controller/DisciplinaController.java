package com.example.trabalhofinalprog.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.trabalhofinalprog.database.ConectaDB;
import com.example.trabalhofinalprog.model.Disciplina;

import java.util.ArrayList;
import java.util.List;

/** Disciplinas que cada professor atende. */
public class DisciplinaController {

    private final ConectaDB banco;

    public DisciplinaController(Context contexto) {
        this.banco = ConectaDB.getInstance(contexto);
    }

    /** Retorna o id da disciplina, ou -1 se o nome for vazio/já existir para esse professor. */
    public long inserir(Disciplina d) {
        if (d.getNome() == null || d.getNome().trim().isEmpty()) return -1;
        ContentValues v = new ContentValues();
        v.put("nome", d.getNome().trim());
        v.put("id_professor", d.getIdProfessor());
        return banco.getWritableDatabase().insert("disciplina", null, v);
    }

    public boolean atualizar(Disciplina d) {
        if (d.getNome() == null || d.getNome().trim().isEmpty()) return false;
        ContentValues v = new ContentValues();
        v.put("nome", d.getNome().trim());
        try {
            return banco.getWritableDatabase().update("disciplina", v, "id_disciplina = ?",
                    new String[]{String.valueOf(d.getId())}) > 0;
        } catch (Exception e) {
            return false; // nome repetido
        }
    }

    public boolean excluir(long id) {
        return banco.getWritableDatabase().delete("disciplina", "id_disciplina = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public List<Disciplina> listarPorProfessor(long idProfessor) {
        List<Disciplina> lista = new ArrayList<>();
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT id_disciplina, nome, id_professor FROM disciplina " +
                        "WHERE id_professor = ? ORDER BY nome COLLATE NOCASE",
                new String[]{String.valueOf(idProfessor)})) {
            while (c.moveToNext()) {
                Disciplina d = new Disciplina();
                d.setId(c.getLong(0));
                d.setNome(c.getString(1));
                d.setIdProfessor(c.getLong(2));
                lista.add(d);
            }
        }
        return lista;
    }
}
