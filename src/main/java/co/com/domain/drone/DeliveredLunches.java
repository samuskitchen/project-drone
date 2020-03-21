package co.com.domain.drone;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class DeliveredLunches {

    private final String numberDrone;
    private final List<Drone> deliveries;
}
