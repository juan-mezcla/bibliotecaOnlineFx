package com.juan.model;

import javax.persistence.*; // O jakarta.persistence si usas Hibernate 6+
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser;

    @Column(unique = true, nullable = false)
    private String usuario;

    private String nombre;
    private String apellidos;

    @Column(unique = true)
    private String email;

    private String password;

    private LocalDate fechaCreacion;

    private boolean admin;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Libro> librosPrestados = new ArrayList<>();

    public User() {
    }

    public User(String usuario, String nombre, String apellidos, String email, String password, boolean admin) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.fechaCreacion = LocalDate.now(); // Se asigna automáticamente al crearlo
    }

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

    public List<Libro> getLibrosPrestados() { return librosPrestados; }
    public void setLibrosPrestados(List<Libro> librosPrestados) { this.librosPrestados = librosPrestados; }
}