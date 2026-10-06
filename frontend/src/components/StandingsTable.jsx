function StandingsTable({ standings }) {
  return (
    <table style={{ borderCollapse: "collapse", width: "100%" }}>
      <thead>
        <tr>
          <th style={cellStyle}>Pos</th>
          <th style={cellStyle}>Driver</th>
          <th style={cellStyle}>Constructor</th>
          <th style={cellStyle}>Points</th>
          <th style={cellStyle}>Wins</th>
        </tr>
      </thead>
      <tbody>
        {standings.map((entry) => (
          <tr key={entry.id}>
            <td style={cellStyle}>{entry.positionText}</td>
            <td style={cellStyle}>
              {entry.driver.givenName} {entry.driver.familyName}
            </td>
            <td style={cellStyle}>{entry.constructor.name}</td>
            <td style={cellStyle}>{entry.points}</td>
            <td style={cellStyle}>{entry.wins}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

const cellStyle = {
  border: "1px solid #ddd",
  padding: "8px",
  textAlign: "left",
};

export default StandingsTable;
