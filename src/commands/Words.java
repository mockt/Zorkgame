package commands;

import java.util.Arrays;
import java.util.List;

public class Words {
    private final List<String> validCommands = Arrays.asList("go", "quit", "help", "back", "pickup", "drop");

    public boolean isCommand(String commandWord) {
        return validCommands.stream().anyMatch(c -> c.equals(commandWord));
    }

    public String showAll() {
        return String.join(" ", validCommands);
    }
}
