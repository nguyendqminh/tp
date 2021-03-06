package trackitnus.logic.parser.lesson;

import trackitnus.commons.core.Messages;
import trackitnus.commons.core.index.Index;
import trackitnus.logic.commands.lesson.DeleteLessonCommand;
import trackitnus.logic.parser.Parser;
import trackitnus.logic.parser.ParserUtil;
import trackitnus.logic.parser.exceptions.InvalidIndexException;
import trackitnus.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteLessonCommand object
 */
public class DeleteLessonCommandParser implements Parser<DeleteLessonCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteLessonCommand
     * and returns a DeleteLessonCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteLessonCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteLessonCommand(index);
        } catch (InvalidIndexException iie) {
            throw new InvalidIndexException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        } catch (ParseException pe) {
            throw new ParseException(
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteLessonCommand.MESSAGE_USAGE), pe);
        }
    }


}
