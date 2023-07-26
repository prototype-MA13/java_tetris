package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import game.sonido.ControladorSonido;


public class Tablero extends JPanel {
    private final int ANCHURA = 10;
    private final int ALTURA = 22;
    private final int INTERVALO = 300;

    private Timer tiempo;
    private boolean caidaCompleta = false;
    private boolean enPausa = false;
    private int LineasCompletas = 0;
    private int posX = 0;
    private int posY = 0;
    private JLabel status;
    private Figura enJuego;
    private Tetromino[] tablero;
    ControladorSonido controladorSonido = new ControladorSonido();

    public Tablero(Tetris parent) {
        initBoard(parent);
    }
    // Inicia el tablero del juego (GUI)
    private void initBoard(Tetris tetris) {
        setFocusable(true);
        status = tetris.getStatusBar();
        addKeyListener(new Controlador());
    }

    private int anchoCuadro() {
        return (int) getSize().getWidth() / ANCHURA;
    }

    private int altoCuadro() {
        return (int) getSize().getHeight() / ALTURA;
    }

    private Tetromino forma(int x, int y) {
        return tablero[(y * ANCHURA) + x];
    }

    // inicia el juego y el temporizador
    void start() {
        enJuego = new Figura();
        tablero = new Tetromino[ANCHURA * ALTURA];

        limpiarTablero();
        nuevaFigura();

        tiempo = new Timer(INTERVALO, new cicloDeJuego());
        tiempo.start();

    }

    private void pausa() {
        enPausa = !enPausa;

        if (enPausa) {
            status.setText("En Pausa");
        } else {
            status.setText(String.valueOf(LineasCompletas));
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color darkGray  = new Color(30, 30, 30);

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.lightGray);
        int boardTop = getHeight() - ALTURA * altoCuadro();

        // Dibujar líneas verticales
        for (int i = 0; i <= ANCHURA; i++) {
            int x = i * anchoCuadro();
            g.drawLine(x, 0, x, getHeight());
        }

        // Dibujar líneas horizontales
        for (int i = 0; i <= ALTURA; i++) {
            int y = boardTop + i * altoCuadro();
            g.drawLine(0, y, getWidth(), y);
        }
        dibujarTablero(g);
    }

    private void dibujarTablero(Graphics g) {
        var size = getSize();
        int boardTop = (int) size.getHeight() - ALTURA * altoCuadro();

        for (int i = 0; i < ALTURA; i++) {
            for (int j = 0; j < ANCHURA; j++) {
                Tetromino shape = forma(j, ALTURA - i - 1);
                if (shape != Tetromino.sinForma) {
                    pintarFigura(g, j * anchoCuadro(),
                            boardTop + i * altoCuadro(), shape);
                }
            }
        }

        if (enJuego.getFigura() != Tetromino.sinForma) {
            for (int i = 0; i < 4; i++) {
                int x = posX + enJuego.x(i);
                int y = posY - enJuego.y(i);
                pintarFigura(g, x * anchoCuadro(),
                        boardTop + (ALTURA - y - 1) * altoCuadro(),
                        enJuego.getFigura());
            }
        }
    }

    private void caidaLibre() {
        int newY = posY;
        while (newY > 0) {
            if (!moverPieza(enJuego, posX, newY - 1)) {
                break;
            }
            newY--;
        }
        caidaCompleta();
    }

    private void caida() {
        if (!moverPieza(enJuego, posX, posY - 1)) {
            caidaCompleta();
        }
    }

    // Llena el tablero de figuras vacias.
    private void limpiarTablero() {
        for (int i = 0; i < ALTURA * ANCHURA; i++) {
            tablero[i] = Tetromino.sinForma;
        }
    }

    /* La figura se coloca en el tablero, y se comprueba si una linea
        esta completa para eliminarla.
        Despues se genera una nueva pieza, si es posible.
     */
    private void caidaCompleta() {
        controladorSonido.playCaida();
        for (int i = 0; i < 4; i++) {
            int x = posX + enJuego.x(i);
            int y = posY - enJuego.y(i);
            tablero[(y * ANCHURA) + x] = enJuego.getFigura();
        }
        borrrarLineasCompletas();
        if (!caidaCompleta) {
            nuevaFigura();
        }
    }

    /* Crea una nueva pieza de Tetris de forma aleatoria,
        si no puede crear porque las coordenadas no estan
        libres, el juego termina.
     */

    private void nuevaFigura() {
        enJuego.setRandomFigura();
        posX = ANCHURA / 2 + 1;
        posY = ALTURA - 1 + enJuego.minY();
        if (!moverPieza(enJuego, posX, posY)) {
            enJuego.setFigura(Tetromino.sinForma);
            tiempo.stop();
            var msg = String.format("Game over. Score: %d", LineasCompletas);
            status.setText(msg);
        }
    }

    /*Mueve la pieza de tetris,
        el metodo da falso si se sobrepasan los limites
        o hay figuras que bloqueen el paso
     */
    private boolean moverPieza(Figura newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= ANCHURA || y < 0 || y >= ALTURA) {
                return false;
            }
            if (forma(x, y) != Tetromino.sinForma) {
                return false;
            }
        }
        enJuego = newPiece;
        posX = newX;
        posY = newY;
        repaint();
        return true;
    }

    /* Comprueba si alguna linea en el tablero esta completa.
        Si hay al menos una la elimina y aumenta el contador
        de lineas.
        Las lineas superiores a la completa se mueven una fila abajo.
     */
    private void borrrarLineasCompletas() {
        int numLineasCompletas = 0;
        for (int i = ALTURA - 1; i >= 0; i--) {
            boolean lineaCompleta = true;
            for (int j = 0; j < ANCHURA; j++) {
                if (forma(j, i) == Tetromino.sinForma) {
                    lineaCompleta = false;
                    break;
                }
            }
            if (lineaCompleta) {
                controladorSonido.playLineaCompleta();
                numLineasCompletas++;
                for (int k = i; k < ALTURA - 1; k++) {
                    for (int j = 0; j < ANCHURA; j++) {
                        tablero[(k * ANCHURA) + j] = forma(j, k + 1);
                    }
                }
            }
        }

        if (numLineasCompletas > 0) {
            this.LineasCompletas += numLineasCompletas;
            status.setText(String.valueOf(this.LineasCompletas));
            caidaCompleta = true;
            enJuego.setFigura(Tetromino.sinForma);
        }
    }

    private void pintarFigura (Graphics g, int x, int y, Tetromino figura) {
        Color colores[] = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        var color = colores[figura.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, anchoCuadro() - 2, altoCuadro() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + altoCuadro() - 1, x, y);
        g.drawLine(x, y, x + anchoCuadro() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + altoCuadro() - 1,
                x + anchoCuadro() - 1, y + altoCuadro() - 1);
        g.drawLine(x + anchoCuadro() - 1, y + altoCuadro() - 1,
                x + anchoCuadro() - 1, y + 1);
    }

    // Crea un nuevo ciclo de juego
    private class cicloDeJuego implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            crearCiclo();
        }
    }

    /* El juego consta de dos ciclos, uno actualiza los
        datos de juego y el otro dibuja el tablero
     */
    private void crearCiclo() {
        actualizarDatos();
        repaint();
    }

    private void actualizarDatos() {
        if (enPausa) {
            return;
        }
        if (caidaCompleta) {
            caidaCompleta = false;
            nuevaFigura();
        } else {
            caida();
        }
    }

    // El controlador maneja la entrada de las teclas con KeyAdapter
    class Controlador extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (enJuego.getFigura() == Tetromino.sinForma) {
                return;
            }
            int keycode = e.getKeyCode();
            // Java 12 switch expressions
            switch (keycode) {
                case KeyEvent.VK_P -> pausa();
                case KeyEvent.VK_LEFT -> moverPieza(enJuego, posX - 1, posY);
                case KeyEvent.VK_RIGHT -> moverPieza(enJuego, posX + 1, posY);
                case KeyEvent.VK_DOWN -> moverPieza(enJuego.girarDerecha(), posX, posY);
                case KeyEvent.VK_UP -> moverPieza(enJuego.girarIzquierda(), posX, posY);
                case KeyEvent.VK_SPACE -> caidaLibre();
                case KeyEvent.VK_D -> caida();
            }
        }
    }
}
