# Spring Boot & Google Cloud

## Crear un proyecto en Google Cloud

Debemos crear un proyecto en [Google Cloud](https://console.cloud.google.com), El proyecto tiene un identificador únido que es necesario en algunos de los comandos de Google Cloud SDK, en el presente ejemplo el identificador es **nice-plexus-158605**

## Instalar Google Cloud SDK

Guía de instalación para linux [aquí](https://cloud.google.com/sdk/docs/quickstart-linux)

### Instalar el componente para Kubernetes en Google Cloud SDK

```
gcloud components install kubectl
```

## Creación del clúster de contenedores

### Autenticación en Google Cloud

```
gcloud auth application-default login
```

### Creación del clúster

```
gcloud container clusters create cluster-meet-sboot
```

### Iniciar la consola de Kubernetes

```
gcloud container clusters get-credentials cluster-meet-sboot --zone us-central1-b --project nice-plexus-158605
kubectl proxy
```

Para ingresar a la consola visitamos el enlace `http://localhost:8001/ui`

## Generación y despliegue de la imagen

### Construcción del proyecto

Ingresamos a la opción `Container Engine` desde la consola de Google Cloud, seleccionamos el clúster `cluster-meet-sboot` creado en los pasos anteriores y activamos Google Cloud Shell. Esto nos presentará una consola shell en el navegador.

Una vez activada la consola nos aseguramos de trabajar con Java 8, configurar la zona del contenedor y construir el proyecto:

```
sudo update-alternatives --config java
gcloud config set compute/zone us-central1-b
gcloud auth application-default login
./gradlew build && java -jar build/libs/meet-spring-boot-docker-1.0.jar
```

Presionamos `CTRL c` para detener la ejecución de la aplicación construida con Spring Boot.

### Generación de imagen Docker

Verificamos que no tenemos aún imágenes Docker:

```
docker images
```

Generamos la imagen:

```
./gradlew build buildDocker
```

Verificamos la imagen generada:

```
docker images
```

Publicamos la imagen en Container Registry:

```
gcloud docker -- push us.gcr.io/nice-plexus-158605/meet-spring-boot-docker:latest
```
 
### Despliegue

Nos autenticamos para poder ejecutar los comandos de Kubernetes, creamos el pod y exponemos el servicio:

```
gcloud container clusters get-credentials cluster-meet-sboot --zone us-central1-b --project nice-plexus-158605
kubectl run k-meet-sboot --image=us.gcr.io/nice-plexus-158605/meet-spring-boot-docker:latest --port=8080
kubectl get deployments
kubectl get pods
kubectl get rs
kubectl expose deployment k-meet-sboot --type="LoadBalancer"
kubectl get service k-meet-sboot
```

**Es posible que el comando `kubectl get service k-meet-sboot` debamos ejecutarlo varias veces hasta que nos muestre la IP externa**.

Verificar el estado del despliegue:

```
kubectl rollout status deployment/k-meet-sboot
```

Para actualizar la imagen del despliegue:

```
kubectl set image deployment/k-meet-sboot k-meet-sboot=us.gcr.io/nice-plexus-158605/meet-spring-boot-docker:1.0
```

## Eliminar el clúster

```
kubectl delete service k-meet-sboot
gcloud container clusters delete cluster-sboot
```

## TODO

- [ ] Buscar cómo listar los push realizados a Container Registry
