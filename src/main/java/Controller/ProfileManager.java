package Controller;

import java.util.logging.Logger;
import java.util.logging.Level;
import Model.GameStats.GameStats;
import Model.Profile.UserProfile;
import com.google.gson.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final Logger LOGGER = Logger.getLogger(ProfileManager.class.getName());
    private static final String PROFILE_PATH = "C:\\progetti\\JBlackJack\\src\\main\\resources\\players.json";
    private final Gson gson;
    private JsonArray profilesArray;
    private List<UserProfile> profiles;

    public ProfileManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        profiles = new ArrayList<>();
        profilesArray = new JsonArray();
        loadProfiles();
    }

    private void loadProfiles() {
        LOGGER.info("Caricamento profili dal file: " + PROFILE_PATH);
        try {
            String content = new String(Files.readAllBytes(Paths.get(PROFILE_PATH)));
            JsonObject rootObject = JsonParser.parseString(content).getAsJsonObject();
            profilesArray = rootObject.getAsJsonArray("users");
            LOGGER.info("Trovati " + profilesArray.size() + " profili");

            for (JsonElement element : profilesArray) {
                JsonObject profileObject = element.getAsJsonObject();
                UserProfile profile = new UserProfile();
                profile.setNickname(profileObject.get("nickname").getAsString());
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
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private void saveToJson() {
        try {
            JsonObject rootObject = new JsonObject();
            rootObject.add("users", profilesArray);
            Files.write(Paths.get(PROFILE_PATH), gson.toJson(rootObject).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserProfile loadOrCreateProfile(String nickname) {
        for (int i = 0; i < profiles.size(); i++)
            if (profiles.get(i).getNickname().equals(nickname))
                return profiles.get(i);

        UserProfile newProfile = new UserProfile();
        newProfile.setNickname(nickname);
        newProfile.setStats(new GameStats());

        // Aggiungi alla lista dei profili
        profiles.add(newProfile);

        // Crea e aggiungi al JsonArray
        JsonObject profileObject = new JsonObject();
        profileObject.addProperty("nickname", newProfile.getNickname());

        JsonObject statsObject = new JsonObject();
        statsObject.addProperty("totalHandsPlayed", newProfile.getStats().getTotalHandsPlayed());
        statsObject.addProperty("handsWon", newProfile.getStats().getHandsWon());
        statsObject.addProperty("handsLost", newProfile.getStats().getHandsLost());
        statsObject.addProperty("currentBalance", newProfile.getStats().getCurrentBalance());

        profileObject.add("stats", statsObject);
        profilesArray.add(profileObject);

        saveToJson();
        return newProfile;
    }

    public void updateProfile(UserProfile updatedProfile) {
        if (updatedProfile != null && updatedProfile.getNickname() != null) {
            // Aggiorna il profilo nella lista
            for (int i = 0; i < profiles.size(); i++) {
                if (profiles.get(i).getNickname().equals(updatedProfile.getNickname())) {
                    profiles.set(i, updatedProfile);
                    break;
                }
            }

            try {
                // Salva la lista aggiornata
                String json = gson.toJson(profiles);
                Files.write(Paths.get(PROFILE_PATH), json.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}