const API_BASE = '/api';

export async function fetchTasks({ query = '', status = '', page = 1, pageSize = 10 }, signal) {
  const params = new URLSearchParams();
  if (query) params.set('q', query);
  if (status) params.set('status', status);
  params.set('page', String(page));
  params.set('pageSize', String(pageSize));

  const response = await fetch(`${API_BASE}/tasks?${params.toString()}`, { signal });

  if (!response.ok) {
    // Spring returns { message } for 4xx; fall back to the bare status if the body isn't JSON.
    let detail = '';
    try {
      detail = (await response.json()).message || '';
    } catch {
      /* ignore non-JSON error bodies */
    }
    throw new Error(detail || `Request failed: ${response.status}`);
  }

  return response.json();
}
