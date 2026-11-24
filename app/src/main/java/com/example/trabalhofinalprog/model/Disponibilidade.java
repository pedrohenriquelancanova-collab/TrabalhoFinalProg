package com.example.trabalhofinalprog.model;

/** Janela de horário em que o professor atende. diaSemana segue Calendar.DAY_OF_WEEK (1=domingo..7=sábado). */
public class Disponibilidade {

    private static final String[] DIAS = {
            "", "Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira",
            "Quinta-feira", "Sexta-feira", "Sábado"
    };

    private long id;
    private long idProfessor;
    private int diaSemana;
    private String horaInicio; // HH:mm
    private String horaFim;    // HH:mm

    public Disponibilidade() {
    }

    public Disponibilidade(long idProfessor, int diaSemana, String horaInicio, String horaFim) {
        this.idProfessor = idProfessor;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public static String nomeDoDia(int diaSemana) {
        return diaSemana >= 1 && diaSemana <= 7 ? DIAS[diaSemana] : "";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getIdProfessor() { return idProfessor; }
    public void setIdProfessor(long idProfessor) { this.idProfessor = idProfessor; }

    public int getDiaSemana() { return diaSemana; }
    public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFim() { return horaFim; }
    public void setHoraFim(String horaFim) { this.horaFim = horaFim; }

    public String getNomeDia() { return nomeDoDia(diaSemana); }

    public String getHorario() { return horaInicio + " às " + horaFim; }

    @Override
    public String toString() { return getNomeDia() + " - " + getHorario(); }
}
