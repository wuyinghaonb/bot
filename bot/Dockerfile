# 指定是基于哪个基础镜像
FROM openjdk:21

# 作者信息
MAINTAINER xxxx

# 挂载点声明
VOLUME /tmp

# 将本地的一个文件或目录，拷贝到容器的文件或目录里
ADD /target/bot-0.0.1-SNAPSHOT.jar /bot-0.0.1-SNAPSHOT.jar

## 将容器的 5678 端口暴露，给外部访问。
#EXPOSE 5678

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 当容器运行起来时执行使用运行jar的指令
ENTRYPOINT ["java", "-jar", "/bot/bot-0.0.1-SNAPSHOT.jar"]

