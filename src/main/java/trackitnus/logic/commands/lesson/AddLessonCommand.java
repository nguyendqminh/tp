package trackitnus.logic.commands.lesson;

import static java.util.Objects.requireNonNull;
import static trackitnus.commons.core.Messages.MESSAGE_MODULE_DOES_NOT_EXIST;
import static trackitnus.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static trackitnus.logic.parser.CliSyntax.PREFIX_CODE;
import static trackitnus.logic.parser.CliSyntax.PREFIX_DATE;
import static trackitnus.logic.parser.CliSyntax.PREFIX_TYPE;

import trackitnus.commons.core.Messages;
import trackitnus.logic.commands.Command;
import trackitnus.logic.commands.CommandResult;
import trackitnus.logic.commands.exceptions.CommandException;
import trackitnus.model.Model;
import trackitnus.model.lesson.Lesson;

public final class AddLessonCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = Lesson.TYPE + " " + COMMAND_WORD
        + ": Adds a lesson to the app.\n"
        + "Parameters: "
        + PREFIX_CODE + "CODE "
        + PREFIX_TYPE + "TYPE "
        + PREFIX_DATE + "DATE "
        + PREFIX_ADDRESS + "ADDRESS\n"
        + "Example: " + Lesson.TYPE + " " + COMMAND_WORD + " "
        + PREFIX_CODE + "CS2103T "
        + PREFIX_TYPE + "tutorial "
        + PREFIX_DATE + "Wed 14:00-15:00 "
        + PREFIX_ADDRESS + "COM1";

    private final Lesson toAdd;

    /**
     * Creates an AddLessonCommand to add the specified {@code Lesson}
     */
    public AddLessonCommand(Lesson lesson) {
        requireNonNull(lesson);
        toAdd = lesson;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.hasModule(toAdd.getCode())) {
            throw new CommandException(MESSAGE_MODULE_DOES_NOT_EXIST);
        }

        if (model.hasLesson(toAdd)) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_LESSON);
        }

        model.addLesson(toAdd);
        return new CommandResult(String.format(Messages.MESSAGE_ADD_LESSON_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof AddLessonCommand // instanceof handles nulls
            && toAdd.equals(((AddLessonCommand) other).toAdd));
    }
}
