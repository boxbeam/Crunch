# Crunch
The fastest Java expression compiler/evaluator

Support Discord: https://discord.gg/agu5xGy2YZ

# Installation for Development

Crunch can be accessed via my build server using Gradle or Maven. Read the section for whichever you use below.

## Gradle:

```		
repositories {
	maven { url 'https://redempt.dev' }
}

```

```
dependencies {
	implementation 'com.github.Redempt:Crunch:Tag'
}
```

Replace `Tag` with a version, like `1.0`.

## Maven:

```
<repository>
	<id>redempt.dev</id>
	<url>https://redempt.dev</url>
</repository>
```

```
<dependency>
	<groupId>com.github.Redempt</groupId>
	<artifactId>Crunch</artifactId>
	<version>Tag</version>
</dependency>
```

Replace `Tag` with a version, like `1.0`.

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

You're also able to define lazy variables, which don't need to be passed as arguments to `evaluate`:

```java
EvaluationEnvironment env = new EvaluationEnvironment();
env.addLazyVariable("x", () -> 4);
CompiledExpression exp = Crunch.compileExpression("x + 1", env);
exp.evaluate(); //This will return 5
```

In the case that you only need to evaluate an expression once and never again, you can use `Crunch#evaluateExpression`:

```java
int exampleVar = 50;
Crunch.evaluateExpression("abs(3 - $1)", exampleVar);
```

However, if the expression will be used more than once, it is highly recommended to keep it as a `CompiledExpression` instead.

CompiledExpressions are NOT thread-safe, and may have issues if `evaluate` is called from multiple threads at the same time. For multi-threaded purposes, please mutex your CompiledExpression or clone it with `CompiledExpression#clone` and pass it off to another thread.

# Performance

Performance is one of the largest benefits of using Crunch. It is designed to be extremely performant, and lives up to that expectation. For cases where you need to perform a lot of evaluations quickly from a string-compiled mathematical expression, Crunch is the best option.

Here I will compare the runtimes of Crunch against two similar librararies: [EvalEx](https://github.com/uklimaschewski/EvalEx) and [exp4j](https://github.com/fasseg/exp4j). I will compare both compilation times and evaluation times.

CPU: AMD Ryzen 7 5800X

Benchmark source: https://github.com/Redempt/CrunchBenchmark

## Compilation
Simple expression: `3*5`
Complex expression: `6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4`
```
Benchmark                                        Mode      Score     Error  Units
CompileBenchmark.crunchCompileComplexExpression  avgt      2.974 ±   0.033  us/op
CompileBenchmark.crunchCompileSimpleExpression   avgt      0.050 ±   0.001  us/op
CompileBenchmark.evalExCompileComplexExpression  avgt     38.450 ±   0.526  us/op
CompileBenchmark.evalExCompileSimpleExpression   avgt      9.156 ±   0.256  us/op
CompileBenchmark.exp4jCompileComplexExpression   avgt      3.464 ±   0.026  us/op
CompileBenchmark.exp4jCompileSimpleExpression    avgt      0.276 ±   0.009  us/op
```

## Evaluation

Simple expression: `(10*x)+5/2`
Constant expression: `6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4`

```
Benchmark                                        Mode      Score     Error  Units
EvalBenchmark.crunchConstantEval                 avgt      0.823 ±   0.020  ns/op
EvalBenchmark.crunchSimpleEval                   avgt      4.296 ±   0.058  ns/op
EvalBenchmark.evalExConstantEval                 avgt  26156.342 ± 183.188  ns/op
EvalBenchmark.evalExSimpleEval                   avgt   2283.572 ±  19.630  ns/op
EvalBenchmark.exp4jConstantEval                  avgt    540.194 ±   4.434  ns/op
EvalBenchmark.exp4jSimpleEval                    avgt     44.727 ±   0.554  ns/op
```

# Operations and Syntax

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

`cbrt` - Get the cube root of a number (`cbrt(8)`)

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

`=` - Compare if two numbers are equal (`1 = 1` will be `1`, `1 = 3` will be `0`), also accepts `==`

`!=` - Compare if two numbers are not equal (`1 != 2` will be `1`, `1 != 1` will be `0`)

`>` - Compare if one number is greater than another (`1 > 0`)

`<` - Compare if one number is less than another (`0 < 1`)

`>=` - Compare if one number is greater than or equal to another (`1 >= 1`)

`<=` - Compare if one number is less than or equal to another (`0 <= 1`)

`|` - Boolean or (`true | false`), also accepts `||`

`&` - Boolean and (`true & true`), also accepts `&&`

`!` - Boolean not/inverse (`!true`)
