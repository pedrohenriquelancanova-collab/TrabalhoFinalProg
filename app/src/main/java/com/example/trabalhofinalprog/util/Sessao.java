package com.example.trabalhofinalprog.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.trabalhofinalprog.model.Usuario;

/**
 * Guarda quem está logado (SharedPreferences), para as outras telas saberem
 * qual usuário usar nas consultas. Ex.: Sessao.getUsuarioId(this).
 */
public final class Sessao {

    private static final String ARQUIVO = "sessao";
    private static final String CHAVE_ID = "id_usuario";
    private static final String CHAVE_NOME = "nome";
    private static final String CHAVE_TIPO = "tipo";

    private Sessao() {
    }

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    public static void iniciar(Context c, Usuario u) {
        prefs(c).edit()
                .putLong(CHAVE_ID, u.getId())
                .putString(CHAVE_NOME, u.getNome())
                .putString(CHAVE_TIPO, u.getTipo())
                .apply();
    }

    public static void encerrar(Context c) {
        prefs(c).edit().clear().apply();
    }

    public static boolean logado(Context c) {
        return getUsuarioId(c) > 0;
    }

    public static long getUsuarioId(Context c) {
        return prefs(c).getLong(CHAVE_ID, -1);
    }

    public static String getNome(Context c) {
        return prefs(c).getString(CHAVE_NOME, "");
    }

    public static String getTipo(Context c) {
        return prefs(c).getString(CHAVE_TIPO, "");
    }
}
