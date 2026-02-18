package com.juan.model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class libro {
    private String ISBN;
    private File imagen;
    private String titulo,genero;
    private LocalDateTime fechaPublicacion,limitePrestado;
    private List<String> autores;
    private boolean reservado,prestado;
}
