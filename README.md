# SqlDumper

A Java libaray to dump MySQL databases, without binary dependencies.

Usage:
```java
SqlDumper sqlDumper = new SqlDumper("jdbc:mysql://localhost:3306/...");
sqlDumper.dump(System.out);
```
