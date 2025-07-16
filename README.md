# Spring Kafka 익명 채팅 예제 

## Spring <-> Kafka

<img width="856" height="480" alt="image" src="https://github.com/user-attachments/assets/1ac90909-40e5-48ba-8a2f-e3110a9cdda3" />

1. Spring 환경변수에 명시된 Kafka 주소로 접근
2. Kafka가 다시 Kafka 브로커 주소(ADVERTISED_LISTENERS) 알려줌
3. Spring은 (ADVERTISED_LISTENERS)로 다시 접근

``` yaml
  kafka:
    image: bitnami/kafka:3.6
    container_name: kafka
    depends_on:
      - zookeeper
    environment:
      ALLOW_PLAINTEXT_LISTENER: "yes"
      KAFKA_CFG_NODE_ID: 1
      KAFKA_CFG_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_CFG_LISTENERS: |
        INTERNAL://Kafka 컨테이너 내부에서 INTERNAL 포트 바인딩,
        EXTERNAL://Kafka 컨테이너 내부에서 EXTERNAL 포트 바인딩
      KAFKA_CFG_ADVERTISED_LISTENERS: |
        INTERNAL://INTERNAL 연결에게 알려주는 Kakfa 브로커 위치,
        EXTERNAL://EXTERNAL 연결에게 알려주는 Kakfa 브로커 위치
      KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_CFG_INTER_BROKER_LISTENER_NAME: INTERNAL
    ports:
      - "19092:19092"
    networks:
      - backend
```
