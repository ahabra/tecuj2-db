# tecuj2-db 

* Initially created on Sun Oct 10 14:27:01 EDT 2021

A simple JDBC wrapper to simplify database interactions in Java.
This used to a part of https://github.com/ahabra/tecuj2, but moved to its own library.

## Programmer's Guide

The following sections will show some examples.

### Initial Assumptions
Assume that we have the following `Book` Java class:

```java
class Book {
	public long id;
	public String title;
	public String authorName;
	public String isbn;
	public double price;
}
```

Assume that there is a database table named `book` that will store our data. 

Assume also that there is a database which can be reached using the following:

* URL: `jdbc:hsqldb:mem:tecuj-test`
* User: `bob`
* Password: `builder`

### DB Connection Setup
To establish a database connection:

```java
DbConnection dbConnection = new DbConnection()
				.url("jdbc:hsqldb:mem:tecuj-test")
				.user("bob").password("builder");
```

Note that the `DbConnection` class has `connect()` and `close()` methods which
can be invoked either explicitly by you, or implicitly when you do read/write 
operations. 

### Reading From a Table
This is an example for reading from the `book` table:

```java
import com.tek271.util2.db.DbConnection;
import com.tek271.util2.db.DbReader;

class BookDao {

	DbConnection getConnection() {
		return new DbConnection()
				.url("jdbc:hsqldb:mem:tecuj-test")
				.user("bob").password("builder");
	}

	List<Book> readBooks(String authorName) {
		DbConnection dbConnection = getConnection();

		DbReader<Book> dbReader = new DbReader<>(dbConnection, Book.class);
		return dbReader.sql("select * from book where author_name = :authorName")
				.param("authorName", authorName)
				.read();
	}

}
```

Note how we do not have to explicitly open and close the DB connection.

### Insert To a Table
Let's see how to add a new book

```java
import com.tek271.util2.db.DbWriter;

class BookDao {

	Book addBook(Book book) {
		String sql = "insert into Book (title, author_name, isbn, price) " +
				"values (:title, :authorName, :isbn, :price)";

		DbWriter dbWriter = new DbWriter(getConnection());
		long id = dbWriter.sql(sql)
				.param("title", book.title)
				.param("authorName", book.authorName)
				.param("isbn", book.isbn)
				.param("price", book.price)
				.writeAndReturnNewKey();
		book.id = id;
		return book;
	}

}

```

### Update a Row in a Table
Let's add a method to change the price of a book by a given percent

```java
import com.tek271.util2.db.DbConnection;
import com.tek271.util2.db.DbReader;
import com.tek271.util2.db.DbWriter;

class BookDao {

	Book changePrice(long bookId, int changePercent) {
		try (DbConnection dbConnection = getDbConnection().connect();) {
			List<Book> books = new DbReader<>(dbConnection, Book.class)
					.sql("select * from book where id = :id")
					.param("id", bookId)
					.read();
			Book book = books.get(0);
			book.price = book.price + (changePercent * book.price) / 100;

			new DbWriter(dbConnection)
					.sql("update book set price= :price where id= :id")
					.param("id", bookId)
					.param("price", book.price)
					.write();
			return book;
		}
	}

}
```
The above example is not ideal, we could have done the update using a single SQL query, but it is 
instructional. Note the explicit invocation of the `connect()` method, and how it is used with
a `try-with-resources` to make sure the connection is actually closed. In this example a single
database connection is used for both queries. 

### Transactions
The `DbConnection` class has the following methods to support transactions:

* `transaction()`: begin a transaction.
* `commit()`: commit current transaction.
* `rollback()`: Roll back current transaction

### Write Scripts
Let's denote `INSERT`, `UPDATE`, `DELETE`, and `CREATE` statements as 'write' statements.

Sometimes we have a group of 'write' statements that we want to run them all. This happens often
when we have table/schema definitions and initialization.

A script is a sequence of 'write' statements separated by a semicolon and a new line.

The `DbWriter` class has the following methods to run scripts:

* `writeScript(String script)`: Run the given script
* `writeScriptFromFile(String fileName)`: Run the script inside the given fileName

### Named Queries
In a non-trivial database app, there could be many SQL queries to run. You can always include
the SQL's strings in the program as needed, but this can get long and ugly.

The A`DbNamedQueries` class provides a simplification. It can read a list of named queries from a
YAML file, which allows us to use these queries when we do read/write operations.

Here is an example of named queries YAML file:

```yaml
# books.yml

findBookById: >
  select * from book where id = :id

findBookByAuthorName: >
  select * from book where author_name = :authorName

insertBook: >
  insert into Book (title, author_name, isbn, price)
  values (:title, :authorName, :isbn, :price)

```

And to use it in our Java code:

```java
import com.tek271.util2.db.DbNamedQueries;

class BookDao {

	Book findById(long id) {
		DbNamedQueries namedQueries = new DbNamedQueries().readFile("books.yml");
		List<Book> books = new DbReader<>(getConnection(), Book.class)
				.sql(namedQueries.get("findBookById"))
				.param("id", id)
				.read();
		return books.get(0);
	}

}
```

Note that in a production environment, you should avoid reading the named queries file
on each query. You should consider caching the DbNamedQueries object, and read
from it as needed.

## Change History

1. 2021.10.17 - Version 0.1.0: First public release 