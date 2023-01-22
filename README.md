# Trade Store Application
## Assumptions 
As this is a test assignment, following assumption are made based on FAQ in the problem statement
### 1. Input
Trades can be input from command line console (details below)
(in the real application trades can come from REST service Or via async messaging system. 
e.g. Springboot @RestController can be used to create REST API)
### 2. Store 
Trades are stored in in-memory datastrcutre. TreeSet is used as it sorts the data. (TreeMap can also be used)
(in the real application trades can be stored in external SQL or NOSQL database to spport persistence and scale.
in that case TradeDao interface need to be implemented which can interact with the database.
Dates are stored as millisecond timestamps. While reading or displaying they are converted to readable format.)
### 3. Trade Expiry
Trade Expirey service runs as a scheduled task. its run rate need be injected to TradeService
as this is test assignment, in order to test it is started with 3 second delay (first run) and 10 second rate (subsequent runs).
(In real application we can use @Scheduled spring annotation to schedule a run.
### 4. Output
All the trades in the system are stored and displayed by tradeId (ascending) and then tradeVersion (descending)

## Command Line input
### 1. Start the java application (App class)
### 2. It asks "Do you want to add pre-defined test data? (Y/N): "
Input "Y" or "y"  so that trade store is initialized with some pre-defined data 
It also test validations by inputting some data which fails the validations
It also demonstrates thread expiry scheduler which sets expiry as true when maturity date and time passes.
### 3. After that there are 3 options 
"Please run the command: add | show | exit"
- add - add a new trade - it will ask to input each field needed by the trade.
- show - display all the trades in the store (Sorted with tradeId (asecnding), tradeVersion (descending)
- exit - exit the application.

## JUNIT Tests
### 1. Integration Tests
All integration tests are in present in TreadeIntegrationTest class.
They test the flow of the data through the system.
### 2. Unit tests
All other tests are unit tests and they test single class.

## Design Patterns
### 1. Dependnecy Injection
DAO, Valdiation chain are injected into TradeService.
Currently Spring is not used but when used so main application need to inject at startup.
When IOC like Spring used, they can be injected automatically using @Autowired.
### 2, Chain of Responsibility
Validations use chain of responsibility pattern so that valdiations can be added and removed from the chain without impacting the client (TradeService)
### 3. Data Access Object
Decouple data access from application so that it can be changed without impacting the main application




