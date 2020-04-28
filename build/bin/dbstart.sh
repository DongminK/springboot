#!/bin/bash

ROOT_PATH=/home/onm/omw/database

$ROOT_PATH/mariadb/bin/mysqld --no-defaults --console --max_allowed_packet=64M --basedir=$ROOT_PATH/mariadb --datadir=$ROOT_PATH/datas --port=13306 --socket=$ROOT_PATH/mariadb.sock --user=onm --character-set-server=utf8 --default-time-zone=+9:00 --lower-case-table-names=1 &
