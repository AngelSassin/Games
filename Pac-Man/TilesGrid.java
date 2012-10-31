public class TilesGrid {
	private int[][] grid;
	private int score, lives, numLivesAdded, frightTimer, bonusPoints;
	private boolean fright, scatter;

	public TilesGrid(int lives, int score) {
		this.grid = new int[28][36];
		setTiles();
		this.lives = lives;
		this.score = score;
		numLivesAdded = 0;
	}
	
	public int getBonusPoints() {
		return bonusPoints;
	}
	
	public void setBonusPoints(int points) {
		bonusPoints = points;
	}

	private void addLife() {
		if (score > 10000*(numLivesAdded+1)) {
			numLivesAdded++;
			lives += 1;
		}
	}

	public void restart() {
		numLivesAdded = 0;
		lives = 2;
		score = 0;
	}

	public void setTiles() {
		for (int i = 0; i < this.grid.length; i++) {
			for (int j = 0; j < this.grid[0].length; j++) {
				grid[i][j] = GridLayout.GRID[i][j];
			}
		}
	}

	public void showBonus() {
		if (this.grid[13][26] == GridLayout.EMPTY_TILE)
			this.grid[13][26] = GridLayout.BONUS_TILE;
	}

	public void hideBonus() {
		if (this.grid[13][26] == GridLayout.BONUS_TILE)
			this.grid[13][26] = GridLayout.EMPTY_TILE;
	}

	public boolean getFright() {
		return fright;
	}

	public void setFright(boolean mode) {
		fright = mode;
	}

	public boolean getScatter() {
		return scatter;
	}

	public void setScatter(boolean mode) {
		scatter = mode;
	}

	public int getLives() {
		return lives;
	}

	public int getScore() {
		return score;
	}

	public boolean loseLife() {
		this.lives--;
		if (this.lives < 0) {
			this.lives++;
			return true;
		}
		return false;
	}

	public int[][] getGrid() {
		return this.grid;
	}

	public int getColumns() {
		return this.grid.length;
	}

	public int getRows() {
		return this.grid[0].length;
	}

	public int getTile(int x, int y) {
		return this.grid[x][y];
	}

	public void setTile(int x, int y, int tile) {
		this.grid[x][y] = tile;
	}

	public int getFrightTimer() {
		return frightTimer;
	}

	public void frightDown() {
		frightTimer++;
	}

	public void addPoints(int points) {
		score += points;
	}

	public void removeDot(int x, int y) {
		if (this.grid[x][y] == GridLayout.BIG_DOT_TILE) {
			score += 50;
			fright = true;
			frightTimer = 0;
		} else if (this.grid[x][y] == GridLayout.DOT_TILE)
			score += 10;
		else if (this.grid[x][y] == GridLayout.BONUS_TILE) {
			score += bonusPoints;
		}
		addLife();
	}
}