package co.com.infrastructure.ports.file;

import co.com.domain.drone.Drone;
import co.com.domain.drone.Route;

import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<Route> readRoutes(Path path) throws ReadingException, InvalidInstructionException;

    void writeFile(Path path, Drone drone) throws WritingException;

}
