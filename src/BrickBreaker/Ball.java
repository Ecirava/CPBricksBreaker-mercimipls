package BrickBreaker;


import BrickBreaker.base.LevelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class Ball {
	private Circle ball;
	private double dx;
	private double dy;
	private Bricks bricks;
	private Paddle paddle;
	
	private List<LevelListener> lossListeners = new ArrayList<LevelListener>();
	
	public Ball(Circle ball, Bricks bricks, Paddle paddle, double speed) {
		this.ball = ball;
		this.bricks = bricks;
        this.paddle = paddle;
        setStartingSpeed(speed);
	}
	
	//default-speed constructor for convenience.
	public Ball(Circle ball, Bricks bricks, Paddle paddle) {
		this(ball, bricks, paddle, 4);
	}
	
	public void setStartingSpeed(double speed) {
		// TODO Auto-generated method stub
		this.dx = (new Random()).nextBoolean() ? speed : -speed;
        this.dy = -speed;
	}
	
	public void addLossListener(LevelListener newListener) {
        this.lossListeners.add(newListener);
    }
	
	//animate the ball, make the ball movable and handle collision detection
	public void animate() {
        // Move the ball
        this.ball.setTranslateX(this.ball.getTranslateX() + dx);
        this.ball.setTranslateY(this.ball.getTranslateY() + dy);
        
        Bounds bounds = this.ball.getBoundsInParent();
        Pane canvas = (Pane)this.ball.getParent();
        boolean atTopBorder = bounds.getMinY() <= 0;
        boolean atRightBorder = bounds.getMaxX() >= canvas.getWidth();
        boolean atBottomBorder = bounds.getMaxY() >= canvas.getHeight();
        boolean atLeftBorder = bounds.getMinX() <= 0;
        
        int atBrick = this.bricks.checkCollision(this.ball);
        int atPaddle = this.paddle.checkCollision(this.ball);
        
        // Calculate angle of reflection (the logic that bounce the ball)
        if(atLeftBorder || atRightBorder || atBrick == -1) dx *= -1;
        if(atTopBorder || atBrick == 1 || atPaddle == 1) dy *= -1;
        
        // If the ball hits the bottom of the screen, the player loses a life
        // Notify any attached loss listeners
        if(atBottomBorder) {
            for(LevelListener ls : this.lossListeners) {
                ls.handleLevelingEvent();
            }
        }
    }
	
	
	/*
	 * Getters and setters for convenience 
	 * to access the internal node's methods
	 */
	public double getTranslateX() { 
        return this.ball.getTranslateX(); 
    }
	
	public double getTranslateY() { 
        return this.ball.getTranslateY(); 
    }
	
	public void setTranslateX(double x) { 
        this.ball.setTranslateX(x); 
    }
	
	public void setTranslateY(double y) { 
        this.ball.setTranslateY(y); 
    }
	
	
	public Circle getNode() {
        return this.ball;
    }
}
