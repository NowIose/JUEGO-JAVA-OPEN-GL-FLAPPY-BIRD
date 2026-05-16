package com.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11; // Necesario para glClearColor y glClear

/**
 * FlappyGame: Esta clase es el cerebro del juego.
 * - El estado general del juego (iniciado, terminado).
 * - Los jugadores (uno o varios objetos Bird).
 * - Las tuberías (generación, movimiento, colisiones).
 * - La puntuación y la dificultad.
 * - La lógica principal de actualización y renderizado del juego.
 */
public class FlappyGame {

    // --- Constantes del Juego ---
    private static final float PIPE_SPEED_BASE = 0.62f; // Velocidad inicial de las tuberías
    private static final float PIPE_SPAWN_INTERVAL_BASE = 1.5f; // Tiempo inicial entre aparición de tuberías
    private static final float GAP_MIN_CENTER_Y = -0.45f; // Posición Y mínima del centro del hueco
    private static final float GAP_MAX_CENTER_Y = 0.45f; // Posición Y máxima del centro del hueco

    // --- Variables de Estado del Juego ---
    private long windowHandle; // Referencia a la ventana GLFW para actualizar el título
    private Renderer renderer; // Instancia de nuestro Renderer para dibujar todo
    private Escenario escenario; // REQUERIMIENTO 2.4: Maneja el fondo y decoraciones
    
    private List<Bird> players; // Lista de pájaros/jugadores en el juego
    private final List<Pipe> pipes; // Lista de obstáculos (tuberías) activos
    private Random random; // Generador de números aleatorios para la posición del hueco de las tuberías

    private boolean gameStarted; // True si el juego ha empezado (primer salto de un pájaro)
    private boolean gameOver;    // True si todos los pájaros han muerto
    private float pipeSpawnTimer; // Temporizador para controlar cuándo aparece la próxima tubería

    // Dificultad del juego (se ajusta con la puntuación)
    private float currentPipeSpeed;
    private float currentPipeSpawnInterval;
    private int dificultad; // Nivel de dificultad actual

    /**
     * Constructor del juego. Inicializa el estado del juego y los objetos principales.
     * @param windowHandle El identificador de la ventana GLFW.
     * @param renderer La instancia del Renderer ya inicializada.
     */
    public FlappyGame(long windowHandle, Renderer renderer) {
        this.windowHandle = windowHandle;
        this.renderer = renderer;
        this.escenario = new Escenario(); // Inicializar el componente del fondo
        this.pipes = new ArrayList<>();
        this.random = new Random();
        this.players = new ArrayList<>();
        
        resetGame(); // Prepara el juego para empezar
    }

    /**
     * Reinicia el estado completo del juego a su configuración inicial.
     */
    public void resetGame() {
        players.clear(); 

        // Requerimiento modo de dos jugadores 
        players.add(new Bird(-0.45f, 0.0f, 0.98f, 0.85f, 0.20f, GLFW.GLFW_KEY_0)); // Jugador 1: amarillo, ESPACIO
        players.add(new Bird(-0.30f, 0.0f, 0.20f, 0.60f, 0.90f, GLFW.GLFW_KEY_W));     // Jugador 2: azul, W

        pipes.clear(); 
        gameStarted = false; 
        gameOver = false;    
        pipeSpawnTimer = 0.0f; 

        // Reinicia la dificultad
        currentPipeSpeed = PIPE_SPEED_BASE;
        currentPipeSpawnInterval = PIPE_SPAWN_INTERVAL_BASE;
        dificultad = 0;

        updateWindowTitle(); 
    }

    /**
     * Procesa el input general del juego usando ManejadorEntrada.
     */
    private void processGameInput() {
        // Reiniciar el juego con la tecla 'R' si está en Game Over
        if (ManejadorEntrada.esTeclaPresionada(GLFW.GLFW_KEY_R) && gameOver) {
            resetGame();
        }

        // Si el juego no ha empezado, detecta el primer salto de CUALQUIER pájaro para iniciar la partida.
        if (!gameStarted && !gameOver) {
            for (Bird player : players) {
                if (ManejadorEntrada.esTeclaPresionada(player.getJumpKey())) {
                    gameStarted = true;
                    player.setInitialJumpVelocity(); 
                    break; 
                }
            }
        }
    }

    /**
     * Actualiza la lógica de todos los elementos del juego en cada frame.
     */
    public void update(float deltaTime) {
        processGameInput(); 

        if (gameOver) {
            return; 
        }

        // --- Actualizar Pájaros ---
        boolean allBirdsDead = true; 
        for (Bird player : players) {
            if (player.isAlive()) { 
                player.actualizar(deltaTime); 
                
                // Si al menos un pájaro sigue vivo, el juego continúa
                if (player.isAlive()) {
                    allBirdsDead = false; 
                }
            }
        }
        
        if (allBirdsDead) {
            gameOver = true;
            updateWindowTitle();
            return;
        }
        
        // --- Actualizar Tuberías ---
        if (gameStarted) {
            pipeSpawnTimer += deltaTime;
            if (pipeSpawnTimer >= currentPipeSpawnInterval) {
                pipeSpawnTimer = 0.0f;
                spawnPipe(); 
                updateDifficulty(); 
            }

            Iterator<Pipe> it = pipes.iterator();
            while (it.hasNext()) {
                Pipe currentPipe = it.next();
                currentPipe.x -= currentPipeSpeed * deltaTime;

                // --- Detección de Colisiones y Puntuación ---
                for (Bird player : players) {
                    if (player.isAlive()) {
                        if (checkCollision(currentPipe, player)) {
                            player.setAlive(false); 
                        }

                        // Puntuación: si el pájaro pasa la tubería
                        if (currentPipe.x + (Pipe.PIPE_WIDTH * 0.5f) < player.getX() && !currentPipe.scored) {
                            currentPipe.scored = true; 
                            player.incrementScore();    
                            updateWindowTitle();        
                        }
                    }
                }

                // Remover tuberías fuera de pantalla
                if (currentPipe.x + (Pipe.PIPE_WIDTH * 0.5f) < -1.3f) { 
                    it.remove();
                }
            }
        }
    }

    /**
     * Dibuja todos los elementos del juego en la pantalla.
     */
    public void render() {
        // Dibujamos el escenario (fondo, suelo, nubes)
        escenario.dibujar(renderer);

        // --- Dibujar Tuberías ---
        for (Pipe currentPipe : pipes) {
            float topPipeCenterY = currentPipe.getTopPipeY() + currentPipe.getTopPipeHeight() * 0.5f;
            renderer.drawRect(currentPipe.x, topPipeCenterY, Pipe.PIPE_WIDTH, currentPipe.getTopPipeHeight(), 0.18f, 0.70f, 0.25f, 0.0f);

            float bottomPipeCenterY = currentPipe.getBottomPipeY();
            renderer.drawRect(currentPipe.x, bottomPipeCenterY, Pipe.PIPE_WIDTH, currentPipe.getBottomPipeHeight(), 0.18f, 0.70f, 0.25f, 0.0f);
        }

        // --- Dibujar Pájaros ---
        for (Bird player : players) {
            player.dibujar(renderer); 
        }

        // --- Overlay de Game Over ---
        if (gameOver) {
            renderer.drawRect(0.0f, 0.0f, 2.0f, 0.22f, 0.15f, 0.18f, 0.22f, 0.0f); 
        }
    }

    private void spawnPipe() {
        float gapCenterY = GAP_MIN_CENTER_Y + random.nextFloat() * (GAP_MAX_CENTER_Y - GAP_MIN_CENTER_Y);
        pipes.add(new Pipe(1.2f, gapCenterY)); 
    }

    private void updateDifficulty() {
        int totalScore = 0; 
        for(Bird p : players) {
            totalScore += p.getScore();
        }

        int newdificultad = totalScore / 5; 
        if (newdificultad > dificultad) {
            dificultad = newdificultad; 
            currentPipeSpeed = PIPE_SPEED_BASE * (1.0f + dificultad * 0.1f); 
            currentPipeSpawnInterval = PIPE_SPAWN_INTERVAL_BASE / (1.0f + dificultad * 0.05f); 

            currentPipeSpeed = Math.min(currentPipeSpeed, 1.5f); 
            currentPipeSpawnInterval = Math.max(currentPipeSpawnInterval, 0.7f); 
        }
    }
    
    private void updateWindowTitle() {
        StringBuilder title = new StringBuilder("Flappy Bird OpenGL");
        for (int i = 0; i < players.size(); i++) {
            Bird player = players.get(i);
            title.append(" | P").append(i + 1).append(": ").append(player.getScore());
            if (!player.isAlive()) title.append(" (KO)");
            }
        
        if (!gameStarted) title.append(" | Pulsa ESPACIO o W para saltar");
        else if (gameOver) title.append(" | GAME OVER - Pulsa R para reiniciar");
        else title.append(" | Nivel: ").append(dificultad);
        
        GLFW.glfwSetWindowTitle(windowHandle, title.toString());
    }

    private boolean checkCollision(Pipe t, Bird p) {
        float birdLeft = p.getX() - (p.getWidth() * 0.5f);
        float birdRight = p.getX() + (p.getWidth() * 0.5f);
        float birdBottom = p.getY() - (p.getHeight() * 0.5f);
        float birdTop = p.getY() + (p.getHeight() * 0.5f);

        float pipeLeft = t.getLeftX();
        float pipeRight = t.getRightX();
        
        if (!(birdRight > pipeLeft && birdLeft < pipeRight)) return false;

        float gapTop = t.getGapTopY();
        float gapBottom = t.getGapBottomY();
        return birdTop > gapTop || birdBottom < gapBottom;
    }

    public boolean isWindowShouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }
}
