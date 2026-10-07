import { Link } from "react-router-dom";

function Navbar() {
  return (
    <nav className="flex gap-6 px-6 py-4 border-b border-gray-200 bg-white">
      <Link to="/" className="text-gray-700 hover:text-blue-600 font-medium">
        Health
      </Link>
      <Link
        to="/drivers"
        className="text-gray-700 hover:text-blue-600 font-medium"
      >
        Drivers
      </Link>
      <Link
        to="/standings"
        className="text-gray-700 hover:text-blue-600 font-medium"
      >
        Standings
      </Link>
      <Link
        to="/constructors"
        className="text-gray-700 hover:text-blue-600 font-medium"
      >
        Constructors
      </Link>
    </nav>
  );
}

export default Navbar;
