package Model.Game;

/**
 * Enumerazione che definisce tutti i tipi di eventi che possono verificarsi durante il gioco di BlackJack.
 * Utilizzata dal sistema di notifiche per informare gli osservatori sui cambiamenti di stato del gioco.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public enum GameEventType {
    GAME_STARTED,
    ROUND_STARTED,
    ROUND_ENDED,
    GAME_STATE_CHANGED,

    CARD_DEALT,
    HAND_UPDATED,
    PLAYER_HIT,
    PLAYER_STAND,
    PLAYER_BUSTED,
    HAND_SPLIT,
    DOUBLE_DOWN_EXECUTED,

    DEALER_CARD_REVEALED,
    DEALER_TURN_STARTED,
    DEALER_BUSTED,

    PLAYER_WINS,
    DEALER_WINS,
    PUSH,
    BLACKJACK_ACHIEVED,

    BET_PLACED,
    INSURANCE_OFFERED,
    INSURANCE_ACCEPTED,
    INSURANCE_DECLINED,
    WINNINGS_PAID
}
