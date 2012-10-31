import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class PacManGame extends Applet implements KeyListener
{
	private static final long serialVersionUID = 1L;

	private static Image OSC;
	private static SFXPlayer player;
	private static int widthOfOSC, heightOfOSC;
	private static TilesGrid grid = new TilesGrid(2,0);
	private static PacMan pacman = new PacMan(grid);
	private static Ghost blinky, pinky, inky, clyde;
	private static boolean leftTurn, rightTurn, upTurn, downTurn;
	private static boolean tInterrupt, paused, mCDecrease, win, death, gameOver;
	private static boolean ateDot;
	private static boolean fright;
	private static boolean moving;
	private static int level, time;
	private static int pacAnimation;
	// LEVEL DEPENDENT VARIABLES //
	private static int numBonusPoints, flash;
	private static double frightTime;
	private static double pacmanSpeed, pacmanFrightSpeed, 
	ghostSpeed, ghostFrightSpeed, ghostTunnelSpeed;

	public void init() {
		player = new SFXPlayer(0, "sfx"); 
		this.setBounds(0,0,448,576);
		GridLayout.setImages();
		pacAnimation = 0;
		numBonusPoints = 200;
		level = 0;
		setLevelDependents();
		blinky = new Ghost(grid, Ghost.BLINKY);
		pinky = new Ghost(grid, Ghost.PINKY);
		inky = new Ghost(grid, Ghost.INKY);
		clyde = new Ghost(grid, Ghost.CLYDE);
		setBackground(Color.BLACK);
		addKeyListener(this);
		win = true;
		paused = true;
		graphicsThread.start();
		timerThread.start();
		pacmanThread.start();
		blinkyThread.start();
		pinkyThread.start();
		inkyThread.start();
		clydeThread.start();
		sfxThread.start();
		requestFocus();
	}

	private void setLevelDependents() {
		switch (level) {
		case 1: grid.setBonusPoints(100); frightTime = 3; pacmanSpeed = PacMan.SLOW_SPEED; pacmanFrightSpeed = PacMan.SLOW_FRIGHT_SPEED; 
		ghostSpeed = Ghost.SLOW_SPEED; ghostFrightSpeed = Ghost.SLOW_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.SLOW_TUNNEL_SPEED; flash=5; break;
		case 2: grid.setBonusPoints(300); frightTime = 3; pacmanSpeed = PacMan.MEDIUM_SPEED; pacmanFrightSpeed = PacMan.MEDIUM_FRIGHT_SPEED; 
		ghostSpeed = Ghost.MEDIUM_SPEED; ghostFrightSpeed = Ghost.MEDIUM_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.MEDIUM_TUNNEL_SPEED; flash=5; break;
		case 3: grid.setBonusPoints(500); frightTime = 3; pacmanSpeed = PacMan.MEDIUM_SPEED; pacmanFrightSpeed = PacMan.MEDIUM_FRIGHT_SPEED; 
		ghostSpeed = Ghost.MEDIUM_SPEED; ghostFrightSpeed = Ghost.MEDIUM_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.MEDIUM_TUNNEL_SPEED; flash=5; break;
		case 4: grid.setBonusPoints(500); frightTime = 2; pacmanSpeed = PacMan.MEDIUM_SPEED; pacmanFrightSpeed = PacMan.MEDIUM_FRIGHT_SPEED; 
		ghostSpeed = Ghost.MEDIUM_SPEED; ghostFrightSpeed = Ghost.MEDIUM_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.MEDIUM_TUNNEL_SPEED; flash=5; break;
		case 5: grid.setBonusPoints(700); frightTime = 2; pacmanSpeed = PacMan.FAST_SPEED; pacmanFrightSpeed = PacMan.FAST_FRIGHT_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.FAST_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.FAST_TUNNEL_SPEED; flash=5; break;
		case 6: grid.setBonusPoints(700); frightTime = 5; pacmanSpeed = PacMan.FAST_SPEED; pacmanFrightSpeed = PacMan.FAST_FRIGHT_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.FAST_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.FAST_TUNNEL_SPEED; flash=5; break;
		case 7: grid.setBonusPoints(1000); frightTime = 4; pacmanSpeed = PacMan.FAST_SPEED; pacmanFrightSpeed = PacMan.FAST_FRIGHT_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.FAST_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.FAST_TUNNEL_SPEED; flash=5; break;
		case 8: grid.setBonusPoints(1000); frightTime = 2; pacmanSpeed = PacMan.FAST_SPEED; pacmanFrightSpeed = PacMan.FAST_FRIGHT_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.FAST_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.FAST_TUNNEL_SPEED; flash=5; break;
		case 9: grid.setBonusPoints(2000); frightTime = 1; pacmanSpeed = PacMan.FAST_SPEED; pacmanFrightSpeed = PacMan.FAST_FRIGHT_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.FAST_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.FAST_TUNNEL_SPEED; flash=3; break;
		case 10: grid.setBonusPoints(2000); frightTime = 4; pacmanSpeed = PacMan.FAST_SPEED; pacmanFrightSpeed = PacMan.FAST_FRIGHT_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.FAST_FRIGHT_SPEED; ghostTunnelSpeed = Ghost.FAST_TUNNEL_SPEED; flash=5; break;
		default: grid.setBonusPoints(3000); frightTime = 2; pacmanSpeed = PacMan.MEDIUM_SPEED; pacmanFrightSpeed = PacMan.MEDIUM_SPEED; 
		ghostSpeed = Ghost.FAST_SPEED; ghostFrightSpeed = Ghost.MEDIUM_SPEED; ghostTunnelSpeed = Ghost.MEDIUM_TUNNEL_SPEED; flash=3; break;
		}
	}

	Thread pacmanThread = new Thread() {
		public void run() {
			int movementCounter = 0;
			boolean frameSkip1 = false;
			while(true) {
				if (tInterrupt) tInterrupt = false;
				if (frameSkip1 && movementCounter == 2) {
					frameSkip1 = false;
				} else movementCounter = nextPacMoveCounter(movementCounter);
				try {
					Thread.sleep((int)((fright) ? pacmanFrightSpeed*30:pacmanSpeed*30+(ateDot?20:0)));
					if (ateDot)
						ateDot = !ateDot;
					synchronized (this){
						while(paused) {
							repaint();
							wait();
						}
					}
				} catch(Exception e) {}
				tryChangeDir();
				if (movementCounter%3 == 0)
					pacAnimation++;
					if (!testForWall()) {
						moving = true;
						ateDot = pacman.move(6);
					} else moving = false;
				repaint();
				if (fright) {
					if (grid.getFrightTimer() < 1) {
						blinky.turnBack();
						pinky.turnBack();
						inky.turnBack();
						clyde.turnBack();
						blinky.setEaten(false);
						pinky.setEaten(false);
						inky.setEaten(false);
						clyde.setEaten(false);
					}
					grid.frightDown();
					if (grid.getFrightTimer() == 5) {
						player.changeFile(3);
						player.play();
					}
					if (grid.getFrightTimer() > frightTime*120) {
						grid.setFright(false);
						fright = false;
						numBonusPoints = 200;
						if (!blinky.getJustEaten()) {
							blinky.setJustEaten(false);
							blinky.setEaten(false);
						}
						if (!pinky.getJustEaten()) {
							pinky.setJustEaten(false);
							pinky.setEaten(false);
						}
						if (!inky.getJustEaten()) {
							inky.setJustEaten(false);
							inky.setEaten(false);
						}
						if (!clyde.getJustEaten()) {
							clyde.setJustEaten(false);
							clyde.setEaten(false);
						}
					}
				}
			}
		}
	};

	Thread blinkyThread = new Thread() {
		public void run() {
			int movementCounter = 0;
			while(true) {
				if (tInterrupt) tInterrupt = false;
				movementCounter = nextGhostCounter(movementCounter);
				try {
					Thread.sleep((int)((blinky.getJustEaten()) ? Ghost.EYEBALL_SPEED*30:
						((blinky.getWarpTunnel()) ? ghostTunnelSpeed*30:
							((fright && !blinky.getEaten()) ? ghostFrightSpeed*30: ghostSpeed*30))));
					synchronized (this) {
						while(paused) {
							repaint();
							wait();
						}
					}
				} catch(Exception e) {}
				blinky.refreshTarget(pacman.getXPosition(), 
						pacman.getYPosition(), 
						blinky.getXPosition(), 
						blinky.getYPosition(), 
						pacman.getDirection());
				if (movementCounter == 1)
					blinky.move();
			}
		}
	};

	Thread pinkyThread = new Thread() {
		public void run() {
			int movementCounter = 0;
			while(true) {
				if (tInterrupt) tInterrupt = false;
				movementCounter = nextGhostCounter(movementCounter);
				try {
					Thread.sleep((int)((pinky.getJustEaten()) ? Ghost.EYEBALL_SPEED*30:
						((pinky.getWarpTunnel()) ? ghostTunnelSpeed*30:
							((fright && !pinky.getEaten()) ? ghostFrightSpeed*30:
								ghostSpeed*30))));
					synchronized (this) {
						while(paused) {
							repaint();
							wait();
						}
					}
				} catch(Exception e) {}
				pinky.refreshTarget(pacman.getXPosition(), 
						pacman.getYPosition(), 
						blinky.getXPosition(), 
						blinky.getYPosition(), 
						pacman.getDirection());
				if (movementCounter == 1)
					pinky.move();
				if (PacManGame.time > 5) {
					if (pinky.inHouse())
						pinky.leaveHouse();
				}
			}
		}
	};

	Thread inkyThread = new Thread() {
		public void run() {
			int movementCounter = 0;
			while(true) {
				if (tInterrupt) tInterrupt = false;
				movementCounter = nextGhostCounter(movementCounter);
				try {
					Thread.sleep((int)((inky.getJustEaten()) ? Ghost.EYEBALL_SPEED*30:
						((inky.getWarpTunnel()) ? ghostTunnelSpeed*30:
							((fright && !inky.getEaten()) ? ghostFrightSpeed*30:
								ghostSpeed*30))));
					synchronized (this) {
						while(paused) {
							repaint();
							wait();
						}
					}
				} catch(Exception e) {}
				inky.refreshTarget(pacman.getXPosition(), 
						pacman.getYPosition(), 
						blinky.getXPosition(), 
						blinky.getYPosition(), 
						pacman.getDirection());
				if (movementCounter == 1)
					inky.move();
				if (time > 10) {
					if (inky.inHouse())
						inky.leaveHouse();
				}
			}
		}
	};

	Thread clydeThread = new Thread() {
		public void run() {
			int movementCounter = 0;
			while(true) {
				if (tInterrupt) tInterrupt = false;
				movementCounter = nextGhostCounter(movementCounter);
				try {
					Thread.sleep((int)((clyde.getJustEaten()) ? Ghost.EYEBALL_SPEED*30:
						((clyde.getWarpTunnel()) ? ghostTunnelSpeed*30:
							((fright && !clyde.getEaten()) ? ghostFrightSpeed*30:
								ghostSpeed*30))));
					synchronized (this) {
						while(paused) {
							repaint();
							wait();
						}
					}
				} catch(Exception e) {}
				clyde.refreshTarget(pacman.getXPosition(), 
						pacman.getYPosition(), 
						blinky.getXPosition(), 
						blinky.getYPosition(), 
						pacman.getDirection());
				if (movementCounter == 1)
					clyde.move();
				if (time > 15) {
					if (clyde.inHouse())
						clyde.leaveHouse();
				}
			}
		}
	};

	private int nextPacMoveCounter(int c) {
		if (c == 6)
			c = 1;
		else c += ((mCDecrease) ? -1:1);
		if (c < 1) {
			c = 2;
			mCDecrease = false;
		}
		if (c == 1)
			mCDecrease = false;
		return c;
	}

	private int nextGhostCounter(int c) {
		if (c >= 6)
			c = 1;
		else c ++;
		return c;
	}

	Thread sfxThread = new Thread() {
		public void run() {
			while(true) {
				try {
					Thread.sleep(280);
				} catch(Exception e){}  
				if (moving)
					player.changeFile(2);
				else if (!paused && !death && !gameOver && !win)
					player.changeFile(1);
				if (!paused && !death && !gameOver && !win)
					player.play();
			}
		}
	};

	Thread graphicsThread = new Thread() {
		public void run() {
			while(true) {
				if (win) {
					winSetup(5000);
				}
				try {
					Thread.sleep(10);
				} catch(Exception e){}  
				fright = grid.getFright();
				for (int i = 0; i < grid.getGrid().length; i++) {
					for (int j = 0; j < grid.getGrid()[0].length; j++) {
						int pmX, pmY, bX, bY, pX, pY, iX, iY, cX, cY;
						pmX = pacman.getXPosition(); pmY = pacman.getYPosition();
						bX = blinky.getXPosition(); bY = blinky.getYPosition();
						pX = pinky.getXPosition(); pY = pinky.getYPosition();
						iX = inky.getXPosition(); iY = inky.getYPosition();
						cX = clyde.getXPosition(); cY = clyde.getYPosition();
						if (grid.getTile(i,j) == GridLayout.PACMAN_TILE) {
							if (!(i == pmX && j == pmY)) {
								grid.setTile(i,j,GridLayout.EMPTY_TILE);
							}
						}
						if (grid.getTile(i,j) == GridLayout.BLINKY_TILE) {
							if (!(i == bX && j == bY)) {
								grid.setTile(i,j,blinky.getTempTile());
							}
						}
						if (grid.getTile(i,j) == GridLayout.PINKY_TILE) {
							if (!(i == pX && j == pY)) {
								grid.setTile(i,j,pinky.getTempTile());
							}
						}
						if (grid.getTile(i,j) == GridLayout.INKY_TILE) {
							if (!(i == iX && j == iY)) {
								grid.setTile(i,j,inky.getTempTile());
							}
						}
						if (grid.getTile(i,j) == GridLayout.CLYDE_TILE) {
							if (!(i == cX && j == cY)) {
								grid.setTile(i,j,clyde.getTempTile());
							}
						}
					}
				}
				repaint();
				testEatGhost();
				if ((PacManGame.time+6)%30 < 5) {
					grid.setScatter(true);
				} else grid.setScatter(false);
				win = getWin();
			}
		}
	};  

	Thread timerThread = new Thread() {
		public void run() {
			try {
				Thread.sleep(2000);
			} catch(Exception e){}  
			while(true) {
				try {
					Thread.sleep(1000);
					synchronized (this) {
						while(paused) {
							repaint();
							wait();
						}
					}
				} catch(Exception e){}
				PacManGame.time++;
				if (PacManGame.time == 40 || PacManGame.time == 80) {
					grid.showBonus();
				}
			}
		}
	};

	private synchronized void restartGame(int time) {
		player.stop();
		player.changeFile(0);
		player.play();
		for (int i = 0; i < frightTime*120; i++)
			grid.frightDown();
		paused = true;
		death = false;
		gameOver = false;
		grid.hideBonus();
		grid.restart();
		ateDot = false;
		pacman.setDirection(90);
		blinky.setJustEaten(false);
		pinky.setJustEaten(false);
		inky.setJustEaten(false);
		clyde.setJustEaten(false);
		pinky.setHouse(true);
		inky.setHouse(true);
		clyde.setHouse(true);
		pacman.setPosition(14,26);
		blinky.setPosition(13,14);
		pinky.setPosition(11,18);
		inky.setPosition(16,18);
		clyde.setPosition(13,16);
		blinky.setDirection(90);
		pinky.setDirection(0);
		inky.setDirection(270);
		clyde.setDirection(90);
		blinky.setLeaving(true);
		pinky.setLeaving(false);
		inky.setLeaving(false);
		clyde.setLeaving(false);
		grid.setTiles();
		grid.setTile(14,26,GridLayout.PACMAN_TILE);
		repaint();
		level = 1;
		setLevelDependents();
		PacManGame.time = 0;
		try {
			Thread.sleep(time);
		} catch (Exception e) {}
		repaint();
		paused = false;
		win = false;
		synchronized(blinkyThread) {
			blinkyThread.notify();
		} synchronized(pinkyThread) {
			pinkyThread.notify();
		} synchronized(inkyThread) {
			inkyThread.notify();
		} synchronized(clydeThread) {
			clydeThread.notify();
		} synchronized(pacmanThread) {
			pacmanThread.notify();
		} synchronized(timerThread) {
			timerThread.notify();
		} 
		PacManGame.time = 0;
	}

	private synchronized void winSetup(int time) {
		player.stop();
		player.changeFile(0);
		player.play();
		for (int i = 0; i < frightTime*120; i++)
			grid.frightDown();
		rightTurn = false; leftTurn = false; upTurn = false; downTurn = false;
		grid.setFright(false);
		grid.hideBonus();
		paused = true;
		ateDot = false;
		pacman.setDirection(90);
		blinky.setJustEaten(false);
		pinky.setJustEaten(false);
		inky.setJustEaten(false);
		clyde.setJustEaten(false);
		pinky.setHouse(true);
		inky.setHouse(true);
		clyde.setHouse(true);
		pacman.setPosition(14,26);
		blinky.setPosition(13,14);
		pinky.setPosition(11,18);
		inky.setPosition(16,18);
		clyde.setPosition(13,16);
		blinky.setDirection(90);
		pinky.setDirection(0);
		inky.setDirection(270);
		clyde.setDirection(90);
		blinky.setLeaving(true);
		pinky.setLeaving(false);
		inky.setLeaving(false);
		clyde.setLeaving(false);
		for (int i = 0; i < grid.getGrid().length; i++) {
			for (int j = 0; j < grid.getGrid()[0].length; j++) {
				grid.setTile(i,j,GridLayout.GRID[i][j]);
			}
		}
		grid.setTile(14,26,GridLayout.PACMAN_TILE);
		grid.setTile(13,14,GridLayout.BLINKY_TILE);
		grid.setTile(13,17,GridLayout.PINKY_TILE);
		grid.setTile(14,17,GridLayout.INKY_TILE);
		grid.setTile(12,17,GridLayout.CLYDE_TILE);
		blinky.setTempTile(GridLayout.EMPTY_TILE);
		pinky.setTempTile(GridLayout.EMPTY_TILE);
		inky.setTempTile(GridLayout.EMPTY_TILE);
		clyde.setTempTile(GridLayout.EMPTY_TILE);
		repaint();
		level++;
		setLevelDependents();
		PacManGame.time = 0;
		try {
			Thread.sleep(time);
		} catch (Exception e) {}
		repaint();
		paused = false;
		win = false;
		synchronized(blinkyThread) {
			blinkyThread.notify();
		} synchronized(pinkyThread) {
			pinkyThread.notify();
		} synchronized(inkyThread) {
			inkyThread.notify();
		} synchronized(clydeThread) {
			clydeThread.notify();
		} synchronized(pacmanThread) {
			pacmanThread.notify();
		} synchronized(timerThread) {
			timerThread.notify();
		} 
		PacManGame.time = 0;
	}

	public synchronized void deathSetup(int timeWait) {
		player.stop();
		player.changeFile(4);
		player.play();
		for (int i = 0; i < frightTime*120; i++)
			grid.frightDown();
		rightTurn = false; leftTurn = false; upTurn = false; downTurn = false;
		gameOver = grid.loseLife();
		grid.hideBonus();
		pacman.setPosition(14,26);
		ateDot = false;
		if (!gameOver) {
			pacman.setDirection(90);
			blinky.setJustEaten(false);
			pinky.setJustEaten(false);
			inky.setJustEaten(false);
			clyde.setJustEaten(false);
			pinky.setHouse(true);
			inky.setHouse(true);
			clyde.setHouse(true);
			blinky.setPosition(13,14);
			pinky.setPosition(11,18);
			inky.setPosition(16,18);
			clyde.setPosition(13,16);
			blinky.setDirection(90);
			pinky.setDirection(0);
			inky.setDirection(270);
			clyde.setDirection(90);
			blinky.setLeaving(true);
			pinky.setLeaving(false);
			inky.setLeaving(false);
			clyde.setLeaving(false);
			repaint();
			grid.setFright(false);
			PacManGame.time = 0;
			try {
				Thread.sleep(timeWait/2);
				repaint();
				Thread.sleep(timeWait/2);
			} catch (Exception e) {}
			repaint();
			paused = false;
			death = false;
			synchronized(blinkyThread) {
				blinkyThread.notify();
			} synchronized(pinkyThread) {
				pinkyThread.notify();
			} synchronized(inkyThread) {
				inkyThread.notify();
			} synchronized(clydeThread) {
				clydeThread.notify();
			} synchronized(pacmanThread) {
				pacmanThread.notify();
			} synchronized(timerThread) {
				timerThread.notify();
			} 
			PacManGame.time = 0;
		}
	}

	public boolean getWin() {
		for (int i = 0; i < grid.getGrid().length; i++) {
			for (int j = 0; j < grid.getGrid()[0].length; j++) {
				if (grid.getGrid()[i][j] == GridLayout.DOT_TILE)
					return false;
			}
		}
		return true;
	}

	public boolean getDeath() {
		return false;
	}

	public void testEatGhost() {
		if (blinky.getXPosition() == pacman.getXPosition() && blinky.getYPosition() == pacman.getYPosition()) {
			if (!blinky.getJustEaten()) {
				if (!(grid.getFright()) || (blinky.getEaten())) {
					paused = true; death = true;
					deathSetup(3000);
				} else {
					blinky.setTarget(13,14);
					blinky.setJustEaten(true);
					if (blinky.getJustEaten() && !(blinky.getEaten())) {
						grid.addPoints(numBonusPoints);
						numBonusPoints *= 2;
					}
					blinky.setEaten(true);
					player.changeFile(3);
					player.play();
				}
			}
		}
		if (pinky.getXPosition() == pacman.getXPosition() && pinky.getYPosition() == pacman.getYPosition()) {
			if (!pinky.getJustEaten()) {
				if (!(grid.getFright()) || (pinky.getEaten())) {
					paused = true; death = true;
					deathSetup(3000);
				} else {
					pinky.setTarget(13,14);
					pinky.setJustEaten(true);
					if (pinky.getJustEaten() && !(pinky.getEaten())) {
						grid.addPoints(numBonusPoints);
						numBonusPoints *= 2;
					}
					pinky.setEaten(true);
					player.changeFile(3);
					player.play();
				}
			}
		}
		if (inky.getXPosition() == pacman.getXPosition() && inky.getYPosition() == pacman.getYPosition()) {
			if (!(inky.getJustEaten())) {
				if (!(grid.getFright()) || (inky.getEaten())) {
					paused = true; death = true;
					deathSetup(3000);
				} else {
					inky.setTarget(13,14);
					inky.setJustEaten(true);
					if (inky.getJustEaten() && !(inky.getEaten())) {
						grid.addPoints(numBonusPoints);
						numBonusPoints *= 2;
					}
					inky.setEaten(true);
					player.changeFile(3);
					player.play();
				}
			}
		}
		if (clyde.getXPosition() == pacman.getXPosition() && clyde.getYPosition() == pacman.getYPosition()) {
			if (!(clyde.getJustEaten())) {
				if (!(grid.getFright()) || (clyde.getEaten())) {
					paused = true; death = true;
					deathSetup(3000);
				} else {
					clyde.setTarget(13,14);
					clyde.setJustEaten(true);
					if (clyde.getJustEaten() && !(clyde.getEaten())) {
						grid.addPoints(numBonusPoints);
						numBonusPoints *= 2;
					}
					clyde.setEaten(true);
					player.changeFile(3);
					player.play();
				}
			}
		}
	}

	public void tryChangeDir() {
		double x = pacman.getXPosition();
		double y = pacman.getYPosition();
		int tempDir = pacman.getDirection();
		if (pacman.getXPosition() > 0.4 && pacman.getXPosition() < 26.6) {
			if (rightTurn) {
				if (!(grid.getGrid()[(int) (x + 1)][(int)y] == GridLayout.WALL_TILE || 
						grid.getGrid()[(int) (x + 1)][(int) y] == GridLayout.DOOR_TILE)) {
					if (Math.abs(pacman.getY()-(pacman.getYPosition())) < .20) {
						pacman.setPosition((pacman.getDirection() == 90)?pacman.getX():pacman.getXPosition(), pacman.getYPosition());
						pacman.setDirection(90);
						rightTurn = false;
					}
				}
			} else if (leftTurn) {
				if (!(grid.getGrid()[(int) (x - 1)][(int) y] == GridLayout.WALL_TILE || 
						grid.getGrid()[(int) (x - 1)][(int) y] == GridLayout.DOOR_TILE)) {
					if (Math.abs(pacman.getY()-(pacman.getYPosition())) < .20) {
						pacman.setPosition((pacman.getDirection() == 270)?pacman.getX():pacman.getXPosition(), pacman.getYPosition());
						pacman.setDirection(270);
						leftTurn = false;
					}
				}
			} else if (upTurn) {
				if (!(grid.getGrid()[(int) x][(int) (y - 1)] == GridLayout.WALL_TILE || 
						grid.getGrid()[(int) x][(int) (y - 1)] == GridLayout.DOOR_TILE)) {
					if (Math.abs(pacman.getX()-(pacman.getXPosition())) < .20) {
						pacman.setPosition(pacman.getXPosition(), (pacman.getDirection() == 180)?pacman.getY():pacman.getYPosition());
						pacman.setDirection(0);
						upTurn = false;
					}
				}
			} else if (downTurn) {
				if (!(grid.getGrid()[(int) x][(int) (y + 1)] == GridLayout.WALL_TILE || 
						grid.getGrid()[(int) x][(int) (y + 1)] == GridLayout.DOOR_TILE)) {
					if (Math.abs(pacman.getX()-(pacman.getXPosition())) < .20) {
						pacman.setPosition(pacman.getXPosition(), (pacman.getDirection() == 0)?pacman.getY():pacman.getYPosition());
						pacman.setDirection(180);
						downTurn = false;
					}
				}
			}
			if (Math.abs(tempDir-pacman.getDirection()) == 180)
				mCDecrease = true;
		}
	}

	public boolean testForWall() {
		int[][] g = grid.getGrid();
		if (!(pacman.getXPosition() > 26.6 || pacman.getXPosition() < 0.4)) {
			switch (pacman.getDirection()) {
			case 0: if (g[(int) pacman.getX()][(int) (pacman.getY())] == GridLayout.WALL_TILE) return true; break;
			case 90: if (g[(int) (pacman.getX() + 1)][(int) pacman.getY()] == GridLayout.WALL_TILE) return true; break;
			case 180: if (g[(int) pacman.getX()][(int) (pacman.getY() + 1)] == GridLayout.WALL_TILE) return true; break;
			case 270: if (g[(int) (pacman.getX())][(int) pacman.getY()] == GridLayout.WALL_TILE) return true; break;
			}
		}
		return false;
	}

	public boolean getPaused() {
		return paused;
	}

	public void paint(Graphics g) {
		Font smallBold = new Font("Roman", Font.BOLD, 14);
		Font bigBold = new Font("Roman", Font.BOLD, 40);
		if (paused) g.setColor(g.getColor().darker().darker());
		int[][] gr = grid.getGrid();
		// Draw Grid Tiles //
		for (int i = 0; i < grid.getGrid().length; i++) {
			for (int j = 0; j < grid.getGrid()[0].length; j++) {
				if (gr[i][j] == GridLayout.DOT_TILE)
					g.drawImage(GridLayout.getDotImage(), i*16, j*16, null);
				else if (gr[i][j] == GridLayout.BIG_DOT_TILE) 
					g.drawImage(GridLayout.getBigDotImage(), i*16, j*16, null);
				else if (gr[i][j] == GridLayout.WALL_TILE || gr[i][j] == GridLayout.DOOR_TILE)
					g.drawImage(GridLayout.getWallImage(i,j), i*16, j*16, null);
			}
		}
		if (gr[13][26] == GridLayout.BONUS_TILE)
			g.drawImage(GridLayout.getBonusImage(), (13*16)-5, (26*16)-5, null);
		// Draw PacMan //
		switch (pacman.getDirection()) {
		case 0: g.drawImage(GridLayout.getPacmanImage(pacAnimation%4, 0), (int)(pacman.getX()*16-6), (int)(pacman.getY()*16-6), null); break;
		case 90: g.drawImage(GridLayout.getPacmanImage(pacAnimation%4, 90), (int)(pacman.getX()*16-6), (int)(pacman.getY()*16-6), null); break;
		case 180: g.drawImage(GridLayout.getPacmanImage(pacAnimation%4, 180), (int)(pacman.getX()*16-6), (int)(pacman.getY()*16-6), null); break;
		case 270: g.drawImage(GridLayout.getPacmanImage(pacAnimation%4, 270), (int)(pacman.getX()*16-6), (int)(pacman.getY()*16-6), null); break;
		}
		// Draw Ghosts //
		boolean isFlash = (grid.getFrightTimer() > frightTime*120-24*flash && grid.getFrightTimer()%24 < 12);
		//		boolean flash = ((grid.getFrightTimer() > frightTime*120-120 && grid.getFrightTimer() < frightTime*120-100) ||
		//				(grid.getFrightTimer() > frightTime*120-80 && grid.getFrightTimer() < frightTime*120-60) ||
		//				(grid.getFrightTimer() > frightTime*120-40 && grid.getFrightTimer() < frightTime*120-20));
		g.drawImage(GridLayout.getBlinkyImage(blinky.getDirection()/90, grid.getFright(), blinky.getJustEaten(), blinky.getEaten(), isFlash), 
				blinky.getXPosition()*16-6, blinky.getYPosition()*16-6, null);
		g.drawImage(GridLayout.getPinkyImage(pinky.getDirection()/90, grid.getFright(), pinky.getJustEaten(), pinky.getEaten(), isFlash), 
				pinky.getXPosition()*16-6, pinky.getYPosition()*16-6, null);
		g.drawImage(GridLayout.getInkyImage(inky.getDirection()/90, grid.getFright(), inky.getJustEaten(), inky.getEaten(), isFlash), 
				inky.getXPosition()*16-6, inky.getYPosition()*16-6, null);
		g.drawImage(GridLayout.getClydeImage(clyde.getDirection()/90, grid.getFright(), clyde.getJustEaten(), clyde.getEaten(), isFlash), 
				clyde.getXPosition()*16-6, clyde.getYPosition()*16-6, null);
		// Draw Lives and Score //
		g.setFont(smallBold);
		g.setColor(Color.WHITE);
		g.drawString("HIGH SCORE", 120, 16);
		g.drawString("" + grid.getScore(), 140, 32);
		g.drawString("LEVEL", 250, 16);
		g.drawString("" + level, 260, 32);
		int[] xPs = new int[3]; //{48,48,32}
		int[] yPs = {544,576,560};
		for (int i = 0; i < grid.getLives(); i++) {
			g.setColor(Color.YELLOW);
			g.fillOval(i*16*2+20, 548, 24, 24);
			g.setColor(Color.BLACK);
			xPs[0] = 16+(i+1)*32;
			xPs[1] = 16+(i+1)*32;
			xPs[2] = (i+1)*32;
			g.fillPolygon(xPs,yPs,3);
		}
		// Draw Status Text //
		g.setFont(bigBold);
		g.setColor(Color.WHITE);
		if (paused) {
			if (gameOver) {
				g.drawString("Game Over",125,238);
				g.setFont(smallBold);
				g.drawString("Press SPACE BAR to restart.",120,265);
			}
			else if (win)
				g.drawString("Get Ready",125,238);
			else if (death)
				g.drawString("Try Again",130,238);
			else g.drawString("PAUSED",145,238);
		}
	}

	public void update(Graphics g) {
		if (OSC == null || widthOfOSC != getSize().width || heightOfOSC != getSize().height) {
			OSC = null;
			OSC = createImage(getSize().width, getSize().height);
			widthOfOSC = getSize().width;
			heightOfOSC = getSize().height;
		}
		Graphics OSGr = OSC.getGraphics();  // Graphics context for drawing to OSC.
		OSGr.setColor(getBackground());
		OSGr.fillRect(0, 0, widthOfOSC, heightOfOSC);
		OSGr.setColor(getForeground());
		OSGr.setFont(getFont());
		paint(OSGr);    // Draw component's contents to OSGr instead of directly to g.
		OSGr.dispose(); // We're done with this graphics context.
		g.drawImage(OSC,0,0,this);  // Copy OSC to screen.
	} // end update()

	public void keyTyped(KeyEvent e) {}
	public synchronized void keyPressed(KeyEvent e) {
		if(!paused) {
			if(e.getKeyCode() == KeyEvent.VK_DOWN && pacman.getDirection() != 180) {downTurn=true; rightTurn=false; leftTurn=false; upTurn=false;}
			if(e.getKeyCode() == KeyEvent.VK_RIGHT && pacman.getDirection() != 90) {rightTurn=true; downTurn=false; leftTurn=false; upTurn=false;}
			if(e.getKeyCode() == KeyEvent.VK_LEFT && pacman.getDirection() != 270) {leftTurn=true; downTurn=false; rightTurn=false; upTurn=false;}
			if(e.getKeyCode() == KeyEvent.VK_UP && pacman.getDirection() != 0) {upTurn=true; downTurn=false; rightTurn=false; leftTurn=false;}
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			tInterrupt = true;
			paused = !paused;
			if (gameOver) {
				restartGame(3000);
				paused = false;
			}
			repaint();
			synchronized(pacmanThread) {
				if (!paused) {
					try {
						pacmanThread.notify();
					} catch (Exception imsExc) {}
				}
			}
			synchronized(blinkyThread) {
				if (!paused) {
					try {
						blinkyThread.notify();
					} catch (Exception imsExc) {}
				}
			}
			synchronized(pinkyThread) {
				if (!paused) {
					try {
						pinkyThread.notify();
					} catch (Exception imsExc) {}
				}
			}
			synchronized(inkyThread) {
				if (!paused) {
					try {
						inkyThread.notify();
					} catch (Exception imsExc) {}
				}
			}
			synchronized(clydeThread) {
				if (!paused) {
					try {
						clydeThread.notify();
					} catch (Exception imsExc) {}
				}
			}
			synchronized(timerThread) {
				if (!paused) {
					try {
						timerThread.notify();
					} catch (Exception imsExc) {}
				}
			} 
		}
	}
	public void keyReleased(KeyEvent e) {}
}