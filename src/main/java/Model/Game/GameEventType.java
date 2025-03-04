package Model.Game;

public enum GameEventType {
    // Eventi del flusso di gioco
    GAME_STARTED,
    ROUND_STARTED,
    ROUND_ENDED,
    GAME_STATE_CHANGED,

    // Eventi delle carte
    CARD_DEALT,
    HAND_UPDATED,

    // Eventi delle azioni
    PLAYER_HIT,
    PLAYER_STAND,
    PLAYER_BUSTED,
    HAND_SPLIT,
    DOUBLE_DOWN_EXECUTED,

    // Eventi del dealer
    DEALER_CARD_REVEALED,
    DEALER_TURN_STARTED,
    DEALER_BUSTED,

    // Eventi dei risultati
    PLAYER_WINS,
    DEALER_WINS,
    PUSH,
    BLACKJACK_ACHIEVED,

    // Eventi delle scommesse
    BET_PLACED,
    INSURANCE_OFFERED,
    INSURANCE_ACCEPTED,
    INSURANCE_DECLINED,
    WINNINGS_PAID
}
