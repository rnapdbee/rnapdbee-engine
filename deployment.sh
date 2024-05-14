#! /bin/bash

image_name="rnapdbee-engine-image"
container_name="rnapdbee-engine-container"
rnapdbee_engine_repository_path="."

DOCKER_BUILDKIT=1 docker build -t $image_name $rnapdbee_engine_repository_path &&
	docker container rm -f $container_name >/dev/null &&
	docker create --name $container_name -p 8081:8081 --net rnapdbee-network $image_name &&
	docker image prune -f >/dev/null &&
	docker builder prune -f >/dev/null &&
	docker start $container_name
