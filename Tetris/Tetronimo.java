import java.awt.*;
import java.util.*;
import java.io.*;
import java.applet.*;

public class Tetronimo {
  private int[][] shape = new int[5][5];
  private int[][] targetShape = new int[5][5];
  private int[][] blockLocations = new int[2][4];
  private int identity;
  private boolean landed;
  private int[] position = new int[2];
  private int width;
  private int height;
  private int maxL;
  private int maxR;
  private int maxU;
  private int maxD;
  private int maxTL;
  private int maxTR;
  private int maxTU;
  private int maxTD;
  private int rotation;
  private static Random rand = new Random();
  
  public Tetronimo() {
    identity = rand.nextInt(7)+1;
    shape = makeShape();
    position[0] = 5; position[1] = 0;
    rotation = 0;

    width = getWidth();
    height = getHeight();
  }
  
  private Tetronimo(int id) {
    identity = id;
    shape = makeShape();
    position[0] = 5; position[1] = 0;
    rotation = 0;

    width = getWidth();
    height = getHeight();
  }
  
  public void fall(int dir) {
    this.setYPosition(this.getYPosition()+dir);

  }
  
  public void setLanded(boolean l) {
    this.landed = l;
  }
  
  public boolean getLanded() {
    return this.landed;
  }
  
  public void move(int dir) {
    this.setXPosition(this.position[0] + dir);

  }
  
  public void rotate(int dir) {
    if (this.identity != 5) {
      if (dir == -1)
        this.setRotation((this.rotation-90)%360);
      if (dir == 1)
        this.setRotation((this.rotation+90)%360);
      if (this.rotation < 0)
        this.rotation += 360;
      this.rotateShape(dir);
    }

  }
  
  public int[][] getPoints() {
    return this.blockLocations;
  }
  
  public void refreshPoints(int[][] grid, int[][] mapGrid) {
    int times = 0;
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 20; j++) {
        if (grid[i][j] > 0 && grid[i][j] < 8 && (mapGrid[i][j] == 0 || mapGrid[i][j] == 8)) {
          this.blockLocations[0][times] = i;
          this.blockLocations[1][times] = j;
          times++;
        }
      }
    }
    for (int i = times; i > 4; i++) {
      this.blockLocations[0][i] = 0;
      this.blockLocations[1][i] = 0;
      times++;
    }
  }
  
  
  public int getIdentity() {
    return this.identity;
  }
  public int getMaxL() {
    return this.maxL;
  }
  public int getMaxR() {
    return this.maxR;
  }
  public int getMaxU() {
    return this.maxU;
  }
  public int getMaxD() {
    return this.maxD;
  }
  public int getMaxTL() {
    return this.maxTL;
  }
  public int getMaxTR() {
    return this.maxTR;
  }
  public int getMaxTU() {
    return this.maxTU;
  }
  public int getMaxTD() {
    return this.maxTD;
  }
  public int getRotation() {
    return this.rotation;
  }
  
  private void setRotation(int rot) {
    this.rotation = rot;
  }
  
  public int getWidth() {
    int wMax = 0;
    int wMin = 4;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (this.shape[i][j] > 0) {
          if (j < wMin)
            wMin = j;
          if (j > wMax)
            wMax = j;
        }
      }
    }
    this.maxL = wMin-2;
    this.maxR = wMax-2;
    return (wMax-wMin)+1;
  }
  
  public int getTWidth() {
    int wMax = 0;
    int wMin = 4;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (this.shape[i][j] > 0) {
          if (j < wMin)
            wMin = j;
          if (j > wMax)
            wMax = j;
        }
      }
    }
    this.maxTL = wMin-2;
    this.maxTR = wMax-2;
    return (wMax-wMin)+1;
  }
  
  public int getHeight() {
    int hMax = 0;
    int hMin = 4;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (this.shape[i][j] > 0) {
          if (i < hMin) 
            hMin = i;
          if (i > hMax)
            hMax = i;
        }
      }
    }
    this.maxU = hMin-2;
    this.maxD = hMax-2;
    return (hMax-hMin)+1;
  }
  
  public int getTHeight() {
    int hMax = 0;
    int hMin = 4;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (this.shape[i][j] > 0) {
          if (i < hMin) 
            hMin = i;
          if (i > hMax)
            hMax = i;
        }
      }
    }
    this.maxTU = hMin-2;
    this.maxTD = hMax-2;
    return (hMax-hMin)+1;
  }
  
  public int getXPosition() {
    return this.position[0];
  }
  
  public int getYPosition() {
    return this.position[1];
  }
  
  private void setXPosition(int pos) {
    this.position[0] = pos;

  }
  
  private void setYPosition(int pos) {
    this.position[1] = pos;

  }
  
  public void setLocation(int[] pos) {
    this.position = pos;

  }
  
  public int[][] getShape() {
    return shape;
  }
  
  public int[][] getTargetShape() {
    return targetShape;
  }
  
  private void rotateShape(int dir) {
    int[][] tempArray = new int[5][5];
    if (dir == 1) {
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          tempArray[i][j] = this.shape[j][4-i];
        }
      }
    } else if (dir == -1) {
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          tempArray[i][j] = this.shape[4-j][i];
        }
      }
    }
    this.shape = tempArray;
    this.getWidth();
    this.getHeight();
  }
  
  public void rotateTShape(int dir) {
    int[][] tempArray = new int[5][5];
    if (dir == 1) {
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          tempArray[i][j] = this.shape[j][4-i];
        }
      }
    } else if (dir == -1) {
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          tempArray[i][j] = this.shape[4-j][i];
        }
      }
    }
    this.targetShape = tempArray;
  }
  
  private int[][] makeShape() {
    switch(this.identity) {
      case 1: int[][] lineB = {{0,0,0,0,0},{0,0,0,0,0},{1,1,1,1,0},{0,0,0,0,0},{0,0,0,0,0}}; return lineB;
      case 2: int[][] lB = {{0,0,0,0,0},{0,0,0,0,0},{0,2,2,2,0},{0,2,0,0,0},{0,0,0,0,0}}; return lB;
      case 3: int[][] tB = {{0,0,0,0,0},{0,0,0,0,0},{0,3,3,3,0},{0,0,3,0,0},{0,0,0,0,0}}; return tB;
      case 4: int[][] rLB = {{0,0,0,0,0},{0,0,0,0,0},{0,4,4,4,0},{0,0,0,4,0},{0,0,0,0,0}}; return rLB;
      case 5: int[][] squareB = {{0,0,0,0,0},{0,0,0,0,0},{0,5,5,0,0},{0,5,5,0,0},{0,0,0,0,0}}; return squareB;
      case 6: int[][] squiglyB = {{0,0,0,0,0},{0,0,0,0,0},{0,6,6,0,0},{0,0,6,6,0},{0,0,0,0,0}}; return squiglyB;
      case 7: int[][] rSquiglyB = {{0,0,0,0,0},{0,0,0,0,0},{0,0,7,7,0},{0,7,7,0,0},{0,0,0,0,0}}; return rSquiglyB;
    }
    return null;
  }
  
  public static void main(String[] args) {
    
  }
}