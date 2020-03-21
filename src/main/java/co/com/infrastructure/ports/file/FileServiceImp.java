package co.com.infrastructure.ports.file;

import co.com.domain.drone.Drone;
import co.com.domain.drone.Route;
import co.com.domain.drone.Instruction;
import co.com.domain.drone.Status;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileServiceImp implements FileService {

    private static final Logger logger = LogManager.getLogger(FileServiceImp.class);

    @Override
    public void writeFile(Path path, Drone drone) throws WritingException {
        String msg = Strings.EMPTY;

        try {
            logger.info("Write File");

            if (Status.FAILED.equals(drone.getStatus())){
                msg = msg.concat(drone.getPosition().toString().concat(" ").concat(Status.FAILED.name()));
            } else {
                msg = msg.concat(drone.getPosition().toString());
            }

            Files.write(Paths.get(path.toUri()), Collections.singleton(msg), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new WritingException(e);
        }
    }

    @Override
    public List<Route> readRoutes(Path path) throws ReadingException, InvalidInstructionException {
        logger.info("Read File");
        List<Route> routeList;

        try (Stream<String> stream = Files.lines(Paths.get(path.toUri().getPath()))) {
            routeList = stream.map(String::toUpperCase)
                    .map(this::readRoute).collect(Collectors.toList());
        } catch (IOException e) {
            throw new ReadingException(e);
        } catch (InvalidInstructionException e){
            throw new InvalidInstructionException(e);
        }

        return routeList;
    }

    private Route readRoute(String line) throws InvalidInstructionException{
        Route route = Route.builder().instructions(Collections.unmodifiableList(Collections.EMPTY_LIST)).build();

        for (char ch : line.toCharArray()) {
           route = Route.builder().instructions(route.addInstruction(validateInstruction(ch))).build();
        }

        return route;
    }


    private Instruction validateInstruction(char ch) {
        Config config = ConfigFactory.load();

        Instruction instruction;
        try {
            instruction = Instruction.valueOf(String.valueOf(ch));
        } catch (Exception e) {
            throw new InvalidInstructionException(config.getString("msg-error.invalid-instruction"));
        }
        return instruction;
    }

}