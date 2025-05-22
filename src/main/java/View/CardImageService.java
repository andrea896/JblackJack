package View;

import Model.Game.Objects.Card;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Servizio per la gestione delle immagini delle carte da gioco.
 * Carica e fornisce accesso alle immagini delle carte e ai dorsi personalizzabili.
 * Implementa un pattern di cache per ottimizzare le performance.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class CardImageService {
    private static final Map<String, Image> cardImages = new HashMap<>();
    private static boolean initialized = false;
    private static Image cardBackImage;

    /**
     * Inizializza il servizio caricando tutte le immagini delle carte.
     * Carica le immagini per tutti i 4 semi e 13 ranghi (52 carte totali).
     */
    public static void initialize() {
        if (initialized) return;

        try {
            String[] suits = {"hearts", "diamonds", "clubs", "spades"};
            String[] ranks = {"two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king", "ace"};

            for (String suit : suits) {
                for (String rank : ranks) {
                    String key = rank + "_of_" + suit;
                    String path = "/GameView/Images/" + key + ".png";
                    try {
                        cardImages.put(key, new Image(CardImageService.class.getResourceAsStream(path)));
                    } catch (Exception e) {
                        System.err.println("Impossibile caricare l'immagine: " + path);
                    }
                }
            }
            initialized = true;
        } catch (Exception e) {
            System.err.println("Errore nell'inizializzazione del CardImageService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Imposta l'immagine del dorso della carta in base alla selezione dell'utente.
     * Permette di personalizzare l'aspetto del dorso delle carte.
     *
     * @param backDesign Il percorso al design del dorso selezionato dall'utente
     */
    public static void setCardBackDesign(String backDesign) {
        backDesign = "/GameMenu/" + backDesign;
        try {
            cardBackImage = new Image(CardImageService.class.getResourceAsStream(backDesign));
        } catch (Exception e) {
            System.err.println("Impossibile caricare l'immagine del dorso della carta: " + backDesign);
        }
    }

    /**
     * Restituisce l'immagine per una carta specifica.
     * 
     * @param card La carta di cui ottenere l'immagine
     * @return L'immagine della carta, null se non trovata
     */
    public static Image getCardImage(Card card) {
        if (!initialized)
            initialize();
        return cardImages.get(card.toString().toLowerCase());
    }

    /**
     * Crea un ImageView per il dorso della carta con styling predefinito.
     * Include ombreggiatura e dimensioni standard.
     * 
     * @return Un ImageView configurato per il dorso della carta
     */
    public static ImageView getCardBackImageView() {
        if (!initialized) initialize();
        ImageView cardBackview = new ImageView(cardBackImage);
        cardBackview.setFitHeight(75);
        cardBackview.setPreserveRatio(true);
        cardBackview.setSmooth(true);
        cardBackview.setEffect(new DropShadow(5, Color.BLACK));
        return cardBackview;
    }

    /**
     * Crea un ImageView per una carta specifica con styling predefinito.
     * Include ombreggiatura e dimensioni standard.
     * 
     * @param card La carta di cui creare l'ImageView
     * @return Un ImageView configurato per la carta specificata
     */
    public static ImageView createCardImageView(Card card) {
        ImageView cardView = new ImageView(getCardImage(card));
        cardView.setFitHeight(75);
        cardView.setPreserveRatio(true);
        cardView.setSmooth(true);
        cardView.setEffect(new DropShadow(5, Color.BLACK));
        return cardView;
    }
}
