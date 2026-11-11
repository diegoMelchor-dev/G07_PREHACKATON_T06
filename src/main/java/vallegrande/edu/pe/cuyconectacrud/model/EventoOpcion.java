package vallegrande.edu.pe.cuyconectacrud.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Elemento liviano para los ComboBox de eventos.
 *
 * El módulo de Inscripciones solo necesita el id, el nombre y la
 * fecha del evento, así que no depende de la clase del módulo
 * Eventos (otro integrante).
 */
public record EventoOpcion(int id, String nombre, LocalDate fecha) {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Texto que muestra el ComboBox. */
    @Override
    public String toString() {
        return fecha == null
                ? nombre
                : nombre + " (" + fecha.format(FORMATO_FECHA) + ")";
    }
}
