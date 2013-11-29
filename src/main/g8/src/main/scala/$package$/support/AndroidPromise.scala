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

import scala.util.{Failure, Success, Try}

trait AndroidPromise[T] {
  def future: AndroidFuture[T]
  def complete(result: Try[T]): Unit
  def complete(future: AndroidFuture[T]): Unit
  final def success(value: T): Unit = complete(Success(value))
  final def failure(exception: Throwable): Unit = complete(Failure(exception))
}

object AndroidPromise {
  def apply[T](): AndroidPromise[T] = new AndroidPromiseImpl[T]
}

class AndroidPromiseImpl[T] extends AndroidPromise[T] with AndroidFuture[T] {

  private[this] var mResult: Option[Try[T]] = None
  private[this] val mListeners = new java.util.ArrayList[Try[T] => _]

  val future: AndroidFuture[T] = this

  def complete(result: Try[T]): Unit = synchronized {
    if (mResult.isDefined) {
      throw new IllegalStateException("Promise already completed.")
    }
    mResult = Some(result)
    val it = mListeners.iterator()
    while (it.hasNext) {
      it.next().apply(result)
    }
    mListeners.clear()
  }

  def complete(future: AndroidFuture[T]): Unit = future.onComplete(complete)

  def onComplete[U](f: Try[T] => U): Unit = synchronized {
    mResult match {
      case None => mListeners.add(f)
      case Some(result) => f(result)
    }
  }

  def onFailure[U](pf: PartialFunction[Throwable, U]): Unit = onComplete {
    case Success(_) => // noop
    case Failure(exception) => pf isDefinedAt exception match {
      case false => // noop
      case true => pf(exception)
    }
  }

  def onSuccess[U](pf: PartialFunction[T, U]): Unit = onComplete {
    case Failure(_) => // noop
    case Success(value) => pf isDefinedAt value match {
      case false => // noop
      case true => pf(value)
    }
  }

  def foreach[U](f: T => U): Unit = onComplete { _ foreach f }

  def map[U](f: T => U): AndroidFuture[U] = {
    val promise = AndroidPromise[U]
    onComplete {
      case Failure(exception) => promise.failure(exception)
      case Success(value) => promise.success(f apply value)
    }
    promise.future
  }

  def flatMap[U](f: T => AndroidFuture[U]): AndroidFuture[U] = {
    val promise = AndroidPromise[U]
    onComplete {
      case Failure(exception) => promise.failure(exception)
      case Success(value) => f(value).onComplete {
        case Failure(exception) => promise.failure(exception)
        case Success(v) => promise.success(v)
      }
    }
    promise.future
  }

}
