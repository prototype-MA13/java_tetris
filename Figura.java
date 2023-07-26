package game;

import java.util.Random;

public class Figura {
    private Tetromino figura;
    private int[][] coordenadas;

    public Figura() {
        // son las coordenadas de una pieza de tetris
        coordenadas = new int[4][2];
        // la figura se inicia sin forma, "nula" pero valida
        setFigura(Tetromino.sinForma);
    }

    // establece todas las coordenadas posibles para formar una figura
    void setFigura(Tetromino piezaTetris) {
        int[][][] coordTabla = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}}, // sinForma
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // enZ
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // enS
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // enI
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // enT
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}}, // Cuadrado
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // enL
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}} // enJ
        };
        // establece las coordenadas para una figura especifica figura
        for (int i = 0; i < 4; i++) {
            System.arraycopy(coordTabla[piezaTetris.ordinal()], 0, coordenadas, 0, 4);
        }
        figura = piezaTetris;
    }

    private void setX(int index, int x) {
        coordenadas[index][0] = x;
    }

    private void setY(int index, int y) {
        coordenadas[index][1] = y;
    }

    int x(int index) {
        return coordenadas[index][0];
    }

    int y(int index) {
        return coordenadas[index][1];
    }

    Tetromino getFigura() {
        return figura;
    }

    /* Crea un figura aleatoria de Tetris,
        esta se genera con un numero aleatorio
        entre 1 y 7, se crea la figura con setShape
     */
    void setRandomFigura() {
        var r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;

        Tetromino[] values = Tetromino.values();
        setFigura(values[x]);
    }

    public int minX() {
        int m = coordenadas[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coordenadas[i][0]);
        }
        return m;
    }

    public int minY() {
        int m = coordenadas[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coordenadas[i][1]);
        }
        return m;
    }

    /* Gira la figura hacia la izquierda,
        adaptando cada coordenada
        solo si no es la figura cuadrada
     */
    Figura girarIzquierda() {
        if (this.figura == Tetromino.Cuadrado) {
            return this;
        }
        var figura = new Figura();
        figura.figura = this.figura;
        for (int i = 0; i < 4; i++) {
            figura.setX(i, y(i));
            figura.setY(i, -x(i));
        }
        return figura;
    }

    /* Gira la figura hacia la derecha,
        adaptando cada coordenada,
        solo si no es la figura cuadrada
     */
    Figura girarDerecha() {
        if (this.figura == Tetromino.Cuadrado) {
            return this;
        }
        var figura = new Figura();
        figura.figura = this.figura;
        for (int i = 0; i < 4; i++) {
            figura.setX(i, -y(i));
            figura.setY(i, x(i));
        }
        return figura;
    }
}
