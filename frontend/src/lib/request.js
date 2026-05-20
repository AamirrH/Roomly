export async function apiRequest(apiBase, tokenKey, path, options = {}) {
  const token = window.localStorage.getItem(tokenKey);
  const response = await fetch(`${apiBase}${path}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    let message = `Request failed with ${response.status}`;
    try {
      const payload = await response.clone().json();
      message = payload?.message || payload?.error || message;
    } catch {
      const text = await response.text();
      message = text || message;
    }
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  const text = await response.text();
  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}
