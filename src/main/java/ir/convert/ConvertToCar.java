package ir.convert;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.entity.Car;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class ConvertToCar<CarDTO> implements Function<CarDTO, Car> {

    @Override
    public Car apply(CarDTO carDto) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(carDto, Car.class);
    }

    public List<Car> convertList(List<CarDTO> carDTOs) {
        return carDTOs.stream()
                .map(this)
                .toList();
    }
}
