package com.graphics;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class GestorAudio {

    // Variable global para controlar el clip de la música de fondo
    private static Clip musicaFondoClip;

    /**
     * Reproduce un efecto de sonido corto .wav en un hilo separado.
     */
    public static void reproducir(String ruta) {
        new Thread(() -> {
            try {
                InputStream is = GestorAudio.class.getResourceAsStream(ruta);
                if (is == null) return;
                
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                System.err.println("Error al reproducir efecto " + ruta + ": " + e.getMessage());
            }
        }).start();
    }

    /**
     * Inicia la música de fondo en un bucle infinito.
     * @param ruta Archivo de sonido (ej: "/sonidos/musica.wav")
     */
    public static void iniciarMusicaFondo(String ruta) {
        // Si ya hay música sonando, la detenemos primero para no duplicar hilos
        detenerMusicaFondo();

        new Thread(() -> {
            try {
                InputStream is = GestorAudio.class.getResourceAsStream(ruta);
                if (is == null) {
                    System.err.println("No se encontró la música de fondo en: " + ruta);
                    return;
                }
                
                InputStream bufferedIn = new BufferedInputStream(is);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                
                musicaFondoClip = AudioSystem.getClip();
                musicaFondoClip.open(audioStream);
                
                // IMPORTANTE: Configura el clip para que se repita infinitamente
                musicaFondoClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicaFondoClip.start();
                
            } catch (Exception e) {
                System.err.println("Error al iniciar música de fondo: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Detiene la música de fondo por completo si se está reproduciendo.
     */
    public static void detenerMusicaFondo() {
        if (musicaFondoClip != null && musicaFondoClip.isRunning()) {
            musicaFondoClip.stop();
            musicaFondoClip.close();
        }
    }
}