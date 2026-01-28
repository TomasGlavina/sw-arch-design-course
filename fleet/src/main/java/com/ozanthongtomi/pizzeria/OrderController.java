package com.ozanthongtomi.pizzeria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RequestMapping("/dronepizza")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/pizzaorders")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest order) {
        try {
            return ResponseEntity.ok(orderService.processOrder(order));
        } catch (OrderException ex) {
            return ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage()));
        }
    }
}
