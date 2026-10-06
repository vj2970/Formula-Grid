import { Link } from "react-router-dom";

function ConstructorCard({ constructor }) {
  return (
    <Link to={`/constructors/${constructor.constructorId}`}>
      <div className="border border-gray-200 rounded-lg p-4 m-2 w-56 cursor-pointer shadow-sm hover:shadow-md hover:border-blue-400 transition">
        <h3 className="text-lg font-semibold text-gray-900">
          {constructor.name}
        </h3>
        <p className="text-sm text-gray-600">
          Nationality: {constructor.nationality}
        </p>
      </div>
    </Link>
  );
}

export default ConstructorCard;
