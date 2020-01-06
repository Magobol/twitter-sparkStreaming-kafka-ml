#!/bin/bash

sudo pip install -r requirements.txt
~/kaf/bin/zookeeper-server-start.sh ~/kaf/config/zookeeper.properties &
sleep 3
~/kaf/bin/kafka-server-start.sh ~/kaf/config/server.properties &
sleep 3
~/kaf/bin/kafka-topics.sh --delete --zookeeper localhost:2181  --topic twitterstream  --if-exists &
sleep 3
~/kaf/bin/kafka-topics.sh --create --zookeeper localhost:2181 --partitions 1 --topic twitterstream  --replication-factor 1 &
sleep 3
python app.py &


./build_and_submit.sh Trainer