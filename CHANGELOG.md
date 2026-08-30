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




