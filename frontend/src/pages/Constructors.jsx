import { useEffect, useState } from 'react';
import { getConstructors } from '../api/client';
import ConstructorCard from '../components/ConstructorCard';

function Constructors() {
  const [constructors, setConstructors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getConstructors()
      .then((res) => setConstructors(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading constructors...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div>
      <h1>Constructors</h1>
      <div style={{ display: 'flex', flexWrap: 'wrap' }}>
        {constructors.map((c) => (
          <ConstructorCard key={c.constructorId} constructor={c} />
        ))}
      </div>
    </div>
  );
}

export default Constructors;