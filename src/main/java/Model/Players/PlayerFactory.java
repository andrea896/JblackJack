package Model.Players;

import Model.Players.StrategyPlay.AggressiveStrategy;
import Model.Players.StrategyPlay.PlayerStrategy;

public class PlayerFactory {

    public static Player createHumanPlayer(String playerName, int intialBalance) {
        return new Player(playerName, intialBalance);
    }

    public static Player createAIPlayer(String playerName, PlayerStrategy strategy) {
        return new AIPlayer(playerName, strategy);
    }

    public static Dealer createDealer() {
        return new Dealer(new AggressiveStrategy());
    }
}
