package co.com.domain.drone;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Position {

    private final Coordinate coordinate;
    private final Orientation orientation;

    public static Position buildNewPosition() {
        return Position.builder().coordinate(Coordinate.builder().x(0).y(0).build()).orientation(Orientation.NORTH).build();
    }

    @Override
    public String toString() {
        return coordinate.toString() + " direction " + orientation.name();
    }
}