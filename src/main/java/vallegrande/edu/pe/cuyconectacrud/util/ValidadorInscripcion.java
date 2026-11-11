package vallegrande.edu.pe.cuyconectacrud.util;

import vallegrande.edu.pe.cuyconectacrud.model.EventoOpcion;
import vallegrande.edu.pe.cuyconectacrud.model.Inscripcion;
import vallegrande.edu.pe.cuyconectacrud.model.ParticipanteOpcion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Validaciones de formulario del módulo Inscripciones (Integrante 3).
 *
 * Solo revisa lo que se puede comprobar sin consultar la base de
 * datos: campos obligatorios, estado permitido y coherencia de
 * fechas. La existencia de evento/participante y los duplicados los
 * verifica el controlador a través del DAO.
 */
public final class ValidadorInscripcion {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ValidadorInscripcion() {
    }

    /**
     * @return lista de mensajes de error; vacía si todo es válido.
     */
    public static List<String> validar(EventoOpcion evento,
                                       ParticipanteOpcion participante,
                                       LocalDate fechaInscripcion,
                                       String estado) {

        List<String> errores = new ArrayList<>();

        if (evento == null) {
            errores.add("Seleccione un evento.");
        }

        if (participante == null) {
            errores.add("Seleccione un participante.");
        }

        if (fechaInscripcion == null) {
            errores.add("Indique una fecha de inscripción válida "
                    + "(dd/mm/aaaa).");
        } else if (evento != null && evento.fecha() != null
                && fechaInscripcion.isAfter(evento.fecha())) {
            errores.add("La fecha de inscripción no puede ser posterior "
                    + "a la fecha del evento ("
                    + evento.fecha().format(FORMATO_FECHA) + ").");
        }

        if (estado == null || estado.isBlank()) {
            errores.add("Seleccione el estado de la inscripción.");
        } else if (!Inscripcion.ESTADOS.contains(estado)) {
            errores.add("El estado seleccionado no es válido.");
        }

        return errores;
    }
}
