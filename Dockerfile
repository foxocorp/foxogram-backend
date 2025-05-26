FROM gcr.io/distroless/java21-debian12:latest
COPY build/libs/foxogram-backend-1.0.0.jar ./foxogram-backend.jar
CMD ["foxogram-backend.jar"]
