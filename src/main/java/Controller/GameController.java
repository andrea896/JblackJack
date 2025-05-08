package Controller;

import Model.Game.GameEvent;
import Model.Game.GameModel;
import Model.Game.GameState;
import View.BlackJackViewImpl;

public class GameController {
    protected final GameModel model;
    protected final BlackJackViewImpl view;

    public GameController(GameModel model, BlackJackViewImpl view) {
        this.model = model;
        this.view = view;
    }

    public void initialize() {
        // Configura listener per l'avvio di una nuova partita
    }

    public void handleEvent(GameEvent event) {
        switch (event.getType()) {
            case GAME_STARTED:
                view.showGameStartAnimation();
                view.updateStatusMessage("La partita è iniziata!");
                break;

            case GAME_STATE_CHANGED:
                GameState newState = (GameState) event.getData().get("gameState");
                updateViewForNewState(newState);
                break;

            case DEALER_TURN_STARTED:
                view.updateStatusMessage("Turno del dealer");
                //view.getDealerView().highlight(true);
                break;

            case ROUND_ENDED:
                handleRoundEnded(event);
                break;

            // Altri eventi...
        }
    }

    private void updateViewForNewState(GameState state) {
        switch (state) {
            case PLAYER_TURN:
                view.updateStatusMessage("È il tuo turno");
                break;

            case AI_PLAYER_TURN:
                view.updateStatusMessage("Turno dei giocatori AI");
                break;

            case DEALER_TURN:
                view.updateStatusMessage("Turno del banco");
                break;

            case GAME_OVER:
                view.updateStatusMessage("Game Over");
                view.showPlayAgainButton(true);
                break;
        }
    }

    private void handleRoundEnded(GameEvent event) {
        view.showPlayAgainButton(true);
        updateViewForNewState(GameState.GAME_OVER);
    }

    private void handleExit() {
        // Implementa la logica per uscire dalla partita
    }
}