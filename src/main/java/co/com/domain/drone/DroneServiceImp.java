package co.com.domain.drone;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DroneServiceImp implements DroneService {

    private static final Logger logger = LogManager.getLogger(DroneServiceImp.class);

    @Override
    public Drone deliverWithRoute(Drone drone, Route route) throws OutOfPerimeterException {
        logger.info("Initiating delivery in: " + drone);

        Drone droneValidated = validateBlocks(drone);

        if(!route.getInstructions().isEmpty() && !Status.FAILED.equals(droneValidated.getStatus())){
            return deliverWithRoute(moveDron(droneValidated, route.getInstructions().get(0)), Route.builder().instructions(route.pullInstruction()).build());
        } else if(route.getInstructions().isEmpty()){
            droneValidated = droneValidated.toBuilder().status(Status.DELIVERED).build();
        }

        return droneValidated;
    }

    private Drone moveDron(Drone drone, Instruction instruction) {

        return switch (instruction) {
            case A:
                yield moveForward(drone.getPosition());
            case I:
                yield turnLeft(drone.getPosition());
            case D:
                yield turnRight(drone.getPosition());
        };
    }

    private Drone moveForward(Position position) {
        Position newPosition;

        return switch (position.getOrientation()) {
            case NORTH:
                newPosition = Position.builder().coordinate(position.getCoordinate().newCoordinate(Coordinate.builder().x(0).y(1).build())).orientation(position.getOrientation()).build();
                yield Drone.builder().position(newPosition).status(Status.MOVING).build();
            case SOUTH:
                newPosition = Position.builder().coordinate(position.getCoordinate().newCoordinate(Coordinate.builder().x(0).y(-1).build())).orientation(position.getOrientation()).build();
                yield Drone.builder().position(newPosition).status(Status.MOVING).build();
            case EAST:
                newPosition = Position.builder().coordinate(position.getCoordinate().newCoordinate(Coordinate.builder().x(1).y(0).build())).orientation(position.getOrientation()).build();
                yield Drone.builder().position(newPosition).status(Status.MOVING).build();
            case WEST:
                newPosition = Position.builder().coordinate(position.getCoordinate().newCoordinate(Coordinate.builder().x(-1).y(0).build())).orientation(position.getOrientation()).build();
                yield Drone.builder().position(newPosition).status(Status.MOVING).build();
        };
    }

    private Drone turnLeft(Position position) {
        return switch (position.getOrientation()) {
            case NORTH:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.WEST).build()).build();
            case EAST:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.NORTH).build()).build();
            case WEST:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.SOUTH).build()).build();
            case SOUTH:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.EAST).build()).build();
        };
    }

    private Drone turnRight(Position position) {
       return switch (position.getOrientation()) {
            case NORTH:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.EAST).build()).build();
            case EAST:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.SOUTH).build()).build();
            case WEST:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.NORTH).build()).build();
            case SOUTH:
                yield Drone.builder().position(Position.builder().coordinate(position.getCoordinate()).orientation(Orientation.WEST).build()).build();
        };
    }

    private Drone validateBlocks(Drone drone) {
        logger.info("Validate Blocks");
        Config config = ConfigFactory.load();
        OutOfPerimeterException outOfPerimeterException = new OutOfPerimeterException(config.getString("msg-error.invalid-blocks"));
        Drone droneValidate = drone;


        if (drone.getPosition().getCoordinate().getX() > 10 || drone.getPosition().getCoordinate().getX() < -10) {
            logger.error(outOfPerimeterException);
            droneValidate = drone.toBuilder().status(Status.FAILED).build();
        } else if (drone.getPosition().getCoordinate().getY() > 10 || drone.getPosition().getCoordinate().getY() < -10) {
            logger.error(outOfPerimeterException);
            droneValidate = drone.toBuilder().status(Status.FAILED).build();
        }

        return droneValidate;
    }
}