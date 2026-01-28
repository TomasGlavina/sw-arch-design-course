import Drone from "../components/Drone";
import { useState, useEffect } from 'react';

function DronePage() {

    const [data, setData] = useState(null);


    useEffect(() => {
        let isMounted = true;
        const fetchData = async () => {
            try {
                const response = await fetch('http://localhost:8082/dronora/drones');
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const jsonData = await response.json();
                if (isMounted) {
                    setData(jsonData);
                }
            } catch (error) {
                console.error('Fetch error:', error);
            }
        };
        fetchData();
        const intervalId = setInterval(fetchData, 3000);
        return () => {
            isMounted = false;
            clearInterval(intervalId);
        };
    }, [])
    /*useEffect(() => {
        fetch('http://localhost:8082/dronora/drones')
            .then(response => {
                if (response.ok) {
                    setData(response);
                }
                console.log(response);

            })
            .catch(error => console.error(error));
    }, [])*/

    return (
        <>
            <div className="page">
                <h2 className="page-title">Drones</h2>
                <div className="grid">
                    {data && data.map((drone, i) => (
                        <Drone
                            key={i}
                            name={drone.name}
                            status={drone.status}
                            capacity={drone.capacity}
                        />
                    ))}
                </div>
            </div>
        </>
    );
}

export default DronePage;
