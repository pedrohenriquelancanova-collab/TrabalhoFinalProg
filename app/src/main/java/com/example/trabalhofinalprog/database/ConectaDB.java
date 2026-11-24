package com.example.trabalhofinalprog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Conexão com o banco SQLite do app (equivalente ao "conectadb" do exemplo).
 * Use sempre ConectaDB.getInstance(context) para compartilhar uma única conexão.
 *
 * Convenções:
 *  - datas são gravadas como texto "yyyy-MM-dd" e horários como "HH:mm";
 *  - dia_semana segue Calendar.DAY_OF_WEEK (1 = domingo ... 7 = sábado);
 */
public class ConectaDB extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "atendemais.db";
    public static final int VERSAO = 1;

    private static ConectaDB instancia;

    public static synchronized ConectaDB getInstance(Context contexto) {
        if (instancia == null) {
            instancia = new ConectaDB(contexto.getApplicationContext());
        }
        return instancia;
    }

    private ConectaDB(Context contexto) {
        super(contexto, NOME_BANCO, null, VERSAO);
    }

    private static final String TAB_USUARIO = "CREATE TABLE IF NOT EXISTS usuario (" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome TEXT NOT NULL, " +
            "email TEXT NOT NULL UNIQUE, " +
            "matricula TEXT, " +
            "senha TEXT NOT NULL, " +
            "tipo TEXT NOT NULL CHECK (tipo IN ('ALUNO','PROFESSOR')))";

    private static final String IDX_MATRICULA = "CREATE UNIQUE INDEX IF NOT EXISTS idx_usuario_matricula " +
            "ON usuario (matricula)";

    private static final String TAB_DISCIPLINA = "CREATE TABLE IF NOT EXISTS disciplina (" +
            "id_disciplina INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome TEXT NOT NULL, " +
            "id_professor INTEGER NOT NULL, " +
            "UNIQUE (nome, id_professor), " +
            "FOREIGN KEY (id_professor) REFERENCES usuario (id_usuario) ON DELETE CASCADE)";

    private static final String TAB_DISPONIBILIDADE = "CREATE TABLE IF NOT EXISTS disponibilidade (" +
            "id_disponibilidade INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_professor INTEGER NOT NULL, " +
            "dia_semana INTEGER NOT NULL CHECK (dia_semana BETWEEN 1 AND 7), " +
            "hora_inicio TEXT NOT NULL, " +
            "hora_fim TEXT NOT NULL, " +
            "FOREIGN KEY (id_professor) REFERENCES usuario (id_usuario) ON DELETE CASCADE)";

    private static final String TAB_SOLICITACAO = "CREATE TABLE IF NOT EXISTS solicitacao (" +
            "id_solicitacao INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_aluno INTEGER NOT NULL, " +
            "id_professor INTEGER NOT NULL, " +
            "id_disciplina INTEGER, " +
            "tipo_atendimento TEXT, " +
            "data_desejada TEXT NOT NULL, " +
            "hora_inicio TEXT NOT NULL, " +
            "hora_fim TEXT, " +
            "modalidade TEXT NOT NULL DEFAULT 'PRESENCIAL', " +
            "descricao TEXT, " +
            "status TEXT NOT NULL DEFAULT 'PENDENTE', " +
            "criado_em TEXT NOT NULL, " +
            "FOREIGN KEY (id_aluno) REFERENCES usuario (id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_professor) REFERENCES usuario (id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_disciplina) REFERENCES disciplina (id_disciplina) ON DELETE SET NULL)";

    private static final String TAB_ATENDIMENTO = "CREATE TABLE IF NOT EXISTS atendimento (" +
            "id_atendimento INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_solicitacao INTEGER, " +
            "id_professor INTEGER NOT NULL, " +
            "id_disciplina INTEGER, " +
            "tipo_atendimento TEXT, " +
            "data TEXT NOT NULL, " +
            "hora_inicio TEXT NOT NULL, " +
            "hora_fim TEXT, " +
            "modalidade TEXT NOT NULL DEFAULT 'PRESENCIAL', " +
            "descricao TEXT, " +
            "status TEXT NOT NULL DEFAULT 'AGENDADO', " +
            "conteudo_tratado TEXT, " +
            "observacoes TEXT, " +
            "FOREIGN KEY (id_solicitacao) REFERENCES solicitacao (id_solicitacao) ON DELETE SET NULL, " +
            "FOREIGN KEY (id_professor) REFERENCES usuario (id_usuario) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_disciplina) REFERENCES disciplina (id_disciplina) ON DELETE SET NULL)";

    private static final String TAB_PRESENCA = "CREATE TABLE IF NOT EXISTS presenca (" +
            "id_atendimento INTEGER NOT NULL, " +
            "id_aluno INTEGER NOT NULL, " +
            "presente INTEGER NOT NULL DEFAULT 0, " +
            "PRIMARY KEY (id_atendimento, id_aluno), " +
            "FOREIGN KEY (id_atendimento) REFERENCES atendimento (id_atendimento) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_aluno) REFERENCES usuario (id_usuario) ON DELETE CASCADE)";

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // O SQLite não aplica chaves estrangeiras por padrão.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TAB_USUARIO);
        db.execSQL(IDX_MATRICULA);
        db.execSQL(TAB_DISCIPLINA);
        db.execSQL(TAB_DISPONIBILIDADE);
        db.execSQL(TAB_SOLICITACAO);
        db.execSQL(TAB_ATENDIMENTO);
        db.execSQL(TAB_PRESENCA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        // Mesmo comportamento do exemplo: recria tudo (apaga os dados).
        // Filhas primeiro, por causa das chaves estrangeiras.
        db.execSQL("DROP TABLE IF EXISTS presenca");
        db.execSQL("DROP TABLE IF EXISTS atendimento");
        db.execSQL("DROP TABLE IF EXISTS solicitacao");
        db.execSQL("DROP TABLE IF EXISTS disponibilidade");
        db.execSQL("DROP TABLE IF EXISTS disciplina");
        db.execSQL("DROP TABLE IF EXISTS usuario");
        onCreate(db);
    }
}
