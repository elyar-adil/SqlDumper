# SqlDumper

A pure Java library to dump MySQL databases, without using binary dependencies like mysqldump.

Usage:
```java
SqlDumper sqlDumper = new SqlDumper("jdbc:mysql://localhost:3306/...");
sqlDumper.dump(System.out);
```
