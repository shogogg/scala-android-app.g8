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

import android.os.AsyncTask
import scala.util.{Failure, Success, Try}

abstract class ScalaAsyncTask[Params,Progress,Result] extends AsyncTask[Params,Progress,Try[Result]] {

  /**
   * バックグラウンド（別スレッド）で実行する処理
   * doInBackground(Params*) の代わりにオーバーライドして使用する
   *
   * @param params execute() または executeOnExecutor() に渡されたパラメーター
   * @return バックグラウンド処理の実行結果
   */
  def asyncAction(params: Params*): Result

  /**
   * asyncAction(Params*) 実行前にUIスレッドで実行する処理
   * onPreExecute() の代わりにオーバーライドして使用する
   */
  def beforeAsyncAction(): Unit = {}

  /**
   * asyncAction(Params*) 実行後にUIスレッドで実行する処理
   * onPostExecute() の代わりにオーバーライドして使用する
   *
   * @param result asyncAction(Params*) の戻り値
   */
  def onSuccess(result: Result): Unit = {}

  /**
   * asyncAction(Params*) 実行中に例外が発生した場合にUIスレッドで実行する処理
   *
   * @param exception asyncAction(Params*) 実行中に発生した例外
   */
  def onFailure(exception: Throwable): Unit = {}

  /**
   * publishProgress(Progress*) が呼び出されたときにUIスレッドで実行する処理
   *
   * @param progress publishProgress(Progress*) に渡された引数の値
   */
  def onProgress(progress: Progress*): Unit = {}

  /**
   * バックグラウンド（別スレッド）で asyncAction(Params*) を実行する
   *
   * @param params execute(..) または executeOnExecutor(..) に渡されたパラメーター
   * @return asyncAction(Params*) が正常に終了した場合は Success(戻り値), 例外が発生した場合は Failure(例外)
   */
  override final def doInBackground(params: Params*): Try[Result] = {
    try {
      Success(asyncAction(params: _*))
    } catch {
      case e: Throwable => Failure(e)
    }
  }

  /**
   * doInBackground(Params*) 実行前にUIスレッドで実行する処理
   */
  override final def onPreExecute(): Unit = beforeAsyncAction()

  /**
   * doInBackground(Params*) 実行後にUIスレッドで実行する処理
   *
   * @param result doInBackground(Params*) の戻り値（バックグラウンド処理の実行結果）
   */
  override final def onPostExecute(result: Try[Result]): Unit = result match {
    case Success(value)     => onSuccess(value)
    case Failure(exception) => onFailure(exception)
  }

  /**
   * publishProgress(Progress*) が呼び出されたときにUIスレッドで実行する処理
   *
   * @param progress publishProgress(Progress*) に渡された引数の値
   */
  override final def onProgressUpdate(progress: Progress*) = onProgress(progress: _*)

}
