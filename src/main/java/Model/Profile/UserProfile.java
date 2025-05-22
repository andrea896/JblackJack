package Model.Profile;

/**
 * Rappresenta il profilo di un utente nel gioco BlackJack.
 * Contiene informazioni personali come nickname, avatar e statistiche di gioco.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 */
public class UserProfile {
    /** Il nickname dell'utente */
    private String nickname;
    
    /** Il percorso dell'immagine avatar dell'utente */
    private String avatarPath;
    
    /** Le statistiche di gioco dell'utente */
    private GameStats stats;

    /**
     * Costruisce un nuovo profilo utente con statistiche di gioco iniziali.
     */
    public UserProfile() {
        this.stats = new GameStats();
    }

    /**
     * Costruisce un nuovo profilo utente con dati specificati.
     * 
     * @param nickname Il nickname dell'utente
     * @param avatarPath Il percorso dell'immagine avatar
     * @param gameStats Le statistiche di gioco
     */
    public UserProfile(String nickname, String avatarPath, GameStats gameStats){
        this.nickname = nickname;
        this.avatarPath = avatarPath;
        this.stats = gameStats;
    }

    /**
     * Restituisce il nickname dell'utente.
     * 
     * @return Il nickname dell'utente
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Imposta il nickname dell'utente.
     * 
     * @param nickname Il nuovo nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Restituisce il percorso dell'immagine avatar.
     * 
     * @return Il percorso dell'avatar
     */
    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * Imposta il percorso dell'immagine avatar.
     * 
     * @param avatarPath Il nuovo percorso dell'avatar
     */
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    /**
     * Restituisce le statistiche di gioco dell'utente.
     * 
     * @return Le statistiche di gioco
     */
    public GameStats getStats() {
        return stats;
    }

    /**
     * Imposta le statistiche di gioco dell'utente.
     * 
     * @param stats Le nuove statistiche di gioco
     */
    public void setStats(GameStats stats) {
        this.stats = stats;
    }
}
