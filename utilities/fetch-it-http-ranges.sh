#!/bin/bash

# Default -n to 1
NUM_PROCS=1

while getopts 'hn:' OPTIONS
do
    case $OPTIONS in
    n)  NUM_PROCS="$OPTARG"
        ;;
    h)  printf "Usage: %s [-n <num>] <url>\n" $(basename $0) >&2
        exit 1
        ;;
    esac
done
shift $(($OPTIND - 1))

BAGURL=$1
shift
if [ -z "$BAGURL" ] ; then
    echo "Missing bag URL."
    exit 1
fi

CURL=$(which curl)
if [ -z "$CURL" ] ; then
    echo "Cannot find curl!"
    exit 2
fi

$CURL -I $BAGURL 2> /dev/null | sed -e 's/.$//' > .headers.$$
HTTP_VER=$(awk '/^HTTP/ { print $1 }' < .headers.$$)
HTTP_STATUS=$(awk '/^HTTP/ { print $2 }' < .headers.$$)
HTTP_TEXT=$(awk '/^HTTP/ { print $3 }' < .headers.$$)
BAG_SIZE=$(awk '/^Content-Length:/ { print $2 }' < .headers.$$)
ACCEPT_RANGES=$(awk '/^Accept-Ranges:/ { print $2 }' < .headers.$$)
rm .headers.$$

if [ "$HTTP_VER" != "HTTP/1.1" ] ; then
    echo "I can only work with HTTP Version 1.1: $HTTP_VER"
    exit 3
fi

if [ "$HTTP_STATUS" != "200" ] ; then
    echo "URL returned an unexpected code: $HTTP_STATUS $HTTP_TEXT"
    exit 3
fi

if [ "$ACCEPT_RANGES" != "bytes" ] ; then
    echo "I can only do Accept-Ranges for bytes: $ACCEPT_RANGES"
    exit 3
fi

# Now we should be good to go.
trap 'for p in $(jobs -p); do kill $p ; done' 2

SPLIT_SIZE=$(expr $BAG_SIZE / $NUM_PROCS)

PROC=0
for (( ; PROC < $NUM_PROCS - 1 ; PROC++ )) ; do
    $CURL -o .bag.${PROC}.$$ -r $(expr $PROC * $SPLIT_SIZE)-$(expr $(expr $PROC + 1) * $SPLIT_SIZE - 1) $BAGURL 2> /dev/null &
done

$CURL -o .bag.${PROC}.$$ -r $(expr $PROC * $SPLIT_SIZE)- $BAGURL 2> /dev/null &

wait

FINAL_BAG_NAME=$(basename $BAGURL)

for (( PROC=0 ; PROC < $NUM_PROCS ; PROC++ )) ; do
    cat .bag.${PROC}.$$ >> $FINAL_BAG_NAME.$$
    rm .bag.${PROC}.$$
done

mv $FINAL_BAG_NAME.$$ $FINAL_BAG_NAME

