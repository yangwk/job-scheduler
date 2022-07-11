FROM java:8-jre

ENV SERVER_PORT=8080

COPY . /projecthome

RUN chmod +x /projecthome/docker-entrypoint.sh && ln -s /projecthome/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

WORKDIR /projecthome

EXPOSE 8080

CMD ["docker-entrypoint.sh"]
