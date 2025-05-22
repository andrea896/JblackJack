package View;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.Queue;

public class AnimationQueue {
    private static final Queue<Runnable> animationQueue = new LinkedList<>();
    private static boolean isAnimating = false;

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
