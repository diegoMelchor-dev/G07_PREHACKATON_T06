
package vallegrande.edu.pe.cuyconectacrud.View;

import vallegrande.edu.pe.cuyconectacrud.model.Admin;
import vallegrande.edu.pe.cuyconectacrud.model.AdminDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class FormularioAdmin extends JDialog {

    private final AdminDAO dao;
    private final Admin adminOriginal;

    private final Color verdeOscuro = new Color(22, 67, 49);
    private final Color verde = new Color(38, 128, 82);
    private final Color fondo = new Color(241, 247, 242);
    private final Color texto = new Color(35, 58, 45);
    private final Color borde = new Color(205, 220, 209);

    private final JTextField txtCorreo = new JTextField(25);
    private final JPasswordField txtPassword = new JPasswordField(25);
    private final JCheckBox chkActivo =
            new JCheckBox("Administrador activo", true);

    private boolean guardado = false;

    public FormularioAdmin(
            VentanaAdministradores propietario,
            AdminDAO dao,
            Admin admin
    ) {
        super(
                propietario,
                admin == null
                        ? "Nuevo administrador"
                        : "Editar administrador",
                true
        );

        this.dao = dao;
        this.adminOriginal = admin;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterfaz();
        pack();
        setMinimumSize(new Dimension(460, admin == null ? 350 : 330));
        setLocationRelativeTo(propietario);
        setResizable(false);
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(15, 18));
        principal.setBackground(fondo);
        principal.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel(
                adminOriginal == null
                        ? "Registrar administrador"
                        : "Editar administrador"
        );
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(verdeOscuro);

        JLabel subtitulo = new JLabel(
                adminOriginal == null
                        ? "Completa los datos de la nueva cuenta."
                        : "Modifica el correo y el estado de la cuenta."
        );
        subtitulo.setForeground(new Color(91, 113, 97));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(7));
        encabezado.add(subtitulo);

        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 8));
        campos.setBackground(Color.WHITE);
        campos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel etiquetaCorreo = new JLabel("Correo electrónico");
        etiquetaCorreo.setForeground(texto);
        campos.add(etiquetaCorreo);

        configurarCampo(txtCorreo);
        campos.add(txtCorreo);

        if (adminOriginal == null) {
            JLabel etiquetaPassword = new JLabel("Contraseña");
            etiquetaPassword.setForeground(texto);
            campos.add(etiquetaPassword);

            configurarCampo(txtPassword);
            campos.add(txtPassword);
        }

        chkActivo.setBackground(Color.WHITE);
        chkActivo.setForeground(texto);
        chkActivo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (adminOriginal != null) {
            txtCorreo.setText(adminOriginal.getCorreo());
            chkActivo.setSelected(adminOriginal.isActivo());
        }

        campos.add(chkActivo);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        botones.setOpaque(false);

        JButton cancelar = crearBoton("Cancelar", verdeOscuro);
        JButton guardar = crearBoton(
                adminOriginal == null
                        ? "Crear administrador"
                        : "Guardar cambios",
                verde
        );

        cancelar.addActionListener(e -> dispose());
        guardar.addActionListener(e -> guardar());

        botones.add(cancelar);
        botones.add(guardar);

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(campos, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);

        setContentPane(principal);
        getRootPane().setDefaultButton(guardar);
    }

    private void configurarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(texto);
        campo.setBackground(new Color(250, 252, 250));
        campo.setCaretColor(verde);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private JButton crearBoton(String nombre, Color color) {
        JButton boton = new JButton(nombre);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBorder(new EmptyBorder(11, 15, 11, 15));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void guardar() {
        String correo = txtCorreo.getText().trim();
        String password = adminOriginal == null
                ? new String(txtPassword.getPassword())
                : "";

        if (correo.isEmpty()) {
            mostrarAviso("Ingresa el correo electrónico.");
            txtCorreo.requestFocus();
            return;
        }

        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            mostrarAviso("Ingresa un correo electrónico válido.");
            txtCorreo.requestFocus();
            return;
        }

        if (adminOriginal == null && password.isBlank()) {
            mostrarAviso(
                    "La contraseña es obligatoria para crear una cuenta."
            );
            txtPassword.requestFocus();
            return;
        }

        if (adminOriginal == null && password.length() < 8) {
            mostrarAviso(
                    "La contraseña debe tener al menos 8 caracteres."
            );
            txtPassword.requestFocus();
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                adminOriginal == null
                        ? "¿Crear el administrador " + correo + "?"
                        : "¿Guardar los cambios del administrador?",
                "Confirmar operación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean resultado;

            if (adminOriginal == null) {
                resultado = dao.crear(correo, password);
            } else {
                resultado = dao.actualizar(
                        adminOriginal.getId(),
                        correo,
                        chkActivo.isSelected()
                );
            }

            if (!resultado) {
                mostrarAviso(
                        "No se pudo guardar el administrador. "
                                + "Verifica si el correo ya existe o si "
                                + "la operación está permitida."
                );
                return;
            }

            guardado = true;

            JOptionPane.showMessageDialog(
                    this,
                    adminOriginal == null
                            ? "Administrador creado correctamente."
                            : "Administrador actualizado correctamente."
                            + "\nLa contraseña se ha mantenido sin cambios.",
                    "CuyConecta 3.0",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (SQLException | IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el administrador.\n"
                            + ex.getMessage(),
                    "Error al guardar",
                    JOptionPane.ERROR_MESSAGE
            );

            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Revisa los datos",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
