package vallegrande.edu.pe.cuyconectacrud.model;

/**
 * Elemento liviano para el ComboBox de participantes.
 *
 * Solo lleva lo necesario para elegir a la persona (id, nombre
 * completo y DNI), sin depender de la clase Participante del
 * módulo del Integrante 2.
 */
public record ParticipanteOpcion(int id, String nombreCompleto, String dni) {

    /** Texto que muestra el ComboBox. */
    @Override
    public String toString() {
        return nombreCompleto + " — DNI " + dni;
    }
}
