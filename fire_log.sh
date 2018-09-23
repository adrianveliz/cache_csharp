#!/bin/bash
export MOZ_LOG=timestamp,cache2:5
export MOZ_LOG_FILE=/tmp/cache-log.txt
~/Downloads/firefox/firefox & # currently testing with firefox 56
