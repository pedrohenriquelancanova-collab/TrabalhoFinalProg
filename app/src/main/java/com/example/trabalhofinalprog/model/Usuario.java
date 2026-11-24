package com.example.trabalhofinalprog.model;

/** Aluno ou professor. A senha (hash) fica só no banco e nunca é carregada no modelo. */
public class Usuario {

    public static final String ALUNO = "ALUNO";
    public static final String PROFESSOR = "PROFESSOR";

    private long id;
    private String nome;
    private String email;
    private String matricula;
    private String tipo;

    public Usuario() {
    }

    public Usuario(String nome, String email, String matricula, String tipo) {
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.tipo = tipo;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isProfessor() { return PROFESSOR.equals(tipo); }
    public boolean isAluno() { return ALUNO.equals(tipo); }

    /** Texto usado em ListView/Spinner. */
    @Override
    public String toString() {
        return isAluno() && matricula != null && !matricula.isEmpty()
                ? nome + " (" + matricula + ")" : nome;
    }
}
