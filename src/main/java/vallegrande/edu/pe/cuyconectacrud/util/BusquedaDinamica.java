package vallegrande.edu.pe.cuyconectacrud.util;

import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Búsqueda dinámica reutilizable para cualquier JTextField.
 *
 * Escucha el evento KeyReleased y ejecuta la acción indicada
 * cuando el usuario deja de escribir durante {@code retrasoMs}
 * milisegundos (debounce). Así no se lanza una consulta a la base
 * de datos por cada tecla pulsada.
 *
 * Uso:
 * <pre>
 *     new BusquedaDinamica(txtBuscar, 300, () -> cargarDatos());
 * </pre>
 */
public class BusquedaDinamica {

    private final Timer temporizador;

    public BusquedaDinamica(JTextField campo, int retrasoMs, Runnable accion) {
        this.temporizador = new Timer(retrasoMs, e -> accion.run());
        this.temporizador.setRepeats(false);

        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (esTeclaIgnorada(e.getKeyCode())) {
                    return;
                }
                temporizador.restart();
            }
        });
    }

    /** Cancela una búsqueda pendiente (útil al cerrar la ventana). */
    public void cancelar() {
        temporizador.stop();
    }

    // Teclas que no modifican el texto del campo.
    private boolean esTeclaIgnorada(int codigo) {
        return codigo == KeyEvent.VK_ENTER
                || codigo == KeyEvent.VK_SHIFT
                || codigo == KeyEvent.VK_CONTROL
                || codigo == KeyEvent.VK_ALT
                || codigo == KeyEvent.VK_TAB
                || codigo == KeyEvent.VK_LEFT
                || codigo == KeyEvent.VK_RIGHT
                || codigo == KeyEvent.VK_UP
                || codigo == KeyEvent.VK_DOWN
                || codigo == KeyEvent.VK_HOME
                || codigo == KeyEvent.VK_END;
    }
}
