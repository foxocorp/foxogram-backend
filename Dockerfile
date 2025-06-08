FROM gcr.io/distroless/java21-debian12:latest
COPY build/libs/foxochat-backend-1.0.0.jar ./foxochat-backend.jar
CMD ["foxochat-backend.jar"]
