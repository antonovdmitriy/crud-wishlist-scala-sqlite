FROM nginx:1.24.0

WORKDIR /usr/share/nginx/html
COPY index.html .
COPY script.js .
COPY style.css .

WORKDIR /etc/nginx/
COPY nginx.conf .

EXPOSE 80
