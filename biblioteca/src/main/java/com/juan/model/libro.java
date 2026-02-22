package com.juan.model;

import javax.persistence.*; 
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad Libro: Representa un libro en el sistema de biblioteca.
 * Se ha diseñado para gestionar estados de préstamo y reserva de forma simultánea.
 */
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    private String isbn; // Clave primaria única.
    private String titulo;
    private String genero;

    @Lob
    @Column(columnDefinition = "LONGBLOB") 
    private byte[] imagen; // Almacena la portada en formato binario.

    private LocalDate fechaPublicacion;
    private LocalDate limitePrestamo; // Fecha en la que el poseedor actual debe devolver el libro.

    @CollectionTable(name = "libro_autores", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "autor")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> autores; // Relación 1-N para autores sin necesidad de crear otra entidad.

    /* * Banderas de estado: 
     * Permiten filtrar rápidamente en la base de datos sin cargar los objetos User.
     */
    private boolean reservado;
    private boolean prestado;

    /**
     * CAMPO CLAVE 1: El Poseedor Actual.
     * Representa al usuario que tiene el libro FÍSICAMENTE.
     * Al estar separado del campo de reserva, este usuario puede mantener su 
     * sesión de "Mis Libros" hasta que pulse "Devolver", aunque otro lo reserve.
     */
    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private User estudiante;

    /**
     * CAMPO CLAVE 2: El Solicitante en Espera.
     * Esta es la solución al problema anterior. Aquí guardamos al Usuario B que pulsa "Reservar".
     * Así, el sistema sabe que el libro tiene dos dueños en diferentes contextos:
     * - Estudiante: Lo está leyendo.
     * - UsuarioReserva: Está haciendo cola para leerlo.
     */
    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private User usuarioReserva;

    // Constructor vacío requerido por JPA
    public Libro() {
    }

    // Constructor completo
    public Libro(String isbn, String titulo, String genero, byte[] imagen, 
                 LocalDate fechaPublicacion, LocalDate limitePrestamo, 
                 List<String> autores, boolean reservado, boolean prestado, User estudiante) {
        
        this.isbn = isbn;
        this.titulo = titulo;
        this.genero = genero;
        this.imagen = imagen;
        this.fechaPublicacion = fechaPublicacion;
        this.limitePrestamo = limitePrestamo;
        this.autores = autores;
        this.reservado = reservado;
        this.prestado = prestado;
        this.estudiante = estudiante;
    }

    // --- Getters y Setters ---

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn){this.isbn=isbn;}

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public byte[] getImagen() { return imagen; }
    public void setImagen(byte[] imagen) { this.imagen = imagen; }

    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public LocalDate getLimitePrestamo() { return limitePrestamo; }
    public void setLimitePrestamo(LocalDate limitePrestamo) { this.limitePrestamo = limitePrestamo; }

    public List<String> getAutores() { return autores; }
    public void setAutores(List<String> autores) { this.autores = autores; }

    public boolean isReservado() { return reservado; }
    public void setReservado(boolean reservado) { this.reservado = reservado; }

    public boolean isPrestado() { return prestado; }
    public void setPrestado(boolean prestado) { this.prestado = prestado; }

    public User getEstudiante() { return estudiante; }
    public void setEstudiante(User estudiante) { this.estudiante = estudiante; }

    public User getUsuarioReserva() { return usuarioReserva; }
    public void setUsuarioReserva(User usuarioReserva) { this.usuarioReserva = usuarioReserva; }
}