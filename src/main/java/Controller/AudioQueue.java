package Controller;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Gestisce una coda di effetti sonori da riprodurre in sequenza.
 */
public class AudioQueue {
    private static final Queue<Runnable> audioQueue = new LinkedList<>();
    private static boolean isPlaying = false;
    private static final double DELAY_BETWEEN_SOUNDS = 500;

    /**
     * Aggiunge un effetto sonoro alla coda di riproduzione.
     *
     * @param effect L'effetto sonoro da riprodurre
     */
    public static void queue(AudioManager.SoundEffect effect) {
        audioQueue.add(() -> {
            AudioManager.getInstance().playSound(effect);
            PauseTransition pause = new PauseTransition(Duration.millis(DELAY_BETWEEN_SOUNDS));
            pause.setOnFinished(e -> playNext());
            pause.play();
        });

        if (!isPlaying)
            playNext();
    }

    /**
     * Riproduce il prossimo audio nella coda.
     */
    private static void playNext() {
        if (audioQueue.isEmpty()) {
            isPlaying = false;
            return;
        }

        isPlaying = true;
        Runnable nextAudio = audioQueue.poll();
        nextAudio.run();
    }
}
