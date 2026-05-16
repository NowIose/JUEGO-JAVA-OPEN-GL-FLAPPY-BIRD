package com.graphics;

import org.lwjgl.opengl.GL11;

/**
 * Clase Escenario:
 * Encapsula el dibujo de los elementos decorativos del fondo.
 * (Suelo, pasto, nubes y color del cielo).
 */
public class Escenario {

    /**
     * Dibuja todos los elementos visuales del fondo.
     * @param renderer El motor de renderizado para dibujar los rectángulos.
     */
    public void dibujar(Renderer renderer) {
        // --- 1. COLOR DEL CIELO ---
        // Establecemos el color de limpieza de pantalla (azul claro)
        GL11.glClearColor(0.52f, 0.80f, 0.92f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        // Activamos el motor de renderizado
        renderer.iniciarDibujo();

        // --- 2. DECORACIONES (REQUERIMIENTO 2.4) ---
        
        // Dibujamos el SUELO (Rectángulo marrón en la parte inferior)
        renderer.drawRect(0.0f, -0.9f, 2.0f, 0.2f, 0.5f, 0.35f, 0.2f, 0.0f);
        
        // Dibujamos el PASTO (Franja verde delgada sobre el suelo)
        renderer.drawRect(0.0f, -0.78f, 2.0f, 0.04f, 0.2f, 0.8f, 0.2f, 0.0f);
        
        // Dibujamos NUBES simples (Rectángulos blancos en el cielo)
        renderer.drawRect(-0.5f, 0.6f, 0.3f, 0.15f, 1.0f, 1.0f, 1.0f, 0.0f);
        renderer.drawRect(0.4f, 0.75f, 0.25f, 0.12f, 1.0f, 1.0f, 1.0f, 0.0f);
        renderer.drawRect(-0.1f, 0.82f, 0.2f, 0.10f, 1.0f, 1.0f, 1.0f, 0.0f);
    }
}
