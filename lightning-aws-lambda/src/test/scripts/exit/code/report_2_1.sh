#!/bin/bash

aws lambda invoke \
    --function-name Lightning \
    --region eu-west-2 \
    --payload '{"region": "eu-west-2","bucket": "automatictester.co.uk-lightning-aws-lambda","mode": "report","jmeterCsv": "csv/jmeter/2_transactions_1_failed.csv"}' \
    response.json \
    &> /dev/null

OUT=`cat response.json | jq -r '.exitCode'`

echo -e ''; echo `basename "$0"`

if [ $OUT = 1 ];then
    echo "EXIT CODE = $OUT"
    echo "TEST PASSED"
    exit 0
else
    echo "EXIT CODE = $OUT"
    echo "TEST FAILED"
    exit 1
fi