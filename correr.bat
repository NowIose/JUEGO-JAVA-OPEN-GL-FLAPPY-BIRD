@echo off
echo ==========================================
echo   INICIANDO FLAPPY BIRD (OPENGL)
echo ==========================================
echo.
echo 1. Compilando codigo...
call mvn compile
echo.
echo 2. Lanzando juego...
echo (Si falla, intenta cerrar otras ventanas de Java)
echo.
call mvn exec:exec
echo.
pause
