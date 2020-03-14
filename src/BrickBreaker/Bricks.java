package BrickBreaker;

import BrickBreaker.base.LevelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;

public class Bricks {
	private GridPane bricks;
	private List<Node> brickList;
	private int[][] damage;
	private int score, bricksCleared, finalScore, finalBricksCleared;
	private MediaPlayer soundEffect;
	
	private final List<String> DAMAGES_STYLES = Arrays.asList("damage-1", "damage-2", "damage-3");
	private List<LevelListener> winListeners = new ArrayList<LevelListener>();
	
	public Bricks(GridPane bricks) {
		this.bricks = bricks;
		this.brickList = bricks.getChildren();
		this.damage = new int[4][6];
	}
	
	public void addWinListener(LevelListener newListener) {
		this.winListeners.add(newListener);
	}
	
	public int checkCollision(Circle ball) {
        Bounds ballBounds = ball.getBoundsInParent();
        double ballMinX = ballBounds.getMinX();
        double ballMinY = ballBounds.getMinY();
        double ballMaxX = ballBounds.getMaxX();
        double ballMaxY = ballBounds.getMaxY();
        
        boolean atBricksTop = ballMinY >= this.bricks.getLayoutY() - ball.getRadius() * 2;
        boolean atBricksBottom = ballMaxY <= (this.bricks.getLayoutY() + this.bricks.getHeight()) + ball.getRadius() * 2;
        
        // Only check for collisions if the ball is near the brick field
        if (atBricksTop && atBricksBottom) {
            // Check in reverse from bottom to top for speed
            for(int i = this.brickList.size() - 1; i >= 0; i--) {
                Region brick = (Region)this.brickList.get(i);
                // Skip already broken bricks
                if(!brick.isVisible()) {
                    continue;
                }
                
                // Precalculate bounds
                Bounds brickBounds = brick.getBoundsInParent();
                double brickMinX = this.bricks.getLayoutX() + brickBounds.getMinX();
                double brickMinY = this.bricks.getLayoutY() + brickBounds.getMinY();
                double brickMaxX = brickMinX + brickBounds.getWidth();
                double brickMaxY = brickMinY + brickBounds.getHeight();
                
                boolean insideX = ballMaxX >= brickMinX && ballMinX <= brickMaxX;
                boolean insideY = ballMaxY >= brickMinY && ballMinY <= brickMaxY;
                
                // Verify that the ball is touching/colliding with the current brick
                if(insideX && insideY) {
                    boolean atTop = ballMinY < brickMinY;
                    boolean atBottom = ballMaxY > brickMaxY;
                    boolean atLeft = ballMinX < brickMinX;
                    boolean atRight = ballMaxX > brickMaxX;
                    
                    if(atTop || atBottom) {
                        increaseDamage(brick);
                        return 1;
                    }
                    if(atLeft || atRight) {
                        increaseDamage(brick);
                        return -1;
                    }
                }
            }
        }
        
        return 0;
    }
	
	public void increaseDamage(Region brick) {
        List<String> styles = brick.getStyleClass();
        // Get row & col from style classes
        int row = Integer.valueOf(styles.get(0).substring(4));
        int col = Integer.valueOf(styles.get(1).substring(4));
        
        this.damage[row][col]++;
        this.score++;
        // Damage of 3 indicates a broken brick
        if(this.damage[row][col] == 3) {
            brick.setVisible(false);
            this.bricksCleared++;
            this.score = this.score + 2;
            //Break sound!
            playBreakSound();
            
            if(isCleared()) {
                // Notify any attached listeners of a win
                for(LevelListener ls : winListeners) {
                    ls.handleLevelingEvent();
                }
            }
        }
        else playHitSound();
     
        System.out.println("Score: " + this.score);
        // Remove all damage styles and add the new one
        styles.removeAll(this.DAMAGES_STYLES);
        styles.add("damage-" + this.damage[row][col]);
        
        // Debug
        System.out.println(Arrays.toString(styles.toArray()));
    }
	
	public boolean isCleared() {
        System.out.println(bricksCleared + " bricks cleared");
        return this.bricksCleared == this.brickList.size() * BrickBreaker.getLevel();
    }
	
	public void reset() {
        for(Node brick : this.brickList) {
            brick.setVisible(true);
            brick.getStyleClass().removeAll(this.DAMAGES_STYLES);
        }
        
        this.damage = new int[4][6];
        this.finalBricksCleared = this.bricksCleared;
        this.bricksCleared = 0;
        this.finalScore = this.score;
        this.score = 0;
    }

	
	
	public void playHitSound() {
		String path = "";
		try {
			path = ClassLoader.getSystemResource("sounds/hit_sound.mp3").toString();
		} catch(NullPointerException e) {
			System.out.println("Specified sound effect does not exist.");
			System.out.println();
			return;
		}
		
		Media media = new Media(path);
		soundEffect = new MediaPlayer(media);
		soundEffect.play();
	}

	public void playBreakSound() {
		String path = "";
		try {
			path = ClassLoader.getSystemResource("sounds/break_sound.mp3").toString();
		} catch(NullPointerException e) {
			System.out.println("Specified sound effect does not exist.");
			System.out.println();
			return;
		}
		
		Media media = new Media(path);
		soundEffect = new MediaPlayer(media);
		soundEffect.play();
	}
	
	public int getFinalScore() {
		return finalScore;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public int getBricksCleared() {
		return bricksCleared;
	}

	public void setBricksCleared(int bricksCleared) {
		this.bricksCleared = bricksCleared;
	}

	public int getFinalBricksCleared() {
		return finalBricksCleared;
	}

	public void setFinalBricksCleared(int num) {
		this.finalBricksCleared = num;
	}
}
