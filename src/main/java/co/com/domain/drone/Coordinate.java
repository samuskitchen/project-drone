package co.com.domain.drone;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Coordinate {

    private final Integer x;
    private final Integer y;

    public Coordinate newCoordinate(Coordinate coordinate){
        Integer newX = this.getX() + coordinate.getX();
        Integer newY = this.getY() + coordinate.getY();

        return Coordinate.builder().x(newX).y(newY).build();
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
