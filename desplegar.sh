#!/bin/bash

set -e

DOCKERHUB_USERNAME="wilmerescobar"

echo "========================================"
echo "1. Bajando stack y eliminando recursos"
echo "========================================"

docker compose down --rmi all --volumes --remove-orphans || true

echo "========================================"
echo "2. Eliminando contenedores sueltos si existen"
echo "========================================"

docker rm -f \
  bgt_api_auth \
  bgt_api_investment \
  bgt_api_notification \
  bgt_api_customer \
  bgt-web-client \
  bgt_mongodb 2>/dev/null || true

echo "========================================"
echo "3. Eliminando imagenes locales especificas"
echo "========================================"

docker rmi -f \
  ${DOCKERHUB_USERNAME}/bgt-api-auth:latest \
  ${DOCKERHUB_USERNAME}/bgt-api-investment:latest \
  ${DOCKERHUB_USERNAME}/bgt-api-notification:latest \
  ${DOCKERHUB_USERNAME}/bgt-api-customer:latest \
  ${DOCKERHUB_USERNAME}/bgt-web-client:latest 2>/dev/null || true

echo "========================================"
echo "4. Eliminando volumenes especificos si existen"
echo "========================================"

docker volume rm \
  mongodb_data 2>/dev/null || true

echo "========================================"
echo "5. Limpieza extra de recursos no usados"
echo "========================================"

docker image prune -f || true
docker container prune -f || true
docker volume prune -f || true
docker network prune -f || true

echo "========================================"
echo "6. Construyendo imagenes desde cero"
echo "========================================"

docker build --no-cache -t ${DOCKERHUB_USERNAME}/bgt-api-auth:latest ./bgt_api_auth
docker build --no-cache -t ${DOCKERHUB_USERNAME}/bgt-api-investment:latest ./bgt_api_investment
docker build --no-cache -t ${DOCKERHUB_USERNAME}/bgt-api-notification:latest ./bgt_api_notification
docker build --no-cache -t ${DOCKERHUB_USERNAME}/bgt-api-customer:latest ./bgt_api_customer
docker build --no-cache -t ${DOCKERHUB_USERNAME}/bgt-web-client:latest ./bgt-web-client

echo "========================================"
echo "7. Subiendo imagenes a Docker Hub"
echo "========================================"

docker push ${DOCKERHUB_USERNAME}/bgt-api-auth:latest
docker push ${DOCKERHUB_USERNAME}/bgt-api-investment:latest
docker push ${DOCKERHUB_USERNAME}/bgt-api-notification:latest
docker push ${DOCKERHUB_USERNAME}/bgt-api-customer:latest
docker push ${DOCKERHUB_USERNAME}/bgt-web-client:latest

echo "========================================"
echo "8. Desplegando stack limpio"
echo "========================================"

docker compose up -d --pull always --force-recreate

echo "========================================"
echo "9. Estado final"
echo "========================================"

docker compose ps