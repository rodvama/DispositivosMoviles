# DispositivosMoviles

### Objetivo
El objetivo principal del proyecto, es el de facilitar la toma de la presión y el ritmo cardíaco de un paciente, mediante el uso de dos dispositivos físicos conectados entre ellos. Además de apoyar a personas principiantes a mejorar o a practicar la toma de presión que normalmente se hace de manera manual. Un objetivo también de la aplicación es poder recaudar información para su fácil análisis posterior en casos de investigación.

### Descripción
Aplicación Móvil nativa para android, la cual se conectará vía bluetooth a un dispositivo externo que mandará paquetes de información a la aplicación, para ser utilizados en cálculos necesarios para desplegar una medición correcta y exacta.
Dicha aplicación, podrá registrar las mediciones de n cantidad de pacientes, para poder después ser analizadas por especialistas, además de que dicha información, se podrá mandar mediante un correo electrónico a la persona deseada, por el usuario.

### Características generales
El proyecto cuenta con las siguientes funciones principales:

- Configurar onda de pulso: La aplicación debe de proporcionar una manera sencilla en la que el usuario pueda unir la aplicación con el aparato externo por medio de Bluetooth.
- Calcular presión de paciente: Basado en la información recolectada por medio de Bluetooth del aparato externo, el dispositivo móvil deberá de analizar y calcular la información necesaria para proveer al profesional de salud (usuario) con suficientes datos para tomar decisiones.
- Enviar información de pacientes a correo electrónico: Por medio de la aplicación móvil, el usuario podrá seleccionar la información que desea recibir por medio de correo electrónico y enviarla.
- Tomar presión: Sección fundamental de la aplicación. Por medio de la comunicación con el aparato externo, la aplicación funcionará como el aparato medidor de presión, indicando al profesional de salud el estado de la presión del paciente con respecto al tiempo y su pulso.

Tecnologías utilizadas.
  - Android Studios:
      - Kotlin
  - GitHub
  - Bluetooth LE

### Versión
Versión: 2.1
Fecha de última actualización: 11/04/2019

### Desarrolladores:

| Nombre  | Correo electrónico |
| ------------- | ------------- |
| Juan Manuel Pérez	| a00819815@itesm.mx |
| Rodrigo Valencia	| A00818256@itesm.mx |
| Renato Sanchez	| A01281104@itesm.mx |
| Ian Granados	| A00818648@itesm.mx |
| Héctor León Quiróz  | A01251806@itesm.mx  |
| Enrique García Torres  | A00818997@itesm.mx  |
| Manuel Torres Magdaleno  | A01066869@itesm.mx  |
| Roberto Ramírez Monroy  | A01366943@itesm.mx  |

### Profesor:

| Nombre  | Email |
| ------------- | ------------- |
| Ing. Martha Sordia Salinas | msordia@itesm.mx |

### Cliente:

| Nombre  | Email |
| ------------- | ------------- |
| Dr. Gonzalez  |  |

### Institución

Este proyecto es hecho para la clase de Proyecto de Desarrollo para Dispositivos Móviles - Versión Android.

Universidad: ITESM
Campus: Campus Monterrey

### Próximas actualizaciones
- Liga al Trello del proyecto: https://trello.com/b/YyBNlbqQ/androides-proyecto
- Cambiar guardado de información de aparato externo de tipo ArrayList en BluetoothHandler a LiveData y escuchar los cambios.
- Cuando se escriben tres digitos en la información de captura manual, pasar automáticamente al input del próximo dato de medición diastólica.
- Revisar el comportamiento de la aplicación al realizar una medición y seleccionar la opción de "Reintentar".


- Agregar cierre de sesión
- Agregar pantalla de selección de tipo de usuario
- Agregar uso de Bluetooth LE
