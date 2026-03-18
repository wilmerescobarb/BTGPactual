# ── Stage 1: Build Angular ────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Instala dependencias primero (capa cacheable)
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN chmod +x node_modules/.bin/ng && npm run build -- --configuration production

# ── Stage 2: Serve con Nginx ──────────────────────────────────────────────────
FROM nginx:alpine

# Copia los archivos estáticos generados por Angular
# Angular 17+ genera en dist/<proyecto>/browser/
COPY --from=builder /app/dist/bgt-web-client/browser /usr/share/nginx/html

# Reemplaza la config default de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
