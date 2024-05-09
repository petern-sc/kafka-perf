Seed topic with data
```
docker-compose up -d
docker exec -it kafka-1 /bin/bash

for i in {1..10000}; do echo "Message $i"; done > messages.txt
kafka-console-producer --broker-list localhost:9091 --topic test-topic-1 < messages.txt
```

`runMain kafkaperf.consumer.ZIOKafkaConsumerPerf`
