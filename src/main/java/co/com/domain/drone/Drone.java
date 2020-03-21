package co.com.domain.drone;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Drone {

    private final Position position;
    private final Status status;

    public static Drone buildNewDrone() {
        return Drone.builder().position(Position.buildNewPosition()).status(Status.INITIATED).build();
    }

    @Override
    public String toString() {
        return position.toString();
    }
}