# spring-starter
[![Build Status](https://travis-ci.org/perusworld/spring-starter.svg?branch=master)](https://travis-ci.org/perusworld/spring-starter)
[![codecov](https://codecov.io/gh/perusworld/spring-starter/branch/master/graph/badge.svg)](https://codecov.io/gh/perusworld/spring-starter)

Spring Starter

## Development

### Startup Core Services Before Dev
```bash
cd docker
docker-compose -f core-services-docker-compose.yml build
docker-compose -f core-services-docker-compose.yml up -d
```
### Stop Core Services
```bash
cd docker
docker-compose -f core-services-docker-compose.yml stop
```
### Test
```bash
mvn compile test
```
### Startup Backend Services During UI Dev
```bash
mvn package
cd docker
docker-compose -f services-docker-compose.yml build
docker-compose -f services-docker-compose.yml up -d
```
### Stop Backend Services During UI Dev
```bash
cd docker
docker-compose -f services-docker-compose.yml stop
```
### Startup Web
```bash
mvn package
cd docker
docker-compose -f web-docker-compose.yml build
docker-compose -f web-docker-compose.yml up -d
```
### Stop Web
```bash
cd docker
docker-compose -f web-docker-compose.yml stop
```

### Dev Instance URLs

| Service | URL | UID/PWD | Health |
| ------------- | ------------- | :-----: | --- |
| Rabbit MQ | [http://localhost:15672/](http://localhost:15672/) | guest/guest | |
| Postgres Admin | [http://localhost:5431](http://localhost:5431) | user@db.com/pwd | |
| Mongo Admin | [http://localhost:27016](http://localhost:27016) | | |
| Sample REST Service | [http://localhost:8081/](http://localhost:8081/) | | [http://localhost:9081/actuator/health](http://localhost:9081/actuator/health) |
| Sample AMQP Service | | | [http://localhost:9083/actuator/health](http://localhost:9083/actuator/health) |
| Sample Spring Web | [http://localhost:8082/](http://localhost:8082/) | | [http://localhost:9082/actuator/health](http://localhost:9082/actuator/health) |


## Documentation
[html version](documentation/sample-rest-service/index.html), [pdf version](documentation/sample-rest-service/index.pdf), [postman collection](documentation/sample-rest-service/postman-collection.json), [insomnia collection](documentation/sample-rest-service/insomnia-collection.json), [swagger](documentation/sample-rest-service/swagger.json)

### Generate Documentation
Requires [wkhtmltopdf](https://wkhtmltopdf.org/index.html), [jq](https://stedolan.github.io/jq/), [restdocs-to-postman](https://github.com/fbenz/restdocs-to-postman), [swaggymnia](https://github.com/mlabouardy/swaggymnia)

```bash
mvn clean package
cd  documentation/sample-rest-service
cp ../../sample-rest-service/target/generated-docs/index.html .
wkhtmltopdf index.html index.pdf
cd  ../../
cd sample-rest-service/target
restdocs-to-postman --input generated-snippets --export-format postman --determine-folder secondLastFolder --replacements ../../documentation/config/replacements.json --output ../../documentation/sample-rest-service/postman-collection.json
restdocs-to-postman --input generated-snippets --export-format insomnia --determine-folder secondLastFolder --replacements ../../documentation/config/replacements.json --output ../../documentation/sample-rest-service/insomnia-collection.json
cd  ../../documentation/sample-rest-service
jq '.info.name="Sample REST Service - Postman"' postman-collection.json > postman-collection.tmp
jq '.item[0].name="Sample Requests"' postman-collection.tmp > postman-collection.json
rm postman-collection.tmp
jq '.resources[0].name="Sample REST Service - Insomnia Sample Requests"' insomnia-collection.json > insomnia-collection.tmp
mv insomnia-collection.tmp insomnia-collection.json
swaggymnia generate -insomnia insomnia-collection.json -config ../config/swaggymnia.json -output json
cd  ../../
```