#!/bin/bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE ROLE dbuser WITH LOGIN PASSWORD 'dbpwd'; 
    CREATE DATABASE userdb;
    GRANT ALL PRIVILEGES ON DATABASE userdb TO dbuser;
EOSQL