import React from 'react';
import './Delivery.css';

function Delivery({ startingLatitude, startingLongitude, destLatitude, destLongitude, weight, drone, status }) {

    const canDeliver = status !== "DELIVERING" && status !== "DELIVERED";
    const statusClass = `status-pill status-${String(status).toLowerCase().replace(' ', '-')}`;

    function handleDeliver() {
        const fetchDelivery = async () => {
            try{
                const response = await fetch('http://localhost:8082/dronora/flight/deliver/' + drone );
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
            } catch (error) {
                console.error('Fetch error:', error);
            }
        };
        fetchDelivery();
    };

    return (
        <div className="delivery-card">
            <div className="delivery-header">
                <div>
                    <h3>Drone {drone}</h3>
                    <p className="muted">Payload {weight} kg</p>
                </div>
                <span className={statusClass}>{status}</span>
            </div>
            <div className="delivery-grid">
                <div>
                    <p className="label">From</p>
                    <p>{startingLatitude}, {startingLongitude}</p>
                </div>
                <div>
                    <p className="label">To</p>
                    <p>{destLatitude}, {destLongitude}</p>
                </div>
            </div>
            <button className="action-btn" onClick={handleDeliver} disabled={!canDeliver}>
                {canDeliver ? "Start delivery" : "In progress"}
            </button>
        </div>
    );
}

export default Delivery;
