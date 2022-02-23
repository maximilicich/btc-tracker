# Bitcoin Tracker (btc-tracker)
### Spring Boot Microservices App

Esta aplicación de tipo Spring Boot Microservices realiza consultas cada 10 segundos a un Servicio REST externo 
(https://cex.io/api/last_price/BTC/USD) para obtener el precio actual del Bitcoin (BTC) en Dolares Estadounidenses (USD). 
y registrar la información obtenida en Base de Datos local. 

Al mismo tiempo, y a través de una API REST, expone la información de los precios registrados, permitiendo realizar 
las siguientes consultas:

1. Obtener la lista completa de precios Bitcoin registrados al momento
2. Obtener el precio del Bitcoin para un cierto timestamp (devuelve el precio inmediato anterior al timestamp recibido)
3. Obtener estadísticas de la evolución del precio del Bitcoin para un cierto período de tiempo (desde-hasta): 
Los datos estadísticos devueltos incluyen 
   1. el precio promedio para el período
   2. el precio máximo registrado para el período
   3. La diferencia porcentual entre el valor promedio y el valor máximo.



## Cómo ejecutar la aplicación en entorno local

### Requisitos
* JDK 1.8
* Maven 3.x (para compilación del código fuente)

### Pasos para ejecutar la aplicación

* Clonar el Repositorio git  
* Desde el directorio local del proyecto, ejecutar el siguiente comando Maven para compilar (build), 
correr tests unitarios y empaquetar (package) la aplicación:
```
    mvn clean package
```
* Una vez compilado y generado el archivo ```jar```, ejecutar la aplicación 
con el siguiente comando :
```
    java -jar target/btc-tracker-0.0.1-SNAPSHOT.jar
```

<br/>

* *Alternativamente, se puede ejecutar directamente el comando Maven:*  
```
    mvn spring-boot:run
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<small>Este comando es del maven plugin spring-boot, y realiza el build y luego el run de la app en un solo paso.</small>

<br/>


La aplicación inicia en pocos segundos y comienza a realizar las llamadas al Servicio externo para consultar el precio del Bitcoin. 
El web server (Tomcat embedded) comienza a escuchar en el port 8090 y ya está listo para recibir requests de consulta.

<br/>

**Consultas y Sugerencias: maximiliano.milicich@gmail.com**

