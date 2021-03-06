package trackitnus.logic.commands;

import static trackitnus.commons.core.Messages.MESSAGE_EXIT_ACKNOWLEDGEMENT;
import static trackitnus.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import trackitnus.model.Model;
import trackitnus.model.ModelManager;

public class ExitCommandTest {
    private final Model model = new ModelManager();
    private final Model expectedModel = new ModelManager();

    @Test
    public void execute_exit_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true, null, null,
            "");
        assertCommandSuccess(new ExitCommand(), model, expectedCommandResult, expectedModel);
    }
}
