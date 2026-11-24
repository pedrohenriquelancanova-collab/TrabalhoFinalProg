package com.example.trabalhofinalprog.model;

import androidx.annotation.NonNull;

/** Pedido de atendimento feito por um aluno a um professor. */
public class Solicitacao {

    public static final String PENDENTE = "PENDENTE";
    public static final String APROVADA = "APROVADA";
    public static final String RECUSADA = "RECUSADA";
    public static final String CANCELADA = "CANCELADA";

    public static final String PRESENCIAL = "PRESENCIAL";
    public static final String REMOTO = "REMOTO";

    public Solicitacao(){}

    public Solicitacao(long idAluno, long idProfessor, long idDisciplina,
                       String tipoAtendimento, String dataDesejada,
                       String horaInicio, String horaFim,
                       String modalidade, String descricao) {

        this.idAluno = idAluno;
        this.idProfessor = idProfessor;
        this.idDisciplina = idDisciplina;
        this.tipoAtendimento = tipoAtendimento;
        this.dataDesejada = dataDesejada;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.modalidade = modalidade;
        this.descricao = descricao;

        this.status = PENDENTE;
    }

    private long id;
    private long idAluno;
    private long idProfessor;
    private long idDisciplina; // 0 = sem disciplina
    private String tipoAtendimento;
    private String dataDesejada; // yyyy-MM-dd
    private String horaInicio;   // HH:mm
    private String horaFim;      // HH:mm
    private String modalidade = PRESENCIAL;
    private String descricao;
    private String status = PENDENTE;
    private String criadoEm;

    // Campos preenchidos pelas consultas (JOIN), só para exibição.
    private String nomeAluno;
    private String nomeProfessor;
    private String nomeDisciplina;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getIdAluno() { return idAluno; }
    public void setIdAluno(long idAluno) { this.idAluno = idAluno; }

    public long getIdProfessor() { return idProfessor; }
    public void setIdProfessor(long idProfessor) { this.idProfessor = idProfessor; }

    public long getIdDisciplina() { return idDisciplina; }
    public void setIdDisciplina(long idDisciplina) { this.idDisciplina = idDisciplina; }

    public String getTipoAtendimento() { return tipoAtendimento; }
    public void setTipoAtendimento(String tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    public String getDataDesejada() { return dataDesejada; }
    public void setDataDesejada(String dataDesejada) { this.dataDesejada = dataDesejada; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFim() { return horaFim; }
    public void setHoraFim(String horaFim) { this.horaFim = horaFim; }

    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCriadoEm() { return criadoEm; }
    public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }

    public String getNomeAluno() { return nomeAluno; }
    public void setNomeAluno(String nomeAluno) { this.nomeAluno = nomeAluno; }

    public String getNomeProfessor() { return nomeProfessor; }
    public void setNomeProfessor(String nomeProfessor) { this.nomeProfessor = nomeProfessor; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    @NonNull
    @Override
    public String toString() {
        return "Nome do Aluno: " + nomeAluno + "\n" +
                "Nome do Professor: " + nomeProfessor + "\n" +
                "Nome da Disciplina: " + nomeDisciplina + "\n" +
                "Tipo de Atendimento: " + tipoAtendimento + "\n" +
                "Data desejada: " + dataDesejada + "\n" +
                "Horario inicio: " + horaInicio +"\n" +
                "Horario fim: " + horaFim + "\n" +
                "Modalidade: " + modalidade +"\n" +
                "Descrição: " + descricao + "\n" +
                "Status: " + status;
    }
}
