clear:
	sudo rm influxdb/config/*
	sudo rm -rf influxdb/data/*

run:
	sudo chmod 777 influxdb/scripts/init.sh
	sudo docker-compose down -v
	sudo docker-compose build --no-cache
	sudo docker-compose up --force-recreate -d

runclear: clear run