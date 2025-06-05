package Controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Gestisce la riproduzione degli effetti sonori nel gioco.
 * Implementata come singleton.
 */
public class AudioManager {
	private static AudioManager instance;

	/**
	 * Enumerazione degli effetti sonori disponibili.
	 */
	public enum SoundEffect {
		MENU_MUSIC("src/main/resources/sounds/MenuMusic.wav"),
		BUTTON_CLICK("src/main/resources/sounds/click.wav"),
		CARD_DEAL("src/main/resources/sounds/card_deal.wav"),
		CHIP_PLACE("src/main/resources/sounds/chip_place.wav"),
		CHIP_STACK("src/main/resources/sounds/chip_stack.wav"),
		SHUFFLE("src/main/resources/sounds/shuffle.wav"),
		WIN("src/main/resources/sounds/win.wav"),
		LOSE("src/main/resources/sounds/lose.wav"),
		BLACKJACK("src/main/resources/sounds/blackjack.wav"),
		SPLIT("src/main/resources/sounds/split.wav"),
		DOUBLE_DOWN("src/main/resources/sounds/double_down.wav");

		private final String filePath;

		SoundEffect(String filePath) {
			this.filePath = filePath;
		}

		public String getFilePath() {
			return filePath;
		}
	}

	/**
	 * Ottiene l'istanza singleton dell'AudioManager.
	 *
	 * @return L'istanza singleton di AudioManager per la gestione degli effetti audio
	 */
	public static AudioManager getInstance() {
		if (instance == null)
			instance = new AudioManager();
		return instance;
	}

	/**
	 * Riproduce un file audio dal percorso specificato.
	 *
	 * @param filename Il percorso del file audio
	 */
	private void play(String filename) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filename));
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (FileNotFoundException e1) {
			System.err.println("File audio non trovato: " + filename);
			e1.printStackTrace();
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e1) {
			System.err.println("Errore durante la riproduzione dell'audio: " + filename);
			e1.printStackTrace();
		}
	}

	/**
	 * Riproduce un effetto sonoro predefinito.
	 *
	 * @param effect L'effetto sonoro da riprodurre
	 */
	public void playSound(SoundEffect effect) {
		play(effect.getFilePath());
	}
}
