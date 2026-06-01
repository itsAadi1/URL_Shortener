export const createShortUrl = async (url, alias) => {
  const payload = {url};
  if(alias) payload.alias=alias;

  const response = await fetch("http://localhost:8081/urls", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (response.status === 409) {
    // Conflict (alias already exists)
    let details = null;
    try { details = await response.json(); } catch (e) { /* ignore */ }
    const err = new Error(details?.message || 'Alias already in use');
    err.code = 409;
    err.details = details;
    throw err;
  }

  if (!response.ok) {
    let text = 'Failed to create short URL';
    try { text = await response.text(); } catch(e) {}
    throw new Error(text || 'Failed to create short URL');
  }

  return response.json();
};