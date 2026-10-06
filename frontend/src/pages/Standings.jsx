import { useEffect, useState } from "react";
import { getStandings } from "../api/client";
import StandingsTable from "../components/StandingsTable";

function Standings() {
  const [standings, setStandings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getStandings()
      .then((res) => setStandings(res.data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading standings...</p>;
  if (error) return <p>Error: {error}</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4 text-gray-900">
        Driver Standings
      </h1>
      <div className="overflow-x-auto">
        <StandingsTable standings={standings} />
      </div>
    </div>
  );
}

export default Standings;
