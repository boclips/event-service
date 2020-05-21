package com.boclips.event

package object aggregator {

  implicit def optionIterable2List[T](option: Option[Iterable[T]]): List[T] = {
    option match {
      case None => Nil
      case Some(iterable) => iterable.toList
    }
  }

}
