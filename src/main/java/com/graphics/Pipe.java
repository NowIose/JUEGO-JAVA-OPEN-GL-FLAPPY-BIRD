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
    // --- Nuevo método en Pipe.java ---
    public void dibujar(Renderer renderer) {
        // --- TUBERÍA SUPERIOR ---
        float topPipeHeight = this.getTopPipeHeight();
        float topPipeCenterY = this.getTopPipeY() + topPipeHeight * 0.5F;
        // Cuerpo
        renderer.drawRect(this.x, topPipeCenterY, 0.18F, topPipeHeight, 0.18F, 0.7F, 0.25F, 0.0F);
        // Pestaña
        float pestanaTopY = this.getGapTopY() + 0.025F;
        renderer.drawRect(this.x, pestanaTopY, 0.22F, 0.05F, 0.22F, 0.8F, 0.3F, 0.0F);

        // --- TUBERÍA INFERIOR ---
        float bottomPipeHeight = this.getBottomPipeHeight();
        float bottomPipeCenterY = this.getBottomPipeY();
        // Cuerpo
        renderer.drawRect(this.x, bottomPipeCenterY, 0.18F, bottomPipeHeight, 0.18F, 0.7F, 0.25F, 0.0F);
        // Pestaña
        float pestanaBottomY = this.getGapBottomY() - 0.025F;
        renderer.drawRect(this.x, pestanaBottomY, 0.22F, 0.05F, 0.22F, 0.8F, 0.3F, 0.0F);
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
