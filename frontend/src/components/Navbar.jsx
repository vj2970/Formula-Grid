import { Link } from "react-router-dom";

function Navbar() {
  return (
    <nav
      style={{
        display: "flex",
        gap: "16px",
        padding: "16px",
        borderBottom: "1px solid #ddd",
      }}
    >
      <Link to="/">Drivers</Link>
      <Link to="/standings">Standings</Link>
      <Link to="/constructors">Constructors</Link>
    </nav>
  );
}

export default Navbar;
