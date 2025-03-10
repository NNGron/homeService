package ir.service;

import ir.convert.ConvertToCar;
import ir.dto.CarDTO;
import ir.entity.Car;
import ir.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@RequiredArgsConstructor
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;
    private final ConvertToCar carMapper;

    public CarService(CarRepository carRepository, ConvertToCar carMapper) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
    }

    public String save(CarDTO carDTO) {
        Car car = carMapper.apply(carDTO);
        carRepository.save(car);
        return "Saved";
    }
}
