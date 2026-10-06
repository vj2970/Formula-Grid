function StandingsTable({ standings }) {
  return (
    <table className="w-full border-collapse">
      <thead>
        <tr className="bg-gray-100 text-left">
          <th className="p-3 font-semibold text-gray-700 border-b border-gray-200">
            Pos
          </th>
          <th className="p-3 font-semibold text-gray-700 border-b border-gray-200">
            Driver
          </th>
          <th className="p-3 font-semibold text-gray-700 border-b border-gray-200">
            Constructor
          </th>
          <th className="p-3 font-semibold text-gray-700 border-b border-gray-200">
            Points
          </th>
          <th className="p-3 font-semibold text-gray-700 border-b border-gray-200">
            Wins
          </th>
        </tr>
      </thead>
      <tbody>
        {standings.map((entry) => (
          <tr key={entry.id} className="hover:bg-gray-50">
            <td className="p-3 border-b border-gray-100">
              {entry.positionText}
            </td>
            <td className="p-3 border-b border-gray-100 font-medium">
              {entry.driver.givenName} {entry.driver.familyName}
            </td>
            <td className="p-3 border-b border-gray-100">
              {entry.constructor.name}
            </td>
            <td className="p-3 border-b border-gray-100">{entry.points}</td>
            <td className="p-3 border-b border-gray-100">{entry.wins}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default StandingsTable;
