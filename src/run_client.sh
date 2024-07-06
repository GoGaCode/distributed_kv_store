CLIENT_IMAGE='project1-client-image'
PROJECT_NETWORK='project1-network'
SERVER_CONTAINER='my-server'

if [ $# -ne 4 ]
then
  echo "Usage: ./run_client.sh <container-name> <protocol> <port-number> <server-index>"
  exit
fi

# run client docker container with cmd args

# rmiregistery $3 &

docker run -it --rm --name "$1" \
 --network $PROJECT_NETWORK $CLIENT_IMAGE \
 java client.ClientApp $SERVER_CONTAINER "$2" "$3" "$4"
 # cmd to run client locally - java client.ClientApp <client-pod-name> RPC 1099 <Server Index>