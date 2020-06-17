package com.boclips.event.aggregator.presentation.formatters.schema.base

import java.time._
import java.util.{Currency, Locale}

import com.boclips.event.aggregator.domain.model.contentpartners.{DistributionMethod, Streaming}
import com.boclips.event.aggregator.domain.model.events.{Event, OtherEvent}
import com.boclips.event.aggregator.domain.model.users.{BoclipsUserIdentity, OrganisationType, SCHOOL_ORGANISATION, UserIdentity}

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

object ExampleInstance {

  val MAX_RECURSION_DEPTH = 3

  def create[T]()(implicit tt: TypeTag[T]): T = {
    create(tt.tpe, Nil).asInstanceOf[T]
  }

  private def create(tpe: universe.Type, dependantTypes: List[universe.Type]): Any = {
    try {
      val cls = runtimeMirror(getClass.getClassLoader).runtimeClass(tpe)
      explicitSchemaBase(tpe, dependantTypes).getOrElse(
        schemaBaseOfClass(tpe, cls, dependantTypes)
      )
    } catch {
      case e: Throwable => throw new UnsupportedOperationException(s"Error creating an example instance of $tpe. If it's an abstract type, it may have to be registered at the bottom of this file.", e)
    }
  }

  private def createOrNoneToStopInfiniteRecursion(tpe: universe.Type, dependentTypes: List[universe.Type]): Option[_] = {
    if (dependentTypes.count(t => t == tpe) > MAX_RECURSION_DEPTH) {
      None
    } else {
      Some(create(tpe, dependentTypes))
    }
  }

  private def schemaBaseOfClass(tpe: universe.Type, cls: Class[_], dependentTypes: List[universe.Type]): Any = {
    val c: universe.Symbol = tpe.member(termNames.CONSTRUCTOR)
    val ru = universe.runtimeMirror(getClass.getClassLoader)

    if (!c.isMethod) {
      throw new RuntimeException(s"Cannot instantiate class $cls, type $tpe")
    }
    val m: universe.MethodSymbol = c.asMethod

    val ll = m.typeSignatureIn(tpe).paramLists.head
    val types = ll.map(_.typeSignatureIn(tpe))
    val args = types.map(t => create(t, tpe :: dependentTypes))

    try {
      ru.reflectClass(tpe.typeSymbol.asClass).reflectConstructor(
        m
      )(args: _*)
    } catch {
      case e: Throwable => throw new UnsupportedOperationException(s"Error instantiating ${tpe.typeSymbol} with args $args", e)
    }
  }


  private def explicitSchemaBase(tpe: universe.Type, dependentTypes: List[universe.Type]): Option[_] = {
    if (tpe =:= typeOf[Int]) {
      Some(0)
    }
    else if (tpe =:= typeOf[Double]) {
      Some(0.0)
    }
    else if (tpe =:= typeOf[Long]) {
      Some(0L)
    }
    else if (tpe =:= typeOf[Float]) {
      Some(0.0F)
    }
    else if (tpe =:= typeOf[String]) {
      Some("")
    }
    else if (tpe =:= typeOf[Boolean]) {
      Some(true)
    }
    else if (tpe =:= typeOf[BigDecimal]) {
      Some(BigDecimal(0))
    }
    else if (tpe <:< typeOf[List[_]]) {
      Some(createOrNoneToStopInfiniteRecursion(tpe.typeArgs.head, dependentTypes).toList)
    }
    else if (tpe <:< typeOf[Set[_]]) {
      Some(createOrNoneToStopInfiniteRecursion(tpe.typeArgs.head, dependentTypes).toSet)
    }
    else if (tpe <:< typeOf[Option[_]]) {
      Some(createOrNoneToStopInfiniteRecursion(tpe.typeArgs.head, dependentTypes))
    }
    else if (tpe =:= typeOf[ZonedDateTime]) {
      Some(ZonedDateTime.now(ZoneOffset.UTC))
    }
    else if (tpe =:= typeOf[LocalDateTime]) {
      Some(LocalDateTime.now(ZoneOffset.UTC))
    }
    else if (tpe =:= typeOf[YearMonth]) {
      Some(YearMonth.now(ZoneOffset.UTC))
    }
    else if (tpe =:= typeOf[LocalDate]) {
      Some(LocalDate.now(ZoneOffset.UTC))
    }
    else if (tpe =:= typeOf[Duration]) {
      Some(Duration.ofSeconds(100))
    }
    else if (tpe =:= typeOf[Locale]) {
      Some(Locale.CANADA_FRENCH)
    }
    else if (tpe =:= typeOf[Period]) {
      Some(Period.ofMonths(1))
    }
    else if (tpe =:= typeOf[Currency]) {
      Some(Currency.getInstance("GBP"))
    }
    else if (tpe <:< typeOf[Map[_, _]]) {
      val key = ExampleInstance.create(tpe.typeArgs.head, dependentTypes)
      val value = ExampleInstance.create(tpe.typeArgs.tail.head, dependentTypes)
      Some(Map(key -> value))
    }
    else if (tpe <:< typeOf[Iterable[_]]) {
      Some(createOrNoneToStopInfiniteRecursion(tpe.typeArgs.head, dependentTypes).toList)
    }
    else if (tpe <:< typeOf[OrganisationType]) {
      Some(SCHOOL_ORGANISATION)
    }
    else if (tpe =:= typeOf[Event]) {
      Some(create[OtherEvent])
    }
    else if (tpe =:= typeOf[UserIdentity]) {
      Some(create[BoclipsUserIdentity]())
    }
    else if (tpe <:< typeOf[DistributionMethod]) {
      Some(Streaming)
    }
    else {
      None
    }
  }
}
