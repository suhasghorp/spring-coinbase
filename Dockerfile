# Use the Eclipse temurin alpine official image
# https://hub.docker.com/_/eclipse-temurin
FROM ghcr.io/graalvm/jdk-community:23

# Create and change to the app directory.
WORKDIR /app

# Copy local code to the container image.
COPY . ./

# Build the app.
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Run the app by dynamically finding the JAR file in the target directory
CMD ["sh", "-c", "java -jar target/*.jar"]

# docker build -t suhasghorp/springcoinbase:1.0 .

# docker run -it -p 8080:8080 --env-file=.secrets suhasghorp/springcoinbase:1.0
# docker login
# docker push suhasghorp/springcoinbase:1.0