FROM nginx

WORKDIR /usr/share/nginx/html
COPY index.html .

WORKDIR /etc/nginx/
COPY nginx.conf .

# Expose port 80 for HTTP traffic
EXPOSE 80