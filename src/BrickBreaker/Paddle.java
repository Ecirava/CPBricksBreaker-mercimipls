package BrickBreaker;


import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Paddle {
	private Rectangle paddle;
	
	public Paddle(Rectangle paddle) {
		this.paddle = paddle;
	}
	
	/*Animate the paddle so it can move with the condition that
	 * it cannot get out of the bounds.
	 * 
	 */
	public boolean animate(double dx) {
        Bounds bounds = this.paddle.getBoundsInParent();
        final boolean atLeftBorder = bounds.getMinX() + dx <= 0;
        final boolean atRightBorder = bounds.getMaxX() + dx >= ((Pane)this.paddle.getParent()).getWidth();
        
        // Don't allow paddle to be moved outside of the game canvas
        // Return true if the paddle was able to be moved.
        if (!atLeftBorder && !atRightBorder) {
            this.paddle.setTranslateX(this.paddle.getTranslateX() + dx);
            return true;
        }
        else {
            return false;
        }
    }
	
	public int checkCollision(Circle ball) {
        Bounds ballBounds = ball.getBoundsInParent();
        Bounds paddleBounds = this.paddle.getBoundsInParent();
        
        final double insideY = ballBounds.getMaxY() - paddleBounds.getMinY();
        
        if(ballBounds.intersects(paddleBounds)) {
            // Quick & dirty hack to fix deep collisions
            if(insideY > 3) {
                ball.setTranslateY(-(insideY - 3));
            }   
            return 1;
        }
        return 0;
    }  
	
	public double getTranslateX() { 
        return this.paddle.getTranslateX(); 
    }
	
	public void setTranslateX(double x) { 
        this.paddle.setTranslateX(x); 
	}
	
	public Rectangle getNode() {
        return this.paddle;
    }
}
