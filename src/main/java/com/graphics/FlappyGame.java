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
        GestorAudio.iniciarMusicaFondo("/sonidos/music.wav");
        resetGame(); // Prepara el juego para empezar
    }

    /**
     * Reinicia el estado completo del juego a su configuración inicial.
     */
    public void resetGame() {
        players.clear(); 

        // Requerimiento modo de dos jugadores 
        players.add(new Bird(-0.45f, 0.0f, 0.98f, 0.85f, 0.20f, GLFW.GLFW_KEY_SPACE)); // Jugador 1: amarillo, ESPACIO
        players.add(new Bird(-0.45f, 0.0f, 0.20f, 0.60f, 0.90f, GLFW.GLFW_KEY_W));     // Jugador 2: azul, W
        players.add(new Bird(-0.45f,0.0f,0.30f, 0.70f, 0.1f, GLFW.GLFW_KEY_0));//jugador 3 :verde 0
        pipes.clear(); 
        gameStarted = false; 
        gameOver = false;    
        pipeSpawnTimer = 0.0f; 

        // Reinicia la dificultad
        currentPipeSpeed = PIPE_SPEED_BASE;
        currentPipeSpawnInterval = PIPE_SPAWN_INTERVAL_BASE;
        dificultad =0;

        updateWindowTitle(); 
    }

    /**
     * Procesa el input general del juego usando ManejadorEntrada.
     */
    private void processGameInput() {
        // Reiniciar el juego con la tecla 'R' si está en Game Over
        if (ManejadorEntrada.esTeclaPresionada(GLFW.GLFW_KEY_R) && gameOver) {
            resetGame();
            GestorAudio.iniciarMusicaFondo("/sonidos/music.wav");
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
        
        if (allBirdsDead || players.get(2).getScore()>=5){
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
                            GestorAudio.reproducir("/sonidos/muerte.wav");
                        }

                        // Puntuación: si el pájaro pasa la tubería
                        if (currentPipe.x + (Pipe.PIPE_WIDTH * 0.5f) < player.getX() && !currentPipe.scored) {
                            currentPipe.scored = true; 
                            player.incrementScore();    
                            GestorAudio.reproducir("/sonidos/punto.wav");
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
    private void dibujarNumero(int numero, float x, float y, float w, float h, float r, float g, float b) {
      String numStr = String.valueOf(numero);
      float spacing = w * 1.5f; // Espaciado entre dígitos
      // Centrar el número completo
      float startX = x - (numStr.length() - 1) * spacing / 2.0f;

      // Mapa de 7 segmentos para los números del 0 al 9
      boolean[][] segmentos = {
          {true, true, true, true, true, true, false},       // 0
          {false, true, true, false, false, false, false},   // 1
          {true, true, false, true, true, false, true},      // 2
          {true, true, true, true, false, false, true},      // 3
          {false, true, true, false, false, true, true},     // 4
          {true, false, true, true, false, true, true},      // 5
          {true, false, true, true, true, true, true},       // 6
          {true, true, true, false, false, false, false},    // 7
          {true, true, true, true, true, true, true},        // 8
          {true, true, true, true, false, true, true}        // 9
      };

      for (int i = 0; i < numStr.length(); i++) {
         int digito = numStr.charAt(i) - '0';
         boolean[] seg = segmentos[digito];
         float px = startX + i * spacing;
         float t = w * 0.25f; // Grosor de la línea

         // A (Arriba)
         if(seg[0]) this.renderer.drawRect(px, y + h/2, w, t, r, g, b, 0.0F);
         // B (Arriba Derecha)
         if(seg[1]) this.renderer.drawRect(px + w/2 - t/2, y + h/4, t, h/2, r, g, b, 0.0F);
         // C (Abajo Derecha)
         if(seg[2]) this.renderer.drawRect(px + w/2 - t/2, y - h/4, t, h/2, r, g, b, 0.0F);
         // D (Abajo)
         if(seg[3]) this.renderer.drawRect(px, y - h/2, w, t, r, g, b, 0.0F);
         // E (Abajo Izquierda)
         if(seg[4]) this.renderer.drawRect(px - w/2 + t/2, y - h/4, t, h/2, r, g, b, 0.0F);
         // F (Arriba Izquierda)
         if(seg[5]) this.renderer.drawRect(px - w/2 + t/2, y + h/4, t, h/2, r, g, b, 0.0F);
         // G (Centro)
         if(seg[6]) this.renderer.drawRect(px, y, w, t, r, g, b, 0.0F);
      }
   }
    /**
     * Dibuja todos los elementos del juego en la pantalla.
     */
    public void render() {
      // 1. Dibujar el fondo
      this.escenario.dibujar(this.renderer);

      // 2. Dibujar las Tuberías (Se dibujan ANTES del HUD para que queden por detrás)
     for(Pipe currentPipe : this.pipes) {
         currentPipe.dibujar(this.renderer);
      }

      // 3. Dibujar a los jugadores
      for(Bird player : this.players) {
         player.dibujar(this.renderer);
      }

      // 4. Dibujar el HUD superior (Fondo oscuro)
      this.renderer.drawRect(0.0F, 0.94F, 2.0F, 0.12F, 0.05F, 0.05F, 0.05F, 0.0F);
      
      // Barra de velocidad
      float pVelHUD = (this.currentPipeSpeed - 0.62F) / 0.88F;
      pVelHUD = Math.min(1.0F, Math.max(0.01F, pVelHUD));
      this.renderer.drawRect(0.0F, 0.9F, 0.5F, 0.02F, 0.2F, 0.2F, 0.2F, 0.0F);
      this.renderer.drawRect(0.0F, 0.9F, 0.5F * pVelHUD, 0.02F, pVelHUD, 1.0F - pVelHUD, 0.0F, 0.0F);

      // 5. Dibujar Puntajes usando el método de 7 segmentos
      for(int i = 0; i < this.players.size(); ++i) {
         Bird bHUD = (Bird)this.players.get(i);
         float rHUD = i == 0 ? 1.0F : 0.3F;
         float gHUD = i == 0 ? 0.9F : 0.6F;
         float bHUD_color = i == 0 ? 0.2F : 1.0F;
         if (i==0)
            bHUD_color=0.2f;
         else
            if(i==1)
            bHUD_color=1.0f;
            else
                bHUD_color=0.3f;


         // Separar la posición del Jugador 1 (Izquierda) y Jugador 2 (Derecha)
         float posX ;//= i == 0 ? -0.85F : 0.85F;
         
         if(i==0)
            posX=-0.85F;
         else
            if(i==1)
            posX=0.85F;
            else
                posX=0.2f;
         // Ancho: 0.04f, Alto: 0.08f
         dibujarNumero(bHUD.getScore(), posX, 0.95F, 0.04F, 0.08F, rHUD, gHUD, bHUD_color);
      }

      
     // Pantalla final de Game Over
      if (this.gameOver) {
         // 1. Panel Principal (Fondo oscuro con un borde blanco alrededor)
         this.renderer.drawRect(0.0F, 0.0F, 1.1F, 0.9F, 1.0F, 1.0F, 1.0F, 0.0F); // Borde
         this.renderer.drawRect(0.0F, 0.0F, 1.05F, 0.85F, 0.15F, 0.15F, 0.18F, 0.0F); // Fondo gris oscuro

         // 2. Gran 'X' Roja (Indicador visual de GAME OVER)
         // Usamos radianes para rotar los rectángulos 45 grados (Math.PI / 4 ≈ 0.785f)
         float angulo45 = 0.785398f;
         this.renderer.drawRect(0.0F, 0.25F, 0.35F, 0.06F, 0.9F, 0.2F, 0.2F, angulo45);
         this.renderer.drawRect(0.0F, 0.25F, 0.35F, 0.06F, 0.9F, 0.2F, 0.2F, -angulo45);

         // 3. Mostrar puntajes finales
         // --- Jugador 1 (Izquierda) ---
         // Cuadrito representativo del P1 (usando sus colores)
         this.renderer.drawRect(-0.25F, -0.05F, 0.15F, 0.15F, 1.0F, 0.9F, 0.2F, 0.0F); 
         // Puntaje final del P1
         dibujarNumero(((Bird)this.players.get(0)).getScore(), -0.25F, -0.2F, 0.06F, 0.12F, 1.0F, 1.0F, 1.0F);
         
         // --- Jugador 2 (Derecha) ---
         if (this.players.size() > 1) {
            // Cuadrito representativo del P2
            this.renderer.drawRect(0.25F, -0.05F, 0.15F, 0.15F, 0.3F, 0.6F, 1.0F, 0.0F); 
            // Puntaje final del P2
            dibujarNumero(((Bird)this.players.get(1)).getScore(), 0.25F, -0.2F, 0.06F, 0.12F, 1.0F, 1.0F, 1.0F);
         }

         // Rectángulo Verde simulando el botón no lo acabe r nomas
         this.renderer.drawRect(0.0F, -0.45F, 0.35F, 0.12F, 0.2F, 0.8F, 0.3F, 0.0F);
         // Símbolo de "Play" encima del botón verde
         // Al triángulo que apunta hacia arriba, lo rotamos -90 grados (-1.57f) para que apunte a la derecha
         this.renderer.drawTriangulo(0.0F, -0.45F, 0.08F, 0.08F, 1.0F, 1.0F, 1.0F, -1.5708f);
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
        
        // Mostrar velocidad actual en el título (REQ 2.3)
        title.append(" | Vel: ").append(String.format("%.2f", currentPipeSpeed));

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
