# Dronora - Drone Pizza Delivery (SW Architecture and Design Final Project)

This repository contains a multi-service simulation of a drone-enabled pizza delivery system. It was built as a Software Architecture and Design course final project, with emphasis on separation of concerns, service boundaries, and end-to-end flows across UI, backend, and device simulators.

---

## High-level architecture

The system is split into four main parts:

1) **Pizzeria UI (customerclient)**  
   - Customer-facing web app to request a delivery by address.
   - Sends orders to the Fleet backend.
   - Shows order progress, fleet snapshot, and delivery messages.

2) **Fleet Admin UI (operatingclient)**  
   - Internal admin console to monitor drones and deliveries.
   - Live views of drone status and flight status.

3) **Fleet Backend (fleet)**  
   - Central orchestration service.
   - Receives orders from the Pizzeria UI.
   - Geocodes addresses (OpenStreetMap Nominatim) and enforces delivery radius.
   - Assigns drones to deliveries.
   - Updates flight status and drone availability.

4) **Drone Simulator (deviceIOT + devices)**  
   - `deviceIOT` is the Spring Boot simulator for a drone device.
   - `devices/` contains per-drone configs and a symlinked jar.
   - Each drone registers itself with the Fleet backend on startup.
   - On shutdown, it marks itself OFFLINE.

---

## Component responsibilities

### Fleet backend
- **Order intake**: `/dronepizza/pizzaorders`
- **Geocoding**: OpenStreetMap Nominatim (server-side)
- **Business rules**:
  - Deliver only within 15 km radius of Hervanta, Tampere.
  - Reject orders outside the radius or with invalid addresses.
  - Reject orders when no drone is available.
- **Drone management**: create/update drones and track availability.
- **Flight lifecycle**:
  - New flight starts as `TO DELIVER`
  - When admin triggers delivery, it becomes `DELIVERING`
  - After simulated delivery, it becomes `DELIVERED`
  - Drone status flips between `AVAILABLE` and `UNAVAILABLE`

### Pizzeria UI
- Collects address + weight from the user.
- Shows timeline updates (local + backend state).
- Shows delivery messages and a live fleet snapshot.

### Fleet Admin UI
- Simple admin console for drones and deliveries.
- Shows drone availability (AVAILABLE / UNAVAILABLE / OFFLINE).
- Shows delivery status (TO DELIVER / DELIVERING / DELIVERED).

### Drone simulator
- Registers on startup by POSTing to Fleet.
- Sends OFFLINE status on shutdown (Ctrl+C).
- Note: hard kills (kill -9) do not trigger OFFLINE updates.

---

## Directory structure

```
sw-arch-design-course/
  customerclient/     # Pizzeria UI (React)
  operatingclient/    # Fleet admin UI (React)
  fleet/              # Fleet backend (Spring Boot)
  deviceIOT/          # Drone simulator backend (Spring Boot)
  devices/            # Drone instances (configs + symlinked jar)
```

---

## Ports

- Fleet backend: `8082`
- Pizzeria UI: `3000`
- Fleet admin UI: `3001`
- Drone simulator (default): `3120`
- Drone instances: `8031`, `8032`, ...

---

## Running the system

### 1) Start Fleet backend
```
cd /home/tglavina/projects/dronora/sw-arch-design-course/fleet
./mvnw spring-boot:run
```

### 2) Build the drone simulator jar
```
cd /home/tglavina/projects/dronora/sw-arch-design-course/deviceIOT
./mvnw -DskipTests package
```

### 3) Start drone instances
Each drone has its own config and port.
```
cd /home/tglavina/projects/dronora/sw-arch-design-course/devices/drone-8031
java -jar drone.jar

cd /home/tglavina/projects/dronora/sw-arch-design-course/devices/drone-8032
java -jar drone.jar
```

### 4) Start Pizzeria UI
```
cd /home/tglavina/projects/dronora/sw-arch-design-course/customerclient
npm install
npm start
```

### 5) Start Fleet Admin UI
```
cd /home/tglavina/projects/dronora/sw-arch-design-course/operatingclient
npm install
npm start
```

---

## Order flow (end-to-end)

1) User submits an address + weight in the Pizzeria UI.  
2) Fleet backend geocodes the address.  
3) Fleet checks if the destination is within 5 km of Hervanta.  
4) Fleet finds an AVAILABLE drone with enough capacity.  
5) Fleet creates a Flight and marks the drone UNAVAILABLE.  
6) Admin UI shows the new delivery.  
7) Admin triggers delivery; flight becomes DELIVERING.  
8) After simulation, flight becomes DELIVERED; drone returns to AVAILABLE.  

---

## APIs (key endpoints)

Fleet endpoints:

- `GET /dronora/drones`
- `GET /dronora/flights`
- `POST /dronora/drones`
- `PUT /dronora/drones/{id}`
- `GET /dronora/flight/deliver/{id}`

Pizzeria endpoint:

- `POST /dronepizza/pizzaorders`
  - Request body:
    ```json
    {
      "address": "Hervanta, Tampere, Finland",
      "weight": 4
    }
    ```
  - Responses:
    - `200 OK` with created Flight
    - `409` if no drones available
    - `422` if address invalid or out of radius

---

## Notes and assumptions

- Geocoding uses OpenStreetMap Nominatim (free, no API key required).
- Distance calculation uses a basic Haversine-like method.
- Drone OFFLINE is only sent on graceful shutdown (Ctrl+C).
- The system currently uses REST for all communication.

---

## Future improvement ideas

- Replace REST polling with WebSockets for real-time UI updates.
- Use MQTT for drone telemetry and status (LWT to auto-mark OFFLINE).
- Add persistent database configuration beyond in-memory defaults.
- Add richer delivery simulation (ETA, route, live position).

---

## License / Course context

Created as a final project for a Software Architecture and Design course.  
Primary focus: architecture, separation of concerns, and system integration.
