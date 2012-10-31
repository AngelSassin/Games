import sun.audio.*;
import java.io.*;
public class SFXPlayer
{
  private InputStream in;
  private AudioStream as;
  private static String[] sfxArr = {"SFX/Intro.wav","SFX/Siren.wav","SFX/WakaWaka.wav","SFX/GhostEat.wav","SFX/Death.wav"};
  private static String[] bgmArr = {"SFX/Intro.wav","SFX/Siren.wav","SFX/WakaWaka.wav","SFX/GhostEat.wav","SFX/Death.wav"};
  private String type = null;
  
  public SFXPlayer(int input, String playtype){
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
  
  public static void main(String[] args)
  {
    SFXPlayer player = new SFXPlayer(0, "bgm");
    player.changeFile(0);
    player.play();
    player.changeFile(1);
    player.play();
    player.changeFile(2);
    player.play();
    player.changeFile(3);
    player.play();
    player.changeFile(4);
    player.play();
  }
}