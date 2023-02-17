package com.distribuida.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

//Representa una entidad/tabla
@Entity
@Table(name = "books")
//accede a la entidad sin getters y setters
@Access(AccessType.FIELD)

@NamedQueries(value = {
        @NamedQuery(name = "findAll",
                query = "SELECT p FROM Book p"),
        @NamedQuery(name = "findById",
                query = "SELECT p FROM Book p WHERE p.id = :id")
})
public class Book {

    @Id
    //generación de clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "isbn")
    private String isbn;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "price")
    private Double price;

}