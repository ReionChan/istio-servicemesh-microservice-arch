#!/bin/bash

VERSION=1.0_istio

BASE=`pwd`

#echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_NAME" --password-stdin

echo ''
echo $BASE
echo ''

echo '--------------------------'
echo 'ARCH-USERS BUILD'
echo '--------------------------'
docker tag arch-users:$VERSION $DOCKER_NAME/arch-users:$VERSION
echo ''

echo '--------------------------'
echo 'ARCH-APP BUILD'
echo '--------------------------'
docker tag arch-app:$VERSION $DOCKER_NAME/arch-app:$VERSION
echo ''

echo '--------------------------'
echo 'ARCH-IAM BUILD'
echo '--------------------------'
docker tag arch-iam:$VERSION $DOCKER_NAME/arch-iam:$VERSION
echo ''


docker images
sleep 5

docker push $DOCKER_NAME/arch-users:$VERSION
docker push $DOCKER_NAME/arch-app:$VERSION
docker push $DOCKER_NAME/arch-iam:$VERSION