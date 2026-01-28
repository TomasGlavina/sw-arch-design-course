import { useEffect, useMemo, useRef, useState } from 'react';
import './App.css';
import pizzaLogo from './assets/pizzalogo.jpg';

function App() {

  const [form, setForm] = useState({
    weight: '',
    address: '',
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [messages, setMessages] = useState([]);
  const [localPhase, setLocalPhase] = useState(0);
  const [serverPhase, setServerPhase] = useState(0);
  const [orderFlight, setOrderFlight] = useState(null);
  const [flights, setFlights] = useState([]);
  const [drones, setDrones] = useState([]);
  const timersRef = useRef([]);
  const lastServerPhaseRef = useRef(0);

  const phases = [
    { title: 'Request received', body: 'We have your order in the queue.' },
    { title: 'Drone assigned', body: 'A drone is preparing for pickup.' },
    { title: 'Out for delivery', body: 'Your pizza is in the air.' },
    { title: 'Delivered', body: 'Hot and ready. Enjoy.' },
  ];

  const clearTimers = () => {
    timersRef.current.forEach((t) => clearTimeout(t));
    timersRef.current = [];
  };

  const statusToPhase = (status) => {
    if (status === 'TO DELIVER') return 1;
    if (status === 'DELIVERING') return 2;
    if (status === 'DELIVERED') return 3;
    return 0;
  };

  const displayPhase = Math.max(localPhase, serverPhase);

  const fleetSummary = useMemo(() => {
    const summary = { total: drones.length, available: 0, unavailable: 0, offline: 0 };
    drones.forEach((drone) => {
      if (drone.status === 'AVAILABLE') summary.available += 1;
      else if (drone.status === 'OFFLINE') summary.offline += 1;
      else summary.unavailable += 1;
    });
    return summary;
  }, [drones]);

  const pushMessage = (text, tone = 'info') => {
    setMessages((prev) => [
      ...prev,
      { id: Date.now() + Math.random(), text, tone },
    ]);
  };

  const scheduleLocalProgress = () => {
    clearTimers();
    timersRef.current = [
      setTimeout(() => setLocalPhase(1), 1500),
      setTimeout(() => setLocalPhase(2), 6000),
    ];
  };

  useEffect(() => {
    let isMounted = true;
    const fetchData = async () => {
      try {
        const [flightsResponse, dronesResponse] = await Promise.all([
          fetch('http://localhost:8082/dronora/flights'),
          fetch('http://localhost:8082/dronora/drones'),
        ]);
        if (flightsResponse.ok) {
          const flightsJson = await flightsResponse.json();
          if (isMounted) setFlights(flightsJson);
        }
        if (dronesResponse.ok) {
          const dronesJson = await dronesResponse.json();
          if (isMounted) setDrones(dronesJson);
        }
      } catch (fetchError) {
        console.error('Fetch error:', fetchError);
      }
    };
    fetchData();
    const intervalId = setInterval(fetchData, 3000);
    return () => {
      isMounted = false;
      clearInterval(intervalId);
    };
  }, []);

  useEffect(() => {
    return () => {
      clearTimers();
    };
  }, []);

  useEffect(() => {
    if (!orderFlight) return;
    const match = flights.find((flight) => flight.id === orderFlight.id);
    if (match) {
      setServerPhase(statusToPhase(match.status));
    }
  }, [flights, orderFlight]);

  useEffect(() => {
    if (serverPhase > lastServerPhaseRef.current) {
      lastServerPhaseRef.current = serverPhase;
      if (serverPhase === 1) pushMessage('Drone assigned. Preparing for pickup.');
      if (serverPhase === 2) pushMessage('Out for delivery. Tracking in progress.');
      if (serverPhase === 3) pushMessage('Delivered. Enjoy your pizza.');
    }
  }, [serverPhase]);

  const handleChange = (field) => (event) => {
    setForm((prev) => ({ ...prev, [field]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setMessages([]);
    setOrderFlight(null);
    setLocalPhase(0);
    setServerPhase(0);
    lastServerPhaseRef.current = 0;

    const payload = {
      weight: parseInt(form.weight, 10),
      address: form.address,
    };

    if (!payload.weight || Number.isNaN(payload.weight)) {
      setError('Weight is required.');
      return;
    }
    if (!payload.address || payload.address.trim().length < 4) {
      setError('Delivery address is required.');
      return;
    }

    setSubmitting(true);
    pushMessage('Order received. Reserving a drone.');
    scheduleLocalProgress();

    try {
      const response = await fetch('http://localhost:8082/dronepizza/pizzaorders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (!response.ok) {
        let message = 'Failed to place the order.';
        try {
          const errorBody = await response.json();
          if (errorBody && errorBody.error) {
            message = errorBody.error;
          }
        } catch (parseError) {
          // ignore parsing errors
        }
        setError(message);
        clearTimers();
        setSubmitting(false);
        return;
      }
      const contentType = response.headers.get('content-type') || '';
      if (contentType.includes('application/json')) {
        const flight = await response.json();
        setOrderFlight(flight);
        setFlights((prev) => {
          const existing = Array.isArray(prev) ? prev.filter((item) => item.id !== flight.id) : [];
          return [flight, ...existing];
        });
        setServerPhase(statusToPhase(flight.status));
      }
      pushMessage('Order placed successfully.');
    } catch (submitError) {
      console.error('Error:', submitError);
      setError('Network error. Please retry.');
      clearTimers();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="App">
      <div className="page">
        <header className="brand">
          <img src={pizzaLogo} className="pizzaLogo" alt="pizzaLogo" />
          <div>
            <p className="eyebrow">Dronora Pizzeria</p>
            <h1>Fresh pizza, airborne delivery.</h1>
            <p className="subtitle">Create a delivery request and follow your drone in real time.</p>
          </div>
        </header>

        <main className="content-grid">
          <form className="card form-card" onSubmit={handleSubmit}>
            <div className="card-header">
              <h2>Place an order</h2>
              <p>Enter the payload weight and coordinates.</p>
            </div>
            <div className="form-grid">
              <label className="input-group">
                <span>Weight (kg)</span>
                <input
                  type="number"
                  step="1"
                  min="1"
                  value={form.weight}
                  onChange={handleChange('weight')}
                  placeholder="e.g. 4"
                />
              </label>
              <label className="input-group">
                <span>Delivery address</span>
                <input
                  type="text"
                  value={form.address}
                  onChange={handleChange('address')}
                  placeholder="Street, Hervanta, Tampere, Finland"
                />
              </label>
            </div>
            <p className="hint">We deliver within 15 km of Hervanta, Tampere.</p>
            {error && <div className="alert error">{error}</div>}
            <button className="primary-btn" type="submit" disabled={submitting}>
              {submitting ? 'Sending order...' : 'Request delivery'}
            </button>
          </form>

          <section className="card status-card">
            <div className="card-header">
              <h2>Order timeline</h2>
              <p>Live progress updates based on fleet signals.</p>
            </div>
            <div className="timeline">
              {phases.map((phase, index) => (
                <div key={phase.title} className={`timeline-step ${displayPhase >= index ? 'active' : ''}`}>
                  <div className="dot" />
                  <div>
                    <h3>{phase.title}</h3>
                    <p>{phase.body}</p>
                  </div>
                </div>
              ))}
            </div>
            <div className="message-feed">
              <h3>Delivery messages</h3>
              {messages.length === 0 ? (
                <p className="muted">No updates yet. Submit an order to start.</p>
              ) : (
                messages.map((message) => (
                  <div key={message.id} className={`message ${message.tone}`}>
                    {message.text}
                  </div>
                ))
              )}
            </div>
          </section>
        </main>

        <section className="card fleet-card">
          <div className="card-header">
            <h2>Fleet status</h2>
            <p>Live snapshot of drones and deliveries.</p>
          </div>
          <div className="fleet-grid">
            <div className="fleet-metric">
              <p>Total drones</p>
              <h3>{fleetSummary.total}</h3>
            </div>
            <div className="fleet-metric">
              <p>Available</p>
              <h3>{fleetSummary.available}</h3>
            </div>
            <div className="fleet-metric">
              <p>Unavailable</p>
              <h3>{fleetSummary.unavailable}</h3>
            </div>
            <div className="fleet-metric">
              <p>Offline</p>
              <h3>{fleetSummary.offline}</h3>
            </div>
          </div>
          <div className="flight-list">
            <h3>Active deliveries</h3>
            {flights.length === 0 ? (
              <p className="muted">No deliveries yet.</p>
            ) : (
              flights.slice(0, 6).map((flight) => (
                <div key={flight.id} className="flight-item">
                  <div>
                    <p className="flight-title">Drone {flight.droneId}</p>
                    <p className="flight-sub">
                      {flight.startingPoint.latitude}, {flight.startingPoint.longitude} to {flight.destination.latitude}, {flight.destination.longitude}
                    </p>
                  </div>
                  <span className={`pill status-${flight.status.replace(' ', '-').toLowerCase()}`}>
                    {flight.status}
                  </span>
                </div>
              ))
            )}
          </div>
        </section>
      </div>
    </div>
  );
}

export default App;
