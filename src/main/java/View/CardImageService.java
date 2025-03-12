package View;

import Model.Game.Objects.Card;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class CardImageService {
    private static final Map<String, Image> cardImages = new HashMap<>();
    private static boolean initialized = false;
    private static Image cardBackImage;

    public static void initialize() {
        if (initialized) return;

        try {
            String[] suits = {"hearts", "diamonds", "clubs", "spades"};
            String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

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
     * Imposta l'immagine del dorso della carta in base alla selezione dell'utente
     *
     * @param backDesign Il percorso al design del dorso selezionato dall'utente
     */
    public static void setCardBackDesign(String backDesign) {
        try {
            cardBackImage = new Image(CardImageService.class.getResourceAsStream(backDesign));
        } catch (Exception e) {
            System.err.println("Impossibile caricare l'immagine del dorso della carta: " + backDesign);
        }
    }

    public static Image getCardImage(Card card) {
        if (!initialized)
            initialize();
        return cardImages.get(card.toString().toLowerCase());
    }

    public static Image getCardBackImage() {
        if (!initialized) initialize();
        return cardImages.get("back");
    }

    public static ImageView createCardImageView(Card card) {
        ImageView cardView = new ImageView(getCardImage(card));
        cardView.setFitHeight(50);
        cardView.setPreserveRatio(true);
        cardView.setSmooth(true);
        cardView.setEffect(new DropShadow(5, Color.BLACK));
        return cardView;
    }
}
