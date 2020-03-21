package co.com;

import co.com.commands.CentralCommand;
import co.com.domain.drone.DroneService;
import co.com.domain.drone.DroneServiceImp;
import co.com.infrastructure.ports.file.FileService;
import co.com.infrastructure.ports.file.FileServiceImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectDroneMain {

    private static final Logger logger = LogManager.getLogger(ProjectDroneMain.class);

    private static FileService fileService = new FileServiceImp();
    private static DroneService droneService = new DroneServiceImp();

    private static final CentralCommand centralCommand = new CentralCommand(fileService, droneService);

    public static void main(String[] args) {
        logger.info("Init Process");
        centralCommand.executeMain();
    }
}