import { useState, useEffect } from 'react'
import { createShortUrl } from './services/urlService';

function App() {
  const [isUrlValid, setIsUrlValid] = useState(true);
  const [showUrlFeedback, setShowUrlFeedback] = useState(false);
  const [url, setUrl] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);
  const [alias, setAlias] = useState("");
  const [error, setError] = useState("");
  const [rateLimitUntil, setRateLimitUntil] = useState(null);
  const [rateLimitRemaining, setRateLimitRemaining] = useState(0);

  const handleChange = (e) => setUrl(e.target.value);

  const normalizeUrl = (value) => {
    if (!value) return '';
    const trimmed = value.trim();
    const withScheme = /^[a-zA-Z][a-zA-Z\d+\-.]*:\/\//.test(trimmed)
      ? trimmed
      : `http://${trimmed}`;

    try {
      const u = new URL(withScheme);
      if (u.protocol !== 'http:' && u.protocol !== 'https:') {
        return '';
      }
      const hostname = u.hostname.toLowerCase();
      const validHostname = hostname === 'localhost' ||
        /^\d{1,3}(?:\.\d{1,3}){3}$/.test(hostname) ||
        hostname.includes('.');
      if (!validHostname) {
        return '';
      }
      return u.toString();
    } catch (err) {
      return '';
    }
  }

  const validateUrl = (value) => {
    return !!normalizeUrl(value);
  }

  const handleChangeAndValidate = (e) => {
    const v = e.target.value;
    setUrl(v);
    // hide feedback while the user is editing
    if (showUrlFeedback) setShowUrlFeedback(false);
    setIsUrlValid(true);
    if (error) setError("");
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    // show validation feedback after user attempts to submit
    setShowUrlFeedback(true);
    const normalizedUrl = normalizeUrl(url);
    if (!normalizedUrl) {
      setIsUrlValid(false);
      setError("");
      return;
    }
    setUrl(normalizedUrl);
    setLoading(true);
    setShortUrl("");
    setCopied(false);
    setError("");
    try {
      const data = await createShortUrl(normalizedUrl, alias);
      setShortUrl(data.shortUrl || '');
    } catch (error) {
      console.error(error);
      if (error && error.code === 409) {
        setError(error.message || 'Alias already in use');
      } else if (error && error.code === 429) {
        console.log("rateinggg")
        const retry = error.retryAfter ?? 60;
        setRateLimitUntil(Date.now() + retry*1000);
        setRateLimitRemaining(retry);
      } else {
        setError(error.message || 'Failed to create short URL');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = async () => {
    if (!shortUrl) return;
    try {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2500);
    } catch (err) {
      console.error('Copy failed', err);
    }
  };

  useEffect(() => {
    if (!rateLimitUntil) return;
    const id = setInterval(() => {
      const sec = Math.max(0, Math.ceil((rateLimitUntil - Date.now()) / 1000));
      setRateLimitRemaining(sec);
      if (sec <= 0) {
        setRateLimitUntil(null);
        setError('');
        clearInterval(id);
      }
    }, 1000);
    return () => clearInterval(id);
  }, [rateLimitUntil]);

  return (
    <div className="app-root d-flex align-items-center justify-content-center min-vh-100">
      <div className="card shadow-lg p-4 app-card">
        <div className="d-flex align-items-center mb-3">
          <i className="bi bi-link-45deg display-6 text-primary me-2" />
          <div>
            <h3 className="mb-0">Shorten your links</h3>
            <small className="text-muted">Clean, fast and reliable URL shortener</small>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="mb-3" noValidate>
          <div className="mb-2">
            <input
              className={"form-control form-control-lg " + (!isUrlValid && showUrlFeedback ? 'is-invalid' : '')}
              type="url"
              placeholder="Paste a long URL (https://...)"
              value={url}
              onChange={handleChangeAndValidate}
              required
              disabled={loading || rateLimitRemaining > 0}
            />
            {!isUrlValid && showUrlFeedback && (
              <div className="invalid-feedback d-block">We'll need a valid URL, like "example.com/shortlink"</div>
            )}
          </div>

          <div className="input-group mb-3">
            <span className="input-group-text text-muted">/</span>
            <input
              className="form-control"
              type="text"
              placeholder="Custom alias (optional)"
              value={alias}
              onChange={(e) => setAlias(e.target.value)}
              disabled={loading || rateLimitRemaining > 0}
            />
            <button className="btn btn-primary" type="submit" disabled={loading || rateLimitRemaining > 0}>
              {loading ? (
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              ) : (
                <><i className="bi bi-rocket-takeoff me-1" /> Shorten</>
              )}
            </button>
          </div>
        </form>

        {rateLimitRemaining > 0 && (
          <div className="alert alert-warning p-2 mb-3">
            Rate limit exceeded. Try again in {rateLimitRemaining} second{rateLimitRemaining !== 1 ? 's' : ''}.
          </div>
        )}

        {error && (
          <div className="alert alert-danger p-2 mb-3">{error}</div>
        )}

        {shortUrl ? (
          <div className="result-area">
            <label className="form-label text-muted">Short URL</label>
            <div className="d-flex">
              <input className="form-control me-2" value={shortUrl} readOnly />
              <button className="btn btn-outline-secondary me-2" onClick={() => window.open(shortUrl, '_blank')}>
                <i className="bi bi-box-arrow-up-right"></i>
              </button>
              <button className="btn btn-success" onClick={handleCopy}>
                <i className="bi bi-clipboard"></i>
              </button>
            </div>
            {copied && <div className="alert alert-success mt-3 p-2">Copied to clipboard</div>}
          </div>
        ) : (
          <div className="text-muted small">Enter a URL and click Shorten to generate a short link.</div>
        )}

        <div className="mt-4 text-center text-muted small">
          Built to learn
        </div>
      </div>
    </div>
  )
}

export default App
