import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Drivers from "./pages/Drivers";
import DriverDetail from "./pages/DriverDetail";
import Standings from "./pages/Standings";
import Navbar from "./components/Navbar";
import Constructors from "./pages/Constructors";
import ConstructorDetail from "./pages/ConstructorDetail";

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/drivers" element={<Drivers />} />
        <Route path="/drivers/:driverId" element={<DriverDetail />} />
        <Route path="/standings" element={<Standings />} />
        <Route path="/constructors" element={<Constructors />} />
        <Route
          path="/constructors/:constructorId"
          element={<ConstructorDetail />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
