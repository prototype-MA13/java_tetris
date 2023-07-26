package game.sonido;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sonido {
    private Clip clip;

    public Sonido (String archivo) {
        try{
            // carga el archivo de sonido
            File archivoSonido = new File("sonidos/"+ archivo);
            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(archivoSonido);

            // crea el clip de sonido
            clip = AudioSystem.getClip();
            clip.open(audio);
        } catch (UnsupportedAudioFileException | IOException |
                 LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public void play() {
        if (clip != null && !clip.isRunning()) {
            // reproduce el sonido
            clip.setFramePosition(0);
            clip.start();
        }
    }
}
