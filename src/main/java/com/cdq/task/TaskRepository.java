package com.cdq.task;

import java.util.List;
import java.util.Optional;

import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.cdq.task.models.Task;

import jakarta.persistence.QueryHint;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @QueryHints({ @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true") })
    List<Task> findAll();

    Optional<Task> findById(int id);

}
