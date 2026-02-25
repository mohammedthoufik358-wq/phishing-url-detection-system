# Use official Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy everything into container
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project inside container
RUN ./mvnw clean package -DskipTests

# Expose Render port
EXPOSE 8080

# Run the jar (auto-detect jar name)
CMD ["sh", "-c", "java -jar target/*.jar"]