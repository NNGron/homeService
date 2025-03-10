package ir.convert;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.dto.CarDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConvertToCarDTO<Car> implements Function<Car, CarDTO> {

    @Override
    public CarDTO apply(Car car) {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .convertValue(car, CarDTO.class);
    }

    public List<CarDTO> convertList(List<Car> cars) {
        return cars.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
