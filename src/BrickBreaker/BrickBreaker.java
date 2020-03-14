package BrickBreaker;

import BrickBreaker.base.Engine;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BrickBreaker extends Engine{
	
	private boolean gameStarted, ballLaunched = false;
    private final int LEVEL_COUNT = 5;
    private final int MAX_LIVES = 5;
    private final double INITIAL_SPEED = 4;
    private final double PADDLE_SPEED = 12d;
    
    private static int level = 1;
    private int livesRemaining = MAX_LIVES;
	
    private Group[] groups;
    private Label levelInd, livesInd, scoreInd, levelIndIn, livesIndIn, scoreIndIn;
    private Label finalScoreInd, bricksClearedInd;
    private Bricks bricks;
    

	private Paddle paddle;
    private Ball ball;
    
    private BackgroundMusic backgroundMusic;
	
	public BrickBreaker(Stage stage, Scene scene) {
		super(stage, scene, "CP Brick Breaker", 60);
		// TODO Auto-generated constructor stub
		
		this.groups = new Group[5];
        this.groups[0] = (Group)scene.lookup("#titleGroup");
        this.groups[1] = (Group)scene.lookup("#gameGroup");
        this.groups[2] = (Group)scene.lookup("#levelInterstitial");
        this.groups[3] = (Group)scene.lookup("#winInterstitial");
        this.groups[4] = (Group)scene.lookup("#lostInterstitial");
        
        this.levelInd = (Label)scene.lookup("#level");
        this.livesInd = (Label)scene.lookup("#lives");
        this.scoreInd = (Label)scene.lookup("#score");
        this.levelIndIn = (Label)scene.lookup("#levelIn");
        this.livesIndIn = (Label)scene.lookup("#livesIn");
        this.scoreIndIn = (Label)scene.lookup("#scoreIn");
        
        this.finalScoreInd = (Label)scene.lookup("#finalscore");
        this.bricksClearedInd = (Label)scene.lookup("#brickscleared");
        
        this.bricks = new Bricks((GridPane)scene.lookup("#bricks"));
        this.paddle = new Paddle((Rectangle)scene.lookup("#paddle"));
        this.ball = new Ball((Circle)scene.lookup("#ball"), this.bricks, this.paddle);
        
        // Add win/loss listeners
        this.bricks.addWinListener(this::levelUp);
        this.ball.addLossListener(this::loseLife);
        
        // Handle non-constant controls
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::toggleGameState);
        
        // Play title screen music
        this.backgroundMusic = new BackgroundMusic();
        this.backgroundMusic.setMusic(0);
        this.backgroundMusic.getBackgroundMusic().setVolume(0.3);
        this.backgroundMusic.play();
	}

	@Override
	public void update(Engine game) {
		// TODO Auto-generated method stub
		if(this.gameStarted) {
            // Animate paddle
            Boolean moved;
            if(game.getKeyPressed() == KeyCode.LEFT) {
                moved = this.paddle.animate(-PADDLE_SPEED);
                // Move ball in sync with paddle if it hasn't been launched yet
                if(!this.ballLaunched && moved) this.ball.setTranslateX(this.ball.getTranslateX() - PADDLE_SPEED);
            }
            if(game.getKeyPressed() == KeyCode.RIGHT) {
                moved = this.paddle.animate(PADDLE_SPEED);
                if(!this.ballLaunched && moved) this.ball.setTranslateX(this.ball.getTranslateX() + PADDLE_SPEED);
            }
            
            // Animate ball if it's been launched
            if(this.ballLaunched) {
                this.ball.animate();
            }
            
            updateScore();
        }
	}
	
	private void setLives(int num) {
        this.livesRemaining = num;
        // Update info bar & interstitial screen
        this.livesInd.setText("" + this.livesRemaining);
        this.livesIndIn.setText("x " + this.livesRemaining);
    }
	
	public static int getLevel() {
		return level;
	}
	
	private void setLevel(int num) {
        this.level = num;
        // Update info bar & interstitial screen
        this.levelInd.setText("Level " + this.level);
        this.levelIndIn.setText("Level " + this.level);
    }
	
	private void updateScore() {
		int score = this.bricks.getScore();
		this.scoreInd.setText("Score: " + score);
		this.scoreIndIn.setText("Score: " + score);
		this.finalScoreInd.setText("Final Score: " + this.bricks.getFinalScore());
		this.bricksClearedInd.setText("Bricks cleared: " + this.bricks.getFinalBricksCleared());
	}
	
	private void switchToGroup(int newGroup) {
        if(newGroup < this.groups.length && newGroup >= 0 && !isGroupVisible(newGroup)) {
            for(int i = 0; i < this.groups.length; i++) {
                // When looping over groups, only make the new group visible
                this.groups[i].setVisible(i == newGroup);
            }
        }
    }
    
	private boolean isGroupVisible(int i) {
        if(i < this.groups.length && i >= 0) {
            return this.groups[i].isVisible();
        }
        else {
            return false;
        }
    }
	
	private void showInterstitial(int first, int second) {
        switchToGroup(first);
        
        // Sleep in async task to play nice with JavaFX's threading model
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(2000);
                } 
                catch (InterruptedException e) {}
                return null;
            }
        };
        sleeper.setOnSucceeded(e -> switchToGroup(second));
        new Thread(sleeper).start();
    }
	
	private void resetPlayer(double startingSpeed) {
        // Stop the ball's animation
        this.ballLaunched = false;
        
        this.paddle.setTranslateX(0);
        this.ball.setStartingSpeed(startingSpeed);
        this.ball.setTranslateX(0);
        this.ball.setTranslateY(0);
    }
	
	private void resetLevel(double startingSpeed) {
		
        this.bricks.reset();
        resetPlayer(startingSpeed);
    }
	
	private void startGame() {
        // Show level interstitial first
        showInterstitial(2, 1);
        
        // Stop title music and start game
        this.backgroundMusic.stop();
        this.gameStarted = true;
    }
	
	private void stopGame() {
        // Stop game if one is in progress
        if(this.gameStarted) {
            this.gameStarted = false;
            
            // Start title music
            // Special music is played after winning the game
            if(this.level == this.LEVEL_COUNT + 1) {
                this.backgroundMusic.setMusic(1);
                System.out.println("1");
            }
            else {
                this.backgroundMusic.setMusic(0);
                System.out.println("0");
            }
            this.backgroundMusic.play();
            
            // Only show title screen here if the game was quit manually
            // levelUp and loseLife show interstitials on their own
            if(isGroupVisible(1)) {
                switchToGroup(0);
            }
            
            // Reset game canvas
            resetLevel(this.INITIAL_SPEED);
            setLives(this.MAX_LIVES);
            setLevel(1);
            updateScore();
        }
        // Quit otherwise
        else {
            stop();
            this.stage.close();
        }
    }
	
	private void levelUp() {
        if(this.gameStarted) {
            setLevel(this.level + 1);
            
            // If the last level was won, show the victory screen
            if(this.level == this.LEVEL_COUNT + 1) {
                showInterstitial(3, 0);
                
                stopGame();
            }
            else {
            	//set the previous score to the current score 
            	
            	
                // Show the level interstitial
                showInterstitial(2, 1);
                
                // On the last level, play special music
                if(this.level == this.LEVEL_COUNT) {
                	System.out.println("2");
                    this.backgroundMusic.setMusic(1);
                    
                    this.backgroundMusic.play();
                }
                else {
                    this.backgroundMusic.stop();
                }
                resetLevel(this.level + 3);
                
                int previousScore = this.bricks.getFinalScore();
            	this.bricks.setScore(previousScore);
                this.scoreIndIn.setText("Score: " + previousScore);
            	this.bricks.setScore(previousScore);
            	
            	int previousBricksCLeared = this.bricks.getFinalBricksCleared();
            	this.bricks.setBricksCleared(previousBricksCLeared);
//            	this.bricksClearedInd.setText("Bricks cleared: " + previousBricksCLeared);
            }
        }
        else {
            System.out.println("Invalid levelUp() event issued");
        }
    }
	
	private void loseLife() {
        if(this.gameStarted) {
            setLives(this.livesRemaining - 1);

            // If player has lives left, show the level interstitial
            if(this.livesRemaining > 0) {
                showInterstitial(2, 1);
                resetPlayer(this.level + 3);
            }
            // Oh no! Show the game over screen
            else {
            	switchToGroup(4);
                stopGame();
            }
        }
        else {
            System.out.println("Invalid loseLife() event issued");
        }
    }
	
	private void toggleGameState(KeyEvent e) {
        KeyCode currentKey = e.getCode();
        
        switch(currentKey) {
            case SPACE:
                // Start game
                // Do not allow startGame if the victory screen is shown
                if(!this.gameStarted && !isGroupVisible(3) && isGroupVisible(0)) {
                    startGame();
                }
                //Back to main menu, if it's on Group 3.
                else if(!this.gameStarted && isGroupVisible(4)) {
                	stop();
                	switchToGroup(0);
                }
                // Launch ball
                // Do not allow if the level interstitial is shown
                else if(this.gameStarted && !this.ballLaunched && !isGroupVisible(2)) {
                    this.ballLaunched = true;
                }
                break;
            case ESCAPE:
                // Only allow on title screen and game canvas
                if(isGroupVisible(0) || isGroupVisible(1)) {
                    stopGame();
                }
                break;
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
            case DIGIT4:
            case DIGIT5:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
                // Allow cheat codes on title screen
                if(!this.gameStarted) startGame();
                // Set to level - 1 because levelUp will increment
                this.level = Integer.valueOf(e.getText()) - 1;
                // Set max lives
                setLives(this.MAX_LIVES);
                bricks.setBricksCleared(24 * (level - 1));
                levelUp();
        }
    }
}
