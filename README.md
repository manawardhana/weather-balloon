# Weather Observations
## Prerequisites
- Oracle JDK 1.8.0
- Scala 2.12.1

## Installation & usage
- Change the current directory to the project directory
- Run the following command in the linux shell

```bash
$sbt clean run
```
- Follow the on-screen instructions

## Notes on the scope selection
- Distance travelled calculation has not been implemented. If the order of data is not guaranteed, they should be ordered by the time to track the movements of the balloon. Sorting bulk data (that doesn't fit in memory) is somewhat challenging. One strategy would be to use database with time indexing.
- The current form of the code base can be refactored and simplified further.
- Most of the error handling scenarios have been compromised.
- Unit tests have not being done