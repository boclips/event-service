package com.boclips.event.aggregator.presentation.formatters.schema.base

import java.time.{Duration, LocalDate, LocalDateTime, ZonedDateTime}

import com.boclips.event.aggregator.testsupport.Test

case class ExampleClass(intVal: Int, strVal: String, optionIntVal: Option[Int])

case class GenericClass[T](intVal: Int, genericVal: T, strVal: String)

case class RecursiveClass(parent: Option[RecursiveClass], siblings: List[RecursiveClass])

class ExampleInstanceTest extends Test {

  it should "handle simple types" in {
    ExampleInstance.create[String]() shouldBe ""
    ExampleInstance.create[Int]() shouldBe 0
    ExampleInstance.create[Double]() shouldBe 0.0
    ExampleInstance.create[Float]() shouldBe 0.0F
    ExampleInstance.create[Long]() shouldBe 0L
    ExampleInstance.create[BigDecimal]() shouldBe BigDecimal("0")
    ExampleInstance.create[Boolean]() shouldBe true
  }

  it should "handle options" in {
    ExampleInstance.create[Option[String]]() shouldBe Some("")
  }

  it should "handle case classes" in {
    ExampleInstance.create[ExampleClass]() shouldBe ExampleClass(0, "", Some(0))
  }

  it should "handle lists" in {
    ExampleInstance.create[List[ExampleClass]]() shouldBe List(ExampleClass(0, "", Some(0)))
  }

  it should "handle sets" in {
    ExampleInstance.create[Set[ExampleClass]]() shouldBe Set(ExampleClass(0, "", Some(0)))
  }

  it should "handle iterables" in {
    ExampleInstance.create[Iterable[ExampleClass]]() should contain only ExampleClass(0, "", Some(0))
  }

  it should "handle maps" in {
    ExampleInstance.create[Map[String, ExampleClass]]() shouldBe Map("" -> ExampleClass(0, "", Some(0)))
  }

  it should "handle java time api types" in {
    ExampleInstance.create[ZonedDateTime]() should not be null
    ExampleInstance.create[LocalDateTime]() should not be null
    ExampleInstance.create[LocalDate]() should not be null
    ExampleInstance.create[Duration]() should not be null
  }

  it should "handle generic classes with generic constructors" in {
    val result = ExampleInstance.create[GenericClass[ExampleClass]]()

    result.genericVal shouldBe ExampleClass(0, "", Some(0))
  }

  it should "handle recursive classes" in {
    val result = ExampleInstance.create[RecursiveClass]

    result.parent should not be empty
    result.siblings should not be empty
  }
}
