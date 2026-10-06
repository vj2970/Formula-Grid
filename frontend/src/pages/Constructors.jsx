import { useEffect, useState } from "react";
import { getConstructors } from "../api/client";
import ConstructorCard from "../components/ConstructorCard";

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
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4 text-gray-900">Constructors</h1>
      <div className="flex flex-wrap">
        {constructors.map((c) => (
          <ConstructorCard key={c.constructorId} constructor={c} />
        ))}
      </div>
    </div>
  );
}

export default Constructors;
