package vallegrande.edu.pe.cuyconectacrud.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Entidad INSCRIPCIONES del sistema de gestión de eventos
 * (módulo del Integrante 3).
 *
 * Además de las columnas propias de la tabla, guarda datos de solo
 * lectura del evento y del participante (traídos con JOIN) para
 * mostrarlos en la tabla y en el reporte.
 */
public class Inscripcion {

    public static final String ESTADO_INSCRITO = "Inscrito";
    public static final String ESTADO_CONFIRMADO = "Confirmado";
    public static final String ESTADO_CANCELADO = "Cancelado";

    public static final List<String> ESTADOS = List.of(
            ESTADO_INSCRITO, ESTADO_CONFIRMADO, ESTADO_CANCELADO);

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ----- Columnas de la tabla inscripciones -----
    private int idInscripcion;
    private int idEvento;
    private int idParticipante;
    private LocalDate fechaInscripcion;
    private String estado;

    // ----- Datos de solo lectura (JOIN) -----
    private String eventoNombre;
    private LocalDate eventoFecha;
    private String eventoLugar;
    private String participanteNombre;
    private String participanteDni;
    private String participanteInstitucion;

    /** Constructor para registros aún sin id (nuevos). */
    public Inscripcion(int idEvento, int idParticipante,
                       LocalDate fechaInscripcion, String estado) {
        this.idEvento = idEvento;
        this.idParticipante = idParticipante;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    /** Constructor completo, para registros leídos de la BD con JOIN. */
    public Inscripcion(int idInscripcion, int idEvento, int idParticipante,
                       LocalDate fechaInscripcion, String estado,
                       String eventoNombre, LocalDate eventoFecha,
                       String eventoLugar, String participanteNombre,
                       String participanteDni, String participanteInstitucion) {
        this(idEvento, idParticipante, fechaInscripcion, estado);
        this.idInscripcion = idInscripcion;
        this.eventoNombre = eventoNombre;
        this.eventoFecha = eventoFecha;
        this.eventoLugar = eventoLugar;
        this.participanteNombre = participanteNombre;
        this.participanteDni = participanteDni;
        this.participanteInstitucion = participanteInstitucion;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public int getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(int idParticipante) {
        this.idParticipante = idParticipante;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEventoNombre() {
        return eventoNombre;
    }

    public LocalDate getEventoFecha() {
        return eventoFecha;
    }

    public String getEventoLugar() {
        return eventoLugar;
    }

    public String getParticipanteNombre() {
        return participanteNombre;
    }

    public String getParticipanteDni() {
        return participanteDni;
    }

    public String getParticipanteInstitucion() {
        return participanteInstitucion;
    }

    /** Fecha de inscripción como texto dd/MM/yyyy (tabla y reporte). */
    public String getFechaInscripcionTexto() {
        return fechaInscripcion == null
                ? "" : fechaInscripcion.format(FORMATO_FECHA);
    }

    /** Fecha del evento como texto dd/MM/yyyy (reporte). */
    public String getEventoFechaTexto() {
        return eventoFecha == null
                ? "" : eventoFecha.format(FORMATO_FECHA);
    }
}
