package vallegrande.edu.pe.g07_prehackaton_t06.model;

import java.time.LocalDate;

public class Evento {
    private int idEvento;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private String lugar;
    private String modalidad;
    private String estado;

    public Evento() {
    }

    public Evento(int idEvento, String nombre, String descripcion, LocalDate fecha,
                  String lugar, String modalidad, String estado) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.modalidad = modalidad;
        this.estado = estado;
    }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
