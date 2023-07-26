package game.sonido;

import javax.sound.sampled.Clip;

public class ControladorSonido {
    private Sonido caida;
    private Sonido lineaCompleta;

    public ControladorSonido() {
        caida = new Sonido("caida.wav");
        lineaCompleta = new Sonido("linea.wav");
    }
    public void playCaida() {
        caida.play();
    }
    public void playLineaCompleta() {
        lineaCompleta.play();
    }
}
