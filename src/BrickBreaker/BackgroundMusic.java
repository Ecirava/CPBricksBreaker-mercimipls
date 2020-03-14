/**
 * 
 * 
 * This class is needed to be completed.
 * 
 * 
 * */


package BrickBreaker;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusic {
	private MediaPlayer backgroundMusic;
	private boolean playBackAllowed = true;
	
	private String[] slots = {
		"popstar.mp3",
		"dancethenightaway.mp3"
	};
	
	
	//Set the music to play/stop.
	public void setMusic(int select) {
		if(this.playBackAllowed) {
			stop();
			
			if (select < this.slots.length && select >= 0) {
				String path = "";
				try {
					/***
					 * If error, check the code around here!
					 * */
					path = ClassLoader.getSystemResource("sounds/" +this.slots[select]).toString();
				} catch(NullPointerException e) {
					System.out.println("Specified music does not exist.");
					System.out.println(select);
					return;
				}
				Media media = new Media(path);
				backgroundMusic = new MediaPlayer(media);
			}
		}
	}

	//Play the music selected.
	public void play() {
		if (this.backgroundMusic != null) {
			this.backgroundMusic.play();
		}
	}
	
	//Stop the playing music.
	public void stop() {
		// TODO Auto-generated method stub
		if (this.backgroundMusic != null) {
			this.backgroundMusic.stop();
		}
	}
	
	//True, if the music is currently playing.
	public boolean isPlaying() {
		return this.backgroundMusic != null && this.backgroundMusic.getStatus() == MediaPlayer.Status.PLAYING;
	}
	
	public MediaPlayer getBackgroundMusic() {
		return backgroundMusic;
	}


	public void setBackgroundMusic(MediaPlayer backgroundMusic) {
		this.backgroundMusic = backgroundMusic;
	}

}
