package com.programacion.distribuida.todo.repo;

import com.programacion.distribuida.todo.db.ToDo;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class TodoRepository implements PanacheRepositoryBase<ToDo, Integer> {




}
