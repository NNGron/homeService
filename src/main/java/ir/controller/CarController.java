package ir.controller;

import ir.dto.CarDTO;
import ir.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping(value = "/save")
    public ResponseEntity<String> saveCar(@RequestBody CarDTO dto) {
        String message = carService.save(dto);
        return ResponseEntity.ok(message);
    }
}
