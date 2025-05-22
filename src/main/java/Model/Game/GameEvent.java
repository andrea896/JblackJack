package Model.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * Rappresenta un evento del gioco di BlackJack.
 * Contiene informazioni sul tipo di evento e dati associati per fornire contesto agli osservatori.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class GameEvent {
    private final GameEventType type;
    private final Map<String, Object> data;

    /**
     * Costruisce un nuovo evento di gioco.
     * 
     * @param type Il tipo di evento
     * @param data I dati associati all'evento (può essere null)
     */
    public GameEvent(GameEventType type, Map<String, Object> data) {
        this.type = type;
        this.data = data != null ? data : new HashMap<>();
    }

    /**
     * Restituisce il tipo di evento.
     * 
     * @return Il tipo di evento
     */
    public GameEventType getType() {
        return type;
    }

    /**
     * Restituisce i dati associati all'evento.
     * 
     * @return Una mappa contenente i dati dell'evento
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Metodo factory per creare eventi con coppie chiave-valore.
     * Facilita la creazione di eventi con dati multipli.
     * 
     * @param type Il tipo di evento
     * @param keyValuePairs Coppie alternanti di chiavi (String) e valori (Object)
     * @return Un nuovo GameEvent con i dati specificati
     * @throws IllegalArgumentException Se il numero di parametri non è pari
     */
    public static GameEvent create(GameEventType type, Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0)
            throw new IllegalArgumentException("Devi fornire coppie chiave-valore");

        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2)
            data.put((String) keyValuePairs[i], keyValuePairs[i + 1]);

        return new GameEvent(type, data);
    }
}
