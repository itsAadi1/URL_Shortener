const apiUrl=import.meta.env.VITE_API_URL;
export const createShortUrl = async (url, alias) => {
  const payload = { url };

  if (alias) {
    payload.alias = alias;
  }

  const response = await fetch(`${apiUrl}/urls`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });
  console.log("response : ",response);
  if (response.status === 409) {
    let details = null;

    try {
      details = await response.json();
    } catch (e) {}

    const err = new Error(
      details?.message || "Alias already in use"
    );

    err.code = 409;
    err.details = details;

    throw err;
  }

  if (response.status === 429) {
    let details = null;

    try {
      details = await response.json();
      console.log("429 details:", details);
    } catch (e) {}

    const retryAfter = Number(
      response.headers.get("Retry-After")
    );

    const err = new Error(
      details?.message ||
      "Rate limit exceeded. Try again later."
    );

    err.code = 429;
    err.details = details;
    err.retryAfter = retryAfter || 86400;
    console.log(
  "Retry-After header:",
  response.headers.get("Retry-After")
);
    throw err;
  }

  if (!response.ok) {
    let text = "Failed to create short URL";

    try {
      text = await response.text();
    } catch (e) {}

    throw new Error(text || "Failed to create short URL");
  }

  return response.json();
};