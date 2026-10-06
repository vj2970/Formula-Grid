import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getConstructorById } from '../api/client';

function ConstructorDetail() {
  const { constructorId } = useParams();
  const [constructor, setConstructor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getConstructorById(constructorId)
      .then((res) => setConstructor(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [constructorId]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!constructor) return <p>Constructor not found.</p>;

  return (
    <div style={{ padding: '16px' }}>
      <Link to="/constructors">&larr; Back to Constructors</Link>
      <h1>{constructor.name}</h1>
      <p><strong>Nationality:</strong> {constructor.nationality}</p>
      <p><a href={constructor.url} target="_blank" rel="noreferrer">Wikipedia</a></p>
    </div>
  );
}

export default ConstructorDetail;