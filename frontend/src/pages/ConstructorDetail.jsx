import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getConstructorById } from "../api/client";

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
    <div className="p-6 max-w-xl">
      <Link to="/constructors" className="text-blue-600 hover:underline">
        &larr; Back to Constructors
      </Link>
      <h1 className="text-3xl font-bold mt-4 mb-2 text-gray-900">
        {constructor.name}
      </h1>
      <div className="space-y-1 text-gray-700">
        <p>
          <span className="font-semibold">Nationality:</span>{" "}
          {constructor.nationality}
        </p>
      </div>
      <a
        href={constructor.url}
        target="_blank"
        rel="noreferrer"
        className="text-blue-600 hover:underline mt-3 inline-block"
      >
        Wikipedia
      </a>
    </div>
  );
}

export default ConstructorDetail;
