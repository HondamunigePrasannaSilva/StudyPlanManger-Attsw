# Study plan manager

[![Java CI with Maven in Linux](https://github.com/HondamunigePrasannaSilva/StudyPlanManger-Attsw/actions/workflows/maven.yml/badge.svg)](https://github.com/HondamunigePrasannaSilva/StudyPlanManger-Attsw/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/HondamunigePrasannaSilva/StudyPlanManger-Attsw/badge.svg?branch=master)](https://coveralls.io/github/HondamunigePrasannaSilva/StudyPlanManger-Attsw?branch=master)

### Sonar cloud
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=bugs)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=coverage)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=HondamunigePrasannaSilva_StudyPlanManger-Attsw&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=HondamunigePrasannaSilva_StudyPlanManger-Attsw)


Project for Advanced techniques and tools for software development (ATTSW) course at University of Florence (Unifi) Prof.Lorenzo Bettini.

## Project structure
```
\StudyPlanManager-Attsw
├── .github/workflows       - contains the github action workflows yaml file
├── attsw-silva             
│    └── src
│   	  ├── e2e/          - contains end to end tests
│   	  ├── it/           - contains integration tests
│   	  ├── test/	    - contains unit tests 
│         └── main/
├── mongo-init              - contains json file of students and courses to init the mongodb 
│   ├── course.json             
│   └── student.json
├── docker-compose.yml	    - Neccessary for a demo
└── readme.md
```
## Demo
If you want to interact with the application, you must first run the Docker Compose file to start a MongoDB server with a database initialized using the data inside the mongo-init folder. The server is started at the address `0.0.0.0` with access to port `27019`.

``` 
docker-compose up
```


Navigate to the `attsw-silva` folder and execute the following command:

``` 
mvn clean package
 ```

to create the JAR files.

Then, you can run the following command:
```
java -jar target/attsw-silva-0.0.1-SNAPSHOT.jar --mongo-host=0.0.0.0 --mongo-port=27019
```

## Test Instructions

For testing purposes, navigate to the `attsw-silva` folder and run the command:

```
mvn clean verify
```

The Docker containers for integration and end-to-end tests will be launched automatically by Maven.


