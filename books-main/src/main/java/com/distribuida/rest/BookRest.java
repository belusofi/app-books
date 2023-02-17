package com.distribuida.rest;

import com.distribuida.clientes.authors.AuthorRestProxy;
import com.distribuida.clientes.authors.AuthorsCliente;
import com.distribuida.db.Book;
import com.distribuida.dtos.BookDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.Response.ok;


@Path("/books")
public class BookRest {

    @PersistenceContext(unitName = "test")
    private EntityManager entityManager;

    @RestClient
    @Inject
    AuthorRestProxy proxyAuthor;

    /**
     * GET          /books/{id}     buscar un libro por ID
     * GET          /books          listar todos
     * POST         /books          insertar
     * PUT/PATCH    /books/{id}     actualizar
     * DELETE       /books/{id}     eliminar
     */

    @GET
    @Operation()
    @RequestBody(
            name = "books",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))

    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> findAll() {
        return entityManager.createNamedQuery("findAll", Book.class).getResultList();
    }

    @GET
    @Path("/{id}")
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Produces(MediaType.APPLICATION_JSON)
    public Book findById(@PathParam("id") String id) {
            return entityManager.find(Book.class, Integer.valueOf(id));
            }


    @GET
    @Path("/all")
    @Operation()
    public List<BookDto> findAllCompleto() throws Exception {
        var books = entityManager.createNamedQuery("findAll", Book.class).getResultList();

        List<BookDto> ret = books.stream()
                .map(s -> {
                    System.out.println("*********buscando " + s.getId() );

                    AuthorsCliente author = proxyAuthor.findById(s.getId().longValue());
                    return new BookDto(
                            s.getId(),
                            s.getIsbn(),
                            s.getTitle(),
                            s.getAuthor(),
                            s.getPrice(),
                            String.format("%s, %s", author.getLastName(), author.getFirtName())
                    );
                })
                .collect(Collectors.toList());

        return ret;
    }

    @DELETE
    @Path("/{id}")
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(@PathParam("id") String id) {
        Book book = findById(id);
        entityManager.remove(book);
    }

    @POST
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void create(Book book) {
            entityManager.persist(book);
    }
    @PUT
    @Path("/{id}")
    @Operation()
    @APIResponse(content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class)))
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public void update(@PathParam("id") String id, Book book) {
             Book book1 = findById(id);
             book1.setAuthor(book.getAuthor());
             book1.setTitle(book.getTitle());
             book1.setIsbn(book.getIsbn());
             book1.setPrice(book.getPrice());
             entityManager.persist(book1);
    }



}
