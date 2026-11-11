package vallegrande.edu.pe.cuyconectacrud;

import javafx.application.Application;

/**
 * Punto de entrada del módulo Inscripciones.
 *
 * La clase con main() no puede extender Application en un proyecto
 * no modular; por eso este launcher es el que se ejecuta (clic en
 * el triángulo verde de IntelliJ) y delega en InscripcionesApp.
 */
public class LauncherInscripciones {

    public static void main(String[] args) {
        Application.launch(InscripcionesApp.class, args);
    }
}
