import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, useNavigate, useParams } from 'react-router-dom';
import DOMPurify from 'dompurify';

// Whitelist of allowed routes for navigation
const allowedRoutes = ['about', 'contact', 'profile'];

function Home() {
  const [comment, setComment] = useState('');
  const [imgUrl, setImgUrl] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleCommentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setComment(e.target.value);
  };

  // Sanitize the comment before rendering to prevent XSS
  const sanitizedComment = DOMPurify.sanitize(comment);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const url = e.target.value;
    // Validate that the URL starts with http:// or https://
    if (url && !/^https?:\/\//.test(url)) {
      setError('Image URL must start with http:// or https://');
      setImgUrl('');
    } else {
      setError('');
      setImgUrl(url);
    }
  };

  const handleNavigate = (e: React.FormEvent) => {
    e.preventDefault();
    const route = (document.getElementById('route') as HTMLInputElement).value;

    // Validate the route against a whitelist to prevent open redirects
    if (allowedRoutes.includes(route)) {
      navigate(`/${route}`);
    } else {
      setError(`Navigation to "/${route}" is not allowed.`);
    }
  };

  return (
    <div>
      <h1>A More Secure React App</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <form onSubmit={handleNavigate}>
        <input id="route" placeholder="Go to route (e.g., about)" />
        <button type="submit">Go</button>
      </form>

      <textarea placeholder="Enter your comment" onChange={handleCommentChange}></textarea>
      <div>
        <strong>Comment Preview:</strong>
        {/* Render the sanitized HTML */}
        <div dangerouslySetInnerHTML={{ __html: sanitizedComment }} />
      </div>

      <input
        type="text"
        placeholder="Enter image URL"
        onChange={handleImageChange}
      />
      <div>
        {/* Only render the image if the URL is set */}
        {imgUrl && <img src={imgUrl} alt="User-provided" width="300" />}
      </div>
    </div>
  );
}

function Page() {
  const { pageId } = useParams<{ pageId: string }>();

  // Also validate the pageId from the URL against the whitelist
  if (pageId && !allowedRoutes.includes(pageId)) {
    return <h2>Page not found.</h2>;
  }

  return <h2>You're on: {pageId}</h2>;
}

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        {/* The route parameter is now validated by the Page component */}
        <Route path="/:pageId" element={<Page />} />
      </Routes>
    </Router>
  );
} 