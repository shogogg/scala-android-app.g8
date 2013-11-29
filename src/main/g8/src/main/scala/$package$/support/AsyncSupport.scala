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

import android.os.{AsyncTask, Build}

trait AsyncSupport {

  /**
   * 非同期処理を行う
   *
   * @param f 非同期で行う処理を記述する
   * @tparam A 非同期で行う処理結果の型
   * @return 非同期処理の処理結果を格納する AndroidFuture[A]
   */
  def async[A](f: => A): AndroidFuture[A] = new FutureTask0[A](f).start()

  /**
   * 非同期処理を行う関数を生成する
   *
   * @param f 非同期で行う処理（関数）
   * @tparam A 生成する関数の引数の型
   * @tparam B 生成する関数の結果の型
   * @return 引数: A を受け取り非同期処理の結果を格納する AndroidFuture[B] を返す関数
   */
  def asyncFunction[A,B](f: A => B): A => AndroidFuture[B] = {
    a => new FutureTask1[A,B](f, a).start()
  }

  abstract private class FutureTask[A] extends ScalaAsyncTask[Void,Void,A] {

    /**
     * 非同期処理を開始する
     * @return 処理結果
     */
    def start(): AndroidFuture[A] = {
      // AsyncTask が HONEYCOMB_MR2 以降、並列で実行されなくなったのでその対応
      // 参考URL:
      //   - http://daichan4649.hatenablog.jp/entry/20120125/1327467103
      //   - http://bon-app-etit.blogspot.jp/2013/04/the-dark-side-of-asynctask.html
      Build.VERSION.SDK_INT match {
        case x if x < Build.VERSION_CODES.HONEYCOMB_MR2 => execute()
        case _ => executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
      }
      promise.future
    }

    private[this] val promise = AndroidPromise[A]()
    override def onSuccess(result: A): Unit = promise success result
    override def onFailure(exception: Throwable): Unit = promise failure exception

  }

  private class FutureTask0[A](f: => A) extends FutureTask[A] {
    def asyncAction(params: Void*): A = f
  }

  private class FutureTask1[A,B](f: A => B, a: A) extends FutureTask[B] {
    def asyncAction(params: Void*): B = f(a)
  }

}
