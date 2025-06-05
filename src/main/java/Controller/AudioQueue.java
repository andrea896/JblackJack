package Controller;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Gestisce una coda di effetti sonori da riprodurre in sequenza.
 * Garantisce che i suoni non si sovrappongano e vengano riprodotti in ordine
 * con un ritardo appropriato tra un effetto e l'altro.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class AudioQueue {
    private static final Queue<Runnable> audioQueue = new LinkedList<>();
    private static boolean isPlaying = false;
    private static final double DELAY_BETWEEN_SOUNDS = 500;

    /**
     * Aggiunge un effetto sonoro alla coda di riproduzione.
     * L'effetto verrà riprodotto quando sarà il suo turno nella coda.
     *
     * @param effect L'effetto sonoro da aggiungere alla coda
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
     * Riproduce il prossimo effetto sonoro nella coda.
     * Se la coda è vuota, imposta il flag di riproduzione a false.
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
