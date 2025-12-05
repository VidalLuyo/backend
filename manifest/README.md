# Kubernetes Deployment

## Requisitos

- Docker Desktop con Kubernetes habilitado
- O Minikube instalado

## Despliegue Local (Minikube)

### 1. Verificar Minikube

```bash
# Ver versión
minikube version

# Ver estado
minikube status

# Iniciar cluster
minikube start
```

### 2. Construir imágenes (si no están en Docker Hub)

```bash
# Backend (si tienes cambios)
cd backend
docker build -t vidalluyo0/vg-ms-assistance:1.0 .
docker push vidalluyo0/vg-ms-assistance:1.0

# Frontend
cd frontend
docker build -t vidalluyo0/vg-frontend:1.0 .
docker push vidalluyo0/vg-frontend:1.0
```

### 3. Aplicar manifiestos

```bash
# Ir a la carpeta manifest
cd backend/manifest

# Aplicar todo
kubectl apply -f .
```

### 4. Verificar despliegue

```bash
# Ver todos los recursos
kubectl get all -n jesus-luyo

# Ver pods
kubectl get pods -n jesus-luyo

# Ver services
kubectl get services -n jesus-luyo

# Ver logs del backend
kubectl logs -f deployment/vg-ms-assistance -n jesus-luyo

# Ver logs del frontend
kubectl logs -f deployment/vg-frontend -n jesus-luyo
```

### 5. Acceder al servicio

```bash
# Listar services de Minikube
minikube service list

# Abrir el frontend
minikube service vg-frontend -n jesus-luyo

# Abrir el backend
minikube service vg-ms-assistance -n jesus-luyo

# O crear túnel de puerto
kubectl port-forward service/vg-frontend 8080:80 -n jesus-luyo
kubectl port-forward service/vg-ms-assistance 8085:9087 -n jesus-luyo
```

Acceder a:

- **Frontend**: http://localhost:8080
- **Backend**: http://localhost:8085/swagger-ui.html

## Despliegue en Nube (Docker Desktop Kubernetes)

### 1. Habilitar Kubernetes

Docker Desktop → Settings → Kubernetes → Enable Kubernetes

### 2. Construir y subir imágenes

```bash
# Frontend
cd frontend
docker build -t vidalluyo0/vg-frontend:1.0 .
docker push vidalluyo0/vg-frontend:1.0

# Backend (si tienes cambios)
cd backend
docker build -t vidalluyo0/vg-ms-assistance:1.0 .
docker push vidalluyo0/vg-ms-assistance:1.0
```

### 3. Aplicar manifiestos

```bash
cd backend/manifest
kubectl apply -f .
```

### 4. Verificar

```bash
kubectl get all -n jesus-luyo
```

### 5. Acceder

- **Frontend**: http://localhost:30080
- **Backend**: http://localhost:30087/swagger-ui.html

## Despliegue en Codespaces

### 1. Construir imágenes

```bash
# Frontend
cd frontend
docker build -t vidalluyo0/vg-frontend:1.0 .
docker push vidalluyo0/vg-frontend:1.0

# Backend
cd backend
docker build -t vidalluyo0/vg-ms-assistance:1.0 .
docker push vidalluyo0/vg-ms-assistance:1.0
```

### 2. Instalar kubectl y Minikube

```bash
# Instalar kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/

# Instalar Minikube
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
chmod +x minikube-linux-amd64
sudo mv minikube-linux-amd64 /usr/local/bin/minikube

# Iniciar Minikube
minikube start --driver=docker
```

### 3. Aplicar manifiestos

```bash
cd backend/manifest
kubectl apply -f .
```

### 4. Exponer servicios

```bash
# Crear túneles para acceder desde Codespaces
kubectl port-forward service/vg-frontend 30080:80 -n jesus-luyo --address 0.0.0.0 &
kubectl port-forward service/vg-ms-assistance 30087:9087 -n jesus-luyo --address 0.0.0.0 &
```


Si cambias el puerto del backend, actualiza este valor y aplica:

```bash
kubectl apply -f 09-deployment-frontend.yaml
kubectl rollout restart deployment/vg-frontend -n jesus-luyo
```

## Comandos útiles

```bash
# Ver namespaces
kubectl get namespaces
kubectl get ns

# Cambiar namespace por defecto
kubectl config set-context --current --namespace=jesus-luyo

# Ver namespace actual (Linux)
kubectl config view --minify | grep namespace

# Ver namespace actual (Windows)
kubectl config view --minify | findstr namespace

# Ver todos los recursos y secrets
kubectl get all,secrets -n jesus-luyo

# Ver logs de un pod específico
kubectl logs nombre-pod -n jesus-luyo

# Reiniciar deployment
kubectl rollout restart deployment/vg-ms-assistance -n jesus-luyo
kubectl rollout restart deployment/vg-frontend -n jesus-luyo

# Ver estado del rollout
kubectl rollout status deployment/vg-ms-assistance -n jesus-luyo

# Escalar replicas
kubectl scale deployment/vg-ms-assistance --replicas=3 -n jesus-luyo
```

## Eliminar

```bash
# Eliminar todo el namespace
kubectl delete namespace jesus-luyo

# O eliminar recursos individuales
kubectl delete -f .
```

## Troubleshooting

```bash
# Ver detalles de un pod
kubectl describe pod nombre-pod -n jesus-luyo

# Ver eventos
kubectl get events -n jesus-luyo

# Ejecutar comando dentro del pod
kubectl exec -it nombre-pod -n jesus-luyo -- sh

# Ver logs en tiempo real
kubectl logs -f deployment/vg-ms-assistance -n jesus-luyo


kubectl set env deployment/vg-frontend API_URL=http://localhost:30087/api -n jesus-luyo
```

## Estructura de archivos

```
00-namespace.yaml              # Namespace jesus-luyo
01-secret.yaml                 # Credenciales de la base de datos
03-deployment-assistance.yaml  # Backend principal (2 replicas)
04-deployment-institution.yaml # Microservicio institution (2 replicas)
05-deployment-students.yaml    # Microservicio students (2 replicas)
06-service-assistance.yaml     # Service backend (NodePort 30087)
07-service-institution.yaml    # Service institution (ClusterIP)
08-service-students.yaml       # Service students (ClusterIP)
09-deployment-frontend.yaml    # Frontend (2 replicas)
10-service-frontend.yaml       # Service frontend (NodePort 30080)
```
