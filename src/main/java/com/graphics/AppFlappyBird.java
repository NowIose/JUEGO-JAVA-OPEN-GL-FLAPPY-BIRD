package com.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

/**
 * AppFlappyBird: Clase principal que arranca el juego.
 * Implementa el bucle de juego y delega la lógica a FlappyGame.
 */
public class AppFlappyBird {

    private long window;
    private Renderer renderer;
    private FlappyGame game;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("No se pudo iniciar GLFW");
        }

        // Configuración de la ventana (Perfil Core 3.3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(900, 700, "Flappy Bird OpenGL - Examen Parcial", 0, 0);
        if (window == 0) {
            throw new RuntimeException("No se pudo crear la ventana");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1); // VSync activado
        GLFW.glfwShowWindow(window);

        // Inicializar capacidades de OpenGL
        GL.createCapabilities();

        // Inicializar el motor de renderizado y el juego
        renderer = new Renderer();
        renderer.init();

        game = new FlappyGame(window, renderer);
        GestorAudio.iniciarMusicaFondo("/sonidos/music.wav");
    }

    private void loop() {
        float lastTime = (float) GLFW.glfwGetTime();
        
        while (!GLFW.glfwWindowShouldClose(window)) {
            float currentTime = (float) GLFW.glfwGetTime();
            float dt = currentTime - lastTime;
            lastTime = currentTime;

            // Capamos el dt para evitar saltos por tirones
            if (dt > 0.033f) dt = 0.033f;

            // 0. Actualizar entrada (teclado) antes de la lógica
            ManejadorEntrada.actualizar(window);

            // 1. Actualizar lógica
            game.update(dt);
            
            // 2. Dibujar
            game.render();

            // 3. Intercambiar buffers y procesar eventos
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        renderer.cleanup();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new AppFlappyBird().run();
    }
}
