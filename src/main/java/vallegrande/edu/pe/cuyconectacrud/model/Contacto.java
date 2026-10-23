
package vallegrande.edu.pe.cuyconectacrud.model;

import java.sql.Timestamp;

public class Contacto {

    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String motivo;
    private String mensaje;
    private Timestamp fecha;

    public Contacto(int id, String nombre, String correo,
                    String telefono, String motivo,
                    String mensaje, Timestamp fecha) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.motivo = motivo;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Timestamp getFecha() {
        return fecha;
    }
}
