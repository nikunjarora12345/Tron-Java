package com.kaminari.tron;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 300;
	private static final int HEIGHT = WIDTH / 16 * 9;
	private static final int SCALE = 3;
	private static final String TITLE = "Tron Legacy!";

	private JFrame frame;

	private Thread thread;

	private boolean running = false;
	private String winner = "";

	private int x1 = 0;
	private int x1_change = 0;
	private int y1 = 0;
	private int y1_change = 0;
	private String direction1 = "";

	private int x2 = WIDTH - 1;
	private int x2_change = 0;
	private int y2 = HEIGHT - 1;
	private int y2_change = 0;
	private String direction2 = "";

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
			.getData();

	public Main() {
		frame = new JFrame(TITLE);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (!direction1.contentEquals("down")) {
						y1_change = -1;
						x1_change = 0;
						direction1 = "up";
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!direction1.contentEquals("up")) {
						y1_change = 1;
						x1_change = 0;
						direction1 = "down";
					}
					break;
				case KeyEvent.VK_LEFT:
					if (!direction1.contentEquals("right")) {
						x1_change = -1;
						y1_change = 0;
						direction1 = "left";
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!direction1.contentEquals("left")) {
						x1_change = 1;
						y1_change = 0;
						direction1 = "right";
					}
					break;
				case KeyEvent.VK_ENTER:
					x1_change = 0;
					y1_change = 0;
					break;
				case KeyEvent.VK_W:
					if (!direction2.contentEquals("down")) {
						y2_change = -1;
						x2_change = 0;
						direction2 = "up";
					}
					break;
				case KeyEvent.VK_S:
					if (!direction2.contentEquals("up")) {
						y2_change = 1;
						x2_change = 0;
						direction2 = "down";
					}
					break;
				case KeyEvent.VK_A:
					if (!direction2.contentEquals("right")) {
						x2_change = -1;
						y2_change = 0;
						direction2 = "left";
					}
					break;
				case KeyEvent.VK_D:
					if (!direction2.contentEquals("left")) {
						x2_change = 1;
						y2_change = 0;
						direction2 = "right";
					}
					break;
				case KeyEvent.VK_SPACE:
					x2_change = 0;
					y2_change = 0;
				}
			}
		});
		setFocusable(true);
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;

		int frames = 0;
		int updates = 0;

		while (running) {
			long currTime = System.nanoTime();
			delta += (currTime - lastTime) / ns;
			lastTime = currTime;
			while (delta >= 1) {
				update();
				updates++;
				delta--;
			}
			render();
			frames++;

			if (System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				frame.setTitle(TITLE + " | fps : " + frames + " | ups : "
						+ updates);
				frames = 0;
				updates = 0;
			}
		}
	}

	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		try {
			pixels[x1 + y1 * WIDTH] = 0x0000ff;
			pixels[x2 + y2 * WIDTH] = 0xff0000;
		} catch (ArrayIndexOutOfBoundsException e) {

		}

		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		if (winner.contentEquals("blue")) {
			g.drawString("Blue Wins!!!", getWidth() / 2 - 10, getHeight() / 2);
		} else if (winner.contentEquals("red")) {
			g.drawString("Red Wins!!!", getWidth() / 2 - 10, getHeight() / 2);
		}
		g.dispose();
		bs.show();
		if (!winner.contentEquals(""))
			stop();
	}

	private void update() {
		x1 += x1_change;
		y1 += y1_change;
		x2 += x2_change;
		y2 += y2_change;

		if (x1 >= WIDTH)
			x1 = 0;
		else if (x1 < 0)
			x1 = WIDTH - 1;
		else if (y1 >= HEIGHT)
			y1 = 0;
		else if (y1 < 0)
			y1 = HEIGHT - 1;

		if (x2 >= WIDTH)
			x2 = 0;
		else if (x2 < 0)
			x2 = WIDTH - 1;
		else if (y2 >= HEIGHT)
			y2 = 0;
		else if (y2 < 0)
			y2 = HEIGHT - 1;

		if (pixels[x1 + y1 * WIDTH] == 0xff0000) {
			clearScreen();
			winner = "red";
		} else if (pixels[x2 + y2 * WIDTH] == 0x0000ff) {
			clearScreen();
			winner = "blue";
		}
	}

	public void clearScreen() {
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0x000000;
	}

	public static void main(String[] args) {
		Main game = new Main();
		game.frame.add(game);
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.pack();
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		game.start();
	}
}
