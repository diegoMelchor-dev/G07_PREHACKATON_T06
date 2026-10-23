
package vallegrande.edu.pe.cuyconectacrud;

import vallegrande.edu.pe.cuyconectacrud.View.VentanaInicio;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println(
                        "Se usará el estilo predeterminado de Java."
                );
            }

            VentanaInicio ventana = new VentanaInicio();
            ventana.setVisible(true);
        });
    }
}
