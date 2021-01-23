# Crunch
A library for compiling and evaluating mathematical expressions with variables

# Installation for Development

Crunch can be accessed via JitPack using Gradle or Maven. Read the section for whichever you use below.

## Gradle:

```		
repositories {
	maven { url 'https://jitpack.io' }
}

```

```
dependencies {
	implementation 'com.github.Redempt:Crunch:-SNAPSHOT'
}
```

## Maven:

```
<repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
```

```
<dependency>
	<groupId>com.github.Redempt</groupId>
	<artifactId>Crunch</artifactId>
	<version>-SNAPSHOT</version>
</dependency>
```

# Usage

Crunch offers a better solution to evaluating complex mathematical expressions than using ScriptManager. It is simple to use, performant, and lightweight.

There are only a handful of methods you will need to use in Crunch. To compile an expression, simply call `Crunch#compileExpression`. Here's an example:

```java
CompiledExpression exp = Crunch.compileExpression("1 + 1");
exp.evaluate(); //This will return 2
```

You can use all the basic operations you're familiar with. If you want to see a list of all supported operations, check the [Operator](https://github.com/Redempt/Crunch/blob/master/src/redempt/crunch/Operator.java) enum.

Variables can also be used with Crunch. They must be numbered, starting with 1. When calling `evaluate` on a CompiledExpression with variables, you must pass them in order of index.

```java
CompiledExpression exp = Crunch.compileExpression("$1 / $2");
exp.evaluate(27, 3); //This will return 9
```

Spaces are ignored entirely, so if you don't feel the need to add them, you may remove them.

In the case that you only need to evaluate an expression once and never again, you can use `Crunch#evaluateExpression`:

```java
int exampleVar = 50;
Crunch.evaluateExpression("abs(3 - $1)", exampleVar);
```

However, if the expression will be used more than once, it is highly recommended to keep it as a `CompiledExpression` instead.

CompiledExpressions are NOT thread-safe, and may have issues if `evaluate` is called from multiple threads at the same time. For multi-threaded purposes, please mutex your CompiledExpression or clone it with `CompiledExpression#clone` and pass it off to another thread.
