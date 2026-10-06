import { Link } from 'react-router-dom';

function DriverCard({ driver }) {
  return (
    <Link to={`/drivers/${driver.driverId}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div style={{
        border: '1px solid #ddd',
        borderRadius: '8px',
        padding: '16px',
        margin: '8px',
        width: '220px',
        cursor: 'pointer',
      }}>
        <h3>{driver.givenName} {driver.familyName}</h3>
        <p>Nationality: {driver.nationality}</p>
        <p>Number: {driver.permanentNumber ?? 'N/A'}</p>
      </div>
    </Link>
  );
}

export default DriverCard;