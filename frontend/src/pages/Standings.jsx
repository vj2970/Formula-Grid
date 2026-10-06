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
    <div>
      <h1>Driver Standings</h1>
      <StandingsTable standings={standings} />
    </div>
  );
}

export default Standings;
