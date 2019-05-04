# Scalacheck `Gen[T]` configurator

[![Build Status](https://travis-ci.org/andyglow/scalacheck-gen-configured.svg)](https://travis-ci.org/andyglow/scalacheck-gen-configured)
![Maven Central 2.11](https://img.shields.io/maven-central/v/com.github.andyglow/scalacheck-gen-configured_2.11.svg)
![Maven Central 2.12](https://img.shields.io/maven-central/v/com.github.andyglow/scalacheck-gen-configured_2.12.svg)
[![Coverage Status](https://coveralls.io/repos/github/andyglow/scalacheck-gen-configured/badge.svg?branch=master)](https://coveralls.io/github/andyglow/scalacheck-gen-configured?branch=master)

With this project you can externalize `Gen[T]` configuration. 

When I needed to generate some amount of test data the first think appeared to my ming was to use `Gen[T]` 
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

With this solution the only think I need is to restart data generation with desired system properties set. 
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
    
## Definitions
Below is a table showing whtcj definitions can be supported by types.  

|                    | Numeric types | Char | String | Boolean | Date types | Duration |
| ------------------ | ------------- | ---- | ------ | ------- | ---------- | -------- |
| identifier         |               |      | X      |         |            |          |
| numChar            |               | X    |        |         |            |          |
| numStr             |               |      | X      |         |            |          |
| alphaUpperChar     |               | X    |        |         |            |          |
| alphaUpperStr      |               |      | X      |         |            |          |
| alphaLowerChar     |               | X    |        |         |            |          |
| alphaLowerStr      |               |      | X      |         |            |          |
| alphaChar          |               | X    |        |         |            |          |
| alphaStr           |               |      | X      |         |            |          |
| alphaNumChar       |               | X    |        |         |            |          |
| alphaNumStr        |               |      | X      |         |            |          |
| asciiChar          |               | X    |        |         |            |          |
| asciiStr           |               |      | X      |         |            |          |
| asciiPrintableChar |               | X    |        |         |            |          |
| asciiPrintableStr  |               |      | X      |         |            |          |
| posNum             | X             |      |        |         |            |          |
| negNum             | X             |      |        |         |            |          |
| const              | X             | X    | X      | X       | X          | X        |
| range              | X             | X    |        |         | X          | X        |
| oneof              | X             | X    | X      | X       | X          | X        |

### String definitions
String definition can be complicated with size restriction.

```
<definition> ::= <term>:<restriction>
<restriction> ::= <strict>
                | <greater-then>
                | <less-then>       
<strict> := DIGIT+
<greater-then> := ">" DIGIT+
<less-then> := "<" DIGIT+
```
Examples
```
identifier: 16
numStr: < 8
asciiStr: > 0
``` 

### Const definition
`const` definition will result in `Gen.const` with given value if converted to specified type.
Examples
```
const: 5
const: foo bar baz
const: 2018-04-04T00:00Z
```  

### Range definition
`range` definition will result in `Gen.choose` with given value if converted to specified type.
Examples
```
range: 5 .. 12 // numeric
range: 2016-04-04T00:00Z .. 2018-04-04T00:00Z // date
range: 5ns .. 8m // duration
```  

### OneOf definition
`oneof` definition will result in `Gen.choose` with given value if converted to specified type.
Examples
```
oneof: 1, 2, 3, 76 // numeric
oneof: 2016-04-04T00:00Z, 2018-04-04T00:00Z // date
oneof: 5ns, 8m, 32d // duration
```  

## How to use

| Build tool | Line |
| ---------- | ---- |
| SBT        | `libraryDependencies += "com.github.andyglow" %% "scalacheck-gen-configured" % "$latestVersion"` |
| Gradle     | `compile "com.github.andyglow:scalacheck-gen-configured_${scalaVersion}:$latestVersion"` |
| Maven      | `<dependency><groupId>com.github.andyglow</groupId><artifactId>scalacheck-gen-configured_${scalaVersion}</artifactId><version>$latestVersion</version></dependency>` |
