package co.com.commands;

import co.com.domain.drone.Drone;
import co.com.domain.drone.DroneService;
import co.com.domain.drone.OutOfPerimeterException;
import co.com.domain.drone.Route;
import co.com.infrastructure.ports.file.FileService;
import co.com.infrastructure.ports.file.InvalidInstructionException;
import co.com.infrastructure.ports.file.ReadingException;
import co.com.infrastructure.ports.file.WritingException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class DeliveryCommand {

    private static final Logger logger = LogManager.getLogger(DeliveryCommand.class);

    private final FileService fileService;
    private final DroneService droneService;

    public DeliveryCommand(FileService fileService, DroneService droneService) {
        this.fileService = fileService;
        this.droneService = droneService;
    }

    public void execute() {
        logger.info("Start One Drone");
        Config config = ConfigFactory.load();

        try {
            Path pathFileIn = Paths.get(getClass().getClassLoader().getResource(config.getString("path-file.file-in")).getPath());
            List<Route> routeList = fileService.readRoutes(pathFileIn);

            Path pathFileOut = Paths.get(String.format("%s%s" + config.getString("path-file.file-out"), System.getProperty("user.dir"), File.separator));
            new FileWriter(pathFileOut.toUri().getPath()).append(config.getString("msg-info.title-report")).append(Strings.LINE_SEPARATOR).close();

            final Drone drone = Drone.buildNewDrone();
            routeList.forEach(route -> {
                Drone droneResult = droneService.deliverWithRoute(drone, route);
                fileService.writeFile(pathFileOut, droneResult);
            });

        } catch (IOException | OutOfPerimeterException | InvalidInstructionException | ReadingException | WritingException e) {
            logger.error(e);
        }
    }


    public List<Drone> executeMultiple(Path pathFileIn) {
        logger.info("Start Multiple Drone");
        List<Drone> droneList = new ArrayList<>();

        try {
            List<Route> routeList = fileService.readRoutes(pathFileIn);

            final Drone drone = Drone.buildNewDrone();
            routeList.forEach(route -> {
                Drone droneResult = droneService.deliverWithRoute(drone, route);
                droneList.add(droneResult);
            });

        } catch (OutOfPerimeterException | InvalidInstructionException | ReadingException | WritingException e) {
            logger.error(e);
        }

        return droneList;
    }
}