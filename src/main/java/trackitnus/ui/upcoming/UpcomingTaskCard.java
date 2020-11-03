package trackitnus.ui.upcoming;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import trackitnus.model.task.Task;
import trackitnus.ui.UiPart;

/**
 * An UI component that displays information of a {@code task}.
 */
public class UpcomingTaskCard extends UiPart<Region> {
    private static final String FXML = "Upcoming/UpcomingTaskListCard.fxml";

    public final Task task;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label code;
    @FXML
    private Label name;
    @FXML
    private Label remark;

    /**
     * Creates a {@code TaskCard} with the given {@code Task} and index to display.
     */
    public UpcomingTaskCard(Task task, int displayedIndex) {
        super(FXML);
        this.task = task;
        id.setText("[" + displayedIndex + "] ");
        name.setText(task.getName().toString());
        remark.setText(task.getRemark());
        code.setText(task.getCode().isPresent() ? task.getCode().get().code + " " : "");
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof trackitnus.ui.task.TaskCard)) {
            return false;
        }

        // state check
        trackitnus.ui.upcoming.UpcomingTaskCard card = (trackitnus.ui.upcoming.UpcomingTaskCard) other;
        return id.getText().equals(card.id.getText())
            && name.equals(card.name);
    }
}