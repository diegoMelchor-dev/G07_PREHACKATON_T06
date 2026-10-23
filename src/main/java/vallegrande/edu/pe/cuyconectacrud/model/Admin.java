package vallegrande.edu.pe.cuyconectacrud.model;

public class Admin {
    private int id;
    private String correo;
    private boolean activo;
    private String fechaCreacion;

    public Admin(int id, String correo, boolean activo,
                 String fechaCreacion) {
        this.id = id;
        this.correo = correo;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Correo: " + correo
                + " | Activo: " + (activo ? "Sí" : "No")
                + " | Creado: " + fechaCreacion;
    }
}
