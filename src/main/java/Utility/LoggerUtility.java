package Utility;

import java.io.IOException;
import java.util.logging.*;

/**
 * Utility per la gestione centralizzata del logging nell'applicazione BlackJack.
 * Fornisce metodi statici per registrare informazioni, avvertimenti ed errori
 * sia su console che su file.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class LoggerUtility {
    private static final Logger logger = Logger.getLogger("GameLogger");
    private static FileHandler fileHandler;

    // Inizializzazione statica del logger
    static {
        try {
            fileHandler = new FileHandler("game_log.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Errore nell'inizializzazione del Logger: " + e.getMessage());
        }
    }

    /**
     * Ottiene il nome della classe e del metodo che ha chiamato il logger.
     * Utilizza StackWalker per tracciare la chiamata origine del log.
     * 
     * @return Una stringa nel formato "ClassName.methodName()"
     */
    private static String getCallerClassName() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames.skip(2).findFirst().map(frame -> frame.getClassName() + "." + frame.getMethodName() + "()").orElse("Sconosciuto"));
    }

    /**
     * Registra un messaggio informativo.
     * 
     * @param message Il messaggio da registrare
     */
    public static void logInfo(String message) {
        logger.info("[" + getCallerClassName() + "] \n" + message);
    }

    /**
     * Registra un messaggio di avvertimento.
     * 
     * @param message Il messaggio di avvertimento da registrare
     */
    public static void logWarning(String message) {
        logger.warning("[" + getCallerClassName() + "] \n" + message);
    }

    /**
     * Registra un messaggio di errore con eccezione associata.
     * 
     * @param message Il messaggio di errore da registrare
     * @param e L'eccezione associata all'errore
     */
    public static void logError(String message, Exception e) {
        logger.log(Level.SEVERE, "[" + getCallerClassName() + "] \n" + message, e);
        e.printStackTrace();
    }
}
