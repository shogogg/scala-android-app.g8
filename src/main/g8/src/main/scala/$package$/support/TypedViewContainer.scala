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

import android.view.View
import $package$.TypedResource

trait TypedViewContainer {

  /**
   * 指定された ID に対応する View を見つけて返す
   * @param id 対象のID
   * @return 指定されたIDに対応するView, 見つからなかった場合は null
   */
  protected def findView(id: Int): View

  /**
   * TR.scala に定義された TypedResource に対応する View を見つけて返す
   * @param r 対象の TypedResource
   * @tparam A 指定された TypedResource に対応する View の型
   * @return 指定された TypedResource に対応する View, 見つからなかった場合は None
   */
  def findView[A <: View](r: TypedResource[A]): Option[A] = Option(findView(r.id).asInstanceOf[A])

  /**
   * TR.scala に定義された TypedResource に対応する View (複数) を見つけて返す
   * @param rs 対象の TypedResource
   * @tparam A 指定された TypedResource に対応する View の型
   * @return 指定された TypedResource に対応するシーケンス, 見つからなかった場合は空のシーケンス
   */
  def findViews[A <: View](rs: TypedResource[A]*): Seq[A] = rs.flatMap(findView[A])

}
