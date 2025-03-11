package View;

import Controller.BlackjackActionListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ControlPanelView extends HBox {
    private final Button hitButton;
    private final Button standButton;
    private final Button doubleDownButton;
    private final Button splitButton;
    private final Button exitButton;
    private BlackjackActionListener actionListener;

    public ControlPanelView() {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(15));
        setPrefSize(800, 200);

        // Crea i pulsanti con le classi di stile appropriate
        hitButton = createButton("HIT", "hit-button");
        standButton = createButton("STAND", "stand-button");
        doubleDownButton = createButton("DOUBLE DOWN", "double-button");
        splitButton = createButton("SPLIT", "split-button");
        exitButton = createButton("MENU", "exit-button");

        // Disabilita pulsanti all'inizio
        hitButton.setDisable(true);
        standButton.setDisable(true);
        doubleDownButton.setDisable(true);
        splitButton.setDisable(true);

        // Aggiungi listener per i pulsanti
        setupButtonListeners();

        // Aggiungi pulsanti al layout
        getChildren().addAll(hitButton, standButton, doubleDownButton, splitButton, exitButton);
        setHgrow(hitButton, Priority.ALWAYS);
        setHgrow(standButton, Priority.ALWAYS);
        setHgrow(doubleDownButton, Priority.ALWAYS);
        setHgrow(splitButton, Priority.ALWAYS);
        setHgrow(exitButton, Priority.ALWAYS);
    }

    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setPrefSize(200, 50);
        return button;
    }

    private void setupButtonListeners() {
        hitButton.setOnAction(e -> {
            if (actionListener != null) {
                actionListener.onHitButtonPressed();
            }
        });

        standButton.setOnAction(e -> {
            if (actionListener != null) {
                actionListener.onStandButtonPressed();
            }
        });

        doubleDownButton.setOnAction(e -> {
            if (actionListener != null) {
                actionListener.onDoubleDownPressed();
            }
        });

        splitButton.setOnAction(e -> {
            if (actionListener != null) {
                actionListener.onSplitButtonPressed();
            }
        });

        exitButton.setOnAction(e -> {
            if (actionListener != null) {
                actionListener.onExitButtonPressed();
            }
        });
    }

    public void setActionListener(BlackjackActionListener listener) {
        this.actionListener = listener;
    }

    public void updateControls(boolean canHit, boolean canStand, boolean canDoubleDown, boolean canSplit) {
        hitButton.setDisable(!canHit);
        standButton.setDisable(!canStand);
        doubleDownButton.setDisable(!canDoubleDown);
        splitButton.setDisable(!canSplit);
    }
}