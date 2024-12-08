ARG DOCKER_HUB="docker.io"
ARG NGINX_VERSION="1.17.6"
ARG NODE_VERSION="16.14-alpine"

FROM $DOCKER_HUB/library/node:$NODE_VERSION as build

WORKDIR /workspace

RUN apk update && apk add --no-cache \
    python3 \
    py3-pip \
    build-base \
    && npm install -g node-gyp \
    && npm install -g @angular/cli@16

COPY . .

RUN npm install

RUN ng build --configuration production --base-href=/petclinic/ --deploy-url=/petclinic/

RUN echo "Files in /workspace/dist:" && ls -al /workspace/dist

FROM $DOCKER_HUB/library/nginx:$NGINX_VERSION as runtime


COPY  --from=build /workspace/dist/ /usr/share/nginx/html/petclinic/

COPY nginx.conf /etc/nginx/conf.d/default.conf

RUN chmod a+rwx /var/cache/nginx /var/run /var/log/nginx                        && \
    # sed -i.bak 's/listen\(.*\)80;/listen 8080;/' /etc/nginx/conf.d/default.conf && \
    sed -i.bak 's/^user/#user/' /etc/nginx/nginx.conf


EXPOSE 8080

USER nginx

HEALTHCHECK CMD curl --fail http://localhost:8080/petclinic/ || exit 1
#HEALTHCHECK     CMD     [ "service", "nginx", "status" ]


