import sun.audio.*;
import java.io.*;
public class TetrisSFXPlayer
{
  private InputStream in;
  private AudioStream as;
  private static String[] sfxArr = {"Tetris Retro Theme.wav"};
  private static String[] bgmArr = {"Tetris Retro Theme.wav"};
  private String type = null;
  
  public TetrisSFXPlayer(int input, String playtype){
    String name = null;
    type = playtype.toLowerCase();
    if (type.equals("sfx")) name = sfxArr[input];
    else if (type.equals("bgm")) name = bgmArr[input];
    try{
      in = new FileInputStream(name);
      as = new AudioStream(in);
    } catch (FileNotFoundException ex) {
    } catch (IOException ex) {
    }
  }
  public void setType(String newType)
  {
    type = newType;    
  }
  public void changeFile(int input)
  {
    String name = null;
    if (type.equals("sfx")) name = sfxArr[input];
    else if (type.equals("bgm")) name = bgmArr[input];
    try{
      in = new FileInputStream(name);
      as = new AudioStream(in);
    } catch (FileNotFoundException ex) {
    } catch (IOException ex) {
    }
  }
  public void changeFile(String name)
  {
    try{
      in = new FileInputStream(name);
      as = new AudioStream(in);
    } catch (FileNotFoundException ex) {
    } catch (IOException ex) {
    }
  }
  
  public void play(){
    AudioPlayer.player.start(as);
  }
  public void stop(){
    AudioPlayer.player.stop(as);
  }
}