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

## Instrucciones de uso para la API REST

* La API REST se encuentra configurada en port 8090 


* El formato esperado para los timestamp de entrada es : ```yyyyMMddHHmmss```
  * Ejemplo: Para indicar el timestamp 28-03-2022 16:43:55 se debe ingresar 20220328164355


* Los ejemplos de uso dados a continuación son en formato JSON, pero la API REST soporta también formato XML


---
Nota: La aplicación cuenta con auto-documentación Swagger: http://localhost:8090/swagger-ui.html

---


### 1. Obtener la lista completa de precios Bitcoin registrados al momento

Request:
```shell
curl --location --request GET 'http://localhost:8090/api/v1/bitcoin/prices' \
--header 'Accept: application/json'
```

Reponse:
```200 OK```
```json
[
    {
        "id": 1,
        "currency": "USD",
        "price": 37580.3,
        "ts": "2022-02-21T22:36:45.190+00:00"
    },
    {
        "id": 2,
        "currency": "USD",
        "price": 37580.3,
        "ts": "2022-02-21T22:36:55.628+00:00"
    },
    {
        "id": 3,
        "currency": "USD",
        "price": 37581.1,
        "ts": "2022-02-21T22:37:05.652+00:00"
    }
]
```
* En caso de no contar con precios registrados aún, devuelve lista vacía.

### 2. Obtener el precio del Bitcoin para un cierto timestamp 


Request
```shell
curl --location --request GET 'http://localhost:8090/api/v1/bitcoin/prices?ts=20220221213020' \
--header 'Accept: application/json'
```

Reponse:
```200 OK```
```json
{
    "id": 681,
    "currency": "USD",
    "price": 37253.5,
    "ts": "2022-02-21T21:30:11.384+00:00"
}
```

* Nota: En caso de no coincidir el timestamp recibido con ninguno de los timestamps registrados,
  devuelve el precio inmediato anterior al timestamp recibido. Es decir, el precio correspondiente
  al período de 10 segundos que hay entre un precio y otro.
  * Esto se puede observar en el request de ejemplo: 
  <br/>Se pidio el timestamp ```2022-02-21 21:30:20```
  <br/>y la API devolvió el precio correspondiente al timestamp ```2022-02-21 21:30:11``` (el precio registrado 9 segundos antes)


* No permite consultas "a futuro": Devuelve 400 BAD REQUEST si el timestamp recibido es posterior a la fecha/hora actuales


* En caso de requerir precio para un timestamp anterior al timestamp del primer precio registrado 
(es decir, un instante anterior al comienzo de la serie temporal registrada), devuelve 404 NOT FOUND 
con un mensaje explicativo en el JSON response body


### 3. Obtener estadísticas del precio del Bitcoin para un cierto período de tiempo (desde-hasta)

Los datos estadísticos devueltos incluyen:
   
* el precio promedio para el período solicitado 
* el precio máximo registrado para el período solicitado
* la diferencia porcentual entre el valor promedio y el valor máximo

Request:
```shell
curl --location --request GET 'http://localhost:8090/api/v1/bitcoin/stats?ts_from=20220221203020&ts_to=20220221205020' \
--header 'Accept: application/json'
```

Reponse:
```200 OK```
```json
{
    "ts_from": "2022-02-21T20:30:20.000+00:00",
    "ts_to": "2022-02-21T20:50:20.000+00:00",
    "max_price": 37378.3,
    "avg_price": 37321.767499999994,
    "percent_price_diff": 0.15124417108324464
}
```

* Valida que el período indicado sea válido (ts_from debe ser anterior a ts_to). Caso contrario devuelve 400 BAD REQUEST

* En caso de no contar con precios registrados para el período indicado, devuelve 200 OK pero el flag "NaN" 
(Not a Number) en los valores estadisticos


## Tecnologías y Frameworks utilizados

* Spring Boot 2.5.9
  * Spring Web con Embedded Tomcat (para la API REST)
  * Spring Data JPA con Hibernate
  * Persistencia de datos en H2 (in-memory database)
* Java Streams API (java.util.stream)
  * <small>Para la recolección y filtrado de la información persistida, y cálculo de los datos estadísticos
  en el layer "service"</small>
* Spring Webclient
  * <small>Para el Proxy Service que realiza la consulta a la API externa de precios</small>
* Spring Scheduling 
  * <small>Para el Scheduler que realiza la consulta cada 10 segundos</small>
* Reactor Core (Java Reactive Programming)
  * <small>El Proxy Service que realiza el request a API externa devuelve un ```Mono``` 
  para que el BitcoinTracker Scheduler que lo consume pueda "subscribir" la acción de registración del precio 
  en forma no bloqueante (avoid blocking execution). </small>
* Lombok
* Swagger 2 (para auto-documentación API REST)
* Spring Test Framework (JUnit 5)

## Consideraciones técnicas adicionales

### Archivo de Configuración de la Aplicación
Los parámetros de configuración de la aplicación se encuentran en el archivo de configuración
```src/main/resources/application.yml```

* URL API Externa para consulta de Precios Bitcoin
```shell
bitcointracker:
  url: 'https://cex.io/api/last_price/BTC/USD'
```
* Cantidad de milisegundos entre requests a API Externa para el Scheduler Delay
```shell
bitcointracker:
  fixedDelay: 10000
```
* Formato del timestamp para los Request Params de la API REST
```shell
bitcointracker:
  timestampFormat: 'yyyyMMddHHmmss'
```
<br/>


### Volatilidad de los datos

La persistencia de datos se realiza en H2 in-memory database. 
Cuando se apaga la ejecución de la aplicación, lógicamente los datos persistidos se pierden.
<br/>
<br/>

### Pruebas Unitarias con Spring Test + JUnit

Las Pruebas Unitarias son insuficientes. 

Por el momento solo se cuenta con un módulo de Spring Unit Test 
```src/test/java/ar/com/wnc/btctracker/BtcTrackerApplicationTests.java``` 
El cual realiza testing del Service Layer para obtención y filtrado de datos persistidos 
(los metodos que utilizan la Stream API)
Faltan otros módulos de Test Unitario, como el Testing del REST API Layer (el web Controller), 
o el Proxy Service (la consulta a la API externa).



### Ejecución con spring.profiles.active=test para acceso a Consola SQL H2
Si se ejecuta la aplicación con el profile de configuración "test", de este modo:
```
    java -jar -Dspring.profiles.active=test target/btc-tracker-0.0.1-SNAPSHOT.jar
```
o bien asi:
```
    mvn spring-boot:run -Dspring-boot.run.profiles=test
```

La aplicación permite el acceso a la CONSOLA H2 para acceder en forma directa por SQL 
a la información almacenada en la Base de Datos

* Consola H2 : http://localhost:8090/h2-console
  * JDBC URL: jdbc:h2:mem:btctracker
  * User: sa
  * Password (blank)

<br/>


**Consultas y Sugerencias: maximiliano.milicich@gmail.com**

