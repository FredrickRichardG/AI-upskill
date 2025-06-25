import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, useNavigate, useParams } from 'react-router-dom';

function Home() {
  const [comment, setComment] = useState('');
  const [imgUrl, setImgUrl] = useState('');
  const navigate = useNavigate();

  const handleCommentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setComment(e.target.value);
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setImgUrl(e.target.value);
  };

  const handleNavigate = (e: React.FormEvent) => {
    e.preventDefault();
    const route = (document.getElementById('route') as HTMLInputElement).value;
    navigate(`/${route}`);
  };

  return (
    <div>
      <h1>Unsafe React App</h1>

      <form onSubmit={handleNavigate}>
        <input id="route" placeholder="Go to route" />
        <button type="submit">Go</button>
      </form>

      <textarea placeholder="Enter your comment" onChange={handleCommentChange}></textarea>
      <div>
        <strong>Comment Preview:</strong>
        <div dangerouslySetInnerHTML={{ __html: comment }} />
      </div>

      <input
        type="text"
        placeholder="Enter image URL"
        onChange={handleImageChange}
      />
      <div>
        <img src={imgUrl} alt="User-provided" width="300" />
      </div>
    </div>
  );
}

function Page() {
  const { pageId } = useParams();

  return <h2>You're on: {pageId}</h2>;
}

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/:pageId" element={<Page />} />
      </Routes>
    </Router>
  );
}