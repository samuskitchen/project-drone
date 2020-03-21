package co.com.commands;

import co.com.domain.drone.DroneLimitException;
import co.com.domain.drone.DroneNotFoundException;
import co.com.domain.drone.DroneService;
import co.com.domain.drone.DroneServiceImp;
import co.com.domain.drone.DeliveredLunches;
import co.com.domain.drone.OutOfPerimeterException;
import co.com.infrastructure.ports.file.FileService;
import co.com.infrastructure.ports.file.FileServiceImp;
import co.com.infrastructure.ports.file.InvalidFileNameException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CentralCommand {

    private static final Logger logger = LogManager.getLogger(CentralCommand.class);

    private static FileService fileService = new FileServiceImp();
    private static DroneService droneService = new DroneServiceImp();

    private final static Integer DRONE_LIMIT = 20;

    private static final DeliveryCommand deliveryCommand = new DeliveryCommand(fileService, droneService);

    public CentralCommand(FileService fileService, DroneService droneService) {
        CentralCommand.fileService = fileService;
        CentralCommand.droneService = droneService;
    }

    public void executeMain() {
        logger.info("Init Process");

        Config config = ConfigFactory.load();

        ExecutorService executorService = Executors.newFixedThreadPool(DRONE_LIMIT);


        Path pathFolderIn = Paths.get(String.format("%s%s" + config.getString("path-multiple-file.file-in"), System.getProperty("user.dir"), File.separator));
        try (Stream<Path> folder = Files.walk(Paths.get(pathFolderIn.toUri()))) {

            //First assignment execution
            CompletableFuture.runAsync(deliveryCommand::execute);

            CompletableFuture<List<Path>> files = CompletableFuture.supplyAsync(() -> folder.filter(Files::isRegularFile).filter(path -> !Strings.isEmpty(getDroneNumber(path.toString()))).collect(Collectors.toList()), executorService);

            if (files.get().size() > DRONE_LIMIT) {
                throw new DroneLimitException("Only one drone is allowed for delivery");
            }

            //Second assignment execution
            files.thenApply(paths -> {
                paths.forEach(pathFileIn -> {
                    CompletableFuture.supplyAsync(() -> DeliveredLunches.builder().numberDrone(getDroneNumber(pathFileIn.toString())).deliveries(deliveryCommand.executeMultiple(pathFileIn)).build(), executorService)
                            .thenApply(deliveredLunches -> {

                                Path filePathOut = getFilePathOut(config, deliveredLunches.getNumberDrone());
                                deliveredLunches.getDeliveries().forEach(drone -> {
                                    fileService.writeFile(filePathOut, drone);
                                });

                                return deliveredLunches;
                            });
                });

                return paths;
            });

        } catch (IOException | OutOfPerimeterException | InvalidInstructionException | ReadingException | WritingException | DroneNotFoundException | InvalidFileNameException | InterruptedException | ExecutionException e) {
            logger.error(e);
        } finally {
            executorService.shutdown();
        }
    }

    private static Path getFilePathOut(Config config, String droneNumber) {
        Path pathFileOut = null;

        try {
            String outputFileName = getOutputFileName(droneNumber);
            pathFileOut = Paths.get(String.format("%s%s" + config.getString("path-multiple-file.file-out") + outputFileName, System.getProperty("user.dir"), File.separator));
            new FileWriter(pathFileOut.toUri().getPath(), false).append(config.getString("msg-info.title-report")).append(Strings.LINE_SEPARATOR).close();
        } catch (IOException e) {
            logger.error(e);
        }

        return pathFileOut;
    }


    private static String getDroneNumber(String filePath) throws DroneNotFoundException {
        String droneNumberString = filePath.substring(filePath.lastIndexOf('/') + 1);
        validateFileName(droneNumberString);

        droneNumberString = droneNumberString.substring(droneNumberString.lastIndexOf('n') + 1, droneNumberString.lastIndexOf('.'));
        validateDroneNumber(droneNumberString);

        return droneNumberString;
    }

    private static String getOutputFileName(String droneNumberString) throws DroneNotFoundException {
        validateDroneNumber(droneNumberString);
        return "out".concat(droneNumberString).concat(".txt");
    }

    private static void validateDroneNumber(String number) {

        if (!Strings.isEmpty(number)) {
            Integer numberDrone = Integer.valueOf(number);

            if (numberDrone > DRONE_LIMIT) {
                throw new DroneNotFoundException("Drone " + numberDrone + " does not exist");
            }
        }
    }

    private static void validateFileName(String fileName) throws InvalidFileNameException {
        if (!fileName.contains("in")) {
            throw new InvalidFileNameException("Invalid File Name");
        }
    }

}
