package Model.Game;

import java.util.HashMap;
import java.util.Map;

public class GameEvent {
    private final GameEventType type;
    private final Map<String, Object> data;

    public GameEvent(GameEventType type, Map<String, Object> data) {
        this.type = type;
        this.data = data != null ? data : new HashMap<>();
    }

    public GameEventType getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    // Metodo helper per creare eventi con più dati
    public static GameEvent create(GameEventType type, Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0)
            throw new IllegalArgumentException("Devi fornire coppie chiave-valore");

        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2)
            data.put((String) keyValuePairs[i], keyValuePairs[i + 1]);

        return new GameEvent(type, data);
    }
}
