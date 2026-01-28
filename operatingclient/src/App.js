import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import './App.css';
import Sidebar from './components/Sidebar';
import DronePage from './Pages/DronePage';
import DeliveryPage from './Pages/DeliveryPage';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <Router>
          <Sidebar />
          <main className="main">
            <Routes>
              <Route path="/" element={<DronePage />} />
              <Route path="/drones" element={<DronePage />} />
              <Route path="/deliveries" element={<DeliveryPage />} />
            </Routes>
          </main>
        </Router>
      </header>
    </div>
  );
}

export default App;
