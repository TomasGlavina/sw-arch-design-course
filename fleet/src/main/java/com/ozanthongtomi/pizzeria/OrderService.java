package com.ozanthongtomi.pizzeria;

import com.ozanthongtomi.drones.helper.Helper;
import com.ozanthongtomi.drones.model.Flight;
import com.ozanthongtomi.drones.model.Location;
import com.ozanthongtomi.drones.model.NewFlightRequest;
import org.springframework.stereotype.Service;
import com.ozanthongtomi.drones.service.FlightService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.text.Normalizer;

@Service
public class OrderService {
    private FlightService flightService;
    private static final double PIZZERIA_LATITUDE = 61.4481;
    private static final double PIZZERIA_LONGITUDE = 23.8521;
    private static final double MAX_RADIUS_KM = 15.0;

    public OrderService(FlightService flightService) {
        this.flightService = flightService;
    }
    public Flight processOrder(OrderRequest order) {
        if (order == null) {
            throw new OrderException(400, "Invalid order request.");
        }
        if (order.getWeight() <= 0) {
            throw new OrderException(422, "Weight must be greater than 0.");
        }

        Location destination = geocodeAddress(order.getAddress());
        if (destination == null) {
            throw new OrderException(422, "Address not found. Please use a full street address.");
        }

        Location pizzeriaLocation = new Location(PIZZERIA_LATITUDE, PIZZERIA_LONGITUDE);
        Helper helper = new Helper();
        double distance = helper.calculateDistance(pizzeriaLocation, destination);
        if (distance > MAX_RADIUS_KM) {
            throw new OrderException(422, "We only deliver within 15 km of Hervanta, Tampere.");
        }

        NewFlightRequest request = new NewFlightRequest(order.getWeight(), destination, pizzeriaLocation);
        Flight newFlight = flightService.createFlight(request);
        if (newFlight == null) {
            throw new OrderException(409, "No drones available right now.");
        }
        return newFlight;
    }

    private Location geocodeAddress(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }
        Location pizzeriaLocation = new Location(PIZZERIA_LATITUDE, PIZZERIA_LONGITUDE);
        Location directMatch = geocodeWithQuery(address, pizzeriaLocation);
        if (directMatch != null) {
            return directMatch;
        }
        Location tampereMatch = geocodeWithQuery(address + ", Tampere, Finland", pizzeriaLocation);
        if (tampereMatch != null) {
            return tampereMatch;
        }
        Location finlandMatch = geocodeWithQuery(address + ", Finland", pizzeriaLocation);
        if (finlandMatch != null) {
            return finlandMatch;
        }

        String stripped = stripDiacritics(address);
        if (!stripped.equals(address)) {
            Location strippedDirect = geocodeWithQuery(stripped, pizzeriaLocation);
            if (strippedDirect != null) {
                return strippedDirect;
            }
            Location strippedTampere = geocodeWithQuery(stripped + ", Tampere, Finland", pizzeriaLocation);
            if (strippedTampere != null) {
                return strippedTampere;
            }
            return geocodeWithQuery(stripped + ", Finland", pizzeriaLocation);
        }
        return null;
    }

    private Location geocodeWithQuery(String query, Location pizzeriaLocation) {
        String uri = UriComponentsBuilder
            .fromHttpUrl("https://nominatim.openstreetmap.org/search")
            .queryParam("q", query)
            .queryParam("format", "jsonv2")
            .queryParam("accept-language", "fi")
            .queryParam("limit", 5)
            .queryParam("email", "team@dronora.local")
            .queryParam("viewbox", "23.55,61.60,24.20,61.35")
            .queryParam("bounded", 1)
            .encode()
            .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Dronora-Study-Project/1.0 (team@dronora.local)");
        headers.set("Accept-Language", "fi");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<NominatimResult[]> response =
            restTemplate.exchange(uri, HttpMethod.GET, entity, NominatimResult[].class);
        NominatimResult[] results = response.getBody();
        if (results == null || results.length == 0) {
            return null;
        }
        return pickClosestResult(results, pizzeriaLocation);
    }

    private Location pickClosestResult(NominatimResult[] results, Location pizzeriaLocation) {
        Helper helper = new Helper();
        NominatimResult best = null;
        double bestDistance = Double.MAX_VALUE;
        for (NominatimResult result : results) {
            if (result.getLat() == null || result.getLon() == null) {
                continue;
            }
            Location candidate = new Location(result.getLat(), result.getLon());
            double distance = helper.calculateDistance(pizzeriaLocation, candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = result;
            }
        }
        if (best == null) {
            return null;
        }
        return new Location(best.getLat(), best.getLon());
    }

    private String stripDiacritics(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
