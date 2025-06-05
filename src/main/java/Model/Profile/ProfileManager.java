package Model.Profile;

import Utility.LoggerUtility;
import com.google.gson.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestore centralizzato per la persistenza e gestione dei profili utente nel gioco BlackJack.
 * Implementa il pattern Singleton per garantire un'unica istanza di gestione dei profili
 * e fornisce operazioni CRUD complete per i profili utente utilizzando JSON come formato di storage.
 * 
 * @author JBlackJack Team
 * @version 1.0
 * @since 1.0
 *
 */
public class ProfileManager {
    private static final LoggerUtility LOGGER = new LoggerUtility();
    private static final String PROFILE_PATH = "src/main/resources/players.json";
    private final Gson gson;
    private JsonArray profilesArray;
    private List<UserProfile> profiles;
    private static ProfileManager instance;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza il Gson configurato per pretty printing, le strutture dati
     * e avvia il caricamento automatico dei profili esistenti.
     * 
     * <p>Durante l'inizializzazione:</p>
     * <ul>
     * <li>Configura Gson con pretty printing per leggibilità del JSON</li>
     * <li>Inizializza le liste per profili in memoria e JSON</li>
     * <li>Carica automaticamente tutti i profili esistenti dal file</li>
     * </ul>
     * 
     * @see #loadProfiles()
     */
    public ProfileManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        profiles = new ArrayList<>();
        profilesArray = new JsonArray();
        loadProfiles();
    }

    /**
     * Restituisce l'istanza singleton del ProfileManager.
     * Implementa il pattern Singleton con inizializzazione lazy per garantire
     * che esista un'unica istanza di gestione profili nell'applicazione.
     * 
     * <p><strong>Nota:</strong> Questo metodo non è thread-safe. Se è richiesto
     * accesso concorrente, sincronizzare esternamente.</p>
     * 
     * @return L'istanza singleton del ProfileManager, mai null
     * @since 1.0
     */
    public static ProfileManager getInstance() {
        if (instance == null)
            instance = new ProfileManager();

        return instance;
    }

    /**
     * Carica tutti i profili utente dal file JSON specificato.
     * Legge il file JSON, deserializza i dati e popola sia la lista in memoria
     * che l'array JSON per le successive operazioni di scrittura.
     */
    private void loadProfiles() {
        LOGGER.logInfo("Caricamento profili dal file: " + PROFILE_PATH);
        try {
            String content = new String(Files.readAllBytes(Paths.get(PROFILE_PATH)));
            JsonObject rootObject = JsonParser.parseString(content).getAsJsonObject();
            profilesArray = rootObject.getAsJsonArray("users");
            LOGGER.logInfo("Trovati " + profilesArray.size() + " profili");

            for (JsonElement element : profilesArray) {
                JsonObject profileObject = element.getAsJsonObject();
                UserProfile profile = new UserProfile();
                profile.setNickname(profileObject.get("nickname").getAsString());
                profile.setAvatarPath(profileObject.get("avatarUrl").getAsString());
                JsonObject statsObject = profileObject.getAsJsonObject("stats");
                GameStats stats = new GameStats();
                stats.setTotalHandsPlayed(statsObject.get("totalHandsPlayed").getAsInt());
                stats.setHandsWon(statsObject.get("handsWon").getAsInt());
                stats.setHandsLost(statsObject.get("handsLost").getAsInt());
                stats.setCurrentBalance(statsObject.get("currentBalance").getAsInt());

                profile.setStats(stats);
                profiles.add(profile);
            }
        } catch (IOException e) {
            LOGGER.logError(e.getMessage(), e);
        }
    }

    /**
     * Salva tutti i profili correnti nel file JSON.
     * Serializza l'array JSON dei profili e lo scrive nel file specificato
     * utilizzando pretty printing per migliorare la leggibilità.
     */
    private void saveToJson() {
        try {
            JsonObject rootObject = new JsonObject();
            rootObject.add("users", profilesArray);
            Files.write(Paths.get(PROFILE_PATH), gson.toJson(rootObject).getBytes());
        } catch (IOException e) {
            LOGGER.logError(e.getMessage(), e);
        }
    }

    /**
     * Crea un nuovo profilo utente con nickname e avatar specificati.
     * Verifica l'unicità del nickname, crea il profilo con statistiche iniziali
     * di default e lo persiste sia in memoria che su file JSON.
     * 
     * @param nickname Il nickname univoco per il nuovo profilo.
     *                 Non deve essere già esistente nel sistema.
     * @param avatarPath Il percorso dell'immagine avatar per il profilo.
     *                   Dovrebbe essere un percorso valido alle risorse.
     * 
     * @return Il nuovo {@link UserProfile} creato, oppure {@code null} se esiste
     *         già un profilo con lo stesso nickname.
     * 
     * @since 1.0
     */
    public UserProfile createProfile(String nickname, String avatarPath) {
        for (UserProfile profile : profiles) {
            if (profile.getNickname().equals(nickname)) {
                LOGGER.logWarning("Profilo " + nickname + " già esistente, scegliere un nickname diverso");
                return null;
            }
        }

        UserProfile newProfile = new UserProfile();
        newProfile.setNickname(nickname);
        newProfile.setAvatarPath(avatarPath);
        newProfile.setStats(new GameStats());

        profiles.add(newProfile);

        // Crea e aggiungi al JsonArray
        JsonObject profileObject = new JsonObject();
        profileObject.addProperty("nickname", newProfile.getNickname());
        profileObject.addProperty("avatarUrl", newProfile.getAvatarPath());

        JsonObject statsObject = new JsonObject();
        statsObject.addProperty("totalHandsPlayed", newProfile.getStats().getTotalHandsPlayed());
        statsObject.addProperty("handsWon", newProfile.getStats().getHandsWon());
        statsObject.addProperty("handsLost", newProfile.getStats().getHandsLost());
        statsObject.addProperty("currentBalance", newProfile.getStats().getCurrentBalance());

        profileObject.add("stats", statsObject);
        profilesArray.add(profileObject);

        saveToJson();
        LOGGER.logInfo("Profilo " + newProfile.getNickname() + " creato con successo");
        return newProfile;
    }

    /**
     * Carica e restituisce un profilo utente specifico tramite nickname.
     * Effettua una ricerca case-insensitive nella lista dei profili caricati
     * per trovare il profilo corrispondente al nickname specificato.
     *
     * @param nickname Il nickname del profilo da cercare.
     *                 La ricerca è case-insensitive.
     *                 Può essere null (restituirà null).
     * 
     * @return Il {@link UserProfile} corrispondente al nickname, oppure {@code null}
     *         se nessun profilo corrisponde o se il nickname è null.
     *
     * 
     * @since 1.0
     */
    public UserProfile loadProfile(String nickname) {
        return profiles.stream()
                .filter(p -> p.getNickname().equalsIgnoreCase(nickname))
                .findFirst()
                .orElse(null);
    }

    /**
     * Restituisce una lista immutabile di tutti i profili utente caricati.
     * Fornisce accesso in sola lettura alla collezione completa dei profili
     * per operazioni di visualizzazione, ricerca o analisi.
     * 
     * @return Lista di tutti i {@link UserProfile} attualmente caricati.
     *         Mai null, ma può essere vuota se non ci sono profili.
     *         La lista riflette lo stato attuale in memoria.
     * 
     * @since 1.0
     */
    public List<UserProfile> getProfiles() {
        return profiles;
    }

    /**
     * Aggiorna le statistiche di un profilo esistente e persiste le modifiche su file.
     * Trova il profilo corrispondente nell'array JSON tramite nickname e aggiorna
     * solo le statistiche di gioco, mantenendo invariati nickname e avatar.
     * 
     * @param updatedProfile Il profilo con le statistiche aggiornate da persistere.
     *                       Deve avere un nickname corrispondente a un profilo esistente.
     *                       Non deve essere null.
     * 
     * @throws NullPointerException se updatedProfile è null o le sue statistiche sono null
     *
     * 
     * @since 1.0
     */
    public void updateProfile(UserProfile updatedProfile) {
        for (int i = 0; i < profilesArray.size(); i++) {
            JsonObject profileObject = profilesArray.get(i).getAsJsonObject();
            if (profileObject.get("nickname").getAsString().equals(updatedProfile.getNickname())) {
                JsonObject statsObject = profileObject.getAsJsonObject("stats");
                statsObject.addProperty("totalHandsPlayed", updatedProfile.getStats().getTotalHandsPlayed());
                statsObject.addProperty("handsWon", updatedProfile.getStats().getHandsWon());
                statsObject.addProperty("handsLost", updatedProfile.getStats().getHandsLost());
                statsObject.addProperty("currentBalance", updatedProfile.getStats().getCurrentBalance());
                saveToJson();
                break;
            }
        }
    }
}