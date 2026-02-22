import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Timer;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

public class DodgeBall extends GraphicsProgram implements ActionListener {
	private ArrayList<GOval> balls;
	private ArrayList<GRect> enemies;
	private GLabel text;
	private GLabel destroyedLabel;
	private GLabel timeLabel; 
	private Timer movement;
	private RandomGenerator rgen;
	private int numTimes;
	private int enemiesDestroyed = 0;
	
	public static final int SIZE = 25;
	public static final int SPEED = 10;
	public static final int MS = 20;
	public static final int MAX_ENEMIES = 10;
	public static final int WINDOW_HEIGHT = 600;
	public static final int WINDOW_WIDTH = 300;
	
	public void run() {
		rgen = RandomGenerator.getInstance();
		balls = new ArrayList<GOval>();
		enemies = new ArrayList<GRect>();
		text = new GLabel(""+enemies.size(), 0, WINDOW_HEIGHT);
		add(text);
		
		destroyedLabel = new GLabel("Destroyed: 0", 10, 40);
		add(destroyedLabel);

		timeLabel = new GLabel("Time: 0", 10, 60);
		add(timeLabel);
		
		movement = new Timer(MS, this);
		movement.start();
		
		addMouseListeners();
	}
	
	public void actionPerformed(ActionEvent e) {
		numTimes++;
		timeLabel.setLabel("Time: " + numTimes);
		moveAllBallsOnce();
		
		if (numTimes % 40 == 0) {
	        addAnEnemy();
	    }
	    
	    moveAllEnemiesOnce();
	    checkCollisions();
	    
	    if (enemies.size() > MAX_ENEMIES) {
			movement.stop();
			removeAll();

			GLabel lost = new GLabel("YOU LOST!");
			lost.setFont("Arial-36");
			GLabel score = new GLabel("Destroyed: " + enemiesDestroyed);
			GLabel survive = new GLabel("Survival Time: " + numTimes);

			add(lost, WINDOW_WIDTH/2 - lost.getWidth()/2,
					   WINDOW_HEIGHT/2 - 40);

			add(score, WINDOW_WIDTH/2 - score.getWidth()/2,
					   WINDOW_HEIGHT/2);

			add(survive, WINDOW_WIDTH/2 - survive.getWidth()/2,
					   WINDOW_HEIGHT/2 + 30);
		}

	}
	
	public void mousePressed(MouseEvent e) {
		for(GOval b:balls) {
			if(b.getX() < SIZE * 2.5) {
				return;
			}
		}
		addABall(e.getY());     
	}
	
	private void addABall(double y) {
		GOval ball = makeBall(SIZE/2, y);
		add(ball);
		balls.add(ball);
	}
	
	public GOval makeBall(double x, double y) {
		GOval temp = new GOval(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
		temp.setColor(Color.RED);
		temp.setFilled(true);
		return temp;
	}
	
	private void addAnEnemy() {
		GRect e = makeEnemy(rgen.nextInt(SIZE/2, WINDOW_HEIGHT-SIZE/2));
		enemies.add(e);
		text.setLabel("" + enemies.size());
		add(e);
	}
	
	public GRect makeEnemy(double y) {
		GRect temp = new GRect(WINDOW_WIDTH-SIZE, y-SIZE/2, SIZE, SIZE);
		temp.setColor(Color.GREEN);
		temp.setFilled(true);
		return temp;
	}

	private void moveAllBallsOnce() {
		for(GOval ball:balls) {
			ball.move(SPEED, 0);
		}
	}
	
	private void moveAllEnemiesOnce() {
		for (GRect enemy : enemies) {
			enemy.move(0, rgen.nextInt(-2, 3));
		}
	}
	
	private void checkCollisions() {

		for (int i = 0; i < balls.size(); i++) {

			GOval ball = balls.get(i);

			double x = ball.getX();
			double y = ball.getY();
			double w = ball.getWidth();
			double h = ball.getHeight();

			GObject obj1 = getElementAt(x + w, y + 2);
			GObject obj2 = getElementAt(x + w, y + h / 2);
			GObject obj3 = getElementAt(x + w, y + h - 2);

			GObject hit = null;

			if (obj1 instanceof GRect) hit = obj1;
			if (obj2 instanceof GRect) hit = obj2;
			if (obj3 instanceof GRect) hit = obj3;

			if (hit != null && enemies.contains(hit)) {
				remove(hit);
				enemies.remove(hit);
				enemiesDestroyed++;
				destroyedLabel.setLabel("Destroyed: " + enemiesDestroyed);
				text.setLabel("" + enemies.size());
				remove(ball);
				balls.remove(ball);
				i--;
			}
		}
	}
	public void init() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	public static void main(String args[]) {
		new DodgeBall().start();
	}
}
