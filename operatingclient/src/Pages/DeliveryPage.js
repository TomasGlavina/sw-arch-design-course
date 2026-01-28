import Delivery from "../components/Delivery";
import { useState, useEffect } from 'react';

function DeliveryPage() {

    const [data, setData] = useState(null);
    useEffect(() => {
        let isMounted = true;
        const fetchData = async () => {
            try {
                const response = await fetch('http://localhost:8082/dronora/flights');
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

    return (
        <>
            <div className="page">
                <h2 className="page-title">Deliveries</h2>
                <div className="stack">
                    {data && data.map((delivery, i) => (
                        <Delivery
                            key={i}
                            startingLatitude={delivery.startingPoint.latitude}
                            startingLongitude={delivery.startingPoint.longitude}
                            destLatitude={delivery.destination.latitude}
                            destLongitude={delivery.destination.longitude}
                            weight={delivery.weight}
                            drone={delivery.droneId}
                            status={delivery.status}
                        />
                    ))}
                </div>
            </div>
        </>
    );
}

export default DeliveryPage;
