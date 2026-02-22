package com.juan.model;
import javax.persistence.*; 
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    private String isbn;
    private String titulo;
    private String genero;

    @Lob
    @Column(columnDefinition = "LONGBLOB") 
    private byte[] imagen;

    private LocalDate fechaPublicacion;
    private LocalDate limitePrestamo;

    @CollectionTable(name = "libro_autores", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "autor")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> autores;

    private boolean reservado;
    private boolean prestado;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private User estudiante;

    @ManyToOne
@JoinColumn(name = "id_reserva")
private User usuarioReserva;

    public Libro() {
    }

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