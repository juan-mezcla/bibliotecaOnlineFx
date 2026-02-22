package com.juan.model;

import javax.persistence.*; // Persistencia estándar para Hibernate
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad User: Representa a los usuarios del sistema (Estudiantes y Administradores).
 */
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser; // Clave primaria autoincremental

    @Column(unique = true, nullable = false)
    private String usuario; // Nombre de usuario único para el login

    private String nombre;
    private String apellidos;

    @Column(unique = true)
    private String email; // Email único para evitar registros duplicados

    private String password;

    private LocalDate fechaCreacion;

    private boolean admin; // Flag para diferenciar privilegios (true=admin, false=estudiante)

    /**
     * RELACIÓN BIDIRECCIONAL:
     * mappedBy = "estudiante": Indica que el dueño de la relación es la clase Libro.
     * cascade = CascadeType.ALL: Si se borra el usuario, se gestionan sus vínculos.
     * fetch = FetchType.LAZY: Los libros no se cargan por defecto para ahorrar memoria,
     * a menos que usemos un JOIN FETCH en el DAO.
     */
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Libro> librosPrestados = new ArrayList<>();

    // Constructor vacío: Obligatorio para que JPA pueda instanciar la clase al consultar la BD
    public User() {
    }

    /**
     * Constructor para nuevos registros (Registro de usuarios).
     * No incluye el ID porque lo genera la base de datos automáticamente.
     */
    public User(String usuario, String nombre, String apellidos, String email, String password, boolean admin) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.fechaCreacion = LocalDate.now(); // Marca de tiempo automática en el registro
    }

    /**
     * Constructor completo para edición o recuperación de datos.
     */
    public User(int idUser, String usuario, String nombre, String apellidos, String email, String password, 
                LocalDate fechaCreacion, boolean admin, List<Libro> librosPrestados) {
        this.idUser = idUser;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.fechaCreacion = fechaCreacion;
        this.admin = admin;
        this.librosPrestados = librosPrestados;
    }

    // --- Getters y Setters ---

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    /**
     * Obtiene la lista de libros que el usuario tiene actualmente.
     * Nota: Si se accede a esto fuera de una transacción con FetchType.LAZY,
     * podría lanzar una LazyInitializationException si no se usó JOIN FETCH.
     */
    public List<Libro> getLibrosPrestados() { return librosPrestados; }
    public void setLibrosPrestados(List<Libro> librosPrestados) { this.librosPrestados = librosPrestados; }
}