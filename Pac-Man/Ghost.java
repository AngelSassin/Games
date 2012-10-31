public class Ghost {
	public static final double SLOW_SPEED = .95;
	public static final double MEDIUM_SPEED = .85;
	public static final double FAST_SPEED = .75;
	public static final double SLOW_FRIGHT_SPEED = 1.35;
	public static final double MEDIUM_FRIGHT_SPEED = 1.25;
	public static final double FAST_FRIGHT_SPEED = 1.15;
	public static final double SLOW_TUNNEL_SPEED = 1.6;
	public static final double MEDIUM_TUNNEL_SPEED = 1.5;
	public static final double FAST_TUNNEL_SPEED = 1.4;
	public static final double EYEBALL_SPEED = .35;
	public static final int BLINKY = 1;
	public static final int PINKY = 2;
	public static final int INKY = 3;
	public static final int CLYDE = 4;
	public final TilesGrid grid;
	private boolean inHouse, warpTunnel, eaten, justEaten, leavingHouse;
	private int tempTile;
	private int identity;
	private int direction;
	private int xPosition;
	private int yPosition;
	private int targetX;
	private int targetY;

	public Ghost(TilesGrid grid, int id) {
		this.grid = grid;
		this.eaten = false;
		this.identity = id;
		this.direction = 90;
		tempTile = 1;
		switch (id) {
		case 1: this.inHouse = false; this.xPosition = 13; this.yPosition = 14; this.direction = 90;
		grid.setTile((int)this.xPosition, (int)this.yPosition, GridLayout.BLINKY_TILE); break;
		case 2: this.inHouse = true; this.xPosition = 11; this.yPosition = 18; this.direction = 0;
		grid.setTile((int)this.xPosition, (int)this.yPosition, GridLayout.PINKY_TILE); break;
		case 3: this.inHouse = true; this.xPosition = 16; this.yPosition = 18; this.direction = 270;
		grid.setTile((int)this.xPosition, (int)this.yPosition, GridLayout.INKY_TILE); break;
		case 4: this.inHouse = true; this.xPosition = 13; this.yPosition = 16; this.direction = 90;
		grid.setTile((int)this.xPosition, (int)this.yPosition, GridLayout.CLYDE_TILE); break;
		}
		leavingHouse = identity == 1;
	}

	public void setTempTile(int tile) {
		this.tempTile = tile;
	}

	public void setTarget(int x, int y) {
		this.targetX = x;
		this.targetY = y;
	}

	public int getTempTile() {
		return this.tempTile;
	}

	public boolean inHouse() {
		return this.inHouse;
	}

	public void setHouse(boolean flag) {
		this.inHouse = flag;
	}

	public boolean getEaten() {
		return this.eaten;
	}

	public void setEaten(boolean eaten) {
		this.eaten = eaten;
	}

	public boolean getJustEaten() {
		return this.justEaten;
	}

	public void setJustEaten(boolean justEaten) {
		this.justEaten = justEaten;
	}

	public void leaveHouse() {
		leavingHouse = true;
	}

	public boolean leavingHouse() {
		return leavingHouse;
	}
	
	public void setLeaving(boolean leaving) {
		leavingHouse = leaving;
	}

	public boolean getWarpTunnel() {
		return this.warpTunnel;
	}

	public int getXPosition() {
		return (int)this.xPosition;
	}

	public int getYPosition() {
		return (int)this.yPosition;
	}

	public void setDirection(int dir) {
		this.direction = dir;
	}

	public int getTargetX() {
		return this.targetX;
	}

	public int getTargetY() {
		return this.targetY;
	}

	public double getX() {
		return xPosition;
	}

	public double getY() {
		return yPosition;
	}

	public void refreshTarget(int pacX, int pacY, int blinkyX, int blinkyY, int pacDir) {
		if (!justEaten) {
			if ((!grid.getFright() || this.eaten) && !grid.getScatter() && !inHouse) {
				switch (this.identity) {
				case 1: this.targetX = pacX; this.targetY = pacY; break;
				case 2: switch (pacDir) {
				case 0: this.targetX = pacX; this.targetY = pacY - 4; break;
				case 90: this.targetX = pacX + 4; this.targetY = pacY; break;
				case 180: this.targetX = pacX; this.targetY = pacY + 4; break;
				case 270: this.targetX = pacX - 4; this.targetY = pacY; break;
				} break;
				case 3: switch (pacDir) {
				case 0: this.targetX = pacX + (pacX - blinkyX); this.targetY = (pacY-2) + ((pacY-2) - blinkyY); break;
				case 90: this.targetX = (pacX+2) + ((pacX+2) - blinkyX); this.targetY = pacY + (pacY - blinkyY); break;
				case 180: this.targetX = pacX + (pacX - blinkyX); this.targetY = (pacY+2) + ((pacY+2) - blinkyY); break;
				case 270: this.targetX = (pacX-2) + ((pacX-2) - blinkyX); this.targetY = pacY + (pacY - blinkyY); break;
				} break;
				case 4: if (Math.sqrt(Math.pow(this.xPosition-pacX, 2) + Math.pow(this.yPosition-pacY, 2)) > 8) {
					this.targetX = pacX; this.targetY = pacY; break;
				} else {
					this.targetX = 0; this.targetY = 34; break;
				}
				}
			} else if (inHouse) {
				targetX = 14;
				if (leavingHouse)
					targetY = 0;
				else targetY = 18;
				if (getYPosition() < 15) {
					inHouse = false;
				}
			} else {
				switch (this.identity) {
				case 1: targetX = 25; targetY = 0; break;
				case 2: targetX = 2; targetY = 0; break;
				case 3: targetX = 27; targetY = 35; break;
				case 4: targetX = 0; targetY = 35; break;
				}
			}
		} else {
			inHouse = true;
			targetX = 14;
			targetY = 17;
		}
	}

	private void warp() {
		if (this.xPosition == 27) {
			this.xPosition = 0;
		} else if (this.xPosition == 0) {
			this.xPosition = 27;
		}
	}

	public void setPosition(int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
	}

	public int getDirection() {
		return this.direction;
	}

	public void move() {
		if ((this.xPosition <= 5 || this.xPosition >= 22) && this.yPosition == 17)
			this.warpTunnel = true;
		else this.warpTunnel = false;
		if ((this.xPosition == 0 && this.direction == 270) || (this.xPosition == 27 && this.direction == 90))
			this.warp();
		else {
			switch (this.direction) {
			case 0: this.yPosition--; break;
			case 90: this.xPosition++; break;
			case 180: this.yPosition++; break;
			case 270: this.xPosition--; break;
			}
		}
		for (int i = 0; i < grid.getGrid().length; i++) {
			for (int j = 0; j < grid.getGrid()[0].length; j++) {
				if (grid.getGrid()[i][j] == this.identity+6 && (i != this.xPosition || j != this.yPosition))
					grid.setTile(i,j,this.tempTile);
			}
		}
		for (int i = 0; i < grid.getGrid().length; i++) {
			for (int j = 0; j < grid.getGrid()[0].length; j++) {
				if (i == this.xPosition && j == this.yPosition) {
					this.tempTile = grid.getTile((int)this.xPosition,(int)this.yPosition);
					grid.setTile(i,j,this.identity+6);
				}
			}
		}
		if (this.xPosition != 0 && this.xPosition != 27) {
			double upDist = 999, downDist = 999, rightDist = 999, leftDist = 999;
			double[] upTile = new double[2], downTile = new double[2], leftTile = new double[2], rightTile = new double[2];
			if (grid.getTile((int)this.xPosition+1,(int)this.yPosition) != GridLayout.WALL_TILE && 
					(leavingHouse || grid.getTile((int)this.xPosition+1,(int)this.yPosition) != GridLayout.DOOR_TILE)) {
				if (this.direction != 270) {
					rightTile[0] = this.xPosition+1; rightTile[1] = this.yPosition;
					rightDist = Math.sqrt(Math.pow(this.targetX-rightTile[0], 2) + Math.pow(this.targetY-rightTile[1], 2));
				}
			}
			if (grid.getTile((int)this.xPosition-1,(int)this.yPosition) != GridLayout.WALL_TILE && 
					(leavingHouse || grid.getTile((int)this.xPosition-1,(int)this.yPosition) != GridLayout.DOOR_TILE)) {
				if (this.direction != 90) {
					leftTile[0] = this.xPosition-1; leftTile[1] = this.yPosition;
					leftDist = Math.sqrt(Math.pow(this.targetX-leftTile[0], 2) + Math.pow(this.targetY-leftTile[1], 2));
				}
			}
			if (grid.getTile((int)this.xPosition,(int)this.yPosition-1) != GridLayout.WALL_TILE && 
					(leavingHouse || grid.getTile((int)this.xPosition,(int)this.yPosition-1) != GridLayout.DOOR_TILE)) {
				if (this.direction != 180) {
					upTile[0] = this.xPosition; upTile[1] = this.yPosition-1;
					upDist = Math.sqrt(Math.pow(this.targetX-upTile[0], 2) + Math.pow(this.targetY-upTile[1], 2));
				}
			}
			if (grid.getTile((int)this.xPosition,(int)this.yPosition+1) != GridLayout.WALL_TILE && 
					(leavingHouse || grid.getTile((int)this.xPosition,(int)this.yPosition+1) != GridLayout.DOOR_TILE)) {
				if (this.direction != 0) {
					downTile[0] = this.xPosition; downTile[1] = this.yPosition+1;
					downDist = Math.sqrt(Math.pow(this.targetX-downTile[0], 2) + Math.pow(this.targetY-downTile[1], 2));
				}
			}
			if (justEaten || !((this.yPosition == 14 || this.yPosition == 26) && (this.xPosition < 17 && this.xPosition > 10))) {
				if (upDist <= downDist && upDist <= leftDist && upDist <= rightDist)
					this.direction = 0;
				else if (downDist <= upDist && downDist <= leftDist && downDist <= rightDist)
					this.direction = 180;
				else if (leftDist <= downDist && leftDist <= rightDist && leftDist <= upDist)
					this.direction = 270;
				else if (rightDist <= downDist && rightDist <= leftDist && rightDist <= upDist)
					this.direction = 90;
			} else if (rightDist <= leftDist)
				this.direction = 90;
			else this.direction = 270;
			if (this.xPosition == targetX && this.yPosition == targetY && this.justEaten) {
				this.justEaten = false;
				this.eaten = true;
			}
		}
	}

	public void turnBack() {
		int xP = (int)this.xPosition;
		int yP = (int)this.yPosition;
		switch (this.direction) {
		case 0: if (grid.getTile(xP,yP-1) != GridLayout.WALL_TILE) this.yPosition--; break;
		case 90: if (grid.getTile(xP+1,yP) != GridLayout.WALL_TILE && xP != 27) this.xPosition++; break;
		case 180: if (grid.getTile(xP,yP+1) != GridLayout.WALL_TILE) this.yPosition++; break;
		case 270: if (grid.getTile(xP-1,yP) != GridLayout.WALL_TILE && xP != 0) this.xPosition--; break;
		}
		this.direction = (this.direction + 180)%360;
	}

	/*
	private void setFrightDirection() {
		if (this.xPosition != 0 && this.xPosition != 27) {
			double upDist = 999, downDist = 999, rightDist = 999, leftDist = 999;
			double[] upTile = new double[2], downTile = new double[2], leftTile = new double[2], rightTile = new double[2];
			if (grid.getTile((int)this.xPosition+1,(int)this.yPosition) != GridLayout.WALL_TILE && 
					grid.getTile((int)this.xPosition+1,(int)this.yPosition) != GridLayout.DOOR_TILE) {
				rightTile[0] = this.xPosition+1; rightTile[1] = this.yPosition;
				rightDist = Math.sqrt(Math.pow(this.targetX-rightTile[0], 2) + Math.pow(this.targetY-rightTile[1], 2));
			}
			if (grid.getTile((int)this.xPosition-1,(int)this.yPosition) != GridLayout.WALL_TILE &&
					grid.getTile((int)this.xPosition-1,(int)this.yPosition) != GridLayout.DOOR_TILE) {
				leftTile[0] = this.xPosition-1; leftTile[1] = this.yPosition;
				leftDist = Math.sqrt(Math.pow(this.targetX-leftTile[0], 2) + Math.pow(this.targetY-leftTile[1], 2));
			}
			if (grid.getTile((int)this.xPosition,(int)this.yPosition-1) != GridLayout.WALL_TILE && 
					grid.getTile((int)this.xPosition,(int)this.yPosition-1) != GridLayout.DOOR_TILE) {
				upTile[0] = this.xPosition; upTile[1] = this.yPosition-1;
				upDist = Math.sqrt(Math.pow(this.targetX-upTile[0], 2) + Math.pow(this.targetY-upTile[1], 2));
			}
			if (grid.getTile((int)this.xPosition,(int)this.yPosition+1) != GridLayout.WALL_TILE && 
					grid.getTile((int)this.xPosition,(int)this.yPosition+1) != GridLayout.DOOR_TILE) {
				downTile[0] = this.xPosition; downTile[1] = this.yPosition+1;
				downDist = Math.sqrt(Math.pow(this.targetX-downTile[0], 2) + Math.pow(this.targetY-downTile[1], 2));
			}
			if (!((this.yPosition == 14 || this.yPosition == 26) && (this.xPosition < 17 && this.xPosition > 10))) {
				if (upDist <= downDist && upDist <= leftDist && upDist <= rightDist)
					this.direction = 0;
				else if (downDist <= upDist && downDist <= leftDist && downDist <= rightDist)
					this.direction = 180;
				else if (leftDist <= downDist && leftDist <= rightDist && leftDist <= upDist)
					this.direction = 270;
				else if (rightDist <= downDist && rightDist <= leftDist && rightDist <= upDist)
					this.direction = 90;
			} else if (rightDist <= leftDist)
				this.direction = 90;
			else this.direction = 270;
		}
	}
	 */

}