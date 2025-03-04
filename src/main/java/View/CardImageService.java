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

    public static void initialize() {
        if (initialized) return;

        try {
            // Carica le immagini delle carte
            // Nota: adatta i percorsi al tuo progetto
            String[] suits = {"hearts", "diamonds", "clubs", "spades"};
            String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

            for (String suit : suits) {
                for (String rank : ranks) {
                    String key = rank + "_of_" + suit;
                    String path = "/images/cards/" + key + ".png";
                    try {
                        cardImages.put(key, new Image(CardImageService.class.getResourceAsStream(path)));
                    } catch (Exception e) {
                        System.err.println("Impossibile caricare l'immagine: " + path);
                        // Crea un'immagine segnaposto
                        cardImages.put(key, createPlaceholderImage());
                    }
                }
            }

            // Carica anche il dorso della carta
            try {
                cardImages.put("back", new Image(CardImageService.class.getResourceAsStream("/images/cards/back.png")));
            } catch (Exception e) {
                System.err.println("Impossibile caricare l'immagine del dorso della carta");
                cardImages.put("back", createPlaceholderImage());
            }

            initialized = true;
        } catch (Exception e) {
            System.err.println("Errore nell'inizializzazione del CardImageService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Image createPlaceholderImage() {
        // Crea un'immagine segnaposto per le carte non trovate
        return new Image(CardImageService.class.getResourceAsStream("/images/placeholder.png"));
    }

    public static Image getCardImage(Card card) {
        if (!initialized) initialize();

        //if (card.isFaceDown()) {
            //return cardImages.getOrDefault("back", createPlaceholderImage());
        //}

        String key = card.toString().toLowerCase();
        return cardImages.getOrDefault(key, cardImages.getOrDefault("back", createPlaceholderImage()));
    }

    public static Image getCardBackImage() {
        if (!initialized) initialize();
        return cardImages.getOrDefault("back", createPlaceholderImage());
    }

    public static ImageView createCardImageView(Card card) {
        ImageView cardView = new ImageView(getCardImage(card));
        cardView.setFitHeight(120);
        cardView.setPreserveRatio(true);
        cardView.setSmooth(true);
        cardView.setEffect(new DropShadow(5, Color.BLACK));
        return cardView;
    }
}
