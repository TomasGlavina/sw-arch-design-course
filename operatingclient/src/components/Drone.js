
import './Drone.css';
import droneImage from './../assets/drone.png';

function Drone({ name, status, capacity }) {
    const statusClass = `status-pill status-${String(status).toLowerCase()}`;

    return (
        <div className="drone_card">
            <div className="drone_header">
                <img src={droneImage} className="drone_image" alt="droneImage" />
                <span className={statusClass}>{status}</span>
            </div>
            <div className="drone_body">
                <p className="drone_name">{name}</p>
                <p className="drone_meta">Capacity: {capacity}</p>
            </div>
        </div>
    );
}

export default Drone;
