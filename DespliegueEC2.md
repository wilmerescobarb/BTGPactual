# Copia y rellena el archivo de variables
cp .env.example .env

# Build y push de cada imagen (cambia 'wilmerescobar' por tu usuario)
docker build -t wilmerescobar/bgt-api-auth:latest       ./bgt_api_auth
docker build -t wilmerescobar/bgt-api-investment:latest  ./bgt_api_investment
docker build -t wilmerescobar/bgt-api-notification:latest ./bgt_api_notification
docker build -t wilmerescobar/bgt-api-customer:latest    ./bgt_api_customer
docker build -t wilmerescobar/bgt-web-client:latest      ./bgt-web-client

docker push wilmerescobar/bgt-api-auth:latest
docker push wilmerescobar/bgt-api-investment:latest
docker push wilmerescobar/bgt-api-notification:latest
docker push wilmerescobar/bgt-api-customer:latest
docker push wilmerescobar/bgt-web-client:latest


# Instalar Docker + Compose
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

## Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

sudo apt install docker.io
sudo apt install docker-compose-plugin

sudo usermod -aG docker $USER && newgrp docker

# Copiar docker-compose.yml y crear .env con tus valores reales
cp .env.example .env && nano .env

# ¡Levantar todo!
docker compose up -d



docker compose down
docker compose pull
docker compose up -d


# Ver logs
docker logs -f bgt_api_auth

















docker build --no-cache -t wilmerescobar/bgt-api-auth:latest        ./bgt_api_auth
docker build --no-cache -t wilmerescobar/bgt-api-investment:latest  ./bgt_api_investment
docker build --no-cache -t wilmerescobar/bgt-api-notification:latest ./bgt_api_notification
docker build --no-cache -t wilmerescobar/bgt-api-customer:latest    ./bgt_api_customer

docker push wilmerescobar/bgt-api-auth:latest
docker push wilmerescobar/bgt-api-investment:latest
docker push wilmerescobar/bgt-api-notification:latest
docker push wilmerescobar/bgt-api-customer:latest

docker compose up -d --pull always