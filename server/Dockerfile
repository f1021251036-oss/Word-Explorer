# Stage 1: Build Frontend SPA (/web)
FROM node:22-alpine AS web-builder
WORKDIR /app/web
COPY web/package*.json ./
RUN npm install --no-audit --no-fund
COPY web ./
COPY app/src/main/res/drawable ./app-assets/drawable
COPY app/src/main/res/font ./app-assets/font
WORKDIR /app
RUN mkdir -p app/src/main/res && cp -r /app/web/app-assets/* app/src/main/res/ && rm -rf /app/web/app-assets
ARG VITE_GOOGLE_WEB_CLIENT_ID=""
ENV VITE_GOOGLE_WEB_CLIENT_ID=$VITE_GOOGLE_WEB_CLIENT_ID
RUN cd /app/web && npm run build

# Stage 2: Build Backend Server (/server)
FROM node:22-alpine AS server-builder
WORKDIR /app/server
COPY server/package*.json ./
RUN npm install --no-audit --no-fund
COPY server ./
RUN npm run build

# Stage 3: Minimal Non-Root Production Runtime
FROM node:22-alpine AS runner
WORKDIR /app/server
ENV NODE_ENV=production
ENV PORT=8080
COPY server/package*.json ./
RUN npm install --omit=dev --no-audit --no-fund
COPY --from=server-builder /app/server/dist ./dist
COPY --from=web-builder /app/web/dist ../web/dist
RUN chown -R node:node /app
USER node
EXPOSE 8080
CMD ["node", "dist/index.js"]
