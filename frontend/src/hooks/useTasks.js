import { useState, useEffect } from 'react';
import { fetchTasks } from '../api';

export function useTasks(query, status, page, pageSize) {
  const [tasks, setTasks] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // One controller per request. When the inputs change (or the component unmounts) the cleanup
    // below aborts the in-flight request, so a slow, older response can never overwrite newer results.
    const controller = new AbortController();
    setLoading(true);
    setError(null);

    fetchTasks({ query, status, page, pageSize }, controller.signal)
      .then((data) => {
        if (controller.signal.aborted) return;
        setTasks(data.items);
        setTotal(data.total);
      })
      .catch((err) => {
        if (err.name === 'AbortError') return; // superseded by a newer request - not a real error
        setError(err.message);
        setTasks([]);
        setTotal(0);
      })
      .finally(() => {
        // Always leave the loading state (it used to stay true forever after a failure),
        // unless a newer request has already taken over.
        if (!controller.signal.aborted) setLoading(false);
      });

    return () => controller.abort();
  }, [query, status, page, pageSize]);

  return { tasks, total, loading, error };
}
