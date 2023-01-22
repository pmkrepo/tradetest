# Trade Store Application
## Assumptions 
As this is a test assignment, following assumption are made based on FAQ in the problem statement
1. Input - Trades can be input from command line console (details below)
in the real application trades can come from REST service Or via async messaging system.
e.g. Springboot @RestController can be used to create REST APi 
2. Store - Trades are stored in in-memory datastrcutre. TreeSet is used as it sorts the data. (TreeMap can also be used)
in the real application trades can be stored in external SQL or NOSQL database to spport persistence and scale.
in that case TradeDao interface need to be implemented which can interact with the database

# Command Line input
1. Start the java application (App)
2. It asks "Do you want to add pre-defined test data? (Y/N): "
Inout Y so that trade store is initialized with some pre-defined data 
It also test validations by inputting some data which fails the validations
It also demonstrates thread expiry scheduler which sets expiry as true when maturity date and time passes.
3. After that there are 3 options 
"Please run the command: add | show | exit"
- add - add a new trade - it will ask to input each field needed by the trade
= show - display all the trades in the store
- exit - exit the application 

