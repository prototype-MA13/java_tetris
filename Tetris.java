package game;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {
    private JLabel statusbar;

    public Tetris() {
        initUI();
    }

    // crea La interfaz grafica
    private void initUI() {
        // La puntuacion se muestra en la parte inferior
        // del tablero
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        // se Crea el tablero del juego y se inicia la partida
        var tablero = new Tablero(this);
        add(tablero);
        tablero.start();

        // muestra el titulo del juego y establece las
        // dimensiones de la pantalla
        setTitle("Tetris");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    JLabel getStatusBar() {
        return statusbar;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var game = new Tetris();
            game.setVisible(true);
        });
    }
}
