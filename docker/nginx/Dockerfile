FROM nginx:stable-alpine

COPY conf.d /etc/nginx/conf.d
COPY cert /etc/nginx/cert
RUN echo "0 0 */16 * *    sh /etc/nginx/cert/get.sh" >> /var/spool/cron/crontabs/root \
    && sh /etc/nginx/cert/get.sh

EXPOSE 80

CMD crond -S & nginx -g "daemon off;"