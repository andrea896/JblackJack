package Model.Game;

public enum Suit {

    HEARTS("Cuori"),
    DIAMONDS("Quadri"),
    CLUBS("Fiori"),
    SPADES("Picche");

    private final String name;

    Suit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
