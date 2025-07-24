package com.programacion.distribuida.todo.rest;

import com.programacion.distribuida.todo.db.ToDo;
import com.programacion.distribuida.todo.repo.TodoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ToDoRest {

    @Inject
    TodoRepository todoRepository;


    @GET
    @Path("/find/{userId}")
    public Response findByUserId(@PathParam("userId") Integer userId) {
        String url = "https://jsonplaceholder.typicode.com/users/" + userId;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return Response.ok(response.body()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    public Response findAll() {
        return Response.ok(todoRepository.listAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(Integer id) {
        var obj = todoRepository.findByIdOptional(id);

        if (obj.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(obj.get()).build();
    }

    @POST
    public Response create(ToDo todo) {
        todoRepository.persist(todo);
        return Response.status(Response.Status.CREATED).entity(todo).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, ToDo todo) {
        var existingTodo = todoRepository.findByIdOptional(id);
        if (existingTodo.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        todo.setId(id);
        todoRepository.persist(todo);
        return Response.ok(todo).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        var existingTodo = todoRepository.findByIdOptional(id);
        if (existingTodo.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        todoRepository.delete(existingTodo.get());
        return Response.noContent().build();
    }
}
