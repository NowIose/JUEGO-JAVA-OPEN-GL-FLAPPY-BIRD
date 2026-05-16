# Flappy Bird OpenGL 

Un clon arcade y multijugador de Flappy Bird desarrollado en **Java** utilizando gráficos de bajo nivel mediante **LWJGL (Lightweight Java Game Library)** y **OpenGL 3.3 core**. El motor gráfico calcula iluminaciones dinámicas en tiempo real mediante Shaders (GLSL) y cuenta con audio integrado nativo.

---

##  Integrantes
 **Jose Dainer Caceres Valencia** - Desarrollador Principal 

---

##  Controles por Jugador

El juego soporta un modo cooperativo/competitivo local para dos jugadores simultáneos:

| Jugador | Acción | Tecla | Código ASCII / GLFW |
| :--- | :--- | :--- | :--- |
| **Jugador 1 (Amarillo)** | Saltar / Volar | `W` | `GLFW_KEY_W` |
| **Jugador 2 (Azul)** | Saltar / Volar | `Espacio` | `GLFW_KEY_SPACE` (ASCII 48) |
| **Global (Game Over)** | Reiniciar Partida | `R` | `GLFW_KEY_R` |

---

##  Instrucciones de Compilación y Ejecución

### Requisitos Previos
* **Java Development Kit (JDK):** Versión 17 o superior instalada.
* **Manejador de dependencias:** Maven o Gradle (según la configuración de tu entorno) con los artefactos de **LWJGL 3** vinculados.

### Compilación y Ejecución (Consola)

Si tu proyecto utiliza **Maven**, ejecuta en la terminal de la raíz:
```bash o cmd
 mvn exec:exec

