package com.juan.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import com.juan.model.Libro;
import com.juan.model.User;

/**
 * Implementación de la interfaz DAO para la gestión de la biblioteca.
 * Utiliza JPA (Hibernate) para la persistencia de datos.
 */
public class DaoBlibliotecaImpl implements DaoBliblioteca {

    /**
     * Registra un nuevo libro en la base de datos.
     * @param libro Objeto libro con los datos iniciales.
     * @return true si se guardó con éxito, false en caso contrario.
     */
    @Override
    public boolean insertarLibro(Libro libro) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(libro); // Guarda el objeto nuevo en la BD
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback(); // Deshace cambios si hay error
            e.printStackTrace();
            return false;
        } finally {
            em.close(); // Siempre cerrar el EntityManager
        }
    }

    /**
     * Registra un nuevo usuario (estudiante) en el sistema.
     */
    @Override
    public boolean insertarUsuario(User user) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Actualiza cualquier cambio en la entidad Libro (título, género, imagen, etc).
     */
    @Override
    public boolean actualizarLibro(Libro libro) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(libro); // Sincroniza el estado del objeto con la BD
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Valida las credenciales de un usuario.
     * @return El ID del usuario si es válido, -1 si falla o no existe.
     */
    @Override
    public int comprobarUsuario(String email, String password) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String select = "SELECT u FROM User u WHERE u.email = :email AND u.password = :pass";
            TypedQuery<User> query = em.createQuery(select, User.class);
            query.setParameter("email", email);
            query.setParameter("pass", password);
            
            User user = query.getSingleResult();
            return user.getIdUser(); 
        } catch (Exception e) {
            return -1; 
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene un usuario completo por su ID.
     * Se usa FETCH para cargar la lista de libros prestados y evitar LazyInitializationException.
     */
    @Override
    public User seleccionarUser(int idUser) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String select = "SELECT u FROM User u " +
                            "LEFT JOIN FETCH u.librosPrestados " +
                            "WHERE u.idUser = :id";
            TypedQuery<User> query = em.createQuery(select, User.class);
            query.setParameter("id", idUser);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    /**
     * Obtiene todos los libros con sus relaciones de usuario cargadas.
     * Es vital el LEFT JOIN FETCH para l.estudiante y l.usuarioReserva.
     */
    @Override
    public List<Libro> obtenerLibros() {
        EntityManager em = JpaUtil.getEntityManager();
        List<Libro> libros = new ArrayList<>(); 
        try {
            String select = "SELECT l FROM Libro l " + 
                            "LEFT JOIN FETCH l.estudiante " +
                            "LEFT JOIN FETCH l.usuarioReserva";
            libros = em.createQuery(select, Libro.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
        return libros;
    }

    /**
     * Filtra libros que están marcados como prestados actualmente.
     */
    @Override
    public List<Libro> obtenerLibrosPrestados() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String select = "SELECT l From Libro l " +
                            "LEFT JOIN FETCH l.estudiante " +
                            "WHERE l.prestado = :prestado";
            TypedQuery<Libro> query = em.createQuery(select, Libro.class);
            query.setParameter("prestado", true);        
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Filtra libros que tienen una reserva activa (alguien esperando).
     */
    @Override
    public List<Libro> obtenerLibrosReservados() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String select = "SELECT l From Libro l " +
                            "LEFT JOIN FETCH l.usuarioReserva " +
                            "WHERE l.reservado = :reservado";
            TypedQuery<Libro> query = em.createQuery(select, Libro.class);
            query.setParameter("reservado", true);        
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Busca libros por su género literario.
     */
    @Override
    public List<Libro> obtenerLibroPorGenero(String genero) {
       EntityManager em = JpaUtil.getEntityManager();
        try {
            String select = "SELECT l From Libro l " +
                            "LEFT JOIN FETCH l.estudiante " +
                            "WHERE l.genero = :genero";
            TypedQuery<Libro> query = em.createQuery(select, Libro.class);
            query.setParameter("genero", genero);        
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Devuelve la lista de libros que un usuario específico tiene en su poder físicamente.
     */
    @Override
    public List<Libro> obtenerLibroPorUsuario(User user) {
       EntityManager em = JpaUtil.getEntityManager();
        try {
            String select = "SELECT l From Libro l " +
                            "LEFT JOIN FETCH l.estudiante " +
                            "WHERE l.estudiante = :estudiante";
            TypedQuery<Libro> query = em.createQuery(select, Libro.class);
            query.setParameter("estudiante", user);        
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Realiza un préstamo directo (de estantería a usuario).
     */
    @Override
    public boolean prestarLibro(String ISBN, User user, LocalDate limitePrestamo) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Libro libro = em.find(Libro.class, ISBN);
            User userGestionado = em.merge(user); // Traer el usuario al contexto de JPA

            if (libro != null && !libro.isPrestado()) {
                libro.setEstudiante(userGestionado);
                libro.setLimitePrestamo(limitePrestamo);
                libro.setPrestado(true);
                libro.setReservado(false); // Por seguridad, quitamos reserva si existiera
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Registra a un usuario en la cola de espera de un libro ya prestado.
     * No borra al poseedor actual; solo rellena la columna 'id_reserva'.
     */
    @Override
    public boolean reservarLibro(String ISBN, User user) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Libro libro = em.find(Libro.class, ISBN);
            User userGestionado = em.merge(user);

            // Solo reservable si está prestado por otro y no hay otra reserva ya
            if (libro != null && libro.isPrestado() && libro.getUsuarioReserva() == null) {
                libro.setUsuarioReserva(userGestionado);
                libro.setReservado(true);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Procesa la devolución. Si hay reserva, hace el traspaso automático de dueño.
     */
    @Override
    public boolean devolverLibro(String ISBN) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Libro libro = em.find(Libro.class, ISBN);

            if (libro != null) {
                if (libro.getUsuarioReserva() != null) {
                    // TRASPASO: El que esperaba pasa a ser el dueño actual
                    libro.setEstudiante(libro.getUsuarioReserva()); 
                    libro.setUsuarioReserva(null); 
                    libro.setReservado(false);
                    libro.setPrestado(true);
                    libro.setLimitePrestamo(LocalDate.now().plusDays(15));
                } else {
                    // LIBERACIÓN: Nadie espera, el libro queda vacío
                    libro.setEstudiante(null);
                    libro.setPrestado(false);
                    libro.setLimitePrestamo(null);
                }
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Extiende la fecha límite de un libro prestado.
     */
    @Override
    public boolean alargarPrestamo(String isbn, int dias) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Libro libro = em.find(Libro.class, isbn);
            if (libro != null && libro.isPrestado()) {
                libro.setLimitePrestamo(libro.getLimitePrestamo().plusDays(dias));
                em.merge(libro);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina físicamente un libro del catálogo.
     */
    @Override
    public boolean eliminarLibro(String isbn) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Libro libro = em.find(Libro.class, isbn);
            if (libro != null) {
                em.remove(libro);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Busca un único libro por su clave primaria (ISBN).
     */
    @Override
    public Libro seleccionarLibro(String ISBN) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT l FROM Libro l " +
                          "LEFT JOIN FETCH l.estudiante " +
                          "LEFT JOIN FETCH l.usuarioReserva " +
                          "WHERE l.isbn = :isbn";
            return em.createQuery(jpql, Libro.class)
                     .setParameter("isbn", ISBN)
                     .getSingleResult();
        } catch (Exception e) { 
            return null; 
        } finally {
            em.close();
        }
    }
}