package Utility;

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtility {

    private static final Logger logger = Logger.getLogger("GameLogger");
    private static FileHandler fileHandler;

    static {
        try {
            fileHandler = new FileHandler("game_log.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Errore nell'inizializzazione del Logger: " + e.getMessage());
        }
    }

    private static String getCallerClassName() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames.skip(2).findFirst().map(frame -> frame.getClassName() + "." + frame.getMethodName() + "()").orElse("Sconosciuto"));
    }

    public static void logInfo(String message) {
        logger.info("[" + getCallerClassName() + "] \n" + message);
    }

    public static void logWarning(String message) {
        logger.warning("[" + getCallerClassName() + "] \n" + message);
    }

    public static void logError(String message, Exception e) {
        logger.log(Level.SEVERE, "[" + getCallerClassName() + "] \n" + message, e);
        e.printStackTrace();
    }
}
