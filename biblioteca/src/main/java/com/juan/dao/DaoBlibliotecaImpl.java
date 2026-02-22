package com.juan.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import com.juan.model.Libro;
import com.juan.model.User;

public class DaoBlibliotecaImpl implements DaoBliblioteca{

    @Override
    public boolean insertarLibro(Libro libro) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(libro);
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

    @Override
    public boolean actualizarLibro(Libro libro) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            em.merge(libro);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
        return true;
    }

    @Override
    public int comprobarUsuario(String email, String password) {
        EntityManager em = JpaUtil.getEntityManager();
        User user=null;
        try {
            String select="SELECT u FROM User u WHERE u.email = :email AND u.password = :pass";
            
            TypedQuery<User> query = em.createQuery(select, User.class);

            query.setParameter("email", email);
            query.setParameter("pass", password);
            
            user = query.getSingleResult();
        } catch (Exception e) {
            
            return -1; 
        } finally {
            em.close();
        }
        return user.getIdUser(); 
    }

    @Override
public User seleccionarUser(int idUser) {
    EntityManager em = JpaUtil.getEntityManager();
    User user = null;
    try {
        // Aseguramos espacios correctos en el String
        String select = "SELECT u FROM User u " +
                        "LEFT JOIN FETCH u.librosPrestados " +
                        "WHERE u.idUser = :id";
        
        TypedQuery<User> query = em.createQuery(select, User.class);
        
        // ESTA ES LA LÍNEA CLAVE QUE TE FALTA:
        query.setParameter("id", idUser);

        user = query.getSingleResult();

    } catch (Exception e) {
        // Importante: imprime el error para saber qué pasa si vuelve a fallar
        System.err.println("Error en seleccionarUser: " + e.getMessage());
        e.printStackTrace(); 
        return null;
    } finally {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    return user;
}

    @Override
public List<Libro> obtenerLibros() {
    EntityManager em = JpaUtil.getEntityManager();
    // Inicializamos como lista vacía para evitar NullPointerException en el controlador
    List<Libro> libros = new ArrayList<>(); 
    try {
        
        String select = "SELECT l FROM Libro l " + 
                        "LEFT JOIN FETCH l.estudiante";
        
        libros = em.createQuery(select, Libro.class).getResultList();

    } catch (Exception e) {
        System.err.println("Error en obtenerLibros: " + e.getMessage());
        e.printStackTrace(); // Esto te dirá en la consola qué falló exactamente
    } finally {
        if (em != null) em.close();
    }
    return libros;
}

    @Override
    public List<Libro> obtenerLibrosPrestados() {
        EntityManager em = JpaUtil.getEntityManager();
        
        List<Libro> libros=null;
        try {
            String select="SELECT l From Libro l"+
                    "LEFT JOIN FETCH l.estudiante"+
                    "WHERE l.prestado= :prestado";

                    TypedQuery<Libro> query = em.createQuery(select, Libro.class);
                    query.setParameter("prestado", true);        

            libros=query.getResultList();

        } catch (Exception e) {
            System.out.println("error en consulta selectLibros");
        }

        return libros;
    }

    @Override
    public List<Libro> obtenerLibrosReservados() {
        EntityManager em = JpaUtil.getEntityManager();
        
        List<Libro> libros=null;
        try {
            String select="SELECT l From Libro l"+
                    "LEFT JOIN FETCH l.estudiante"+
                    "WHERE l.reservado= :reservado";
                    
                    TypedQuery<Libro> query = em.createQuery(select, Libro.class);
                    query.setParameter("reservado", true);        

            libros=query.getResultList();

        } catch (Exception e) {
            System.out.println("error en consulta selectLibros");
        }

        return libros;
    }

    @Override
    public List<Libro> obtenerLibroPorGenero(String genero) {
       EntityManager em = JpaUtil.getEntityManager();
        
        List<Libro> libros=null;
        try {
            String select="SELECT l From Libro l"+
                    "LEFT JOIN FETCH l.estudiante"+
                    "WHERE l.genero= :genero";
                    
                    TypedQuery<Libro> query = em.createQuery(select, Libro.class);
                    query.setParameter("genero", genero);        

            libros=query.getResultList();

        } catch (Exception e) {
            System.out.println("error en consulta selectLibros");
        }

        return libros;
    }

    @Override
    public List<Libro> obtenerLibroPorUsuario(User user) {
       EntityManager em = JpaUtil.getEntityManager();
        
        List<Libro> libros=null;
        try {
            String select="SELECT l From Libro l"+
                    "LEFT JOIN FETCH l.estudiante"+
                    "WHERE l.estudiante= :estudiante";
                    
                    TypedQuery<Libro> query = em.createQuery(select, Libro.class);
                    query.setParameter("estudiante", user);        

            libros=query.getResultList();

        } catch (Exception e) {
            System.out.println("error en consulta selectLibros");
        }

        return libros;
    }

    @Override
    public boolean prestarLibro(String ISBN, User user, LocalDate limitePrestamo) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx=em.getTransaction();
        
        try {
            tx.begin();

            Libro libro=em.find(Libro.class,ISBN);

            if (libro != null) {
                libro.setEstudiante(user);
                libro.setLimitePrestamo(limitePrestamo);
                libro.setPrestado(true);
                libro.setReservado(false);
                em.merge(libro);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }

        return true;
    }

    @Override
public boolean reservarLibro(String ISBN, User user) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        Libro libro = em.find(Libro.class, ISBN);
        User userGestionado = em.merge(user);

        // Se puede reservar si está prestado y nadie más lo ha reservado aún
        if (libro != null && libro.isPrestado() && libro.getUsuarioReserva() == null) {
            libro.setUsuarioReserva(userGestionado);
            libro.setReservado(true);
            em.getTransaction().commit();
            return true;
        }
        return false;
    } finally { em.close(); }
}
    @Override
public boolean devolverLibro(String ISBN) {
    EntityManager em = JpaUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        Libro libro = em.find(Libro.class, ISBN);

        if (libro != null) {
            if (libro.getUsuarioReserva() != null) {
                // Si había alguien esperando:
                libro.setEstudiante(libro.getUsuarioReserva()); // Pasa de reserva a préstamo
                libro.setUsuarioReserva(null); // Ya no hay reserva pendiente
                libro.setReservado(false);
                libro.setPrestado(true);
                libro.setLimitePrestamo(LocalDate.now().plusDays(15));
            } else {
                // Si no había nadie esperando:
                libro.setEstudiante(null);
                libro.setPrestado(false);
                libro.setLimitePrestamo(null);
            }
        }
        em.getTransaction().commit();
        return true;
    } finally { em.close(); }
}

    @Override
public boolean alargarPrestamo(String isbn, int dias) {
    EntityManager em = JpaUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        
        // 1. Buscamos el libro en la base de datos
        Libro libro = em.find(Libro.class, isbn);
        
        if (libro != null && libro.isPrestado() && libro.getLimitePrestamo() != null) {
            // 2. Obtenemos la fecha límite actual y le sumamos los días indicados
            LocalDate nuevaFecha = libro.getLimitePrestamo().plusDays(dias);
            
            // 3. Actualizamos la entidad
            libro.setLimitePrestamo(nuevaFecha);
            em.merge(libro);
            
            tx.commit();
            return true;
        } else {
            // Si el libro no existe o no está prestado, no se puede alargar
            return false;
        }
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
        System.err.println("Error al alargar préstamo: " + e.getMessage());
        return false;
    } finally {
        em.close();
    }
}

    @Override
    public boolean eliminarLibro(String isbn) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // En JPA, para borrar primero debemos encontrar la entidad gestionada
            Libro libro = em.find(Libro.class, isbn);
            if (libro != null) {
                em.remove(libro);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

	@Override
	public Libro seleccionarLibro(String ISBN) {
		EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Libro l WHERE l.isbn = :isbn", Libro.class)
                     .setParameter("isbn", ISBN)
                     .getSingleResult();
        } catch (Exception e) { return null; }
    
	}

    

}
