import { useEffect, useState } from "react";
import { getHealth } from "../api/client";

function Health() {
  const [health, setHealth] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getHealth()
      .then((res) => setHealth(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading health info...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4 text-gray-900">Health</h1>
      <div className="flex flex-wrap">
        {health && (
          <div className="bg-green-100 text-green-800 p-4 rounded-md">
            <p>System is healthy!</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default Health;
