From myhealthorg/springboot-base:alpine
COPY target/user-role-service-0.0.1-SNAPSHOT.jar app.jar
# Expose port (optional)
EXPOSE 8080

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]