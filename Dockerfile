FROM bellsoft/liberica-openjdk-alpine:21

ENV APP_URL=http://localhost:8082
ENV CMS_SERVER_URL=http://localhost:3002
ENV CONTACT_MAIL=hackybear@gmail.com
ENV CUSTOMER_SERVER_URL=http://192.168.50.2:3001
ENV IS_PRODUCTION=false
ENV JENKINS_SERVER_URL=http://localhost:8080
ENV JWT_EXPIRATION=57600000
ENV MAIL_HOST=smtp-relay.brevo.com
ENV MAIL_PORT=587
ENV NO_REPLY_MAIL=hackybear7@gmail.com
ENV PORT=8082

EXPOSE 8082

COPY ./target/hungry-scan-core-0.0.1.war app.jar
ENTRYPOINT ["java","-jar","/app.jar"]