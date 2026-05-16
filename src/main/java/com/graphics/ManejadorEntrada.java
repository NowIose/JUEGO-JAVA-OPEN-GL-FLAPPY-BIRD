package com.graphics;

import org.lwjgl.glfw.GLFW;

/**
 * Clase ManejadorEntrada: 
 * Centraliza el control del teclado para evitar que cada clase maneje su propio estado de teclas.
 * Permite detectar si una tecla está presionada o si acaba de ser pulsada (detección de flanco).
 * 
 * Uso:
 * 1. Llamar a ManejadorEntrada.actualizar(window) al inicio del bucle de juego.
 * 2. Usar esTeclaPresionada(tecla) para acciones de un solo toque (ej. saltar, reiniciar).
 * 3. Usar esTeclaAbajo(tecla) para acciones continuas.
 */
public class ManejadorEntrada {

    // Arreglo para guardar el estado actual y anterior de las teclas (GLFW usa códigos hasta ~348)
    private static final int MAX_TECLAS = 350;
    private static boolean[] teclas = new boolean[MAX_TECLAS];
    private static boolean[] teclasPrevias = new boolean[MAX_TECLAS];

    /**
     * Actualiza el estado de todas las teclas leyendo la ventana de GLFW.
     * Debe llamarse una vez por frame al inicio del bucle principal en AppFlappyBird.
     */
    public static void actualizar(long ventana) {
        for (int i = 32; i < MAX_TECLAS; i++) { // Las teclas útiles empiezan en el espacio (32)
            teclasPrevias[i] = teclas[i];
            teclas[i] = GLFW.glfwGetKey(ventana, i) == GLFW.GLFW_PRESS;
        }
    }

    /**
     * Devuelve true si la tecla está siendo presionada en este momento.
     */
    public static boolean esTeclaAbajo(int tecla) {
        if (tecla < 0 || tecla >= MAX_TECLAS) return false;
        return teclas[tecla];
    }

    /**
     * Devuelve true solo en el frame en que la tecla fue pulsada.
     * Es ideal para el SALTO o para REINICIAR el juego sin que se repita la acción.
     */
    public static boolean esTeclaPresionada(int tecla) {
        if (tecla < 0 || tecla >= MAX_TECLAS) return false;
        return teclas[tecla] && !teclasPrevias[tecla];
    }
}
