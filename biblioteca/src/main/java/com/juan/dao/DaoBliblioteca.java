package com.juan.dao;

import java.time.LocalDate;
import java.util.List;

import com.juan.model.Libro;
import com.juan.model.User;

public interface DaoBliblioteca {
    boolean insertarLibro(Libro libro);
    boolean insertarUsuario(User user);
    boolean actualizarLibro(Libro libro);


    int comprobarUsuario(String email,String password);
    User seleccionarUser(int idUser);
    Libro seleccionarLibro(String ISBN);

    List<Libro> obteneLibros();
    List<Libro> obtenerLibrosPrestados();
    List<Libro> obtenerLibrosReservados();
    List<Libro> obtenerLibroPorGenero(String genero);
    List<Libro> obtenerLibroPorUsuario(User user);

    boolean prestarLibro(String ISBN, User user, LocalDate limitePrestamo);
    boolean reservarLibro(String ISBN,User user);
    boolean devolverLibro(String ISBN);
}
