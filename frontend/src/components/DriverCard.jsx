import { Link } from "react-router-dom";

function DriverCard({ driver }) {
  return (
    <Link to={`/drivers/${driver.driverId}`}>
      <div className="border border-gray-200 rounded-lg p-4 m-2 w-56 cursor-pointer shadow-sm hover:shadow-md hover:border-blue-400 transition">
        <h3 className="text-lg font-semibold text-gray-900">
          {driver.givenName} {driver.familyName}
        </h3>
        <p className="text-sm text-gray-600">
          Nationality: {driver.nationality}
        </p>
        <p className="text-sm text-gray-600">
          Number: {driver.permanentNumber ?? "N/A"}
        </p>
      </div>
    </Link>
  );
}

export default DriverCard;
