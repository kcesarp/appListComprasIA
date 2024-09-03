# App de Lista de Compras con IA

Esta aplicación te permite crear y gestionar una lista de compras, marcando los ítems que necesitas. Además, puedes aprovechar la funcionalidad de IA para generar sugerencias de recetas basadas en los ingredientes que tienes disponibles.

## Características

- **Gestión de Lista de Compras**: Puedes agregar, editar, y eliminar ítems de tu lista de compras.
- **Marcado de Ítems**: Marca los ítems que ya has comprado o que ya tienes en tu inventario.
- **Sugerencias de Recetas con IA**: Basado en los ingredientes que has marcado en tu lista, la aplicación puede sugerir platos que puedes cocinar, o indicarte qué ingredientes adicionales necesitas para recetas específicas.
- **Cambio de Tema**: Alterna entre modo oscuro y claro según tu preferencia.

![image](https://github.com/user-attachments/assets/4b6d3a3f-1858-46ab-8cab-7213bee54dd9)


## Requisitos Previos

- Android Studio 4.0 o superior
- Java 8 o superior
- Conexión a Internet para utilizar la funcionalidad de IA
- Clave de API para acceder a la API de OpenAI (reemplazar en el código)

## Instalación

1. Clona este repositorio en tu máquina local:
    ```bash
    git clone https://github.com/tuusuario/tu-repositorio.git
    ```
2. Abre el proyecto en Android Studio.

3. Configura tu clave de API de OpenAI:
   - Reemplaza la variable `API_KEY` en `MainActivity.java` con tu clave de API de OpenAI.

4. Conecta un dispositivo Android o usa un emulador y ejecuta la aplicación.

## Uso

- **Agregar Ítems**: Usa el botón de agregar para añadir un nuevo ítem a tu lista de compras.
- **Editar/Eliminar Ítems**: Presiona el icono de 6puntos para editar o eliminarlo.
- **Generar Recetas**: Marca los ítems que tienes disponibles y selecciona la opción "Ver comidas IA" en el menú de opciones para obtener sugerencias de recetas.
- **Cambio de Tema**: Usa la opción "Cambiar Tema" en el menú de opciones para alternar entre el modo oscuro y claro.

## Tecnologías Utilizadas

- **Lenguaje**: Java
- **IDE**: Android Studio
- **Base de Datos**: SQLite
- **Interfaz de Usuario**: Material Design
- **API de IA**: OpenAI API para generación de recetas

## Contribuciones

Las contribuciones son bienvenidas. Si deseas mejorar esta aplicación o añadir nuevas características, por favor, abre un pull request.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo `LICENSE` para obtener más información.

