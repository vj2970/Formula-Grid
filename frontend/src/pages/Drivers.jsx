import { useEffect, useState } from "react";
import { getDrivers } from "../api/client";
import DriverCard from "../components/DriverCard";

function Drivers() {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getDrivers()
      .then((res) => setDrivers(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading drivers...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4 text-gray-900">Drivers</h1>
      <div className="flex flex-wrap">
        {drivers.map((driver) => (
          <DriverCard key={driver.driverId} driver={driver} />
        ))}
      </div>
    </div>
  );
}

export default Drivers;
