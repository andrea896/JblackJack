package View;

import javafx.animation.Animation;
import java.util.LinkedList;
import java.util.Queue;

public class AnimationQueue {
    private static final Queue<Runnable> animationQueue = new LinkedList<>();
    private static boolean isAnimating = false;

    public static void queue(Animation animation, Runnable afterAnimation) {
        animationQueue.add(() -> {
            animation.setOnFinished(e -> {
                if (afterAnimation != null)
                    afterAnimation.run();
                playNext();
            });
            animation.play();
        });

        // Se non c'è già un'animazione in corso, avvia la prima
        if (!isAnimating)
            playNext();
    }

    // Overload semplificato
    public static void queue(Animation animation) {
        queue(animation, null);
    }

    // Metodo per aggiungere una funzione generica alla coda
    public static void queue(Runnable action) {
        animationQueue.add(() -> {
            action.run();
            playNext();
        });

        if (!isAnimating)
            playNext();

    }

    private static void playNext() {
        if (animationQueue.isEmpty()) {
            isAnimating = false;
            return;
        }

        isAnimating = true;
        Runnable nextAnimation = animationQueue.poll();
        nextAnimation.run();
    }
}
