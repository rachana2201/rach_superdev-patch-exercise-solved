package com.internal.tasktracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Validate paging input: a bad value used to reach subList() and surface as a 500.
        if (page < 1 || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be >= 1 and pageSize must be between 1 and " + MAX_PAGE_SIZE);
        }

        // Normalize query input. Locale.ROOT avoids locale quirks (e.g. Turkish dotted/dotless i).
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + escapeLike(query.toLowerCase(Locale.ROOT)) + "%";

        // Parse status filter; an unknown value is a client error (400), not a server error (500).
        String normalizedStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT)).name();
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unknown status '" + status + "'. Allowed: " + Arrays.toString(TaskStatus.values()));
            }
        }

        log.debug("search q.length={} status={} page={} pageSize={}",
                query.length(), normalizedStatus, page, pageSize);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        // long math: (page - 1) * pageSize can overflow int for a huge page number.
        long start = (long) (page - 1) * pageSize;
        List<Task> pageResults = (start < allResults.size())
                ? allResults.subList((int) start, (int) Math.min(start + pageSize, allResults.size()))
                : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }

    // Escape LIKE wildcards so user input is matched literally ("%" or "_" are not wildcards).
    // The repository query declares ESCAPE '\'.
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
