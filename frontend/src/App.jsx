import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Drivers from './pages/Drivers';
import DriverDetail from './pages/DriverDetail';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Drivers />} />
        <Route path="/drivers/:driverId" element={<DriverDetail />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;