package com.example.trabalhofinalprog.model;

public class Disciplina {

    private long id;
    private String nome;
    private long idProfessor;

    public Disciplina() {
    }

    public Disciplina(String nome, long idProfessor) {
        this.nome = nome;
        this.idProfessor = idProfessor;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public long getIdProfessor() { return idProfessor; }
    public void setIdProfessor(long idProfessor) { this.idProfessor = idProfessor; }

    @Override
    public String toString() { return nome; }
}
