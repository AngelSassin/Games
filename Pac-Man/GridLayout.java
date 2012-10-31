import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class GridLayout {
	public static final int EMPTY_TILE = 1;
	public static final int WALL_TILE = 2;
	public static final int DOT_TILE = 3;
	public static final int BIG_DOT_TILE = 4;
	public static final int DOOR_TILE = 5;
	public static final int PACMAN_TILE = 6;
	public static final int BLINKY_TILE = 7;
	public static final int PINKY_TILE = 8;
	public static final int INKY_TILE = 9;
	public static final int CLYDE_TILE = 10;
	public static final int BONUS_TILE = 11;
	public static final int MIDDLE_Y = 17;
	public static final int MIDDLE_X = 14;
	public static final int[][] GRID = 
		{ {1,1,1,2,2,2,2,2,2,2,2,2,2,1,1,1,2,1,2,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1,1},
		{1,1,1,2,3,3,4,3,3,3,3,3,2,1,1,1,2,1,2,1,1,1,2,3,3,3,4,2,2,3,3,3,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,3,2,1,1,1,2,1,2,1,1,1,2,3,2,2,3,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,3,2,1,1,1,2,1,2,1,1,1,2,3,2,2,3,3,3,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,3,2,1,1,1,2,1,2,1,1,1,2,3,2,2,2,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,3,2,2,2,2,2,1,2,2,2,2,2,3,2,2,2,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,2,2,2,2,2,2,1,2,2,2,2,2,3,2,2,3,2,2,2,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,2,2,2,2,2,2,1,2,2,2,2,2,3,2,2,3,2,2,2,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,3,3,3,2,2,1,1,1,1,1,1,1,1,1,3,2,2,3,3,3,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,3,2,2,1,2,2,2,2,2,1,2,2,3,2,2,3,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,3,2,2,1,2,1,1,1,2,1,2,2,3,2,2,3,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,3,3,3,3,2,2,3,1,1,1,2,1,1,1,2,1,2,2,3,3,3,3,2,2,3,3,3,3,2,1,1},
		{1,1,1,2,2,2,2,2,3,2,2,2,2,2,1,5,1,1,1,2,1,2,2,2,2,2,1,2,2,2,2,2,3,2,1,1},
		{1,1,1,2,2,2,2,2,3,2,2,2,2,2,1,5,1,1,1,2,1,2,2,2,2,2,1,2,2,2,2,2,3,2,1,1},
		{1,1,1,2,3,3,3,3,3,2,2,3,1,1,1,2,1,1,1,2,1,2,2,3,3,3,3,2,2,3,3,3,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,3,2,2,1,2,1,1,1,2,1,2,2,3,2,2,3,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,3,2,2,1,2,2,2,2,2,1,2,2,3,2,2,3,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,3,3,3,2,2,1,1,1,1,1,1,1,1,1,3,2,2,3,3,3,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,2,2,2,2,2,2,1,2,2,2,2,2,3,2,2,3,2,2,2,2,2,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,2,2,2,2,2,2,1,2,2,2,2,2,3,2,2,3,2,2,2,2,2,3,2,1,1},
		{1,1,1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,3,2,2,2,2,2,1,2,2,2,2,2,3,2,2,2,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,3,2,1,1,1,2,1,2,1,1,1,2,3,2,2,2,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,1,2,3,2,2,3,2,1,1,1,2,1,2,1,1,1,2,3,2,2,3,3,3,3,2,2,3,2,1,1},
		{1,1,1,2,3,2,2,2,3,2,2,3,2,1,1,1,2,1,2,1,1,1,2,3,2,2,3,2,2,3,2,2,3,2,1,1},
		{1,1,1,2,3,3,4,3,3,3,3,3,2,1,1,1,2,1,2,1,1,1,2,3,3,3,4,2,2,3,3,3,3,2,1,1},
		{1,1,1,2,2,2,2,2,2,2,2,2,2,1,1,1,2,1,2,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,1,1}};

	public static final int G_BLANK = 0;
	public static final int G_D_T = 1;
	public static final int G_D_R = 2;
	public static final int G_D_L = 3;
	public static final int G_D_D = 4;
	public static final int G_S_V = 5;
	public static final int G_S_H = 6;
	public static final int G_DCO_TR = 7;
	public static final int G_DCO_TL = 8;
	public static final int G_DCO_BR = 9;
	public static final int G_DCO_BL = 10;
	public static final int G_DCI_TR = 11;
	public static final int G_DCI_TL = 12;
	public static final int G_DCI_BR = 13;
	public static final int G_DCI_BL = 14;
	public static final int G_SC_TR = 15;
	public static final int G_SC_TL = 16;
	public static final int G_SC_BR = 17;
	public static final int G_SC_BL = 18;
	public static final int G_T_TL = 19;
	public static final int G_T_TR = 20;
	public static final int G_R_BR = 21;
	public static final int G_R_TR = 22;
	public static final int G_L_BL = 23;
	public static final int G_L_TL = 24;
	public static final int G_LD = 25;
	public static final int G_RD = 26;
	private static BufferedImage blank;
	private static BufferedImage dT;
	private static BufferedImage dR;
	private static BufferedImage dL;
	private static BufferedImage dB;
	private static BufferedImage sV;
	private static BufferedImage sH;
	private static BufferedImage dOTR;
	private static BufferedImage dOTL;
	private static BufferedImage dOBR;
	private static BufferedImage dOBL;
	private static BufferedImage dITR;
	private static BufferedImage dITL;
	private static BufferedImage dIBR;
	private static BufferedImage dIBL;
	private static BufferedImage sTR;
	private static BufferedImage sTL;
	private static BufferedImage sBR;
	private static BufferedImage sBL;
	private static BufferedImage tTL;
	private static BufferedImage tTR;
	private static BufferedImage rBR;
	private static BufferedImage rTR;
	private static BufferedImage lBL;
	private static BufferedImage lTL;
	private static BufferedImage lD;
	private static BufferedImage rD;
	private static BufferedImage dot;
	private static BufferedImage bDot;
	private static BufferedImage cherry;
	private static BufferedImage[] pac = new BufferedImage[16];
	private static BufferedImage[] blinky = new BufferedImage[4];
	private static BufferedImage[] pinky = new BufferedImage[4];
	private static BufferedImage[] inky = new BufferedImage[4];
	private static BufferedImage[] clyde = new BufferedImage[4];
	private static BufferedImage ghostFlash;
	private static BufferedImage[] fright = new BufferedImage[4];
	private static BufferedImage[] eyes = new BufferedImage[4];
	public static final int[][] G_GRID =  
		{ {0 ,0 ,0 ,8 ,3 ,3 ,3 ,3 ,3 ,3 ,3 ,3 ,10,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,8 ,3 ,3 ,3 ,3 ,23,24,3 ,3 ,3 ,3 ,10,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,16,5 ,18,0 ,16,18,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,16,18,0 ,15,17,0 ,16,18,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,6 ,6 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,6 ,6 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,15,5 ,5 ,18,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,15,5 ,17,0 ,15,17,0 ,11,3 ,3 ,3 ,13,0 ,11,3 ,3 ,3 ,13,0 ,15,5 ,5 ,5 ,17,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,16,5 ,18,0 ,16,5 ,5 ,5 ,5 ,5 ,5 ,18,0 ,16,5 ,5 ,5 ,18,0 ,16,18,0 ,16,5 ,5 ,17,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,15,5 ,5 ,18,16,5 ,5 ,17,0 ,15,5 ,5 ,5 ,17,0 ,6 ,6 ,0 ,15,5 ,5 ,18,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,16,18,0 ,6 ,6 ,0 ,12,2 ,2 ,2 ,14,0 ,16,18,0 ,6 ,6 ,0 ,16,18,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,15,5 ,17,0 ,6 ,6 ,0 ,15,17,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,6 ,0 ,15,17,0 ,6 ,6 ,0 ,15,17,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,20,5 ,5 ,5 ,18,0 ,6 ,15,5 ,5 ,18,0 ,25,0 ,0 ,0 ,1 ,0 ,6 ,15,5 ,5 ,18,0 ,6 ,15,5 ,5 ,18,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,19,5 ,5 ,5 ,17,0 ,6 ,16,5 ,5 ,17,0 ,26,0 ,0 ,0 ,1 ,0 ,6 ,16,5 ,5 ,17,0 ,6 ,16,5 ,5 ,17,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,16,5 ,18,0 ,6 ,6 ,0 ,16,18,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,6 ,0 ,16,18,0 ,6 ,6 ,0 ,16,18,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,15,17,0 ,6 ,6 ,0 ,11,3 ,3 ,3 ,13,0 ,15,17,0 ,6 ,6 ,0 ,15,17,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,16,5 ,5 ,17,15,5 ,5 ,18,0 ,16,5 ,5 ,5 ,18,0 ,6 ,6 ,0 ,16,5 ,5 ,17,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,15,5 ,17,0 ,15,5 ,5 ,5 ,5 ,5 ,5 ,17,0 ,15,5 ,5 ,5 ,17,0 ,15,17,0 ,15,5 ,5 ,18,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,16,5 ,18,0 ,16,18,0 ,12,2 ,2 ,2 ,14,0 ,12,2 ,2 ,2 ,14,0 ,16,5 ,5 ,5 ,18,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,6 ,6 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,16,5 ,5 ,17,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,6 ,0 ,6 ,0 ,6 ,6 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,15,5 ,17,0 ,15,17,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,15,17,0 ,16,18,0 ,15,17,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,6 ,6 ,0 ,0 ,0 ,0 ,4 ,0 ,0 },
		{0 ,0 ,0 ,7 ,2 ,2 ,2 ,2 ,2 ,2 ,2 ,2 ,9 ,0 ,0 ,0 ,1 ,0 ,4 ,0 ,0 ,0 ,7 ,2 ,2 ,2 ,2 ,21,22,2 ,2 ,2 ,2 ,9 ,0 ,0 } };

	public static void setImages() {
		try {
			blank = ImageIO.read(new File("Tile_Images/Blank.png"));
			dT = ImageIO.read(new File("Tile_Images/Double Top.png"));
			dR = ImageIO.read(new File("Tile_Images/Double Right.png"));
			dL = ImageIO.read(new File("Tile_Images/Double Left.png"));
			dB = ImageIO.read(new File("Tile_Images/Double Bottom.png"));
			sV = ImageIO.read(new File("Tile_Images/Single Vertical.png"));
			sH = ImageIO.read(new File("Tile_Images/Single Horizontal.png"));
			dOTR = ImageIO.read(new File("Tile_Images/Double Outside Top Right.png"));
			dOTL = ImageIO.read(new File("Tile_Images/Double Outside Top Left.png"));
			dOBR = ImageIO.read(new File("Tile_Images/Double Outside Bottom Right.png"));
			dOBL = ImageIO.read(new File("Tile_Images/Double Outside Bottom Left.png"));
			dITR = ImageIO.read(new File("Tile_Images/Double Inside Top Right.png"));
			dITL = ImageIO.read(new File("Tile_Images/Double Inside Top Left.png"));
			dIBR = ImageIO.read(new File("Tile_Images/Double Inside Bottom Right.png"));
			dIBL = ImageIO.read(new File("Tile_Images/Double Inside Bottom Left.png"));
			sTR = ImageIO.read(new File("Tile_Images/Single Top Right.png"));
			sTL = ImageIO.read(new File("Tile_Images/Single Top Left.png"));
			sBR = ImageIO.read(new File("Tile_Images/Single Bottom Right.png"));
			sBL = ImageIO.read(new File("Tile_Images/Single Bottom Left.png"));
			tTL = ImageIO.read(new File("Tile_Images/Top with Top Left.png"));
			tTR = ImageIO.read(new File("Tile_Images/Top with Top Right.png"));
			rBR = ImageIO.read(new File("Tile_Images/Right with Bottom Right.png"));
			rTR = ImageIO.read(new File("Tile_Images/Right with Top Right.png"));
			lBL = ImageIO.read(new File("Tile_Images/Left with Bottom Left.png"));
			lTL = ImageIO.read(new File("Tile_Images/Left with Top Left.png"));
			lD = ImageIO.read(new File("Tile_Images/Left Door.png"));
			rD = ImageIO.read(new File("Tile_Images/Right Door.png"));
			pac[0] = ImageIO.read(new File("Tile_Images/PacManU1.png"));
			pac[1] = ImageIO.read(new File("Tile_Images/PacManU2.png"));
			pac[2] = ImageIO.read(new File("Tile_Images/PacMan3.png"));
			pac[3] = ImageIO.read(new File("Tile_Images/PacManU2.png"));
			pac[4] = ImageIO.read(new File("Tile_Images/PacManR1.png"));
			pac[5] = ImageIO.read(new File("Tile_Images/PacManR2.png"));
			pac[6] = ImageIO.read(new File("Tile_Images/PacMan3.png"));
			pac[7] = ImageIO.read(new File("Tile_Images/PacManR2.png"));
			pac[8] = ImageIO.read(new File("Tile_Images/PacManD1.png"));
			pac[9] = ImageIO.read(new File("Tile_Images/PacManD2.png"));
			pac[10] = ImageIO.read(new File("Tile_Images/PacMan3.png"));
			pac[11] = ImageIO.read(new File("Tile_Images/PacManD2.png"));
			pac[12] = ImageIO.read(new File("Tile_Images/PacManL1.png"));
			pac[13] = ImageIO.read(new File("Tile_Images/PacManL2.png"));
			pac[14] = ImageIO.read(new File("Tile_Images/PacMan3.png"));
			pac[15] = ImageIO.read(new File("Tile_Images/PacManL2.png"));
			blinky[0] = ImageIO.read(new File("Tile_Images/Blinky Up.png"));
			blinky[3] = ImageIO.read(new File("Tile_Images/Blinky Left.png"));
			blinky[1] = ImageIO.read(new File("Tile_Images/Blinky Right.png"));
			blinky[2] = ImageIO.read(new File("Tile_Images/Blinky Down.png"));
			pinky[0] = ImageIO.read(new File("Tile_Images/Pinky Up.png"));
			pinky[3] = ImageIO.read(new File("Tile_Images/Pinky Left.png"));
			pinky[1] = ImageIO.read(new File("Tile_Images/Pinky Right.png"));
			pinky[2] = ImageIO.read(new File("Tile_Images/Pinky Down.png"));
			inky[0] = ImageIO.read(new File("Tile_Images/Inky Up.png"));
			inky[3] = ImageIO.read(new File("Tile_Images/Inky Left.png"));
			inky[1] = ImageIO.read(new File("Tile_Images/Inky Right.png"));
			inky[2] = ImageIO.read(new File("Tile_Images/Inky Down.png"));
			clyde[0] = ImageIO.read(new File("Tile_Images/Clyde Up.png"));
			clyde[3] = ImageIO.read(new File("Tile_Images/Clyde Left.png"));
			clyde[1] = ImageIO.read(new File("Tile_Images/Clyde Right.png"));
			clyde[2] = ImageIO.read(new File("Tile_Images/Clyde Down.png"));
			ghostFlash = ImageIO.read(new File("Tile_Images/Ghost Flash.png"));
			fright[0] = ImageIO.read(new File("Tile_Images/Fright Up.png"));
			fright[3] = ImageIO.read(new File("Tile_Images/Fright Left.png"));
			fright[1] = ImageIO.read(new File("Tile_Images/Fright Right.png"));
			fright[2] = ImageIO.read(new File("Tile_Images/Fright Down.png"));
			eyes[0] = ImageIO.read(new File("Tile_Images/Eyes Up.png"));
			eyes[3] = ImageIO.read(new File("Tile_Images/Eyes Left.png"));
			eyes[1] = ImageIO.read(new File("Tile_Images/Eyes Right.png"));
			eyes[2] = ImageIO.read(new File("Tile_Images/Eyes Down.png"));
			dot = ImageIO.read(new File("Tile_Images/Dot.png"));
			bDot = ImageIO.read(new File("Tile_Images/Big Dot.png"));
			cherry = ImageIO.read(new File("Tile_Images/Cherry.png"));
		} catch (IOException e) {System.out.println(e);} 
	}

	public static Image getWallImage(int x, int y) {
		switch (G_GRID[x][y]) {
		case 0: return blank;
		case 1: return dT;
		case 2: return dR;
		case 3: return dL;
		case 4: return dB;
		case 5: return sV;
		case 6: return sH;
		case 7: return dOTR;
		case 8: return dOTL;
		case 9: return dOBR;
		case 10: return dOBL;
		case 11: return dITR;
		case 12: return dITL;
		case 13: return dIBR;
		case 14: return dIBL;
		case 15: return sTR;
		case 16: return sTL;
		case 17: return sBR;
		case 18: return sBL;
		case 19: return tTL;
		case 20: return tTR;
		case 21: return rBR;
		case 22: return rTR;
		case 23: return lBL;
		case 24: return lTL;
		case 25: return lD;
		case 26: return rD;
		}
		return null;
	}

	public static Image getPacmanImage(int animation, int dir) {
		return pac[animation + (dir/90)*4];
	}

	public static Image getDotImage() {
		return dot;
	}

	public static Image getBigDotImage() {
		return bDot;
	}

	public static Image getBonusImage() {
		return cherry;
	}

	public static Image getBlinkyImage(int animation, boolean frightMode, boolean justEaten, boolean eaten, boolean flash) {
		if (justEaten)
			return eyes[animation];
		if (frightMode) {
			if (eaten)
				return blinky[animation];
			if (flash)
				return ghostFlash; 
			return fright[animation];
		} 
		return blinky[animation];
	}

	public static Image getPinkyImage(int animation, boolean frightMode, boolean justEaten, boolean eaten, boolean flash) {
		if (justEaten)
			return eyes[animation];
		if (frightMode) {
			if (eaten)
				return pinky[animation];
			if (flash)
				return ghostFlash; 
			else return fright[animation];
		} 
		return pinky[animation];
	}

	public static Image getInkyImage(int animation, boolean frightMode, boolean justEaten, boolean eaten, boolean flash) {
		if (justEaten)
			return eyes[animation];
		if (frightMode) {
			if (eaten)
				return inky[animation];
			if (flash)
				return ghostFlash; 
			else return fright[animation];
		} 
		return inky[animation];
	}

	public static Image getClydeImage(int animation, boolean frightMode, boolean justEaten, boolean eaten, boolean flash) {
		if (justEaten)
			return eyes[animation];
		if (frightMode) {
			if (eaten)
				return clyde[animation];
			if (flash)
				return ghostFlash; 
			else return fright[animation];
		} 
		return clyde[animation];
	}

	public static int getTile(int x, int y) {
		return GRID[x][y];
	}

	public static int[][] getGrid() {
		return GRID;
	}

	public static int getRows() {
		return GRID.length;
	}

	public static int getColumns() {
		return GRID[0].length;
	}
}