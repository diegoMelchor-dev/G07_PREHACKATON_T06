
package vallegrande.edu.pe.cuyconectacrud.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaInicio extends JFrame {

    // Paleta elegante: verde bosque, dorado y tonos crema.
    private static final Color VERDE_BOSQUE = new Color(20, 54, 41);
    private static final Color VERDE_OSCURO = new Color(29, 76, 55);
    private static final Color VERDE_MEDIO = new Color(44, 101, 70);
    private static final Color DORADO = new Color(201, 164, 78);
    private static final Color DORADO_CLARO = new Color(239, 221, 166);

    private static final Color FONDO = new Color(246, 246, 240);
    private static final Color BLANCO = new Color(255, 255, 252);
    private static final Color TEXTO = new Color(249, 248, 240);
    private static final Color TEXTO_SUAVE = new Color(216, 225, 215);
    private static final Color TEXTO_OSCURO = new Color(45, 58, 48);
    private static final Color GRIS = new Color(112, 120, 111);

    private static VentanaInicio instancia;

    private boolean transicionandoModulo = false;

    private VentanaAdministradores ventanaAdministradores;
    private VentanaPedidos ventanaPedidos;
    private VentanaContactos ventanaContactos;
    private VentanaReportes ventanaReportes;

    public VentanaInicio() {
        instancia = this;

        setTitle("CuyConecta | Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (instancia == VentanaInicio.this) {
                    instancia = null;
                }
            }
        });

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        raiz.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(raiz);
    }

    public static void mostrarInicio() {
        if (instancia != null && instancia.isDisplayable()) {
            instancia.setVisible(true);
            instancia.setState(JFrame.NORMAL);
            instancia.toFront();
            instancia.requestFocus();
        } else {
            VentanaInicio nueva = new VentanaInicio();
            nueva.setVisible(true);
        }
    }

    public static VentanaInicio obtenerInstancia() {
        return instancia;
    }

    private void navegar(String destino) {
        switch (destino) {
            case "Inicio":
                mostrarInicio();
                break;
            case "Administradores":
                abrirAdministradores();
                break;
            case "Compras / Pedidos":
            case "Pedidos":
                abrirPedidos();
                break;
            case "Contactos":
                abrirContactos();
                break;
            case "Reportes":
                abrirReportes();
                break;
            default:
                mostrarPendiente(destino);
                break;
        }
    }

    public static void navegarDesdeModulo(
            String destino, JFrame ventanaActual) {

        VentanaInicio inicio = instancia;

        if (inicio == null || !inicio.isDisplayable()) {
            JOptionPane.showMessageDialog(
                    ventanaActual,
                    "La ventana principal ya no está disponible.",
                    "Error de navegación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (inicio.transicionandoModulo) {
            return;
        }

        inicio.transicionandoModulo = true;

        try {
            if ("Inicio".equals(destino)) {
                inicio.setVisible(true);
                inicio.setState(JFrame.NORMAL);
                inicio.toFront();
                inicio.requestFocus();

                if (ventanaActual != null && ventanaActual != inicio) {
                    ventanaActual.dispose();
                }
                return;
            }

            inicio.navegar(destino);

            if (ventanaActual != null && ventanaActual != inicio) {
                ventanaActual.dispose();
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    ventanaActual,
                    "No se pudo abrir el módulo " + destino + ":\n"
                            + ex.getMessage(),
                    "Error de navegación",
                    JOptionPane.ERROR_MESSAGE
            );

            inicio.setVisible(true);
            inicio.setState(JFrame.NORMAL);
            inicio.toFront();

        } finally {
            inicio.transicionandoModulo = false;
        }
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(18, 22, 12, 22));

        // Encabezado compacto.
        JPanel encabezado = new JPanel(new BorderLayout(16, 8));
        encabezado.setBackground(VERDE_BOSQUE);
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DORADO, 1),
                new EmptyBorder(17, 22, 17, 22)
        ));

        JPanel textosEncabezado = new JPanel();
        textosEncabezado.setOpaque(false);
        textosEncabezado.setLayout(
                new BoxLayout(textosEncabezado, BoxLayout.Y_AXIS)
        );

        JLabel etiqueta = new JLabel("SISTEMA DE GESTIÓN");
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11));
        etiqueta.setForeground(DORADO_CLARO);

        JLabel titulo = new JLabel("CuyConecta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 29));
        titulo.setForeground(TEXTO);

        JLabel descripcion = new JLabel(
                "Administra tus procesos desde un solo lugar."
        );
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descripcion.setForeground(TEXTO_SUAVE);

        textosEncabezado.add(etiqueta);
        textosEncabezado.add(Box.createVerticalStrut(5));
        textosEncabezado.add(titulo);
        textosEncabezado.add(Box.createVerticalStrut(5));
        textosEncabezado.add(descripcion);

        JLabel marca = new JLabel("CC");
        marca.setFont(new Font("Serif", Font.BOLD, 26));
        marca.setForeground(DORADO_CLARO);
        marca.setHorizontalAlignment(SwingConstants.CENTER);
        marca.setPreferredSize(new Dimension(64, 64));
        marca.setOpaque(true);
        marca.setBackground(VERDE_OSCURO);
        marca.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DORADO, 2),
                new EmptyBorder(4, 4, 4, 4)
        ));

        encabezado.add(textosEncabezado, BorderLayout.CENTER);
        encabezado.add(marca, BorderLayout.EAST);

        // Sección de módulos.
        JPanel zona = new JPanel(new BorderLayout(0, 13));
        zona.setOpaque(false);

        JPanel tituloModulos = new JPanel();
        tituloModulos.setOpaque(false);
        tituloModulos.setLayout(
                new BoxLayout(tituloModulos, BoxLayout.Y_AXIS)
        );

        JLabel modulos = new JLabel("Módulos del sistema");
        modulos.setFont(new Font("SansSerif", Font.BOLD, 21));
        modulos.setForeground(VERDE_BOSQUE);

        JLabel ayuda = new JLabel(
                "Selecciona una opción para comenzar."
        );
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ayuda.setForeground(GRIS);

        JPanel lineaDorada = new JPanel();
        lineaDorada.setBackground(DORADO);
        lineaDorada.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 2)
        );
        lineaDorada.setPreferredSize(new Dimension(100, 2));

        tituloModulos.add(modulos);
        tituloModulos.add(Box.createVerticalStrut(4));
        tituloModulos.add(ayuda);
        tituloModulos.add(Box.createVerticalStrut(9));
        tituloModulos.add(lineaDorada);

        // Cuatro tarjetas distribuidas en dos filas.
        JPanel tarjetas = new JPanel(new GridLayout(2, 2, 14, 14));
        tarjetas.setOpaque(false);

        tarjetas.add(crearTarjeta(
                "Administradores",
                "Gestiona los usuarios y sus accesos al sistema.",
                "ABRIR MÓDULO",
                "01",
                this::abrirAdministradores
        ));

        tarjetas.add(crearTarjeta(
                "Compras / Pedidos",
                "Consulta y administra los pedidos registrados.",
                "ABRIR MÓDULO",
                "02",
                this::abrirPedidos
        ));

        tarjetas.add(crearTarjeta(
                "Contactos",
                "Registra, consulta y administra los contactos.",
                "ABRIR MÓDULO",
                "03",
                this::abrirContactos
        ));

        tarjetas.add(crearTarjeta(
                "Reportes",
                "Consulta los reportes disponibles del sistema.",
                "ABRIR MÓDULO",
                "04",
                this::abrirReportes
        ));

        zona.add(tituloModulos, BorderLayout.NORTH);
        zona.add(tarjetas, BorderLayout.CENTER);

        JLabel pie = new JLabel("CUYCONECTA | SISTEMA DE GESTIÓN");
        pie.setFont(new Font("SansSerif", Font.BOLD, 10));
        pie.setForeground(GRIS);

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(zona, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);

        return contenido;
    }

    private JPanel crearTarjeta(
            String titulo,
            String descripcion,
            String textoBoton,
            String numero,
            Runnable accion) {

        JPanel tarjeta = new JPanel(new BorderLayout(0, 10));
        tarjeta.setBackground(BLANCO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(224, 220, 202), 1),
                new EmptyBorder(13, 16, 13, 16)
        ));

        JPanel franja = new JPanel(new BorderLayout());
        franja.setBackground(BLANCO);
        franja.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel insignia = new JLabel("  " + numero + "  ");
        insignia.setFont(new Font("SansSerif", Font.BOLD, 11));
        insignia.setForeground(VERDE_BOSQUE);
        insignia.setOpaque(true);
        insignia.setBackground(new Color(246, 239, 218));
        insignia.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(DORADO, 1),
                new EmptyBorder(4, 4, 4, 4)
        ));

        JPanel linea = new JPanel();
        linea.setBackground(DORADO);
        linea.setPreferredSize(new Dimension(35, 2));

        franja.add(insignia, BorderLayout.WEST);
        franja.add(linea, BorderLayout.SOUTH);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(textos, BoxLayout.Y_AXIS)
        );

        JLabel nombre = new JLabel(titulo);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 17));
        nombre.setForeground(VERDE_BOSQUE);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detalle = new JLabel(
                "<html><div style='width:250px'>"
                        + descripcion
                        + "</div></html>"
        );
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detalle.setForeground(GRIS);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(nombre);
        textos.add(Box.createVerticalStrut(7));
        textos.add(detalle);

        JButton boton = new JButton(textoBoton + "   →");
        boton.setFont(new Font("SansSerif", Font.BOLD, 11));
        boton.setForeground(DORADO_CLARO);
        boton.setBackground(VERDE_BOSQUE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
        ));
        boton.setBorder(new EmptyBorder(10, 12, 10, 12));
        boton.setPreferredSize(new Dimension(175, 38));
        boton.setHorizontalAlignment(SwingConstants.CENTER);

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(VERDE_MEDIO);
                boton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(VERDE_BOSQUE);
                boton.setForeground(DORADO_CLARO);
            }
        });

        boton.addActionListener(e -> accion.run());

        tarjeta.add(franja, BorderLayout.NORTH);
        tarjeta.add(textos, BorderLayout.CENTER);
        tarjeta.add(boton, BorderLayout.SOUTH);

        return tarjeta;
    }

    private void abrirAdministradores() {
        if (ventanaAdministradores != null
                && ventanaAdministradores.isDisplayable()) {
            mostrarVentana(ventanaAdministradores);
            return;
        }

        ventanaAdministradores = new VentanaAdministradores();
        ventanaAdministradores.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        ventanaAdministradores.setLocationRelativeTo(this);

        ventanaAdministradores.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaAdministradores = null;
                if (!transicionandoModulo) {
                    regresarAlInicio();
                }
            }
        });

        setVisible(false);
        ventanaAdministradores.setVisible(true);
    }

    private void abrirPedidos() {
        if (ventanaPedidos != null && ventanaPedidos.isDisplayable()) {
            mostrarVentana(ventanaPedidos);
            return;
        }

        ventanaPedidos = new VentanaPedidos();
        ventanaPedidos.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        ventanaPedidos.setLocationRelativeTo(this);

        ventanaPedidos.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaPedidos = null;
                if (!transicionandoModulo) {
                    regresarAlInicio();
                }
            }
        });

        setVisible(false);
        ventanaPedidos.setVisible(true);
    }

    private void abrirContactos() {
        if (ventanaContactos != null && ventanaContactos.isDisplayable()) {
            mostrarVentana(ventanaContactos);
            return;
        }

        ventanaContactos = new VentanaContactos();
        ventanaContactos.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        ventanaContactos.setLocationRelativeTo(this);

        ventanaContactos.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaContactos = null;
                if (!transicionandoModulo) {
                    regresarAlInicio();
                }
            }
        });

        setVisible(false);
        ventanaContactos.setVisible(true);
    }

    private void abrirReportes() {
        if (ventanaReportes != null && ventanaReportes.isDisplayable()) {
            mostrarVentana(ventanaReportes);
            return;
        }

        ventanaReportes = new VentanaReportes();
        ventanaReportes.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );
        ventanaReportes.setLocationRelativeTo(this);

        ventanaReportes.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaReportes = null;
                if (!transicionandoModulo) {
                    regresarAlInicio();
                }
            }
        });

        setVisible(false);
        ventanaReportes.setVisible(true);
    }

    private void mostrarVentana(JFrame ventana) {
        setVisible(false);
        ventana.setVisible(true);
        ventana.setState(JFrame.NORMAL);
        ventana.toFront();
        ventana.requestFocus();
    }

    private void regresarAlInicio() {
        if (!isDisplayable() || transicionandoModulo) {
            return;
        }

        setVisible(true);
        setState(JFrame.NORMAL);
        toFront();
        requestFocus();
    }

    private void mostrarPendiente(String modulo) {
        JOptionPane.showMessageDialog(
                this,
                "El módulo " + modulo + " todavía no está disponible.",
                "CuyConecta",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void navegarDesdePanel(String destino) {
        navegar(destino);
    }
}
