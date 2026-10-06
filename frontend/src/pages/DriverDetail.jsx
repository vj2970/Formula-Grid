import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getDriverById } from "../api/client";

function DriverDetail() {
  const { driverId } = useParams();
  const [driver, setDriver] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getDriverById(driverId)
      .then((res) => setDriver(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [driverId]);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!driver) return <p>Driver not found.</p>;

  return (
    <div className="p-6 max-w-xl">
      <Link to="/" className="text-blue-600 hover:underline">
        &larr; Back to Drivers
      </Link>
      <h1 className="text-3xl font-bold mt-4 mb-2 text-gray-900">
        {driver.givenName} {driver.familyName}
      </h1>
      <div className="space-y-1 text-gray-700">
        <p>
          <span className="font-semibold">Code:</span> {driver.code}
        </p>
        <p>
          <span className="font-semibold">Number:</span>{" "}
          {driver.permanentNumber ?? "N/A"}
        </p>
        <p>
          <span className="font-semibold">Nationality:</span>{" "}
          {driver.nationality}
        </p>
        <p>
          <span className="font-semibold">Date of Birth:</span>{" "}
          {driver.dateOfBirth}
        </p>
      </div>
      <a
        href={driver.url}
        target="_blank"
        rel="noreferrer"
        className="text-blue-600 hover:underline mt-3 inline-block"
      >
        Wikipedia
      </a>
    </div>
  );
}

export default DriverDetail;
