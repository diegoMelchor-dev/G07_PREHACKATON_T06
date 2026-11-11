package vallegrande.edu.pe.cuyconectacrud.util;

import vallegrande.edu.pe.cuyconectacrud.model.Participante;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validaciones del formulario de participantes (Integrante 2).
 *
 * Cada método devuelve el mensaje de error o {@code null} cuando
 * el dato es correcto, para poder mostrar alertas claras.
 */
public class ValidadorParticipante {

    private static final Pattern PATRON_DNI =
            Pattern.compile("\\d{8}");

    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("9\\d{8}");

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    /** Verifica que un campo obligatorio no esté vacío. */
    public static String validarObligatorio(String etiqueta, String valor) {
        if (valor == null || valor.isBlank()) {
            return "El campo " + etiqueta + " es obligatorio.";
        }
        return null;
    }

    /** DNI peruano: exactamente 8 dígitos numéricos. */
    public static String validarDni(String dni) {
        if (dni == null || dni.isBlank()) {
            return "El campo DNI es obligatorio.";
        }
        if (!PATRON_DNI.matcher(dni.trim()).matches()) {
            return "El DNI debe tener exactamente 8 dígitos numéricos.";
        }
        return null;
    }

    /** Teléfono móvil peruano: 9 dígitos y empieza con 9. */
    public static String validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return "El campo teléfono es obligatorio.";
        }
        if (!PATRON_TELEFONO.matcher(telefono.trim()).matches()) {
            return "El teléfono debe tener 9 dígitos y comenzar con 9.";
        }
        return null;
    }

    /** Correo con formato válido (usuario@dominio). */
    public static String validarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return "El campo correo es obligatorio.";
        }
        if (!PATRON_CORREO.matcher(correo.trim()).matches()) {
            return "El correo no tiene un formato válido (ej.: nombre@dominio.com).";
        }
        return null;
    }

    /**
     * Valida todos los campos del participante.
     *
     * @return lista de mensajes de error; vacía si todo es válido.
     */
    public static List<String> validar(Participante p) {
        List<String> errores = new ArrayList<>();

        agregarSi(errores, validarObligatorio("nombres", p.getNombres()));
        agregarSi(errores, validarObligatorio("apellidos", p.getApellidos()));
        agregarSi(errores, validarDni(p.getDni()));
        agregarSi(errores, validarCorreo(p.getCorreo()));
        agregarSi(errores, validarTelefono(p.getTelefono()));
        agregarSi(errores, validarObligatorio("institución", p.getInstitucion()));

        return errores;
    }

    private static void agregarSi(List<String> errores, String mensaje) {
        if (mensaje != null) {
            errores.add(mensaje);
        }
    }
}
