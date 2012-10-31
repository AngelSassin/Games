public class PacMan {
	public static final double SLOW_SPEED = .9;
	public static final double MEDIUM_SPEED = .8;
	public static final double FAST_SPEED = .7;
	public static final double SLOW_FRIGHT_SPEED = .8;
	public static final double MEDIUM_FRIGHT_SPEED = .75;
	public static final double FAST_FRIGHT_SPEED = .7;
	public final TilesGrid grid;
	private int direction;
	private double xPosition;
	private double yPosition;

	public PacMan(TilesGrid tGrid) {
		this.direction = 90;
		this.xPosition = 14;
		this.yPosition = 26;
		grid = tGrid;
		grid.setTile((int)(this.xPosition), (int)(this.yPosition), GridLayout.PACMAN_TILE);
	}

	private void warp() {
		if (getXPosition() == 27) {
			this.xPosition = 0;
		} else if (getXPosition() == 0) {
			this.xPosition = 27;
		}
		for (int i = 0; i < grid.getGrid().length; i++) {
			for (int j = 0; j < grid.getGrid()[0].length; j++) {
				if (i == this.xPosition && j == this.yPosition)
					grid.setTile(i,j,GridLayout.PACMAN_TILE);
				else if (grid.getGrid()[i][j] == GridLayout.PACMAN_TILE)
					grid.setTile(i,j,GridLayout.EMPTY_TILE);
			}
		}
	}

	public int getXPosition() {
		return (int)(this.xPosition+.5);
	}

	public int getYPosition() {
		return (int)(this.yPosition+.5);
	}
	
	public double getX() {
		return xPosition;
	}
	
	public double getY() {
		return yPosition;
	}
	
	public void setPosition(double x, double y) {
		xPosition = x;
		yPosition = y;
	}

	public void setDirection(int d) {
		this.direction = d;
	}

	public int getDirection() {
		return this.direction;
	}

	public boolean move(int counter) {
		boolean removedDot = false;
		if ((this.xPosition <= .3 && this.direction == 270) || (this.xPosition >= 26.7 && this.direction == 90))
			this.warp();
		else {
			switch (this.direction) {
			case 0: this.yPosition -= 1.0/counter; break;
			case 90: this.xPosition += 1.0/counter; break;
			case 180: this.yPosition += 1.0/counter; break;
			case 270: this.xPosition -= 1.0/counter; break;
			}
			if (grid.getGrid()[getXPosition()][getYPosition()] == GridLayout.DOT_TILE || 
					grid.getGrid()[getXPosition()][getYPosition()] == GridLayout.BIG_DOT_TILE) {
				grid.removeDot(getXPosition(),getYPosition()); removedDot = true;
			} else if (grid.getGrid()[getXPosition()][getYPosition()] == GridLayout.BONUS_TILE)
				grid.removeDot(getXPosition(),getYPosition());
			for (int i = 0; i < grid.getGrid().length; i++) {
				for (int j = 0; j < grid.getGrid()[0].length; j++) {
					if (i == getXPosition() && j == getYPosition())
						grid.setTile(i,j,GridLayout.PACMAN_TILE);
					else if (grid.getGrid()[i][j] == GridLayout.PACMAN_TILE)
						grid.setTile(i,j,GridLayout.EMPTY_TILE);
				}
			}
		}
		return removedDot;
	}
}