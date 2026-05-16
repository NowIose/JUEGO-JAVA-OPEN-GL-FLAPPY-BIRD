package com.graphics;

/**
 * Clase Pipe: Representa una tubería (obstáculo) en el juego.
 * Contiene la posición y las dimensiones del hueco (GAP).
 */
public class Pipe {
    public float x; // Posición horizontal
    public float gapCenterY; // Centro vertical del hueco
    public boolean scored; // Indica si ya se sumó el punto por esta tubería

    // Constantes compartidas (deben coincidir con FlappyGame o ser pasadas)
    public static final float PIPE_WIDTH = 0.18f;
    public static final float GAP_HEIGHT = 0.48f;

    public Pipe(float x, float gapCenterY) {
        this.x = x;
        this.gapCenterY = gapCenterY;
        this.scored = false;
    }

    // --- Métodos de ayuda para el renderizado ---

    public float getTopPipeY() {
        return gapCenterY + (GAP_HEIGHT * 0.5f);
    }

    public float getTopPipeHeight() {
        return 1.0f - getTopPipeY();
    }

    public float getBottomPipeY() {
        return -1.0f + (getBottomPipeHeight() * 0.5f);
    }

    public float getBottomPipeHeight() {
        return gapCenterY + 1.0f - (GAP_HEIGHT * 0.5f);
    }

    // --- Métodos para colisiones ---

    public float getLeftX() { return x - (PIPE_WIDTH * 0.5f); }
    public float getRightX() { return x + (PIPE_WIDTH * 0.5f); }
    public float getGapTopY() { return gapCenterY + (GAP_HEIGHT * 0.5f); }
    public float getGapBottomY() { return gapCenterY - (GAP_HEIGHT * 0.5f); }
}
