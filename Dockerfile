# 使用官方的 OpenJDK 21 运行时镜像作为基础镜像
FROM eclipse-temurin:21-jre

# 设置工作目录
WORKDIR /app

# 将构建的 jar 文件复制到容器中
COPY target/*.jar app.jar

# 暴露应用程序运行的端口
EXPOSE 8080

# 运行 Spring Boot 应用程序，并动态指定 spring.profiles.active 参数, 默认是 dev
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=dev"]

