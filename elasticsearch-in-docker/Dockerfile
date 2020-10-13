FROM ubuntu:18.04

ENV http_proxy=http://proxy.cht.com.tw:8080
ENV https_proxy=http://proxy.cht.com.tw:8080

RUN apt-get update
RUN apt-get install -y curl vim

RUN sysctl -w vm.max_map_count=262144

RUN curl http://chttl.dynns.com/wwwwww/jdk-11.0.1_linux-x64_bin.tar.gz | tar zx
RUN ln -s jdk-11.0.1 jdk
ENV JAVA_HOME=/jdk

RUN curl https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.4.3.tar.gz | tar zx
RUN ln -s elasticsearch-6.4.3 elasticsearch

COPY elasticsearch.yml elasticsearch/config/elasticsearch.yml
COPY jvm.options elasticsearch/config/jvm.options

RUN adduser --disabled-password --gecos "" es
RUN chown -R es:es elasticsearch/

CMD ["bash"]

