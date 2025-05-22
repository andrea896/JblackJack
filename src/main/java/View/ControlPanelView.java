package View;

import Controller.BlackjackActionListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Pannello di controllo per le azioni del giocatore nel BlackJack.
 * Contiene i pulsanti per Hit, Stand, Double Down e Split.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class ControlPanelView extends HBox {
    private final Button hitButton;
    private final Button standButton;
    private final Button doubleDownButton;
    private final Button splitButton;
    private BlackjackActionListener actionListener;

    /**
     * Costruisce il pannello di controllo con tutti i pulsanti di azione.
     * I pulsanti sono inizialmente disabilitati fino a quando il gioco non inizia.
     */
    public ControlPanelView() {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(15));
        setPrefSize(800, 200);

        hitButton = createButton("HIT", "hit-button");
        standButton = createButton("STAND", "stand-button");
        doubleDownButton = createButton("D. DOWN", "double-button");
        splitButton = createButton("SPLIT", "split-button");

        hitButton.setDisable(true);
        standButton.setDisable(true);
        doubleDownButton.setDisable(true);
        splitButton.setDisable(true);

        setupButtonListeners();

        getChildren().addAll(hitButton, standButton, doubleDownButton, splitButton);
        setHgrow(hitButton, Priority.ALWAYS);
        setHgrow(standButton, Priority.ALWAYS);
        setHgrow(doubleDownButton, Priority.ALWAYS);
        setHgrow(splitButton, Priority.ALWAYS);
    }

    /**
     * Crea un pulsante con testo e classe di stile specificati.
     * 
     * @param text Il testo del pulsante
     * @param styleClass La classe CSS da applicare
     * @return Il pulsante creato
     */
    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setPrefSize(200, 50);
        return button;
    }

    /**
     * Configura i listener per tutti i pulsanti del pannello.
     * Ogni pulsante chiama il metodo corrispondente nel listener.
     */
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
    }

    /**
     * Imposta il listener per le azioni dei pulsanti.
     * 
     * @param listener Il listener che gestirà le azioni dei pulsanti
     */
    public void setActionListener(BlackjackActionListener listener) {
        this.actionListener = listener;
    }

    /**
     * Aggiorna lo stato di abilitazione/disabilitazione dei controlli.
     * 
     * @param canHit Se il pulsante Hit deve essere abilitato
     * @param canStand Se il pulsante Stand deve essere abilitato
     * @param canDoubleDown Se il pulsante Double Down deve essere abilitato
     * @param canSplit Se il pulsante Split deve essere abilitato
     */
    public void updateControls(boolean canHit, boolean canStand, boolean canDoubleDown, boolean canSplit) {
        hitButton.setDisable(!canHit);
        standButton.setDisable(!canStand);
        doubleDownButton.setDisable(!canDoubleDown);
        splitButton.setDisable(!canSplit);
    }
}
