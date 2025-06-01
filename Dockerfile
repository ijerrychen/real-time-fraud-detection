# 设定基础镜像 使用JRE21
FROM eclipse-temurin:21-jre
# 设定工作目录
WORKDIR /app
# 复制构建的jar包到容器中
COPY target/*.jar app.jar
# 应用程序运行端口
EXPOSE 8080

# 运行Spring Boot应用程序，指定spring.profiles.active参数, 默认dev环境
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=dev"]

