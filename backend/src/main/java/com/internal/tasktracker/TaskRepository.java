package com.internal.tasktracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Search non-archived tasks by term (title OR description) and optional status.
    // The OR is parenthesised on purpose: AND binds tighter than OR in SQL, so without
    // the parentheses the archived and status filters only applied to the title match.
    // "id DESC" is a tie-breaker so rows with the same created_at keep a stable order across pages.
    @Query(value = "SELECT * FROM tasks WHERE archived = FALSE "
                 + "AND (LOWER(title) LIKE :term ESCAPE '\\' OR LOWER(description) LIKE :term ESCAPE '\\') "
                 + "AND (:status IS NULL OR status = :status) "
                 + "ORDER BY created_at DESC, id DESC",
           nativeQuery = true)
    List<Task> searchTasks(@Param("term") String term, @Param("status") String status);
}
