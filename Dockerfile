FROM tomcat:latest

COPY target/ar-bloodbank-1.0.wa /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh","run"]