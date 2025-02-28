package Model.Players;

import Model.Players.StrategyPlay.PlayerStrategy;

public class AIPlayer extends Player {
    private PlayerStrategy strategy;

    public AIPlayer(String name, PlayerStrategy strategy) {
        super(name, 2000);
        this.strategy = strategy;
    }

    public PlayerStrategy getStrategy() {
        return strategy;
    }

}
