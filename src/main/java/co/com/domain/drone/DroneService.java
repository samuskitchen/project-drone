package co.com.domain.drone;

public interface DroneService {

    Drone deliverWithRoute(Drone drone, Route route) throws OutOfPerimeterException;
}
