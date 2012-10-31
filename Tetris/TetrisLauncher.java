import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.applet.*;

public class TetrisLauncher extends Applet 
{
  public static void main(String[] args)
  {
    JFrame frame = new JFrame("Tetris");
    Tetris game = new Tetris();
    frame.setContentPane(game);
    frame.setBounds(400,100,420,538);
    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    game.init();
    game.start();
    frame.setVisible(true);
  }
}