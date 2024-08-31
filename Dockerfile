FROM eclipse-temurin:22-jdk-jammy AS build
WORKDIR /service

# Copy Maven wrapper and project files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src src

# Add executable permissions to mvnw
RUN chmod +x mvnw

# Resolve dependencies and build the application
RUN ./mvnw dependency:resolve
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final image with the application
FROM eclipse-temurin:22-jre-jammy
WORKDIR /service

# Copy the packaged JAR from the build stage
COPY --from=build /service/target/Telegrambot-0.0.1-SNAPSHOT.jar Telebot.jar

# Command to run the JAR file
CMD ["java", "-jar", "Telebot.jar"]