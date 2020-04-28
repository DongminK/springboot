#!/bin/bash

#echo $# arguments

if [ "$#" -ne 1 ]; then
	echo [USAGE] ./mariadb_console.sh {id} 
	exit 2
fi

ROOT_PATH=/home/onm/omw/database
$ROOT_PATH/mariadb/bin/mysql --socket=$ROOT_PATH/mariadb.sock -u$1 -p
