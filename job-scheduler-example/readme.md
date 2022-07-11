
# readme

This is job-scheduler example.

# how to run

##### mysql docker

```
docker pull mysql:5.7
docker network create yangwk
docker run -d --network yangwk --name mysql -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 mysql:5.7
```

##### execute required SQL

job-scheduler-core/sql/create.sql


##### docker build example

```
cd job-scheduler
mvn clean package -DskipTests=true
mkdir ~/docker-build
cp job-scheduler-example/docker-entrypoint.sh ~/docker-build/
cp job-scheduler-example/Dockerfile ~/docker-build/
cp job-scheduler-example/target/job-scheduler-example-0.0.1-SNAPSHOT.jar ~/docker-build/
cd ~/docker-build/
docker build --tag job-scheduler-example .
```

##### docker run example

```
docker run --network yangwk --name job-scheduler-example-0 -p 8080:8080  job-scheduler-example
docker run --network yangwk --name job-scheduler-example-1 -p 8081:8080  job-scheduler-example
```

# example api

```
curl http://localhost:8080/JobScheduler/invoke?action=create\&name=HelloWorldJob
curl http://localhost:8080/JobScheduler/invoke?action=create\&name=DiscoveryJob
curl http://localhost:8081/JobScheduler/invoke?action=update\&name=DiscoveryJob
curl http://localhost:8081/JobScheduler/invoke?action=query\&name=DiscoveryJob
curl http://localhost:8081/JobScheduler/invoke?action=delete\&name=DiscoveryJob
```
