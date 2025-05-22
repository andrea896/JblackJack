package View;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Gestisce una coda di animazioni da eseguire in sequenza.
 * Garantisce che le animazioni non si sovrappongano e vengano eseguite in ordine.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class AnimationQueue {
    private static final Queue<Runnable> animationQueue = new LinkedList<>();
    private static boolean isAnimating = false;

    /**
     * Aggiunge un'animazione alla coda di esecuzione.
     * L'animazione verrà eseguita quando sarà il suo turno nella coda.
     * 
     * @param animation L'animazione JavaFX da aggiungere alla coda
     */
    public static void queue(Animation animation) {
        animationQueue.add(() -> {
            animation.play();
            PauseTransition pause = new PauseTransition(Duration.millis(650));
            pause.setOnFinished(e -> playNext());
            pause.play();
        });

        if (!isAnimating)
            playNext();
    }

    /**
     * Esegue la prossima animazione nella coda.
     * Se la coda è vuota, imposta il flag di animazione a false.
     */
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
