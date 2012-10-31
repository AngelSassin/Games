import javax.swing.*;
import java.applet.*;

public class PacManLauncher extends Applet 
{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Pac-Man");
		PacManGame game = new PacManGame();
		frame.setContentPane(game);
		frame.setBounds(400,100,464,615);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.init();
		frame.setVisible(true);
	}
}