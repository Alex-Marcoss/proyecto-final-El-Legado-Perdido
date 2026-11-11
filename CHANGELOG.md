## [Creación del proyecto] 2026-08-22
### Agregados:

- creación de archivo README.md
- creacion de archivo CHANGELOG.md
- Creación de WIKI.
- creación de carpetas donde vamos guardando cada avance de la parte lógica del juego.


## [Inicio de la programación del proyecto] 2026-08-23
### Agregados:

- Creación de algunas clases como jugador, inventario, principal, ítem y recursos
- Implementación de funciones como recolectar, mostrar inventario, recoger ítem, mostrar estado del jugador, recibir daño, entre otras
- Todo el código creado es todo funcionamiento lógico, todavía no se comenzó con la parte grafica

## [Avance en la programación del proyecto] 2026-08-24
### Agregados:

- Creacion de nuevas clases mas especificas como herramienta, tipo de herramienta, hacha pico, árbol y roca
- Se implemento el poder extraer recursos (madera y piedra) con herramientas y poder almacenarlas en el inventario
- Los elementos se pueden extraer solo con la herramienta indicada y si se tiene la energía suficiente.

## [Nuevos avances y mejoras] 2026-08-25
### Agregados:

- Creación de nuevas clases como receta, ingrediente y planta (nuevo recurso).
- Implementación de acciones como fabricar, validar la fabricación y descontar objetos posterior a fabricar.
- Se unifico las interacciones para simplificar las funciones de minar() talar() recolectar() en interactuar() y optimizar el código.
- En la clase inventario se implementaron funciones de consultar y gastar recursos.

## [Nuevos avances y mejoras] 2026-08-27
### Agregados:

- Creación de nuevas clases como enemigos, lobo, araña, y generador de enemigos.
- Implementación de sistemas como combate y ciclo de día y noche.
- Creacion logica de construccion con funciones como validar recursos, gastar recursos y construir.
- Se comentaron casi todas las clases para entender mejor el código y que sea mas legible para otro.
  
  Aclaración: Todas las implementaciones mencionadas anteriormente son lógicas, la parte grafica con libGDX todavia no fue implementada.

## [Nuevos avances y mejoras] 2026-08-30
### Agregados: 

- Creacion de nuevas clases como animal, gema, estructuraRescate, objetivoFinal, comida, Union Sistemas, entre otras.
- Implementacion logica del final del juego despues de derrotar al jefe, obtener la gema y reconstruir la estructura del rayo.
- Implementacion de la comida, funcion comer y conexion con inventario para restar cantidades.
- Implementacion de las mecanicas de pasar hambre y perder vida por falta de comida con el paso del tiempo (vinculado con ciclo dia/noche)

## [Nuevos avances y mejoras] 2026-09-02
### Agregados: 

- Implementado sistema de mapa dividido en 3 zonas. Agregada clase Zona para gestionar las diferentes áreas del mapa.
- Agregada clase LugarEspecial para representar ubicaciones importantes.
- Agregada posición X/Y al jugador.
- Implementado movimiento con límites del mapa.
- Implementado sistema de correr.
- Mejorado el sistema de combate entre jugador y enemigos.
- Implementado sistema de muerte del jugador.
- Agregado método estaVivo() y gameOver() en Jugador
- Creada clase Energia. Implementado sistema de recuperación automática de energía, con una recuperación de 5 puntos cada 3 segundos.

## [Nuevos avances y mejoras] 2026-09-11
### Agregados: 

- Realizado el diseño del jugador y de los enemigos.
- Ralizados el diseño de los recursos necesarios para el juego.
- Todos los sprites hechos.

## [Nuevos avances y mejoras] 2026-09-21
### Agregados: 

- Se incorporo el mapa.
- Se incorporo el jugador.
- Se incorporo el seguimiento con la camara al jugador.

## [Nuevos avances y mejoras] 2026-09-24
### Agregados: 

- Creacion de arboles en el mapa
- Creacion de accion para talar y guardar madera en el inventario
- creacion grafica de inventario con 30 slots de guardado y cantidades de recursos

## [Nuevos avances y mejoras] 2026-09-24
### Agregados: 

- Creacion grafica de barras de supervivencia (vida hambre y energia)
- Gasto de las barras

## [Nuevos avances y mejoras] 2026-09-29
### Agregados: 

- Creacion grafica de ricas en el mapa
- Creacion de accion de Minar
  
## [Nuevos avances y mejoras] 2026-09-30
### Agregados:

- Creacion de barra rapida ya funcional que puede seleccionarse con numeros del 1 al 9 y con la rueda del mouse.
- Mejoras en el inventario permitiendo mover items y conectado con la barra rapida.
- Implementacion de recuperacion de energia y corregido el correr para no gastar energia tan agresivamente.
- Creacion de pantalla de inicio y de la pantalla de derrota al perder toda la vida.
- Primeras pruebas de combate con IA del guardian (jefe final del juego).

## [Nuevos avances y mejoras] 2026-10-03
### Agregados:

-Limites del mapa.
-colisiones con rocas y arboles.

## [Nuevos avances y mejoras] 2026-10-04
### Agregados:

- Creacion de estructura de rescate creada por fases, que va cambiando con la cada construccion.
- Pantalla de victoria despues de armar la estructura y solicitar el rescate.
  
## [Nuevos avances y mejoras] 2026-10-05
### Agregados:
- Se agrego la animacion de minar.

## [Nuevos avances y mejoras] 2026-10-07
### Agregados:

- Se agrego la animacion de talar, golpear y recibir daño.
- Se creo la parte interior y exterior del templo.
- Se arreglo la pantalla para que si se quiere cambiar el tamaño no se deforme.

## [Nuevos avances y mejoras] 2026-10-08
### Agregados

- Creacion del templo y guardian ya ubicado en el mismo.
- Creacion de la funcion comer, arbusto de bayas y plantas de donde se obtienen fibras.
- Creacion de recetas de herramientas y proximamente mas objetos.
- El guardian ya tiene ataques propios animados y muerte animada.
- El hambre se conecta con la vida permitiendo recuperarla si tiene suficiente y perderla si queda en 0.

## [Nuevos avances y mejoras] 2026-10-25
### Agregados
- Se pusieron los limites del templo asi el jugador no podra atravesar las paredes ni salir de la estructura del templo.
- se agregaron los sprites de golpear con respectivos objetos.

## [Nuevos avances y mejoras] 2026-11-02
### Agregados:
- Se agregaron las estructuras con sus funcionalidades: Horno, cofre, mesa de fabricacion, cama y fogata
- Se agregaron nuevos recursos y recetas al juego
- Se agregaron armaduras que protegen al jugador y le dan mas vida
- Agregado de vendaje que permite curar directo la vida del jugador
- se crearon pico hacha y espada de hierro y quedaron pico y hacha primitivos, con menos durabilidad y que permiten obtener menos recursos
- Correcion de errores con la comida y algunos recursos

## [Nuevos avances y mejoras] 2026-11-09
### Agregados:
- Se agregaron animaciones Al pegar con la espada, pegar con el hacha, pegar con el pico, y pegar de forma predeterminada (sin ninguna de las 3 mencionadas)

## [Nuevos avances y mejoras] 2026-11-10
- Se arreglo la interfaz de controles para que entren todos
- Se agregaron barras de desgaste en las herramientas y la eliminacion de las mismas cuando llega a 0 su durabilidad
- Se cambiaron los costos de las estructuras y de la estructura de rescate en cada una de sus fases
- Se agregaron mensajes y alertas para mejorar la dinamica del juego y para que el usuario entienda mejor que pasa o porque no puede realizar tal accion
- Se mejoro la animacion del hacha al talar
- Se agrego la funcion de dividir recursos adentro del inventario
- Se agrego un menu de pausa al tocar el escape

## [Nuevos avances y mejoras] 2026-11-11
- Se hicieron balances y cambios en el guardian, la vaca, el lobo y la araña en base a su velocidad, vida, velocidad de ataque y daño
- Se Actualizo el ciclo dia y noche para que dure un poco mas cada hora
- Se arreglaron problemas en el mapa con la aparicion de recursos y colisiones

