package com.graphics;

import static org.lwjgl.glfw.GLFW.*;
import static java.lang.Math.*;

public class Bird {

    private static final float GRAVITY = -1.9f; 
    private static final float JUMP_IMPULSE = 0.85f; 
    private static final float MAX_FALL_VELOCITY = -1.8f; 
    private static final float BIRD_ALTO = 0.10f; 
    private static final float BIRD_ANCHO = 0.10f; 

    private float x, y;
    private float velY;
    private float red, green, blue; 
    private boolean vivo;
    private int puntaje; 
    private int jumpKey; 

    private float tiempoAnimacion; 
    
    public Bird(float startX, float startY, float red, float green, float blue, int teclaSalto) {
        this.x = startX;
        this.y = startY;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.jumpKey = teclaSalto;
        this.velY = 0.0f;
        this.vivo = true;
        this.puntaje = 0;
        this.tiempoAnimacion = 0.0f;
    }

    /**
     * Actualiza la lógica del pájaro (física).
     * @param deltaTime El tiempo transcurrido desde el último frame.
    */
    public void actualizar(float deltaTime) {
        if (!vivo) return; // No actualizar si el pájaro está muerto
        

        // Manejo del salto usando el nuevo ManejadorEntrada 
        if (ManejadorEntrada.esTeclaPresionada(jumpKey)) {
            velY = JUMP_IMPULSE;
            
        }
      
        // Aplicar gravedad
        this.velY += GRAVITY * deltaTime;
        if (this.velY < MAX_FALL_VELOCITY) this.velY = MAX_FALL_VELOCITY;
        this.y += this.velY * deltaTime;

        this.tiempoAnimacion += deltaTime * 12.0f; 

        float mitadAlto = BIRD_ALTO / 2.0f;
        if (this.y + mitadAlto >= 1.0f || this.y - mitadAlto <= -1.0f) {
            this.y = Math.max(-1.0f + mitadAlto, Math.min(1.0f - mitadAlto, this.y));
            this.vivo = false; 
        }
    }

    public void dibujar(Renderer renderer) {
        if (!vivo) return;

        float rotacion = map(velY, MAX_FALL_VELOCITY, JUMP_IMPULSE, (float)toRadians(-45), (float)toRadians(30));
        
        // 1. COLA
        renderer.drawRect(x - BIRD_ANCHO * 0.45f, y, BIRD_ANCHO * 0.4f, BIRD_ALTO * 0.6f, red * 0.8f, green * 0.8f, blue * 0.8f, rotacion);
       // renderer.drawTriangulo(x + 0.05f, y - 0.01f, 0.04f, 0.04f, 1.0f, 0.7f, 0.0f, rotacion); 
        // 2. CUERPO
        renderer.drawRect(x, y, BIRD_ANCHO, BIRD_ALTO, red, green, blue, rotacion);

        // 3. ALA
        float offsetAlaY = (float)sin(tiempoAnimacion) * 0.03f;
        renderer.drawRect(x, y + offsetAlaY, BIRD_ANCHO * 0.6f, BIRD_ALTO * 0.4f, red * 1.2f, green * 1.2f, blue * 1.2f, rotacion);

        // 4. PICO
        //renderer.drawRect(x + BIRD_ANCHO * 0.5f, y - BIRD_ALTO * 0.1f, BIRD_ANCHO * 0.4f, BIRD_ALTO * 0.2f, 1.0f, 0.7f, 0.0f, rotacion); 
        renderer.drawTriangulo(x + 0.06f, y-0.025f, 0.04f, 0.03f, 1.0f, 0.7f, 0.0f, 0.0f);
        // renderer.drawTriangulo(x + 0.07f, y, 0.04f, 0.03f, 1.0f, 0.7f, 0.0f, 0.0f); 
        // 5. OJO
        renderer.drawRect(x + BIRD_ANCHO * 0.2f, y + BIRD_ALTO * 0.15f, BIRD_ANCHO * 0.2f, BIRD_ALTO * 0.2f, 1.0f, 1.0f, 1.0f, rotacion); 
        renderer.drawRect(x + BIRD_ANCHO * 0.25f, y + BIRD_ALTO * 0.15f, BIRD_ANCHO * 0.08f, BIRD_ALTO * 0.08f, 0.0f, 0.0f, 0.0f, rotacion);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return BIRD_ANCHO; }
    public float getHeight() { return BIRD_ALTO; }
    public boolean isAlive() { return vivo; }
    public void setAlive(boolean alive) { this.vivo = alive; }
    public int getScore() { return puntaje; }
    public void incrementScore() { this.puntaje++; }
    public int getJumpKey() { return jumpKey; }
    public void setInitialJumpVelocity() { this.velY = JUMP_IMPULSE; }

    private float map(float value, float inMin, float inMax, float outMin, float outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}
