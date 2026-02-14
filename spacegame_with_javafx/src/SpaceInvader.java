/**
 * SpaceInvader class that runs the SpaceInvader Game using JavaFX.
 * Contains the game initialization, controls, and core game loop logic.
 * start and gameover functions set up screen.
 * startGame function starts main game loop, and manage the game
 */

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

// SpaceInvader class that runs the Rocket Game
public class SpaceInvader extends Application {
    int WIDTH = 1383; // Screen width
    int HEIGHT = 782; // Screen height
    private boolean paused = false; // Tracks if the game is paused


    public static final Image PLAYER_IMG = new Image("assets/player.png");
    public static final Image ENEMY_IMG = new Image("assets/enemy.png");
    public static final Image REWARD_IMG = new Image("assets/reward.png");
    public static final Image PUNISHMENT_IMG = new Image("assets/punishment.png");


    private double playerX = WIDTH / 2 - 50; // Player's starting X position
    private double playerY = HEIGHT - 100;  // Player's fixed Y position
    private final double PLAYER_SPEED = 40; // Player movement speed

    private GraphicsContext gc; // Used to draw game entities
    private List<Enemy> enemies = new ArrayList<>(); // Stores active enemies
    private List<Bullet> bullets = new ArrayList<>(); // Stores active bullets
    private List<Reward> rewards = new ArrayList<>(); // Stores active rewards
    private List<Punishment> punishments = new ArrayList<>(); // Stores active punishments
    private Queue<Message> messages = new LinkedList<>(); // Stores messages displayed on screen
    private boolean canShoot = true; // Cooldown status for shooting bullets
    private boolean powerUpActive = false; // Tracks if the power-up is active
    private int score = 0; // Tracks the player's score
    private AnimationTimer enemySpawner; // Timer to spawn enemies
    private AnimationTimer gameLoop; // Main game loop



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the main menu screen

        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);

        // Background image
        Image image = new Image("assets/main.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        // Texts
        Text text = new Text("PRESS ENTER TO PLAY");
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.WHITE);
        text.setX(WIDTH / 2 - 300);
        text.setY(HEIGHT / 2 - 50);

        Text text2 = new Text("PRESS ESC TO EXIT");
        text2.setFont(Font.font("Verdana", 50));
        text2.setFill(Color.WHITE);
        text2.setX(WIDTH / 2 - 300);
        text2.setY(HEIGHT / 2 + 50);

        root.getChildren().addAll(imageView, text, text2);

        // Keyboard controls
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    startGame(primaryStage); // Start the game
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                primaryStage.close(); // Exit the application
            }
        });

        primaryStage.setTitle("SpaceInvader_Game");
        primaryStage.getIcons().add(image);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Displays the Game Over screen
    public void gameOver(Stage primaryStage,Integer last_score) {
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.LIGHTBLUE);

        // Background image
        Image image = new Image("assets/main.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        // Texts
        Text text = new Text("GAME OVER");
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.WHITE);
        text.setX(WIDTH / 2 - 300);
        text.setY(HEIGHT / 2 - 50);

        Text text1 = new Text("PRESS R TO RESTART");
        text1.setFont(Font.font("Verdana", 50));
        text1.setFill(Color.WHITE);
        text1.setX(WIDTH / 2 - 300);
        text1.setY(HEIGHT / 2 + 50);



        Text text2 = new Text("PRESS ESC TO MAIN MENU");
        text2.setFont(Font.font("Verdana", 50));
        text2.setFill(Color.WHITE);
        text2.setX(WIDTH / 2 - 300);
        text2.setY(HEIGHT / 2 + 150);

        Text text3 = new Text("Your Score is: " + last_score);
        text3.setFont(Font.font("Verdana", 50));
        text3.setFill(Color.WHITE);
        text3.setX(WIDTH / 2 - 300);
        text3.setY(HEIGHT / 2 + 250);

        root.getChildren().addAll(imageView, text, text1, text2, text3);

        // Keyboard controls
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                try {
                    startGame(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                try{
                    if (gameLoop != null) {
                        gameLoop.stop(); // Stop the game loop
                    }
                    start(primaryStage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setTitle("Rocket Game - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGame(Stage primaryStage) {
        // Stop the previous game loop if it exists.
        if (gameLoop != null) {
            gameLoop.stop();
        }
        // Reset all game entities.
        enemies.clear();
        bullets.clear();
        rewards.clear();
        punishments.clear();
        playerX = WIDTH / 2 - 50;// Set player's initial position.
        score = 0;// Reset the score.

        // Create the main scene and root group.
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Configure game controls.
        setupControls(scene, primaryStage);

        // Spawns enemies at regular intervals.
        enemySpawner = new AnimationTimer() {
            private long lastSpawn = 0;

            @Override
            public void handle(long now) {
                if (paused) return; // Do not spawn enemies if the game is paused.
                if (now - lastSpawn >= 2_000_000_000L) {  // Spawn every 2 seconds.
                    enemies.add(new Enemy(Math.random() * (WIDTH - 50), 0));
                    lastSpawn = now;
                }
            }
        };
        enemySpawner.start();

        // Main game loop.
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (paused) return; // Pause the game if the paused is true.
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                drawPlayer();// Draw the player.
                drawScore();// Display the score.

                // Draw messages
                removeExpiredMessages();
                drawMessages();


                // Draw and move enemies
                Iterator<Enemy> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    Enemy enemy = enemyIterator.next();
                    enemy.move();
                    enemy.draw(gc);

                    if (enemy.y > HEIGHT) {
                        enemyIterator.remove();// Remove enemies that leave the screen.
                    }
                }

                // Draw and move bullets
                Iterator<Bullet> bulletIterator = bullets.iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    bullet.move();
                    bullet.draw(gc);

                    if (bullet.y < 0) {
                        bulletIterator.remove();// Remove bullets that leave the screen.
                    }
                }

                // Draw and move rewards
                Iterator<Reward> rewardIterator = rewards.iterator();
                while (rewardIterator.hasNext()) {
                    Reward reward = rewardIterator.next();
                    reward.move();
                    reward.draw(gc);

                    // Check for player collision with rewards.
                    if (playerX + 100 > reward.x && playerX < reward.x + 30 &&
                            playerY + 100 > reward.y && playerY < reward.y + 30) {
                        addMessage("Reward! Collect the power-up!", Color.GREEN, 5000);
                        activatePowerUp();// Activate the power-up effect.
                        rewardIterator.remove();
                    }
                }
                // Update and draw punishments.
                Iterator<Punishment> punishmentIterator = punishments.iterator();
                while (punishmentIterator.hasNext()) {
                    Punishment punishment = punishmentIterator.next();
                    punishment.move();
                    punishment.draw(gc);

                    // Check for player collision with punishments.
                    if (playerX + 100 > punishment.x && playerX < punishment.x + 30 &&
                            playerY + 100 > punishment.y && playerY < punishment.y + 30) {
                        score -= 50;
                        addMessage("Penalty! -50 punishment!", Color.RED, 2000);
                        punishmentIterator.remove();
                    }
                }

                // Check collisions
                checkCollisions(primaryStage);
            }
        };
        gameLoop.start(); // Start the game loop.

        primaryStage.setScene(scene); // Set the scene for the stage.
        primaryStage.show(); // Display the stage.
    }

    private void setupControls(Scene scene, Stage primaryStage) {
        // keyboard controls.
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.P) {
                paused = !paused; //Toggle pause
                if (paused) {
                    enemySpawner.stop(); // Stop enemy production
                } else {
                    enemySpawner.start(); // Continue enemy production
                }
            }
            else{
                if (event.getCode() == KeyCode.ESCAPE) {
                    // go main menu if 'ESC' key press.
                    try {
                        if (gameLoop != null) {
                            gameLoop.stop(); // Stop the game loop
                        }
                        start(primaryStage);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (event.getCode() == KeyCode.LEFT) {
                    movePlayer(-PLAYER_SPEED);// Move player left on 'LEFT' arrow key press.
                } else if (event.getCode() == KeyCode.RIGHT) {
                    movePlayer(PLAYER_SPEED);// Move player right on 'RIGHT' arrow key press.
                } else if (event.getCode() == KeyCode.SPACE && canShoot) {
                    // Shoot bullets on 'SPACE' key press.
                    bullets.add(new Bullet(playerX, playerY));
                    if (powerUpActive) {
                        bullets.add(new Bullet(playerX, playerY, -2)); // Left diagonal
                        bullets.add(new Bullet(playerX, playerY, 2));  // Right diagonal
                    }
                    canShoot = false;

                    // Timer for bullet cooldown.
                    new AnimationTimer() {
                        private long lastUpdate = 0;

                        @Override
                        public void handle(long now) {
                            if (now - lastUpdate >= 300_000_00) { // Cooldown
                                canShoot = true;
                                stop();
                            }
                            lastUpdate = now;
                        }
                    }.start();
                }
            }
        });
    }


    private void checkCollisions(Stage primaryStage) {
        // Iterate over all bullets to check for collisions with enemies.
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            // Iterate over all enemies to check for collisions with the current bullet.
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();

                // Check if the bullet collides with the enemy.
                if (bullet.x + 10 > enemy.x && bullet.x < enemy.x + 50 &&
                        bullet.y < enemy.y + 50 && bullet.y + 20 > enemy.y) {
                    bulletIterator.remove(); // Remove the bullet after collision.
                    enemyIterator.remove(); // Remove the enemy after collision.

                    score += 100; // Increase score

                    // Determine whether to spawn a reward or punishment.
                    double random_int = Math.random();
                    if (random_int < 0.2) { // 20% chance to spawn a reward
                        rewards.add(new Reward(enemy.x + 10, enemy.y + 10));
                    } else if (random_int < 0.4 && random_int> 0.2) {
                        score += 50; //  20% chance to Additional score bonus.
                        addMessage("+50 Points!", Color.GREEN, 2000);
                    } else if (random_int<0.6 && random_int> 0.4 ) {// 20% chance to spawn a punishment
                        punishments.add(new Punishment(enemy.x + 10, enemy.y + 10));
                    }
                    break;
                }
            }
        }
        // Check if any enemy collides with the player.
        for (Enemy enemy : enemies) {
            if (playerX + 100 > enemy.x && playerX < enemy.x + 50 &&
                    playerY + 100 > enemy.y && playerY < enemy.y + 50) {
                gameOver(primaryStage,score); // Trigger game over if a collision occurs.

                return;
            }
        }
    }
    // Adds a message to be displayed on the screen
    private void addMessage(String text, Color color, long durationInMillis) {
        messages.add(new Message(text, color, durationInMillis));
    }
    // Draws all active messages on the screen
    private void drawMessages() {
        int messageY = 60; // Starting Y position for messages
        for (Message message : messages) {
            gc.setFill(message.color);
            gc.setFont(Font.font(20));
            gc.fillText(message.text, 10, messageY);
            messageY += 30; // Space between messages
        }
    }
    // Removes expired messages from the queue
    private void removeExpiredMessages() {
        while (!messages.isEmpty() && messages.peek().isExpired()) {
            messages.poll();
        }
    }
    // Draws the player on the canvas.
    private void drawPlayer() {
        gc.drawImage(PLAYER_IMG, playerX, playerY, 100, 100);
    }
    // Displays the current score on the screen.
    private void drawScore() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(30));
        gc.fillText("Score: " + score, 10, 30);
    }
    // Moves the player left or right by a given amount.
    private void movePlayer(double deltaX) {
        playerX += deltaX;
        if (playerX < 0) { // Prevent the player from moving off the left side.
            playerX = 0;
        } else if (playerX > WIDTH - 100) {// Prevent the player from moving off the right side.
            playerX = WIDTH - 100;
        }
    }
    // Activates a power-up that lasts for a limited duration.
    private void activatePowerUp() {
        powerUpActive = true;
        // Timer to deactivate the power-up after 5 seconds.
        new AnimationTimer() {
            private long startTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (now - startTime >= 5_000_000_000L) { // 3 seconds
                    powerUpActive = false;
                    stop();
                }
            }
        }.start();
    }
}

// Enemy class
class Enemy {
    double x, y;// Position of the enemy
    static double speed = 2;// Movement speed of the enemy

    public Enemy(double startX, double startY) {    // Constructor to initialize the enemy's position

        this.x = startX;
        this.y = startY;
    }

    public void move() {    // Method to move the enemy downward
        y += speed;
    }

    // Method to draw the enemy
    public void draw(GraphicsContext gc) {
        gc.drawImage(SpaceInvader.ENEMY_IMG, x, y, 50, 50);// Draw enemy image at the specified position
    }
}

// Bullet class
class Bullet {
    double x, y; // Position of the bullet
    double speed = 5; // Speed of the bullet
    double dx = 0; // Horizontal movement offset for the bullet

    // Constructor to initialize the bullet's position
    public Bullet(double startX, double startY) {
        this.x = startX + 45;
        this.y = startY;
    }
    // Overloaded constructor to allow horizontal movement for the bullet
    public Bullet(double startX, double startY, double dx) {
        this(startX, startY);
        this.dx = dx;
    }

    // Method to move the bullet
    public void move() {
        y -= speed; // Move the bullet upward
        x += dx; // Adjust horizontal position if dx is set
    }

    // Method to draw the bullet
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.RED); // Set the bullet's color to red
        gc.fillRect(x, y, 10, 20); // Draw the bullet as a red rectangle
    }
}

// Reward class
class Reward {
    double x, y; // Position of the reward
    double speed = 2; // Speed of the reward's downward movement

    // Constructor to initialize the reward's position
    public Reward(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    // Method to move the reward downward
    public void move() {
        y += speed;
    }

    // Method to draw the reward
    public void draw(GraphicsContext gc) {
        gc.drawImage(SpaceInvader.REWARD_IMG, x, y, 30, 30); // Draw reward image at the specified position
    }
}
// Punishment class
class Punishment {
    double x, y; // Position of the punishment
    double speed = 2; // Speed of the punishment's downward movement

    // Constructor to initialize the punishment's position
    public Punishment(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    // Method to move the punishment downward
    public void move() {
        y += speed;
    }

    // Method to draw the punishment
    public void draw(GraphicsContext gc) {
        gc.drawImage(SpaceInvader.PUNISHMENT_IMG, x, y, 30, 30); // Draw punishment image at the specified position
    }
}

// Message class represents in-game messages that appear on the screen
class Message {
    String text; // The content of the message
    Color color; // The color of the message text
    long expirationTime; // The time when the message will expire

    // Constructor to create a message with a duration
    public Message(String text, Color color, long durationInMillis) {
        this.text = text;
        this.color = color;
        this.expirationTime = System.currentTimeMillis() + durationInMillis; // Set the expiration time
    }

    // Method to check if the message has expired
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}



