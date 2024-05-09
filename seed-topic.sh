docker exec -it kafka-1 /bin/bash -c 'for i in {1..10000}; do echo "Message $i" | kafka-console-producer --broker-list localhost:9091 --topic test-topic-1; done'
