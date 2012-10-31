import java.applet.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Tetris extends Applet implements KeyListener 
{
  private static int[][] grid = new int[10][20];
  private static int[][] targetGrid = new int[10][20];
  private static int[][] mapGrid = new int[10][20];
  private static int numGrid, numMap;
  private static boolean dropSoft, dropHard;
  private static int speed, lines, combo, numClear, dropNum, score, maxCombo;
  private static int graphicSet;
  private static int width = 250;
  private static int height = 500;
  private static TetrisSFXPlayer player;
  private static Tetronimo piece = new Tetronimo();
  private static Tetronimo ghost = piece;
  private static Tetronimo firstPiece = new Tetronimo();
  private static Tetronimo secondPiece = new Tetronimo();
  private static Tetronimo thirdPiece = new Tetronimo();
  private static Tetronimo holdPiece = new Tetronimo();
  private static Image OSC;
  private static int widthOfOSC, heightOfOSC;
  private static boolean tInterrupt, holdDone, ghosting;
  private static boolean paused, gameOver, gameMenu, optionsMenu;
  private static int timer;
  
  Thread threadDropPause = new Thread() {
    public void run() {
      while (true) {
        if (!tInterrupt) {
          fall(false, false);
        } else 
          tInterrupt = false;
        refreshGrid();
        repaint();
        try {
          Thread.sleep(1000-(int)(speed*(49-Math.sqrt(2*speed))));
          if(paused) {
            synchronized(this){
              while(paused) {
                repaint();
                wait();
              }
            }
          }
        } catch(InterruptedException e){}
        repaint();
      }
    }
  }; 
  
  Thread threadGraphics = new Thread(){
    public void run() {
      while(true) {
        try {
          Thread.sleep(10);
        } catch(Exception e){}
        if (gameOver)
          paused = true;
        piece.refreshPoints(grid,mapGrid);
        tryGhost();
      }
    }
  };  
  
  Thread threadTimer = new Thread(){
    public void run() {
      while(true) {
        try {
          Thread.sleep(1000);
        } catch(Exception e){}
        if (gameOver || paused || gameMenu || optionsMenu) {}
        else timer++;
      }
    }
  };  
  
  Thread sfxThread = new Thread() {
    public void run() {
      while(true) {
        player.play();
        try {
          Thread.sleep(77165);
        } catch(Exception e){}  
      }
    }
  };
  
  public synchronized void init() {
    player = new TetrisSFXPlayer(0,"sfx");
    gameMenu = true;
    ghosting = true;
    this.requestFocus();
    timer = 0;
    width = 250;
    height = 500;
    setBackground(Color.BLACK);
    addKeyListener(this);
    threadDropPause.start();
    threadGraphics.start();
    threadTimer.start();
    sfxThread.start();
    refreshGrid();
    piece.refreshPoints(grid,mapGrid);
    tryGhost();
  }
  
  private void checkLine() {
    if (!gameOver) {
      for (int j = 0; j < 20; j++) {
        int k=0;
        for (int i = 0; i < 10; i++) {
          if (grid[i][j] > 0)
            k++;
        }
        if (k==10) { 
          for (int a = 0; a < 10; a++) {
            for (int b = j; b >= 0; b--) {
              if (b==0) grid[a][b] = 0;
              else {
                mapGrid[a][b] = mapGrid[a][b-1];
              }
            }
          }
          numClear++;
          lines++;
          speed = (lines/30);
        }
      }
      switch (numClear) {
        case 0: combo = 0; score += dropNum; break;
        case 1: combo++; score += 100*(speed+1) + 50*combo*(speed+1) + 2*dropNum; break;
        case 2: combo++; score += 300*(speed+1) + 50*combo*(speed+1) + 3*dropNum; break;
        case 3: combo++; score += 500*(speed+1) + 50*combo*(speed+1) + 4*dropNum; break;
        case 4: combo++; score += 800*(speed+1) + 50*combo*(speed+1) + 5*dropNum; break;
      } 
      if (maxCombo < combo) maxCombo = combo;
      numClear = 0;
      dropNum = 0;
    }
  }
  
  private void fall(boolean hard, boolean controlled) {
    boolean canFall = true;
    piece.getWidth(); piece.getHeight();
    do {
      refreshGrid();
      for (int c=piece.getXPosition()+piece.getMaxL(), i=0; i<piece.getWidth(); i++) {
        for (int d=piece.getYPosition()+piece.getMaxU(), j=0; j<piece.getHeight(); j++) {
          if (d+j < 0 || (grid[c+i][d+j] > 0 && grid[c+i][d+j] < 8 && (mapGrid[c+i][d+j] == 0 || mapGrid[c+i][d+j] == 8))) {
            if (d+j+1 == 20 || (d+j >= 0 && (mapGrid[c+i][d+j+1] > 0 && mapGrid[c+i][d+j+1] < 8)))
              canFall = false;
          }
        }
      }
      if (canFall) {
        piece.fall(1);
        if (controlled) {
          if (hard)
            dropNum+=2;
          else dropNum++;
        }
      }
    } while (hard && canFall);
    refreshGrid();
    if (!canFall) {
      for (int i=0; i<10; i++) {
        for (int j = 0; j<20; j++) {
          mapGrid[i][j] = grid[i][j];
          if (mapGrid[i][j] == 8) mapGrid[i][j] = 0;
        }
      }
      refreshGrid();
      checkLine();
      piece = firstPiece; 
      firstPiece = secondPiece;
      secondPiece = thirdPiece;
      thirdPiece = new Tetronimo();
      gameOver = getGameOver();
      holdDone = false;
      piece.refreshPoints(grid, mapGrid);
      tryGhost();
    }
  }
  
  private void tryGhost() {
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 20; j++) {
        if (mapGrid[i][j] == 8) mapGrid[i][j] = 0;
      }
    }
    if (ghosting) {
      boolean canGo = true;
      int[][] gPoints = piece.getPoints();
      while(canGo) {
        for (int i = 0; i < 4; i++) {
          gPoints[1][i]++;
        }
        for (int i = 0; i < 4; i++) {
          if (gPoints[1][i] == 20 || (mapGrid[gPoints[0][i]][gPoints[1][i]] > 0 && mapGrid[gPoints[0][i]][gPoints[1][i]] < 8)) {
            canGo = false;
            for (int j = 0; j < 4; j++) {
              gPoints[1][j]--;
            }
            break;
          }
        }
      }
      for (int j = 0; j < 4; j++) {
        mapGrid[gPoints[0][j]][gPoints[1][j]] = 8;
      }
    } 
    refreshGrid();
  }
  
  private void tryMove(int dir) {
    boolean canMove = true;
    piece.getWidth(); piece.getHeight();
    for (int c=piece.getXPosition()+piece.getMaxL(), i=0; i<piece.getWidth(); i++) {
      for (int d=piece.getYPosition()+piece.getMaxU(), j=0; j<piece.getHeight(); j++) {
        if (d+j > -1 && grid[c+i][d+j] > 0 && grid[c+i][d+j] < 8 && (mapGrid[c+i][d+j] == 0 || mapGrid[c+i][d+j] == 8)) { // Check if piece is found
          switch (dir) {
            case -1: if (c+i-1 == -1 || (mapGrid[c+i-1][d+j] > 0 && mapGrid[c+i-1][d+j] < 8))
              canMove = false;
            break;
            case 1: if (c+i+1 == 10 || (mapGrid[c+i+1][d+j] > 0 && mapGrid[c+i+1][d+j] < 8))
              canMove = false;
            break;
          }
        }
      }
    }
    if (canMove)
      piece.move(dir);
    refreshGrid();
    piece.refreshPoints(grid,mapGrid);
    tryGhost();
  }
  
  private void tryRotate(int dir) {
    piece.rotate(dir);
    boolean canRotate = true;
    int testTry = 0;
    int[] tempLoc = {piece.getXPosition(), piece.getYPosition()};
    do {
      piece.getWidth(); piece.getHeight();
      refreshGrid();
      int numOnGrid = 0;
      while (piece.getXPosition()+piece.getMaxL() < 0) {
        piece.move(1);
        refreshGrid();
      }
      while (piece.getXPosition()+piece.getMaxR() > 9) {
        piece.move(-1);
        refreshGrid();
      }
      if (numGrid - numMap != 4) {
        switch (testTry) {
          case 0: piece.move(1); break;
          case 1: piece.move(-2); break;
          case 2: piece.move(1); piece.fall(-1); break;
          case 3: piece.fall(1); piece.fall(1); if(piece.getIdentity() == 1 && piece.getYPosition() == 1 && piece.getRotation() == 270) piece.fall(1); break;
          case 4: piece.fall(-1); if(piece.getIdentity() == 1 && piece.getYPosition() == 1) piece.fall(-1); canRotate = false; break;
        }
        testTry++;
      } else testTry = 5;
    } while (testTry < 5);
    if (!canRotate) {
      if (piece.getYPosition()+piece.getMaxU() >= 0) {
        piece.rotate(dir*-1);
        piece.setLocation(tempLoc);
      }
    }
    piece.refreshPoints(grid,mapGrid);
    tryGhost();
  }
  
  private void tryHold() {
    if (!holdDone) {
      Tetronimo tempPiece = holdPiece;
      holdPiece = piece;
      piece = tempPiece;
      while(piece.getRotation() !=0) {
        piece.rotate(1);
      }
      int[] newPos = {5,0};
      piece.setLocation(newPos);
      holdDone = true;
      refreshGrid();
      piece.refreshPoints(grid,mapGrid);
      tryGhost();
    }
  }
  
  private boolean getGameOver() {
    for (int i = 0; i < 10; i++) {
      if (mapGrid[i][0] > 0 && mapGrid[i][0] < 8) {
        refreshGrid();
        return true;
      }
    }
    refreshGrid();
    return false;
  }
  
  private void restartGame() {
    timer = 0;
    maxCombo = 0;
    gameOver = false;
    speed = 1;
    score = 0;
    grid = new int[10][20];
    mapGrid = new int[10][20];
    lines = 0;
    piece = new Tetronimo();
    firstPiece = new Tetronimo();
    secondPiece = new Tetronimo();
    thirdPiece = new Tetronimo();
    holdPiece = new Tetronimo();
    piece.refreshPoints(grid,mapGrid);
    tryGhost();
  }
  
  private void refreshGrid() {
    for (int i=0; i<10; i++) {
      for (int j = 0; j<20; j++) {
        grid[i][j] = mapGrid[i][j];
      }
    }
    int xL;
    int xR;
    int yU;
    int yD;
    
    switch(piece.getXPosition()) {
      case 0: xL=0; xR=2; break;
      case 1: xL=-1; xR=2; break;
      case 8: xL=-2; xR=1; break;
      case 9: xL=-2; xR=0; break;
      default: xL=-2; xR=2;
    }
    switch(piece.getYPosition()) {
      case 0: yU=0; yD=2; break;
      case 1: yU=-1; yD=2; break;
      case 18: yU=-2; yD=1; break;
      case 19: yU=-2; yD=0; break;
      default: yU=-2; yD=2;
    }
    try {
      for (int i = piece.getXPosition()+xL, a=2+xL; i <= piece.getXPosition()+xR; i++, a++) {
        for (int j = piece.getYPosition()+yU, b=2+yU; j <= piece.getYPosition()+yD; j++, b++) {
          if (piece.getShape()[b][a] > 0) {
            grid[i][j] = piece.getShape()[b][a];
          }
        }
      }
    } catch(Exception e) {}
    
    numGrid = 0;
    numMap = 0;
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 20; j++) {
        if (grid[i][j] > 0 && grid[i][j] < 8)
          numGrid++;
        if (mapGrid[i][j] > 0 && mapGrid[i][j] < 8)
          numMap++;
      }
    }
    repaint();
  }
  
  // use a certain color for certain pieces
  // CYAN ORANGE MAGENTA BLUE YELLOW RED GREEN
  private Color getColorG(int i) {
    switch(i) {
      case 0: return Color.BLACK;  // BLACK
      case 1: return Color.CYAN;  // CYAN
      case 2: return Color.ORANGE;  // ORANGE
      case 3: return Color.MAGENTA;  // MAGENTA
      case 4: return Color.BLUE;  // BLUE
      case 5: return Color.YELLOW;  // YELLOW
      case 6: return Color.RED;  // RED
      case 7: return Color.GREEN;  // GREEN
      case 8: return Color.GRAY;  // GRAY
    }
    return null;
  }
  
  public void paint(Graphics g) {
    Font normFont = g.getFont();
    g.setFont(normFont);
    if (!gameMenu) {
      if (!paused) {
        for (int i = 0; i < 10; i++) {
          for (int j = 0; j < 20; j++) {
            g.setColor(getColorG(grid[i][j]));
            switch (graphicSet) {
              case 2: g.fillRect(i*25+1,j*25+1,25-1,25-1); break;
              default: g.setColor(g.getColor().darker().darker()); g.fillRect(i*25+1,j*25+1,25-1,25-1); g.setColor(g.getColor().brighter());
                g.fillRect(i*25+3,j*25+3,25-5,25-5); g.setColor(g.getColor().brighter()); g.fillRect(i*25+5,j*25+5,25-9,25-9); break;
              case 3: g.drawRect(i*25+1,j*25+1,25-2,25-3); break;
              case 4: g.drawRect(i*25+1,j*25+1,25-2,25-3); g.drawRect(i*25+4,j*25+4,25-5,25-6); g.drawRect(i*25+7,j*25+7,25-8,25-9); g.drawRect(i*25+10,j*25+10,25-11,25-12);
                g.drawRect(i*25+13,j*25+13,25-14,25-15); g.drawRect(i*25+16,j*25+16,25-17,25-18); g.drawRect(i*25+19,j*25+19,25-20,25-21); break;
              case 5: g.drawRect(i*25+1,j*25+1,25-2,25-3); g.drawRect(i*25+4,j*25+4,25-8,25-9); g.drawRect(i*25+7,j*25+7,25-14,25-15); break;
              case 6: g.fillRect(i*25+1,j*25+1,25-1,25-1); g.setColor(Color.BLACK); g.fillRect(i*25+3,j*25+3,25-5,25-5); 
                if (grid[i][j] != 0) g.setColor(Color.GRAY); g.fillRect(i*25+5,j*25+5,25-9,25-9); break;
            }
          }
        }
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0,0,width,height-1);
        g.drawString("NEXT",304,150);
        g.drawString("Time: " + timer,260,325);
        g.drawString("Lines: "+lines,260,350);
        g.drawString("Score: "+score,260,375);
        g.drawString("Combo: "+combo,260,400);
        g.drawString("press",304,450);
        g.drawString("P",316,462);
        g.drawString("to pause",295,474);
        g.drawString("Q to quit",295,495);
        
        switch(firstPiece.getIdentity()) {
          case 1: g.setColor(Color.CYAN); g.fillRect(280,170,19,19); g.fillRect(300,170,19,19); g.fillRect(320,170,19,19); g.fillRect(340,170,19,19); break;
          case 2: g.setColor(Color.ORANGE); g.fillRect(290,160,19,19); g.fillRect(310,160,19,19); g.fillRect(330,160,19,19); g.fillRect(290,180,19,19); break;
          case 3: g.setColor(Color.MAGENTA); g.fillRect(290,160,19,19); g.fillRect(310,160,19,19); g.fillRect(330,160,19,19); g.fillRect(310,180,19,19); break;
          case 4: g.setColor(Color.BLUE); g.fillRect(290,160,19,19); g.fillRect(310,160,19,19); g.fillRect(330,160,19,19); g.fillRect(330,180,19,19); break;
          case 5: g.setColor(Color.YELLOW); g.fillRect(300,160,19,19); g.fillRect(320,160,19,19); g.fillRect(300,180,19,19); g.fillRect(320,180,19,19); break;
          case 6: g.setColor(Color.RED); g.fillRect(290,160,19,19); g.fillRect(310,160,19,19); g.fillRect(310,180,19,19); g.fillRect(330,180,19,19); break;
          case 7: g.setColor(Color.GREEN); g.fillRect(290,180,19,19); g.fillRect(310,180,19,19); g.fillRect(310,160,19,19); g.fillRect(330,160,19,19); break;
        }
        switch(secondPiece.getIdentity()) {
          case 1: g.setColor(Color.CYAN); g.fillRect(280,220,19,19); g.fillRect(300,220,19,19); g.fillRect(320,220,19,19); g.fillRect(340,220,19,19); break;
          case 2: g.setColor(Color.ORANGE); g.fillRect(290,210,19,19); g.fillRect(310,210,19,19); g.fillRect(330,210,19,19); g.fillRect(290,230,19,19); break;
          case 3: g.setColor(Color.MAGENTA); g.fillRect(290,210,19,19); g.fillRect(310,210,19,19); g.fillRect(330,210,19,19); g.fillRect(310,230,19,19); break;
          case 4: g.setColor(Color.BLUE); g.fillRect(290,210,19,19); g.fillRect(310,210,19,19); g.fillRect(330,210,19,19); g.fillRect(330,230,19,19); break;
          case 5: g.setColor(Color.YELLOW); g.fillRect(300,210,19,19); g.fillRect(320,210,19,19); g.fillRect(300,230,19,19); g.fillRect(320,230,19,19); break;
          case 6: g.setColor(Color.RED); g.fillRect(290,210,19,19); g.fillRect(310,210,19,19); g.fillRect(310,230,19,19); g.fillRect(330,230,19,19); break;
          case 7: g.setColor(Color.GREEN); g.fillRect(290,230,19,19); g.fillRect(310,230,19,19); g.fillRect(310,210,19,19); g.fillRect(330,210,19,19); break;
        }
        switch(thirdPiece.getIdentity()) {
          case 1: g.setColor(Color.CYAN); g.fillRect(280,270,19,19); g.fillRect(300,270,19,19); g.fillRect(320,270,19,19); g.fillRect(340,270,19,19); break;
          case 2: g.setColor(Color.ORANGE); g.fillRect(290,260,19,19); g.fillRect(310,260,19,19); g.fillRect(330,260,19,19); g.fillRect(290,280,19,19); break;
          case 3: g.setColor(Color.MAGENTA); g.fillRect(290,260,19,19); g.fillRect(310,260,19,19); g.fillRect(330,260,19,19); g.fillRect(310,280,19,19); break;
          case 4: g.setColor(Color.BLUE); g.fillRect(290,260,19,19); g.fillRect(310,260,19,19); g.fillRect(330,260,19,19); g.fillRect(330,280,19,19); break;
          case 5: g.setColor(Color.YELLOW); g.fillRect(300,260,19,19); g.fillRect(320,260,19,19); g.fillRect(300,280,19,19); g.fillRect(320,280,19,19); break;
          case 6: g.setColor(Color.RED); g.fillRect(290,260,19,19); g.fillRect(310,260,19,19); g.fillRect(310,280,19,19); g.fillRect(330,280,19,19); break;
          case 7: g.setColor(Color.GREEN); g.fillRect(290,280,19,19); g.fillRect(310,280,19,19); g.fillRect(310,260,19,19); g.fillRect(330,260,19,19); break;
        }
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("HOLD",303,50);
        switch(holdPiece.getIdentity()) {
          case 1: g.setColor(Color.CYAN); g.fillRect(280,70,19,19); g.fillRect(300,70,19,19); g.fillRect(320,70,19,19); g.fillRect(340,70,19,19); break;
          case 2: g.setColor(Color.ORANGE); g.fillRect(290,60,19,19); g.fillRect(310,60,19,19); g.fillRect(330,60,19,19); g.fillRect(290,80,19,19); break;
          case 3: g.setColor(Color.MAGENTA); g.fillRect(290,60,19,19); g.fillRect(310,60,19,19); g.fillRect(330,60,19,19); g.fillRect(310,80,19,19); break;
          case 4: g.setColor(Color.BLUE); g.fillRect(290,60,19,19); g.fillRect(310,60,19,19); g.fillRect(330,60,19,19); g.fillRect(330,80,19,19); break;
          case 5: g.setColor(Color.YELLOW); g.fillRect(300,60,19,19); g.fillRect(320,60,19,19); g.fillRect(300,80,19,19); g.fillRect(320,80,19,19); break;
          case 6: g.setColor(Color.RED); g.fillRect(290,60,19,19); g.fillRect(310,60,19,19); g.fillRect(310,80,19,19); g.fillRect(330,80,19,19); break;
          case 7: g.setColor(Color.GREEN); g.fillRect(290,80,19,19); g.fillRect(310,80,19,19); g.fillRect(310,60,19,19); g.fillRect(330,60,19,19); break;
        } 
      }
      if (paused) {
        Font gameOverFont = new Font("roman", Font.BOLD, 32);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setFont(gameOverFont);
        g.setColor(Color.RED);
        g.drawString("PAUSED",135,175);
        gameOverFont = new Font("roman", Font.BOLD, 20);
        g.setColor(Color.MAGENTA);
        g.setFont(gameOverFont);
        g.drawString("INSTRUCTIONS",125,225);
        gameOverFont = new Font("roman", Font.BOLD, 15);
        g.setColor(Color.RED);
        g.setFont(gameOverFont);
        g.drawString("Unpause: P",160,250);
        g.drawString("Rotate: Z or X / A or D",124,270);
        g.drawString("Move: Left or Right",130,290);
        g.drawString("Drop: Down or S",141,310);
        g.drawString("Hard Drop: Up or Space",115,330);
        g.drawString("Hold: Control",152,350);
        g.drawString("Toggle Ghosting: V",130,370);
      }
      if (gameOver) {
        Font gameOverFont = new Font("roman", Font.BOLD, 32);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        g.setFont(gameOverFont);
        g.setColor(Color.RED);
        g.drawString("GAME OVER",105,175);
        gameOverFont = new Font("roman", Font.BOLD, 20);
        g.setColor(Color.MAGENTA);
        g.setFont(gameOverFont);
        g.drawString("RESULTS",155,225);
        gameOverFont = new Font("roman", Font.BOLD, 15);
        g.setColor(Color.RED);
        g.setFont(gameOverFont);
        g.drawString("Time Played: " + timer,155,250);
        g.drawString("Max Combo: " + maxCombo,155,270);
        g.drawString("Lines: " + lines,155,290);
        g.drawString("Score: " + score,155,310);
        g.setColor(Color.MAGENTA);
        g.drawString("Press P to continue",130,350);
      }
    } else {
      if (!optionsMenu) {
        Font startMenuFont = new Font("roman", Font.BOLD, 25);
        g.setFont(startMenuFont);
        g.setColor(Color.WHITE);
        g.drawString("Press SPACE BAR to Start",43,200);
        g.drawString("Press O for Options",85,235);
      } else {
        Font opFont = new Font("roman", Font.BOLD, 30);
        g.setFont(opFont);
        g.setColor(Color.RED);
        g.drawString("OPTIONS",135,200);
        Font optionsMenuFont = new Font("roman", Font.BOLD, 18);
        g.setFont(optionsMenuFont);
        g.setColor(Color.WHITE);
        g.drawString("Press 1-6 to change graphics settings",40,230);
        g.drawString("Press G to toggle ghosting: " + ((ghosting) ? "ON":"OFF"),60,260);
        g.drawString("Graphics Setting Preview:",80,290);
        g.setColor(Color.RED);
        for (int i = 0; i < 2; i++) {
          g.setColor((i==0) ? Color.RED:Color.BLUE);
          switch (graphicSet) {
            default: g.setColor(g.getColor().darker().darker()); g.fillRect(150,300,100,100); g.setColor(g.getColor().brighter());
            g.fillRect(158,308,84,84); g.setColor(g.getColor().brighter()); g.fillRect(165,315,70,70); break;
            case 2: g.fillRect(150,300,100,100); break;
            case 3: g.drawRect(150,300,100,100); break;
            case 4: g.drawRect(150,300,100,100); g.drawRect(165,315,85,85); g.drawRect(180,330,70,70); g.drawRect(195,345,55,55);
            g.drawRect(210,360,40,40); g.drawRect(225,375,25,25); g.drawRect(240,390,10,10); break;
            case 5: g.drawRect(150,300,100,100); g.drawRect(165,315,70,70); g.drawRect(180,330,40,40); break;
            case 6: g.fillRect(150,300,100,100); g.setColor(Color.BLACK); g.fillRect(160,310,80,80); 
            g.setColor(Color.GRAY); g.fillRect(170,320,60,60); break;
          } 
        }
        Font startMenuFont = new Font("roman", Font.BOLD, 25);
        g.setColor(Color.WHITE);
        g.setFont(startMenuFont);
        g.drawString("Press SPACE BAR to Start",43,435);
      }
      g.setColor(Color.MAGENTA);
      g.fillRect(10,50,19,19); g.fillRect(30,50,19,19); g.fillRect(50,50,19,19); g.fillRect(30,70,19,19); g.fillRect(30,90,19,19); 
      g.fillRect(30,110,19,19); g.fillRect(30,130,19,19);
      g.setColor(Color.YELLOW);
      g.fillRect(70,50,19,19); g.fillRect(90,50,19,19); g.fillRect(110,50,19,19); g.fillRect(70,70,19,19); g.fillRect(70,90,19,19); 
      g.fillRect(70,110,19,19); g.fillRect(70,130,19,19); g.fillRect(90,130,19,19); g.fillRect(110,130,19,19); g.fillRect(90,90,19,19); 
      g.setColor(Color.RED);
      g.fillRect(130,50,19,19); g.fillRect(150,50,19,19); g.fillRect(170,50,19,19); g.fillRect(150,70,19,19); g.fillRect(150,90,19,19); 
      g.fillRect(150,110,19,19); g.fillRect(150,130,19,19);
      g.setColor(Color.BLUE);
      g.fillRect(190,50,19,19); g.fillRect(190,70,19,19); g.fillRect(190,90,19,19); g.fillRect(190,110,19,19); g.fillRect(190,130,19,19);
      g.fillRect(210,50,19,19); g.fillRect(230,50,19,19); g.fillRect(230,70,19,19); g.fillRect(210,90,19,19); g.fillRect(230,110,19,19); g.fillRect(230,130,19,19);
      g.setColor(Color.CYAN);
      g.fillRect(250,50,19,19); g.fillRect(270,50,19,19); g.fillRect(290,50,19,19); g.fillRect(270,70,19,19); g.fillRect(270,90,19,19); 
      g.fillRect(270,110,19,19); g.fillRect(270,130,19,19); g.fillRect(250,130,19,19); g.fillRect(290,130,19,19);
      g.setColor(Color.GREEN);
      g.fillRect(310,50,19,19); g.fillRect(310,70,19,19); g.fillRect(310,90,19,19); g.fillRect(310,130,19,19); g.fillRect(330,130,19,19);
      g.fillRect(330,50,19,19); g.fillRect(350,50,19,19); g.fillRect(350,90,19,19); g.fillRect(330,90,19,19); g.fillRect(350,130,19,19); g.fillRect(350,110,19,19);
      g.setColor(Color.ORANGE);
      g.fillRect(372,50,19,19); g.fillRect(372,70,19,19); g.fillRect(372,90,19,19); g.fillRect(372,130,19,19);
      g.setColor(Color.LIGHT_GRAY);
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
  
  public synchronized void keyPressed(KeyEvent e) {
    if (gameMenu) {
      if(e.getKeyCode() == KeyEvent.VK_O)
        optionsMenu = !optionsMenu;
      if(optionsMenu) {
        if(e.getKeyCode() == KeyEvent.VK_G)
          ghosting = !ghosting;
        if(e.getKeyCode() == KeyEvent.VK_1)
          graphicSet = 1;
        if(e.getKeyCode() == KeyEvent.VK_2)
          graphicSet = 2;
        if(e.getKeyCode() == KeyEvent.VK_3)
          graphicSet = 3;
        if(e.getKeyCode() == KeyEvent.VK_4)
          graphicSet = 4;
        if(e.getKeyCode() == KeyEvent.VK_5)
          graphicSet = 5;
        if(e.getKeyCode() == KeyEvent.VK_6)
          graphicSet = 6;
      }
    }
    
    if(e.getKeyCode() == KeyEvent.VK_P) {
      tInterrupt = true;
      paused = !paused;
      if (gameOver) {
        restartGame();
      }
      if (gameMenu) {
        restartGame();
        gameMenu = false;
        paused = false;
      }
      synchronized(threadDropPause) {
        if (!paused)
          threadDropPause.notify();
      }
    }
    if(!paused) {
      if(e.getKeyCode() == KeyEvent.VK_LEFT) {
        tryMove(-1);
      }
      if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
        tryMove(1);
      }
      if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
        tInterrupt = true;
        threadDropPause.interrupt();
        try {
          threadDropPause.start();
        } catch (IllegalThreadStateException asdf) {}
        fall(false, true);
      }
      if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
        tInterrupt = true;
        threadDropPause.interrupt();
        try {
          threadDropPause.start();
        } catch (IllegalThreadStateException asdf) {}
        fall(true, true);
        if (gameMenu) {
          restartGame();
          gameMenu = false;
          paused = false;
        }
      }
      if(e.getKeyCode() == KeyEvent.VK_V) {
        ghosting = !ghosting;
      }
      if(e.getKeyCode() == KeyEvent.VK_Q) {
        paused = false;
        restartGame();
        gameOver = false;
        gameMenu = true;
        optionsMenu = false;
      }
    }
  }
  
  public synchronized void keyReleased(KeyEvent e) { 
    if(!paused) {
      if(e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_A) {
        tryRotate(1);
      }
      if(e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_D) {
        tryRotate(-1);
      }
      if(e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_CONTROL) {
        tryHold();
      }
    }
  }
  public void keyTyped(KeyEvent e) { }
}