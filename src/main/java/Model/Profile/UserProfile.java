package Model.Profile;

public class UserProfile {
    private String nickname;
    private String avatarPath;
    private GameStats stats;

    public UserProfile() {
        this.stats = new GameStats();
    }

    public UserProfile(String nickname, String avatarPath, GameStats gameStats){
        this.nickname = nickname;
        this.avatarPath = avatarPath;
        this.stats = gameStats;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public GameStats getStats() {
        return stats;
    }

    public void setStats(GameStats stats) {
        this.stats = stats;
    }
}
