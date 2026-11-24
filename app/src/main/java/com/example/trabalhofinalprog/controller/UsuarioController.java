package com.example.trabalhofinalprog.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.example.trabalhofinalprog.database.ConectaDB;
import com.example.trabalhofinalprog.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/** CRUD e autenticação de usuários (alunos e professores). */
public class UsuarioController {

    /** Códigos negativos retornados por cadastrar(). Valores > 0 são o id do novo usuário. */
    public static final long ERRO = -1;
    public static final long EMAIL_DUPLICADO = -2;
    public static final long MATRICULA_DUPLICADA = -3;

    private static final String COLUNAS = "id_usuario, nome, email, matricula, tipo";

    private final ConectaDB banco;

    public UsuarioController(Context contexto) {
        this.banco = ConectaDB.getInstance(contexto);
    }

    /** Cadastra um usuário. A senha é gravada com hash. Retorna o id ou um dos códigos negativos. */
    public long cadastrar(String nome, String email, String matricula, String senha, String tipo) {
        email = email.trim().toLowerCase();
        matricula = vazioParaNulo(matricula);
        if (emailExiste(email, 0)) return EMAIL_DUPLICADO;
        if (matricula != null && matriculaExiste(matricula, 0)) return MATRICULA_DUPLICADA;

        ContentValues v = new ContentValues();
        v.put("nome", nome.trim());
        v.put("email", email);
        v.put("matricula", matricula);
        v.put("senha", senha);
        v.put("tipo", tipo);
        try {
            return banco.getWritableDatabase().insertOrThrow("usuario", null, v);
        } catch (Exception e) {
            return ERRO;
        }
    }

    /** Cadastro de aluno feito pelo professor: a senha inicial é a própria matrícula. */
    public long cadastrarAluno(String nome, String matricula, String email) {
        return cadastrar(nome, email, matricula, matricula, Usuario.ALUNO);
    }

    /** Retorna o usuário se e-mail/senha conferem; senão null. */
    public Usuario autenticar(String email, String senha) {
        SQLiteDatabase db = banco.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT " + COLUNAS + ", senha FROM usuario WHERE email = ?",
                new String[]{email.trim().toLowerCase()})) {
            if (c.moveToFirst() && senha.equals(c.getString(5))) {
                return paraUsuario(c);
            }
        }
        return null;
    }

    public Usuario buscarPorId(long id) {
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT " + COLUNAS + " FROM usuario WHERE id_usuario = ?", new String[]{String.valueOf(id)})) {
            return c.moveToFirst() ? paraUsuario(c) : null;
        }
    }

    /** Lista alunos ordenados por nome. filtro (opcional) busca em nome ou matrícula. */
    public List<Usuario> listarAlunos(String filtro) {
        String sql = "SELECT " + COLUNAS + " FROM usuario WHERE tipo = 'ALUNO'";
        String[] args = null;
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql += " AND (nome LIKE ? OR matricula LIKE ?)";
            String like = "%" + filtro.trim() + "%";
            args = new String[]{like, like};
        }
        return consultar(sql + " ORDER BY nome COLLATE NOCASE", args);
    }

    public List<Usuario> listarAlunosSemFiltro() {
        String sql = "SELECT " + COLUNAS +
                " FROM usuario WHERE tipo = 'ALUNO' " +
                "ORDER BY nome COLLATE NOCASE";

        return consultar(sql, null);
    }

    public List<Usuario> listarProfessores() {
        return consultar("SELECT " + COLUNAS + " FROM usuario WHERE tipo = 'PROFESSOR' ORDER BY nome COLLATE NOCASE", null);
    }

    /** Atualiza nome, e-mail e matrícula. Retorna false se o e-mail/matrícula já pertencem a outro usuário. */
    public boolean atualizar(Usuario u) {
        String email = u.getEmail().trim().toLowerCase();
        String matricula = vazioParaNulo(u.getMatricula());
        if (emailExiste(email, u.getId())) return false;
        if (matricula != null && matriculaExiste(matricula, u.getId())) return false;

        ContentValues v = new ContentValues();
        v.put("nome", u.getNome().trim());
        v.put("email", email);
        v.put("matricula", matricula);
        try {
            return banco.getWritableDatabase().update("usuario", v, "id_usuario = ?",
                    new String[]{String.valueOf(u.getId())}) > 0;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    public boolean alterarSenha(long id, String novaSenha) {
        ContentValues v = new ContentValues();
        v.put("senha", novaSenha);
        return banco.getWritableDatabase().update("usuario", v, "id_usuario = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    /** Exclui o usuário e (por cascata) suas disciplinas, solicitações e presenças. */
    public boolean excluir(long id) {
        return banco.getWritableDatabase().delete("usuario", "id_usuario = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean emailExiste(String email, long ignorarId) {
        return existe("email = ? COLLATE NOCASE", email.trim(), ignorarId);
    }

    public boolean matriculaExiste(String matricula, long ignorarId) {
        return existe("matricula = ?", matricula.trim(), ignorarId);
    }

    private boolean existe(String condicao, String valor, long ignorarId) {
        try (Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT 1 FROM usuario WHERE " + condicao + " AND id_usuario <> ?",
                new String[]{valor, String.valueOf(ignorarId)})) {
            return c.moveToFirst();
        }
    }

    private List<Usuario> consultar(String sql, String[] args) {
        List<Usuario> lista = new ArrayList<>();
        try (Cursor c = banco.getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) lista.add(paraUsuario(c));
        }
        return lista;
    }

    private Usuario paraUsuario(Cursor c) {
        Usuario u = new Usuario();
        u.setId(c.getLong(0));
        u.setNome(c.getString(1));
        u.setEmail(c.getString(2));
        u.setMatricula(c.getString(3));
        u.setTipo(c.getString(4));
        return u;
    }

    private static String vazioParaNulo(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
