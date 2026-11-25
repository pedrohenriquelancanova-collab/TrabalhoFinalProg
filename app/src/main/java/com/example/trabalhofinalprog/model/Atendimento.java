package com.example.trabalhofinalprog.model;

import androidx.annotation.NonNull;

/** Atendimento agendado (criado quando o professor aprova uma solicitação) e seu registro de resultado. */
public class Atendimento {

    public static final String AGENDADO = "AGENDADO";
    public static final String ALTERACAO_SOLICITADA = "ALTERACAO_SOLICITADA";
    public static final String CANCELAMENTO_SOLICITADO = "CANCELAMENTO_SOLICITADO";
    public static final String REALIZADO = "REALIZADO";
    public static final String CANCELADO = "CANCELADO";
    public static final String FALTA = "FALTA";

    private long id;
    private long idSolicitacao;
    private long idProfessor;
    private long idDisciplina;
    private String tipoAtendimento;
    private String data;
    private String horaInicio;
    private String horaFim;
    private String modalidade;
    private String descricao;
    private String status = AGENDADO;
    private String conteudoTratado;
    private String observacoes;

    // Preenchidos pelas consultas (JOIN), só para exibição.
    private String nomeProfessor;
    private String nomeDisciplina;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getIdSolicitacao() { return idSolicitacao; }
    public void setIdSolicitacao(long idSolicitacao) { this.idSolicitacao = idSolicitacao; }


    public long getIdProfessor() { return idProfessor; }
    public void setIdProfessor(long idProfessor) { this.idProfessor = idProfessor; }

    public long getIdDisciplina() { return idDisciplina; }
    public void setIdDisciplina(long idDisciplina) { this.idDisciplina = idDisciplina; }

    public String getTipoAtendimento() { return tipoAtendimento; }
    public void setTipoAtendimento(String tipoAtendimento) { this.tipoAtendimento = tipoAtendimento; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

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

    public String getConteudoTratado() { return conteudoTratado; }
    public void setConteudoTratado(String conteudoTratado) { this.conteudoTratado = conteudoTratado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getNomeProfessor() { return nomeProfessor; }
    public void setNomeProfessor(String nomeProfessor) { this.nomeProfessor = nomeProfessor; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    @NonNull
    @Override
    public String toString() {
        return "Disciplina - " + nomeDisciplina + "\n" +
              "Data - " + data + "\n" +
                horaInicio + " às " + horaFim + "\n" +
                "Status: " + status;
    }
}
