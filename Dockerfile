FROM tomcat:latest

COPY target/ar-bloodbank-1.0.war /usr/local/tomcat/webapps/

EXPOSE 8080

CMD ["catalina.sh","run"]