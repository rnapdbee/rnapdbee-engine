#! /bin/bash

image_name="rnapdbee-engine-image"
container_name="rnapdbee-engine-container"
gurobi_license_path="gurobi_wls_license/gurobi.lic"
rnapdbee_engine_repository_path="./rnapdbee-engine"

cp $gurobi_license_path $rnapdbee_engine_repository_path/gurobi.lic

DOCKER_BUILDKIT=1 docker build --build-arg LICENSE_PATH='gurobi.lic' -t $image_name $rnapdbee_engine_repository_path && \
docker container rm -f $container_name > /dev/null && \
docker create --name $container_name -p 8081:8081 --net rnapdbee-network $image_name && \
docker image prune -f > /dev/null && \
docker builder prune -f > /dev/null && \
docker start $container_name

rm -f $rnapdbee_engine_repository_path/gurobi.lic