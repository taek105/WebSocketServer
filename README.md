# Spring Kafka 익명 채팅 예제

## Spring <-> Kafka

<img width="856" height="480" alt="image" src="https://github.com/user-attachments/assets/1ac90909-40e5-48ba-8a2f-e3110a9cdda3" />

1. Spring 환경변수에 명시된 Kafka 주소로 접근
2. Kafka는 브로커 주소(ADVERTISED_LISTENERS) 반환
3. Spring은 반환받은 브로커 주소(ADVERTISED_LISTENERS)로 다시 접근

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
        INTERNAL://INTERNAL 연결에 대해 Kafka가 실제로 노출할 주소
        EXTERNAL://EXTERNAL 연결에 대해 Kafka가 실제로 노출할 주소
      KAFKA_CFG_ADVERTISED_LISTENERS: |
        INTERNAL://INTERNAL 연결에게 알려줄 Kafka에 접근할 주소
        EXTERNAL://EXTERNAL 연결에게 알려줄 Kafka에 접근할 주소
      KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_CFG_INTER_BROKER_LISTENER_NAME: INTERNAL
    ports:
      - "19092:19092"
    networks:
      - backend
```

### 사용방법
1. docker-compose.yaml 손보기
2. docker compose up -d
3. 로컬에서 kafka cli를 통해 토픽 구독해서 모니터링 가능