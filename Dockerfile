FROM centos:7
MAINTAINER Ryota Sakamoto

RUN yum install git java-devel unzip wget -y
RUN wget http://downloads.lightbend.com/scala/2.12.0/scala-2.12.0.tgz
RUN tar xvzf scala-2.12.0.tgz
RUN mv scala-2.12.0 /usr/local/scala

RUN wget https://downloads.typesafe.com/typesafe-activator/1.3.12/typesafe-activator-1.3.12.zip
RUN unzip typesafe-activator-1.3.12.zip
RUN mv activator-dist-1.3.12 /usr/local/activator

ENV SCALA_HOME /usr/local/scala
ENV PATH $PATH:$SCALA_HOME/bin
ENV PATH $PATH:/usr/local/activator/bin

RUN mkdir -p /var/www/html
WORKDIR /var/www/html
EXPOSE 9000