# Ryanair - Task 2 - Java/Spring - Interconnecting Flights

A Spring MVC based RESTful API application which serves information about possible direct and interconnected flights (maximum 1 stop) based on the data consumed from external APIs.

Requirements:
- The application should response to following request URI with given query parameters:
http://localhost:8765/api/interconnections?departure={departure}&arrival={arrival}&departureDateTime={departureDateTime}&arrivalDateTime={arrivalDateTime} where:
- departure - a departure airport IATA code
- departureDateTime - a departure datetime in the departure airport timezone in ISO format
- arrival - an arrival airport IATA code
- arrivalDateTime - an arrival datetime in the arrival airport timezone in ISO format
For example:
http://localhost:8765/api/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00

The application should return a list of flights departing from a given departure airport not earlier than the specified departure datetime and arriving to a given arrival airport not later than the specified arrival datetime. The list should consist of:
- All direct flights if available (for example: DUB - WRO)
- All interconnected flights with a maximum of one stop if available (for example: DUB - STN - WRO)
- For interconnected flights the difference between the arrival and the next departure should be 2h or greater
