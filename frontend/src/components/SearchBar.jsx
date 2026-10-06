import { useState, useEffect } from 'react';

// Keeps the typed text locally and only reports it to the parent after the user pauses typing,
// so we send one request per pause instead of one per keystroke.
export default function SearchBar({ value, onChange, delay = 300 }) {
  const [text, setText] = useState(value);

  useEffect(() => {
    const trimmed = text.trim();
    if (trimmed === value) return;
    const timer = setTimeout(() => onChange(trimmed), delay);
    return () => clearTimeout(timer);
  }, [text, value, onChange, delay]);

  return (
    <input
      type="text"
      className="search-input"
      placeholder="Search tasks..."
      aria-label="Search tasks"
      value={text}
      onChange={(e) => setText(e.target.value)}
    />
  );
}
