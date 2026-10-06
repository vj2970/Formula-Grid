import { Link } from 'react-router-dom';

function ConstructorCard({ constructor }) {
  return (
    <Link to={`/constructors/${constructor.constructorId}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div style={{
        border: '1px solid #ddd',
        borderRadius: '8px',
        padding: '16px',
        margin: '8px',
        width: '220px',
        cursor: 'pointer',
      }}>
        <h3>{constructor.name}</h3>
        <p>Nationality: {constructor.nationality}</p>
      </div>
    </Link>
  );
}

export default ConstructorCard;