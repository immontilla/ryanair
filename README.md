# Ryanair - Task 2 - Java/Spring - Interconnecting Flights

### Task
Write a Spring MVC based RESTful API application which serves information about possible direct and interconnected flights (maximum 1 stop) based on the data consumed from external APIs.
The application can consume data from the following two microservices:
- *<a href="https://api.ryanair.com/core/3/routes" title="Routes API">Routes API</a>*: 
which returns a list of all available routes based on the airport's IATA codes. Please note that only routes with empty connectingAirport should be used (value set to null).
- *<a href="https://api.ryanair.com/timetable/3/schedules/{departure}/{arrival}/years/{year}/months/{month}" title="Schedules API">Schedules API</a>*: 
which returns a list of available flights for a given departure airport IATA code, an arrival airport IATA code, a year and a month.

### Tools
- Spring Boot 2.0
- Swagger UI
- Ehcache
### Solution

I've built the solution, using a Spring Boot 2.0 project. I've added a Cache manager to save the Route API response. Also, I've documented the API using Swagger.

To build and run the project. Run this command:

```
mvn clean package spring-boot:run
```

To read the API documentation, open a web browser at http://localhost:8765/swagger-ui.html.

To use the API, you can:
- Open a web browser at http://localhost:8765/swagger-ui.html#/main-controller/flightResultsUsingGET and click on "Try it out".
- Use curl.
**Remember** to escape the **:** character with **%3A** on **departureDateTime** and **arrivalDateTime parameters**.

```
curl -i -X GET "http://localhost:8765/api/interconnections?departure=DUB&arrival=BCN&departureDateTime=2018-06-01T06%3A15&arrivalDateTime=2018-06-15T21%3A15" -H  "accept: application/json" && echo ''
```

