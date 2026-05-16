package com.graphics;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

/**
 * Clase Renderer: Motor gráfico de la aplicación.
 * Maneja la creación de Shaders, la subida de vértices a la GPU (VBO/VAO) 
 * y la ejecución de comandos de dibujo.
 */
public class Renderer {
    
    // Identificador del programa de Shaders compilado en la GPU
    private int programa;

    // VAO (Vertex Array Object): Guarda la configuración de los atributos de vértices
    // VBO (Vertex Buffer Object): Buffer de memoria en la GPU que contiene los datos de los vértices
    private int vaoCuadrado;
    private int vboCuadrado;

    private int vaoTriangulo;
    private int vboTriangulo;

    // Ubicaciones (IDs) de las variables uniformes en los Shaders
    private int uOffset;    // Para mover el objeto (x, y)
    private int uScale;     // Para cambiar el tamaño (ancho, alto)
    private int uColor;     // Para el color (rojo, verde, azul)
    private int uRotation;  // Para la rotación en radianes

    /**
     * Inicializa los recursos de OpenGL necesarios para empezar a dibujar.
     */
    public void init(){
        crearShaders();
        crearCuadrado();
        crearTriangulo();
    }

    /**
     * Crea y compila el Pipeline Gráfico (Vertex y Fragment Shaders).
     */
    private void crearShaders(){
        // Vertex Src: Procesa cada punto (vértice) y aplica rotación, escala y posición.
        String vertexSrc="""
            #version 330 core
            layout (location = 0) in vec3 aPos; // Entrada: posición local del vértice
            uniform vec2 uOffset;               // Uniform: posición en pantalla
            uniform vec2 uScale;                // Uniform: tamaño
            uniform float uRotation;            // Uniform: ángulo de giro
            
            void main(){
                // Cálculos de seno y coseno para la matriz de rotación 2D
                float s = sin(uRotation);
                float c = cos(uRotation);

                // Aplicamos la rotación: x' = x*cos - y*sin , y' = x*sin + y*cos
                vec2 rotatedPos = vec2(
                    aPos.x * c - aPos.y * s,
                    aPos.x * s + aPos.y * c
                );
                
                // Aplicamos escala y luego el desplazamiento final (offset)
                vec2 finalPos = rotatedPos * uScale + uOffset;
                
                // Enviamos la posición final a OpenGL en coordenadas NDC
                gl_Position = vec4(finalPos, aPos.z, 1.0);
            }
            """;

        // Fragment Src: Define el color final de cada píxel del objeto.
        String fragmentSrc="""
            #version 330 core
            uniform vec3 uColor; // Color enviado desde Java
            out vec4 fragColor;  // Color de salida hacia la pantalla
            
            void main(){
                fragColor = vec4(uColor, 1.0); // Asigna el color con opacidad 1.0
            }
            """;

        // Compilar individualmente cada shader
        int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSrc, "Vertex");
        int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSrc, "Fragment");

        // Crear el programa y enlazar (link) los shaders
        programa = GL20.glCreateProgram();
        GL20.glAttachShader(programa, vertexShader);
        GL20.glAttachShader(programa, fragmentShader);
        GL20.glLinkProgram(programa);


        // verifica el estado del enlace del programa
        if (GL20.glGetProgrami(programa, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error al enlazar el programa: " + GL20.glGetProgramInfoLog(programa));
        } 
        
        // Obtener las direcciones de memoria de los Uniforms para usarlos después
        uOffset = GL20.glGetUniformLocation(programa, "uOffset");
        uScale = GL20.glGetUniformLocation(programa, "uScale");
        uColor = GL20.glGetUniformLocation(programa, "uColor");
        uRotation = GL20.glGetUniformLocation(programa, "uRotation");

        // Liberar los shaders individuales ya que el programa ya está listo
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }   

    /**
     * Compila un shader de OpenGL y verifica si hubo errores de sintaxis.
     */
    private int compileShader(int type, String src, String NameShader){
        int id = GL20.glCreateShader(type);
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Error al compilar el shader " + NameShader + ": " + GL20.glGetShaderInfoLog(id));
        }
        return id;
    }

    //iniciando par adibujar triangulo
    private void crearTriangulo(){
        // Datos de los 3 vértices en coordenadas locales (-0.5 a 0.5)
        float[] vertices ={
       /*   0.0f,  0.5f, 0.0f, // Arriba
            -0.5f, -0.5f, 0.0f, // abajo Izquierda
             0.5f, -0.5f, 0.0f  // Derecha
        */


            0.5f,  0.0f, 0.0f, // PUNTA (Derecha centro)
           -0.5f,  0.4f, 0.0f, // ATRÁS ARRIBA (Izquierda arriba)
           -0.5f, -0.4f, 0.0f  // ATRÁS ABAJO (Izquierda abajo)

           
        };
        // Aquí faltaría la lógica de generar VBO/VAO similar a crearCuadrado()
        vaoTriangulo=GL30.glGenVertexArrays();
        vboTriangulo=GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoTriangulo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vboTriangulo);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);

        //configurar el atributo y su posicion
        GL20.glVertexAttribPointer(0,3,GL11.GL_FLOAT,false,3*Float.BYTES,0);
        GL20.glEnableVertexAttribArray(0);

        //desvincular
        GL30.glBindVertexArray(0);

    }
    public void drawTriangulo(float x,float y,float ancho,float alto,float r,float g,float b,float rotacion)
    {
        GL30.glBindVertexArray(vaoTriangulo);
        GL20.glUniform2f(uOffset, x,y);
        GL20.glUniform2f(uScale, ancho,alto );
        GL20.glUniform3f(uColor,r,g,b);
        GL20.glUniform1f(uRotation,rotacion);
        

        //dibujar los vertices
        GL11.glDrawArrays(GL11.GL_TRIANGLES,0,3);
   }

    /**
     * Crea un cuadrado unitario (2 triángulos) y lo sube a la GPU.
     */
    private void crearCuadrado(){
        // Vértices de dos triángulos que forman un cuadrado
        float[] vertices = {
            -0.5f, -0.5f, 0.0f, 
             0.5f, -0.5f, 0.0f, 
             0.5f,  0.5f, 0.0f, 
            -0.5f, -0.5f, 0.0f,  
             0.5f,  0.5f, 0.0f, 
            -0.5f,  0.5f, 0.0f
        };

        // Generar y activar el VAO
        vaoCuadrado = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoCuadrado);

        // Generar y subir los datos al VBO
        vboCuadrado = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboCuadrado);

        // Crear un buffer directo (nativo) para enviar los datos
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Configurar el atributo de posición (location = 0 en el shader)
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0); 

        // Desvincular para evitar cambios accidentales
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);      
    }

    /**
     * Prepara a OpenGL para dibujar usando el programa y el VAO del cuadrado.
     */
    public void iniciarDibujo() {
        GL20.glUseProgram(programa);
        //GL30.glBindVertexArray(vaoCuadrado);
    }

    /**
     * Comando final para dibujar un rectángulo con parámetros específicos.
     */
    public void drawRect(float x, float y, float width, float height, float r, float g, float b, float rotation){
        GL30.glBindVertexArray(vaoCuadrado);
        // Enviamos los datos a los Shaders a través de los Uniforms
        GL20.glUniform2f(uOffset, x, y);
        GL20.glUniform2f(uScale, width, height);
        GL20.glUniform3f(uColor, r, g, b);
        GL20.glUniform1f(uRotation, rotation);
        
        // Dibujamos 6 vértices (2 triángulos)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
    }

    /**
     * Libera la memoria ocupada en la GPU al cerrar la aplicación.
     */
    public void cleanup(){
        GL30.glDeleteVertexArrays(vaoCuadrado);
        GL15.glDeleteBuffers(vboCuadrado);
        

        GL30.glDeleteVertexArrays(vaoTriangulo);
        GL15.glDeleteBuffers(vboTriangulo);
        GL20.glDeleteProgram(programa);

    }
}
