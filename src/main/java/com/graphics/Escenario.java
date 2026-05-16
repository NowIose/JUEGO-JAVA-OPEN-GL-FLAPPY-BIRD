package com.graphics;

import org.lwjgl.opengl.GL11;

/**
 * Clase Escenario:
 * Encapsula el dibujo de los elementos decorativos del fondo.
 * (Suelo, pasto, nubes y montañas).
 */
public class Escenario {

    /**
     * Dibuja todos los elementos visuales del fondo.
     * @param renderer El motor de renderizado para dibujar los rectángulos y triángulos.
     */
    public void dibujar(Renderer renderer) {
        // --- 1. COLOR DEL CIELO ---
        GL11.glClearColor(0.52f, 0.80f, 0.92f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        // Activamos el motor de renderizado
        renderer.iniciarDibujo();

        // --- 2. DECORACIONES DEL CIELO ---
        // Dibujamos NUBES simples
        renderer.drawRect(-0.5f, 0.6f, 0.3f, 0.15f, 1.0f, 1.0f, 1.0f, 0.0f);
        renderer.drawRect(0.4f, 0.75f, 0.25f, 0.12f, 1.0f, 1.0f, 1.0f, 0.0f);
        renderer.drawRect(-0.1f, 0.82f, 0.2f, 0.10f, 1.0f, 1.0f, 1.0f, 0.0f);

        // --- 3. MONTAÑAS ---
        // Como el triángulo base apunta a la derecha, lo rotamos 90 grados (PI/2 radianes) 
        // para que la punta mire hacia arriba.
        float rotacionArriba = (float) Math.PI / 2.0f;

        // Montaña Izquierda
        renderer.drawTriangulo(-0.6f, -0.4f, 0.9f, 0.8f, 0.35f, 0.40f, 0.35f, rotacionArriba); 
        // Montaña Derecha
        renderer.drawTriangulo(0.6f, -0.45f, 0.8f, 0.7f, 0.30f, 0.35f, 0.30f, rotacionArriba);  
        // Montaña Centro (Un poco más clara y grande)
        renderer.drawTriangulo(0.0f, -0.45f, 1.2f, 0.9f, 0.40f, 0.45f, 0.40f, rotacionArriba); 

        // --- 4. PRIMER PLANO ---
        // Dibujamos el SUELO (Tapa la base de las montañas)
        renderer.drawRect(0.0f, -0.9f, 2.0f, 0.2f, 0.5f, 0.35f, 0.2f, 0.0f);
        
        // Dibujamos el PASTO (Franja verde delgada sobre el suelo)
        renderer.drawRect(0.0f, -0.78f, 2.0f, 0.04f, 0.2f, 0.8f, 0.2f, 0.0f);
    }
}