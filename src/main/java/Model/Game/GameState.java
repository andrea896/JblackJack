package Model.Game;

/**
 * Enumerazione che rappresenta i possibili stati del gioco di BlackJack.
 * Utilizzata per controllare il flusso del gioco e determinare quali azioni sono permesse.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public enum GameState {
    WAITING_FOR_PLAYERS,
    PLAYER_TURN,
    DEALER_TURN,
    AI_PLAYER_TURN,
    GAME_OVER
}
