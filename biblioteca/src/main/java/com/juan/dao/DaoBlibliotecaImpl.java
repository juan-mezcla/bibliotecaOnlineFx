package com.juan.dao;

import java.time.LocalDate;
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
        
        User user=null;
        try {
            String select="SELECT u From User u"+
                            "LEFT JOIN FETCH u.librosPrestados";
            TypedQuery<User> query = em.createQuery(select, User.class);

            user=query.getResultList().get(0);

        } catch (Exception e) {
           return null;
        }

        return user;
    }

    @Override
    public List<Libro> obteneLibros() {
        EntityManager em = JpaUtil.getEntityManager();
        
        List<Libro> libros=null;
        try {
            String select="SELECT l From Libro l"+
                    "LEFT JOIN FETCH l.estudiante ";
            TypedQuery<Libro> query = em.createQuery(select, Libro.class);

            libros=query.getResultList();

        } catch (Exception e) {
            System.out.println("error en consulta selectLibros");
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
        EntityTransaction tx=em.getTransaction();
        
        try {
            tx.begin();

            Libro libro=em.find(Libro.class,ISBN);

            if (libro != null) {
                libro.setEstudiante(user);
                libro.setReservado(true);
                libro.setPrestado(false);
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
    public boolean devolverLibro(String ISBN) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx=em.getTransaction();
        
        try {
            tx.begin();

            Libro libro=em.find(Libro.class,ISBN);

            if (libro != null) {
                libro.setEstudiante(null);
                libro.setPrestado(false);
                libro.setLimitePrestamo(null);
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
	public Libro seleccionarLibro(String ISBN) {
		EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Libro l WHERE l.isbn = :isbn", Libro.class)
                     .setParameter("isbn", ISBN)
                     .getSingleResult();
        } catch (Exception e) { return null; }
    
	}

    

}
