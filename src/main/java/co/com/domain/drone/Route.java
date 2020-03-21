package co.com.domain.drone;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Builder(toBuilder = true)
@Getter
public class Route {

    private final List<Instruction> instructions;

    public List<Instruction> addInstruction(Instruction instruction){
        List<Instruction> newInstructions = new ArrayList<>(this.instructions);
        newInstructions.add(instruction);

        return Collections.unmodifiableList(newInstructions);
    }

    public List<Instruction> pullInstruction(){
        List<Instruction> newInstructions = new ArrayList<>(this.instructions);
        newInstructions.remove(0);

        return Collections.unmodifiableList(newInstructions);
    }

}
