# Flappy Bird

Juego de Flappy Bird desarrollado con JavaFX como proyecto educativo para aprender Java, Maven, POO y diseño de videojuegos.

## Requisitos previos

- **Java 11** o superior (requerido por JavaFX 18)

## Como ejecutarlo

### Desde la terminal (sin Maven instalado)

El proyecto incluye Maven Wrapper, por lo que no es necesario instalar Maven:

```bash
./mvnw javafx:run
```

En Windows:

```cmd
mvnw.cmd javafx:run
```

### Desde la terminal (con Maven instalado)

```bash
mvn javafx:run
```

### Desde Eclipse

1. Click derecho en el proyecto
2. Run As... > Maven Build...
3. En Goals, escribir `javafx:run`
4. Click en Run

## Tecnologias

- **Java 11+**
- **JavaFX 18.0.1** (controles, media, gráficos)
- **Maven** (gestion de dependencias y build)
