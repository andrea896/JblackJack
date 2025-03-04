package View;

import Controller.BlackjackActionListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;;


public class ControlPanelView extends HBox {
    private final Button hitButton;
    private final Button standButton;
    private final Button doubleDownButton;
    private final Button splitButton;
    private final Button exitButton;

    private BlackjackActionListener actionListener;

    public ControlPanelView() {
        setAlignment(javafx.geometry.Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));

        // Crea i pulsanti
        hitButton = createButton("Hit", "hit-button");
        standButton = createButton("Stand", "stand-button");
        doubleDownButton = createButton("Double Down", "double-button");
        splitButton = createButton("Split", "split-button");
        exitButton = createButton("Menu", "exit-button");

        // Disabilita pulsanti all'inizio
        hitButton.setDisable(true);
        standButton.setDisable(true);
        doubleDownButton.setDisable(true);
        splitButton.setDisable(true);

        // Aggiungi listener per i pulsanti
        setupButtonListeners();

        // Aggiungi pulsanti al layout
        getChildren().addAll(hitButton, standButton, doubleDownButton, splitButton, exitButton);
    }

    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
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
