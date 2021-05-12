# Crunch
The fastest Java library for compiling and evaluating mathematical expressions with variables

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
	implementation 'com.github.Redempt:Crunch:master-SNAPSHOT'
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
	<version>master-SNAPSHOT</version>
</dependency>
```

# Usage

Crunch offers a better solution to evaluating complex mathematical expressions than using ScriptManager. It is simple to use, performant, and lightweight.

There are only a handful of methods you will need to use in Crunch. To compile an expression, simply call `Crunch#compileExpression`. Here's an example:

```java
CompiledExpression exp = Crunch.compileExpression("1 + 1");
exp.evaluate(); //This will return 2
```

You can use all the basic operations you're familiar with. If you want to see a list of all supported operations, check the [Operator](https://github.com/Redempt/Crunch/blob/master/src/redempt/crunch/Operator.java) enum, or the Operations section below.

Variables can also be used with Crunch. They must be numbered, starting with 1, and preceded by a `$`. This is part of what makes Crunch so performant. If you need named variables, however, you can specify names for them with an `EvaluationEnvironment`. When calling `evaluate` on a CompiledExpression with variables, you must pass them in order of index.

```java
CompiledExpression exp = Crunch.compileExpression("$1 / $2");
exp.evaluate(27, 3); //This will return 9
```

Spaces are ignored entirely, so if you don't feel the need to add them, you may remove them.

You can also define your own functions fairly simply:

```java
EvaluationEnvironment env = new EvaluationEnvironment();
//                name  # args   lambda to do logic
env.addFunction("mult", 2, (d) -> d[0] * d[1]);
CompiledExpression exp = Crunch.compileExpression("mult(2, 3)", env);
exp.evaluate(); //This will return 6
```

With an EvaluationEnvironment, you're also able to specify names for your variables:

```java
EvaluationEnvironment env = new EvaluationEnvironment();
env.setVariableNames("x", "y");
CompiledExpression exp = Crunch.compileExpression("x - y", env);
exp.evaluate(3, 4); //This will return -1
```

The values for the variables must be passed in the same order that you passed the variable names in.

In the case that you only need to evaluate an expression once and never again, you can use `Crunch#evaluateExpression`:

```java
int exampleVar = 50;
Crunch.evaluateExpression("abs(3 - $1)", exampleVar);
```

However, if the expression will be used more than once, it is highly recommended to keep it as a `CompiledExpression` instead.

CompiledExpressions are NOT thread-safe, and may have issues if `evaluate` is called from multiple threads at the same time. For multi-threaded purposes, please mutex your CompiledExpression or clone it with `CompiledExpression#clone` and pass it off to another thread.

# Performance

Performance is one of the largest benefits of using Crunch. It is designed to be extremely performant, and lives up to that expectation. For cases where you need to perform a lot of evaluations quickly from a string-compiled mathematical expression, Crunch is the best option.

Here I will compare the runtimes of Crunch against three similar librararies: [EvalEx](https://github.com/uklimaschewski/EvalEx), [exp4j](https://github.com/fasseg/exp4j), and [java.math.expression.parser](https://github.com/sbesada/java.math.expression.parser). I will compare both compilation times and evaluation times.

CPU: AMD Ryzen 5 2600

## Compilation

Expression: `3*5`

| Crunch | EvalEx | exp4j  | java.math.expression.parser |
|:-------|--------|--------|-------|
|22μs    |502μs   |64μs    |48μs|

Expression: `6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4`

| Crunch | EvalEx | exp4j | java.math.expression.parser |
|:-------|--------|-------|---------|
|532μs   |559μs   |811μs  |687μs|

As you can see, Crunch is much faster at compiling short expressions, and stacks up pretty evenly with long expressions. EvalEx is actually misleading here, though, as it doesn't compile an expression when its `Expression` class is initialized - it doesn't touch the string at all until evaluate is called. Even so, Crunch can compile this long expression in the time it takes EvalEx to initialize its operators without even touching the string. java.math.expression.parser and exp4j are both roughly comparable to Crunch for compilation times here, moreso for longer expressions.

## Evaluation

The times shown below are for 10,000 evaluations.

Expression: `6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4`

| Crunch | EvalEx | exp4j  | java.math.expression.parser |
|:-------|--------|--------|----------|
|504μs   |24,621μs|30,778μs|279,162μs |

Crunch has an unfair advantage in this scenario, though, since it simplifies expressions using only constants where possible. Since this expression is made of only constants, Crunch will reduce it to a single constant value rather than running through all of the values every time. java.math.expression.parser doesn't seem to have a way to compile an expression, so it essentially has to start from scratch for each evaluation.

So now for a more realistic test: A simple expression using a variable.

Expression: `3*x`


| Crunch | EvalEx | exp4j | java.math.expression.parser |
|:-------|--------|-------|-----------|
|1,315μs |31,654μs|9,495μs|62,581μs |

In both cases, Crunch is about 25-50 times faster than EvalEx for evaluation, and 7-60 times faster than exp4j. There is no reasonable comparison to java.math.expression.parser because it cannot truly compile expressions.

# Operations and Syntax

Implicit multiplication - `xy` is identical to `x*y`, `3x` is identical to `3*x`, `3(4)` is identical to `3*(4)`, `3cosx` is identical to `3*cosx`, etc.

`()` - Create a parenthetical expression which will be evaluated first (`3 * (4 + 1)`)

`$` - Denotes a variable (`$1 / 3`)

`e` - Euler's constant (`log(e)`)

`pi` - pi (`sin(pi)`)

`+` - Add two numbers (`1 + 1`)

`-` - Subtract two numbers, or negate one (`3-2`, `-(4+2)`)

`/` - Divide two numbers (`3 / 4`)

`*` - Multiply two numbers (`2 * 3`)

`^` - Raise one number to the power of another (`3^3`)

`%` - Take the modulus, or division remainder, of one number with another (`7 % 4`)

`abs` - Take the absolute value of a number (`abs$1`, `abs-1`)

`round` - Rounds a number to the nearest integer (`round1.5`, `round(2.3)`)

`ceil` - Rounds a number up to the nearest integer (`ceil1.05`)

`floor` - Rounds a number down to the nearest integer (`floor0.95`)

`rand` - Generate a random number between 0 and the specified upper bound (`rand4`)

`log` - Get the natural logarithm of a number (`log(e)`)

`sqrt` - Get the square root of a number (`sqrt4`)

`sin` - Get the sine of a number (`sin$2`)

`cos` - Get the cosine of a number (`cos(2*pi)`)

`tan` - Get the tangent of a number (`tanpi`)

`asin` - Get the arcsine of a number (`asin$2`)

`acos` - Get the arccosine of a number (`acos0.45`)

`atan` - Get the arctangent of a number (`atan1`)

`sinh` - Get the hyperbolic sine of a number (`sinh(4)`)

`cosh` - Get the hyperbolic cosine of a number (`sinh(4)`)

`true` - Boolean constant representing 1

`false` - Boolean constant representing 0

`=` - Compare if two numbers are equal (`1 = 1` will be `1`, `1 = 3` will be 0)

`>` - Compare if one number is greater than another (`1 > 0`)

`<` - Compare if one number is less than another (`0 < 1`)

`>=` - Compare if one number is greater than or equal to another (`1 >= 1`)

`<=` - Compare if one number is less than or equal to another (`0 <= 1`)

`|` - Boolean or (`true | false`)

`&` - Boolean and (`true & true`)

`!` - Boolean not/inverse (`!true`)
