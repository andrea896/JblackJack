package View;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public abstract class GameComponentView extends Pane {
    protected final Label statusLabel;

    public GameComponentView() {
        getStyleClass().add("game-component");

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setVisible(false);
    }

    public void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> statusLabel.setVisible(false));
        pause.play();
    }

    public void highlight(boolean active) {
        if (active) {
            getStyleClass().add("highlighted");
        } else {
            getStyleClass().remove("highlighted");
        }
    }
}
