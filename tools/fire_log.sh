#!/bin/bash
#export MOZ_LOG=timestamp,rotate:200,nsHttp:5,nsSocketTransport:5,nsStreamPump:5,nsHostResolver:5
#this command was the original, removing all other params leaves us with cache events
#export MOZ_LOG=timestamp,rotate:50,nsHttp:5,nsSocketTransport:5,nsStreamPump:5,nsHostResolver:5,cache2:5
export MOZ_LOG=timestamp,rotate:50,cache2:5
export MOZ_LOG_FILE=/tmp/cache-log.txt
cd /usr/bin/
./firefox
