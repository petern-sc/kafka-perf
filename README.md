Seed topic with data
```
docker-compose up -d
docker exec -it kafka-1 /bin/bash

kafka-topics --bootstrap-server localhost:9091 --topic test-topic-1 --partitions 100 --create

kafka-1 kafka-producer-perf-test \
  --topic test-topic-1 \
  --num-records 10000000 \
  --record-size 100 \
  --throughput -1 \
  --producer-props acks=1 bootstrap.servers=localhost:9091
```

`runMain kafkaperf.consumer.ZIOKafkaConsumerPerf`
