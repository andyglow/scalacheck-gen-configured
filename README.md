# Scalacheck `Gen[T]` configurator

[![Build Status](https://github.com/andyglow/scalacheck-gen-configured/actions/workflows/ci.yml/badge.svg)]()
[![codecov](https://codecov.io/gh/andyglow/scalacheck-gen-configured/branch/master/graph/badge.svg?token=LRRSC6V0RE)](https://codecov.io/gh/andyglow/scalacheck-gen-configured)
[![mvn](https://img.shields.io/badge/dynamic/json.svg?label=mvn&query=%24.response.docs%5B0%5D.latestVersion&url=https%3A%2F%2Fsearch.maven.org%2Fsolrsearch%2Fselect%3Fq%3Dscalacheck-gen-configured_2.13%26start%3D0%26rows%3D1)](https://search.maven.org/artifact/com.github.andyglow/scalacheck-gen-configured_2.13/)

With this project you can externalize `Gen[T]` configuration. 

When I needed to generate some amount of test data the first think appeared to my mind was to use `Gen[T]` 
from awesome `scalacheck` project. I have written first version of data generator really quickly. I have deployed then
into right place, started, checked my app started receiving test data. That was cool. 

Let me show you the example.

```scala
import java.time._
import org.scalacheck._

case class MyEvent(id: String, timeStamp: Instant, message: String)

val myEventGen = for {
  id        <- Gen.identifier
  timeStamp <- Gen.choose(0, Instant.now().toEpochMilli) map { Instant.ofEpochMilli }
  message   <- Gen.alphaNumStr
} yield MyEvent(id, timeStamp, message)
```       
The code above is pretty much what I have started with.
But then I realized that I need to change test data generation profile. I needed to replace some `Gen.const` with, 
say, `Gen.oneof` or `Gen.choose`. So, the only solution was to change the code and redeploy, restart, etc..

That is what always makes me unhappy.
 
So I thought, that would be helpful if I could change test data generation profile without real code change. 
Can we, for example, utilize environment variables to read generator definition from there?
In particular, can we do something like this
```scala
def genFor[T](name: String): Option[Gen[T]] = {
  sys.props.get(name) flatMap {
    ParseGen(_).fold(
      { err => log.warning(err); None },
      { gen => Some(gen) })
  }
}
    
val myEventGen = for {
  id        <- genFor[String] ("identifier") getOrElse Gen.identifier
  timeStamp <- genFor[Instant]("time-stamp") getOrElse Gen.choose(0, Instant.now().toEpochMilli) map { Instant.ofEpochMilli }
  message   <- genFor[String] ("message")    getOrElse Gen.alphaNumStr
} yield MyEvent(id, timeStamp, message)
```   

With this solution the only thing I need is to restart data generation with desired system properties set. 
(`-Didentifier=alphaNumStr -Dtime-stamp="range: 2018-04-04T00:00Z .. 2018-04-14T23:59Z"`) 

The project is in it's initial phase so do not expect too much form it yet please.

The only pretty strict amount of type supported yet.
- Most of scala numeric types (`Byte`, `Short`, `Int`, `Long`, `Float`, `Double`)
- `Char` and `String`
- `Boolean`
- Date time types 
    - `java.time.Instant`
    - `java.time.LocalDateTime`
    - `java.time.LocalDate`
    - `java.time.LocalTime`
    - `java.time.OffsetDateTime`
    - `java.time.ZonedDateTime`
    - `java.util.Date`
    - `java.util.Calendar`
    - `java.sql.Date`
    - `java.sql.Time`
- Duration types
    - `FiniteDuration`
    - `Duration`

## API
`ParseGen[T](defn: String): Either[String, Gen[T]]`

Examples
```scala
import com.github.andyglow.scalacheck._

val Right(idGen)        = ParseGen[String]("identifier: 16")
val Right(posLongGen)   = ParseGen[Long]("posNum")
val Right(timestampGen) = ParseGen[java.time.Instant]("range: 2018-01-01T00:00Z .. 2018-12-31T23:59Z")
```
    
## Definitions
Below is a table showing which definitions can be supported by types.  

|                      | Numeric types | Char | String | Boolean | Date types | Duration |
| -------------------- | ------------- | ---- | ------ | ------- | ---------- | -------- |
| `identifier`         |               |      | [x]    |         |            |          |
| `numChar`            |               | [x]  |        |         |            |          |
| `numStr`             |               |      | [x]    |         |            |          |
| `alphaUpperChar`     |               | [x]  |        |         |            |          |
| `alphaUpperStr`      |               |      | [x]    |         |            |          |
| `alphaLowerChar`     |               | [x]  |        |         |            |          |
| `alphaLowerStr`      |               |      | [x]    |         |            |          |
| `alphaChar`          |               | [x]  |        |         |            |          |
| `alphaStr`           |               |      | [x]    |         |            |          |
| `alphaNumChar`       |               | [x]  |        |         |            |          |
| `alphaNumStr`        |               |      | [x]    |         |            |          |
| `asciiChar`          |               | [x]  |        |         |            |          |
| `asciiStr`           |               |      | [x]    |         |            |          |
| `asciiPrintableChar` |               | [x]  |        |         |            |          |
| `asciiPrintableStr`  |               |      | [x]    |         |            |          |
| `posNum`             | [x]           |      |        |         |            |          |
| `negNum`             | [x]           |      |        |         |            |          |
| `const`              | [x]           | [x]  | [x]    | [x]     | [x]        | [x]      |
| `range`              | [x]           | [x]  |        |         | [x]        | [x]      |
| `oneof`              | [x]           | [x]  | [x]    | [x]     | [x]        | [x]      |

### String definitions
String definition can be complicated with size restriction.

```
<definition>   ::= <term> ":" <restriction>
<restriction>  ::= <strict>
                 | <greater-then>
                 | <less-then>       
<strict>       := DIGIT+
<greater-then> := ">" DIGIT+
<less-then>    := "<" DIGIT+
```
Examples
```
identifier: 16
numStr: < 8
asciiStr: > 0
``` 

### Const definition
`const` definition will result in `Gen.const` with given value if converted to specified type.

```
<definition>   ::= "const" ":" <value>
<value>        ::= <any>
```

Examples
```
const: 5
const: foo bar baz
const: 2018-04-04T00:00Z
```  

### Range definition
`range` definition will result in `Gen.choose` with given value if converted to specified type.

```
<definition>   ::= "range" ":" <value> ".." <value>
<value>        ::= <any>
```
Examples
```
range: 5 .. 12 // numeric
range: 2016-04-04T00:00Z .. 2018-04-04T00:00Z // date
range: 5ns .. 8m // duration
```  

### OneOf definition
`oneof` definition will result in `Gen.choose` with given value if converted to specified type.

```
<definition>   ::= "oneof" ":" <value> ("," <value>)*
<value>        ::= <any>
```
Examples
```
oneof: 1, 2, 3, 76 // numeric
oneof: 2016-04-04T00:00Z, 2018-04-04T00:00Z // date
oneof: 5ns, 8m, 32d // duration
```  

## How to use

###### SBT
```scala
libraryDependencies += "com.github.andyglow" %% "scalacheck-gen-configured" % "$latestVersion"
```
###### Gradle
```
compile "com.github.andyglow:scalacheck-gen-configured_${scalaVersion}:$latestVersion"
```
###### Maven
```xml
<dependency>
    <groupId>com.github.andyglow</groupId>
    <artifactId>scalacheck-gen-configured_${scalaVersion}</artifactId>
    <version>$latestVersion</version>
</dependency>
```

## TODO
- [ ] options
- [ ] containers (`List`, `Set`, `Map`, etc)
- [ ] handle strings quoted (especially important when used woth `oneof`)
- [ ] enums
- [ ] case classes
- [x] allow pluggable definition formats. now it's strings only, but could be something else (consider typesafe `Config`, json `AST`, ...)
- [ ] implement typesafe-config enabled `DefnFormat`
