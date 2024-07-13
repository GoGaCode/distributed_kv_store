PROJECT_NETWORK='project1-network'
SERVER_IMAGE='project1-server-image'
SERVER_CONTAINER='my-server'
CLIENT_IMAGE='project1-client-image'
CLIENT_CONTAINER='my-client'


# clean up existing resources, if any
echo "----------Cleaning up existing resources----------"
docker container stop $SERVER_CONTAINER 2> /dev/null && docker container rm $SERVER_CONTAINER 2> /dev/null
#docker container stop $CLIENT_CONTAINER 2> /dev/null && docker container rm $CLIENT_CONTAINER 2> /dev/null
docker ps -a --filter "name=$CLIENT_CONTAINER_PATTERN" --format "{{.ID}}" | xargs -r docker container stop 2> /dev/null
docker ps -a --filter "name=$CLIENT_CONTAINER_PATTERN" --format "{{.ID}}" | xargs -r docker container rm 2> /dev/null
docker network rm $PROJECT_NETWORK 2> /dev/null

# only cleanup
if [ "$1" == "cleanup-only" ]
then
  exit
fi

# create a custom virtual network
echo "----------creating a virtual network----------"
docker network create $PROJECT_NETWORK

# build the images from Dockerfile
echo "----------Building images----------"
docker build -t $CLIENT_IMAGE --target client-build .
docker build -t $SERVER_IMAGE --target server-build .

# run the image and open the required ports
echo "----------Running sever app----------"
docker run -d -p 1099:1099 --name $SERVER_CONTAINER --network $PROJECT_NETWORK $SERVER_IMAGE

# start rmi registry

echo "----------watching logs from server----------"
docker logs $SERVER_CONTAINER -f