export default function TaskTable({ tasks, loading, error }) {
  if (error) {
    return <div className="state-message error" role="alert">Error: {error}</div>;
  }

  // Only replace the table with a message on the very first load; on later searches keep the
  // previous rows visible (dimmed) so the page doesn't flicker on every keystroke.
  if (loading && (!tasks || tasks.length === 0)) {
    return <div className="state-message">Loading tasks...</div>;
  }

  if (!tasks || tasks.length === 0) {
    return <div className="state-message">No tasks found.</div>;
  }

  return (
    <table className={`task-table${loading ? ' is-loading' : ''}`} aria-busy={loading}>
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Status</th>
          <th>Priority</th>
          <th>Assignee</th>
        </tr>
      </thead>
      <tbody>
        {tasks.map((task) => (
          <tr key={task.id}>
            <td>{task.id}</td>
            <td>
              <div className="task-title">{task.title}</div>
              <div className="task-desc">{task.description}</div>
            </td>
            <td>
              <span className={`status-badge ${task.status.toLowerCase()}`}>{task.status.replace('_', ' ')}</span>
            </td>
            <td>{task.priority}</td>
            <td>{task.assignee || '\u2014'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
