package com.example.trabalhofinalprog.model;

/** Presença de um aluno em um atendimento. */
public class Presenca {

    private long idAtendimento;
    private long idAluno;
    private boolean presente;
    private String nomeAluno; // preenchido pela consulta (JOIN)

    public Presenca() {
    }

    public Presenca(long idAtendimento, long idAluno, boolean presente) {
        this.idAtendimento = idAtendimento;
        this.idAluno = idAluno;
        this.presente = presente;
    }

    public long getIdAtendimento() { return idAtendimento; }
    public void setIdAtendimento(long idAtendimento) { this.idAtendimento = idAtendimento; }

    public long getIdAluno() { return idAluno; }
    public void setIdAluno(long idAluno) { this.idAluno = idAluno; }

    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }

    public String getNomeAluno() { return nomeAluno; }
    public void setNomeAluno(String nomeAluno) { this.nomeAluno = nomeAluno; }

    @Override
    public String toString() { return nomeAluno; }
}
