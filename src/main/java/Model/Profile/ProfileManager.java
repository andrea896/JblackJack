package Model.Profile;

import Utility.LoggerUtility;
import com.google.gson.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final LoggerUtility LOGGER = new LoggerUtility();
    private static final String PROFILE_PATH = "src/main/resources/players.json";
    private final Gson gson;
    private JsonArray profilesArray;
    private List<UserProfile> profiles;
    private static ProfileManager instance;

    public ProfileManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        profiles = new ArrayList<>();
        profilesArray = new JsonArray();
        loadProfiles();
    }

    public static ProfileManager getInstance() {
        if (instance == null)
            instance = new ProfileManager();

        return instance;
    }

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

    private void saveToJson() {
        try {
            JsonObject rootObject = new JsonObject();
            rootObject.add("users", profilesArray);
            Files.write(Paths.get(PROFILE_PATH), gson.toJson(rootObject).getBytes());
        } catch (IOException e) {
            LOGGER.logError(e.getMessage(), e);
        }
    }

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

        // Aggiungi alla lista dei profili
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

    public UserProfile loadProfile(String nickname) {
        return profiles.stream()
                .filter(p -> p.getNickname().equalsIgnoreCase(nickname))
                .findFirst()
                .orElse(null);
    }

    public List<UserProfile> getProfiles() {
        return profiles;
    }

    public void updateProfile(UserProfile updatedProfile) {
        for (int i = 0; i < profilesArray.size(); i++) {
            JsonObject profileObject = profilesArray.get(i).getAsJsonObject();
            if (profileObject.get("nickname").getAsString().equals(updatedProfile.getNickname())) {
                JsonObject statsObject = profileObject.getAsJsonObject("stats");
                statsObject.addProperty("totalHandsPlayed", updatedProfile.getStats().getTotalHandsPlayed());
                statsObject.addProperty("handsWon", updatedProfile.getStats().getHandsWon());
                statsObject.addProperty("handsLost", updatedProfile.getStats().getHandsLost());
                statsObject.addProperty("currentBalance", updatedProfile.getStats().getCurrentBalance());
                break;
            }
        }
    }
}