#!/bin/bash
docker compose down -v
rm -rf ./data/mysql
rm -rf ./data/kafka
docker compose up --build
