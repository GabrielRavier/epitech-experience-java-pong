import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import javax.swing.JFrame;

public class Game extends JFrame {
	
	public class GameState implements Runnable {
		
		public class Paddle implements Runnable {
			
			Rectangle rect;
			Rectangle movement;
			int score;
			boolean isPlayer2;
			int movementSpeed;
			
			public Paddle(int xPos, int yPos, boolean isPlayer2) {
				this.rect = new Rectangle(xPos, yPos, 10, 50);
				this.movement = new Rectangle(0, 0, 0, 0);
				this.isPlayer2 = isPlayer2;
			}
			
			public void keyPressed(KeyEvent event) {
				if ((!this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_W) ||
					( this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_UP))
					this.movement.y = -1;
				if ((!this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_S) ||
					( this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_DOWN))
					this.movement.y = 1;
			}
			
			public void keyReleased(KeyEvent event) {
				if (((!this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_W) ||
					(this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_UP)) && this.movement.y == -1)
					this.movement.y = 0;
				if (((!this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_S) ||
					(this.isPlayer2 && event.getKeyCode() == KeyEvent.VK_DOWN)) && this.movement.y == 1)
					this.movement.y = 0;
			}
			
			public void draw(Graphics graphicsHandle) {
				graphicsHandle.setColor(Color.WHITE);
				graphicsHandle.fillRect(this.rect.x, this.rect.y, this.rect.width, this.rect.height);
			}
			
			@Override public void run() {
				try {
					while (true) {
						this.rect.y += this.movement.y * this.movementSpeed;
						if (this.rect.y <= 15)
							this.rect.y = 15;
						if (this.rect.y >= 340)
							this.rect.y = 340;
						Thread.sleep(7);
					}
				} catch (Exception exception) {
					System.err.println(exception.getMessage());
					exception.printStackTrace();
				}
			}
		}
		
		Paddle paddle1 = new Paddle(10, 25, false);
		Paddle paddle2 = new Paddle(485 + 150, 25, true);
		
		Rectangle ballRect;
		Rectangle ballMovement;
		int movementSpeed = 2;
		
		public GameState(int ballXPos, int ballYPos) {
			this.ballRect = new Rectangle(ballXPos, ballYPos, 15, 15);
			this.ballMovement = new Rectangle(1, 1, 0, 0);
		}
		
		public void draw(Graphics graphicsHandle) {
			graphicsHandle.setColor(Color.WHITE);
			graphicsHandle.fillRect(this.ballRect.x, this.ballRect.y, this.ballRect.width, this.ballRect.height);
			this.paddle1.draw(graphicsHandle);
			this.paddle2.draw(graphicsHandle);
			graphicsHandle.setColor(Color.WHITE);
			graphicsHandle.drawString("" + this.paddle1.score, 35, 40);
			graphicsHandle.drawString("" + this.paddle2.score, 465 + 150, 40);
		}
		
		private void incrementScore(boolean isPlayer2) {
			if (!isPlayer2)
				++this.paddle1.score;
			else
				++this.paddle2.score;
			
			this.movementSpeed = 2;
		}
		
		private void setBallMovementX(int value) {
			this.ballMovement.x = value;

			if (this.movementSpeed < 10)
				++this.movementSpeed;
		}
		
		@Override public void run() {
			try {
				while (true) {
					if (this.ballRect.intersects(this.paddle1.rect))
						this.setBallMovementX(1);
					if (this.ballRect.intersects(this.paddle2.rect))
						this.setBallMovementX(-1);
					this.ballRect.x += this.ballMovement.x * this.movementSpeed;
					this.ballRect.y += this.ballMovement.y * this.movementSpeed;
					this.paddle1.movementSpeed = this.movementSpeed;
					this.paddle2.movementSpeed = this.movementSpeed;

					if (this.ballRect.x < 0 && this.ballMovement.x != 1) {
						this.ballMovement.x = 1;
						this.incrementScore(true);
					}
					if (this.ballRect.x >= 485 + 150 && this.ballMovement.x != -1) {
						this.ballMovement.x = -1;
						this.incrementScore(false);
					}
					
					if (this.ballRect.y <= 15)
						this.ballMovement.y = 1;
					if (this.ballRect.y >= 386)
						this.ballMovement.y = -1;
					
					Thread.sleep(8);
				}
			} catch (Exception exception) {
				System.err.println(exception.getMessage());
				exception.printStackTrace();
			}
		}
	}
	
	GameState gameState = new GameState(250, 200);
	
	static final int windowWidth = 650;
	static final int windowHeight = 400;
	static final Dimension windowSize = new Dimension(windowWidth, windowHeight);

	public class GameKeyListener extends KeyAdapter {
		
		@Override public void keyPressed(KeyEvent event) {
			gameState.paddle1.keyPressed(event);
			gameState.paddle2.keyPressed(event);
		}
		
		@Override public void keyReleased(KeyEvent event) {
			gameState.paddle1.keyReleased(event);
			gameState.paddle2.keyReleased(event);
		}
	}

	Image image;
	Graphics graphics;
	
	@Override public void paint(Graphics graphicsHandle) {
		this.image = this.createImage(650, 400);
		this.graphics = this.image.getGraphics();
		this.gameState.draw(this.graphics);
		this.repaint();
		graphicsHandle.drawImage(this.image, 0, 0, this);
	}
	
	public Game()
	{
		this.setTitle("Pong but it's made with Java");
		this.setSize(windowSize);
		this.setResizable(false);
		this.setVisible(true);
		this.setBackground(new Color(40, 45, 52, 255));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(new GameKeyListener());
		
		Thread threadGameState = new Thread(this.gameState);
		Thread threadPaddle1 = new Thread(this.gameState.paddle1);
		Thread threadPaddle2 = new Thread(this.gameState.paddle2);
		
		threadGameState.start();
		threadPaddle1.start();
		threadPaddle2.start();
	}
}
