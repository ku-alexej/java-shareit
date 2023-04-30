FROM amazoncorretto:11
COPY target/*.jar serverApp.jar
#RUN ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
ENTRYPOINT ["java","-jar","/serverApp.jar"]