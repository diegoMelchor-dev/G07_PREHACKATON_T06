package vallegrande.edu.pe.cuyconectacrud;

import javafx.application.Application;

/**
 * Punto de entrada alternativo del módulo Participantes.
 *
 * Permite ejecutar la app JavaFX sin el plugin de JavaFX:
 * la clase con main() no puede extender Application en un
 * proyecto no modular. El javafx-maven-plugin está configurado
 * con esta clase como mainClass.
 */
public class LauncherParticipantes {

    public static void main(String[] args) {
        Application.launch(ParticipantesApp.class, args);
    }
}
