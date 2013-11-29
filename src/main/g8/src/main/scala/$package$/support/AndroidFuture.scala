/*
 * Copyright (C) 2013 Shogo Kawase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package $package$.support

import scala.util.{Success, Failure, Try}

trait AndroidFuture[+T] {

  def onComplete[U](f: Try[T] => U): Unit
  def onSuccess[U](pf: PartialFunction[T,U]): Unit
  def onFailure[U](pf: PartialFunction[Throwable,U]): Unit

  def map[U](f: T => U): AndroidFuture[U]
  def flatMap[U](f: T => AndroidFuture[U]): AndroidFuture[U]

}

object AndroidFuture {

  def empty[T]: AndroidFuture[T] = Empty
  def failed[T](exception: Throwable): AndroidFuture[T] = new Failed[T](exception)
  def successful[T](value: T): AndroidFuture[T] = new Successful[T](value)

  object Empty extends AndroidFuture[Nothing] {
    def onComplete[U](f: (Try[Nothing]) => U): Unit = {}
    def onSuccess[U](pf: PartialFunction[Nothing,U]): Unit = {}
    def onFailure[U](pf: PartialFunction[Throwable,U]): Unit = {}
    def map[U](f: Nothing => U): AndroidFuture[U] = this
    def flatMap[U](f: Nothing => AndroidFuture[U]): AndroidFuture[U] = this
  }

  private final class Failed[T](exception: Throwable) extends AndroidFuture[T] {
    def onComplete[U](f: Try[T] => U): Unit = f(Failure(exception))
    def onSuccess[U](pf: PartialFunction[T,U]): Unit = {}
    def onFailure[U](pf: PartialFunction[Throwable,U]): Unit = pf isDefinedAt exception match {
      case false => // noop
      case true  => pf(exception)
    }
    def map[U](f: T => U): AndroidFuture[U] = this.asInstanceOf[AndroidFuture[U]]
    def flatMap[U](f: T => AndroidFuture[U]) = this.asInstanceOf[AndroidFuture[U]]
  }

  private final class Successful[T](value: T) extends AndroidFuture[T] {
    def onComplete[U](f: Try[T] => U): Unit = f(Success(value))
    def onSuccess[U](pf: PartialFunction[T,U]): Unit = pf isDefinedAt value match {
      case false => // noop
      case true  => pf(value)
    }
    def onFailure[U](pf: PartialFunction[Throwable,U]): Unit = {}
    def map[U](f: T => U): AndroidFuture[U] = new Successful[U](f apply value)
    def flatMap[U](f: T => AndroidFuture[U]): AndroidFuture[U] = f(value)
  }

}
