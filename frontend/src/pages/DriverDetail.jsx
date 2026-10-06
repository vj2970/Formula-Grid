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
    <div style={{ padding: "16px" }}>
      <Link to="/">&larr; Back to Drivers</Link>
      <h1>
        {driver.givenName} {driver.familyName}
      </h1>
      <p>
        <strong>Code:</strong> {driver.code}
      </p>
      <p>
        <strong>Number:</strong> {driver.permanentNumber ?? "N/A"}
      </p>
      <p>
        <strong>Nationality:</strong> {driver.nationality}
      </p>
      <p>
        <strong>Date of Birth:</strong> {driver.dateOfBirth}
      </p>
      <p>
        <a href={driver.url} target="_blank" rel="noreferrer">
          Wikipedia
        </a>
      </p>
    </div>
  );
}

export default DriverDetail;
