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

### 2. Aplicar manifiestos

```bash
# Ir a la carpeta manifest
cd backend/manifest

# Crear namespace
kubectl apply -f 00-namespace.yaml

# Crear secret
kubectl apply -f 01-secret.yaml

# Crear deployments
kubectl apply -f 03-deployment-assistance.yaml
kubectl apply -f 04-deployment-institution.yaml
kubectl apply -f 05-deployment-students.yaml

# Crear services
kubectl apply -f 06-service-assistance.yaml
kubectl apply -f 07-service-institution.yaml
kubectl apply -f 08-service-students.yaml

# O aplicar todo a la vez
kubectl apply -f .
```

### 3. Verificar despliegue

```bash
# Ver todos los recursos
kubectl get all -n jesus-luyo

# Ver pods
kubectl get pods -n jesus-luyo

# Ver services
kubectl get services -n jesus-luyo

# Ver logs
kubectl logs -f deployment/vg-ms-assistance -n jesus-luyo
```

### 4. Acceder al servicio

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

### 5. Probar el servicio

```bash
# Health check
curl http://localhost:8085/actuator/health

# Swagger
curl http://localhost:8085/swagger-ui.html
```

## Despliegue en Nube (Docker Desktop Kubernetes)

### 1. Habilitar Kubernetes

Docker Desktop → Settings → Kubernetes → Enable Kubernetes

### 2. Aplicar manifiestos

```bash
cd backend/manifest
kubectl apply -f .
```

### 3. Verificar

```bash
kubectl get all -n jesus-luyo
```

### 4. Acceder

http://localhost:30087/api/attendance

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

# Ver estado del rollout
kubectl rollout status deployment/vg-ms-assistance -n jesus-luyo
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
```
