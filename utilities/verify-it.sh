#!/bin/bash

# Default -n to 1
NUM_PROCS=1

while getopts 'hn:' OPTIONS
do
    case $OPTIONS in
    n)  NUM_PROCS="$OPTARG"
        ;;
    h)  printf "Usage: %s [-n <num>] <manifest>\n" $(basename $0) >&2
        exit 1
        ;;
    esac
done
shift $(($OPTIND - 1))

MANIFEST=$1
shift

if [ -z "$MANIFEST" ] ; then
    echo "Missing manifest file."
    exit 1
fi

pushd $(dirname $MANIFEST) > /dev/null
MANIFEST=$(basename $MANIFEST)

ALG=$(echo $MANIFEST | sed -e 's/manifest-//' | sed -e 's/.txt//')

# Split the manifest into several temp files, 
MANIFEST_LENGTH=$(wc -l < $MANIFEST)
SPLIT_SIZE=$(expr $MANIFEST_LENGTH / $NUM_PROCS + 1)
split -l $SPLIT_SIZE $(basename $MANIFEST) .manifest-$ALG-split.$$.
i=0

# Kick off an md5sum process for each split file, and collect the process ids.
for SPLIT_FILE in .manifest-$ALG-split.$$.*
do
    ${ALG}sum -c $SPLIT_FILE > $SPLIT_FILE.out &
    CHILD_PIDS[i]=$!
    ((i++))
done

trap 'for p in ${CHILD_PIDS[*]} ; do kill $p ; done' 2

# Wait for everyone to finish.
for p in ${CHILD_PIDS[*]}
do
    wait $p
done

# Collect the results, and look for any failures.
cat .manifest-$ALG-split.$$.*.out | grep -v OK > manifest-$ALG-bad.txt
rm -f .manifest-$ALG-split.$$.*

if [ $(stat -c %s manifest-$ALG-bad.txt) == 0 ]
then
    rm manifest-$ALG-bad.txt
else
    echo "Bad files saved to manifest-$ALG-bad.txt"
fi

popd > /dev/null
