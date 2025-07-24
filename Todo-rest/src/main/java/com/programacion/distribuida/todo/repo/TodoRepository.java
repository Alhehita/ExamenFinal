package com.programacion.distribuida.todo.repo;

import com.programacion.distribuida.todo.db.ToDo;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class TodoRepository implements PanacheRepositoryBase<ToDo, Integer> {

    // Java
    public List<ToDo> findByUserId(Integer userId) {
        return list("userId", userId);
    }


}
