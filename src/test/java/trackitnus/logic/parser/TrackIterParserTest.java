package trackitnus.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import trackitnus.commons.core.Messages;
import trackitnus.logic.commands.ExitCommand;
import trackitnus.logic.commands.HelpCommand;
import trackitnus.logic.commands.contact.AddContactCommand;
import trackitnus.logic.commands.contact.DeleteContactCommand;
import trackitnus.logic.commands.contact.EditContactCommand;
import trackitnus.logic.parser.exceptions.ParseException;
import trackitnus.model.contact.Contact;
import trackitnus.testutil.Assert;
import trackitnus.testutil.ContactUtil;
import trackitnus.testutil.builder.ContactBuilder;
import trackitnus.testutil.builder.EditContactDescriptorBuilder;
import trackitnus.testutil.typical.TypicalIndexes;

public class TrackIterParserTest {

    private final TrackIterParser parser = new TrackIterParser();

    @Test
    public void parseCommand_add() throws Exception {
        Contact contact = new ContactBuilder().build();
        AddContactCommand command = (AddContactCommand) parser.parseCommand(Contact.TYPE
            + " " + ContactUtil.getAddCommand(contact));
        assertEquals(new AddContactCommand(contact), command);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteContactCommand command = (DeleteContactCommand) parser.parseCommand(
            Contact.TYPE + " " + DeleteContactCommand.COMMAND_WORD + " " + TypicalIndexes.INDEX_FIRST
                .getOneBased());
        assertEquals(new DeleteContactCommand(TypicalIndexes.INDEX_FIRST), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Contact contact = new ContactBuilder().build();
        EditContactCommand.EditContactDescriptor descriptor = new EditContactDescriptorBuilder(contact).build();
        EditContactCommand command = (EditContactCommand) parser.parseCommand(Contact.TYPE + " "
            + EditContactCommand.COMMAND_WORD + " " + TypicalIndexes.INDEX_FIRST.getOneBased() + " "
            + ContactUtil.getEditContactDescriptorDetails(descriptor));
        assertEquals(new EditContactCommand(TypicalIndexes.INDEX_FIRST, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        Assert.assertThrows(ParseException.class, String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
            HelpCommand.MESSAGE_USAGE), () -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        Assert.assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(
            "unknownCommand"));
    }
}
