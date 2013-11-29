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

package $package$

import android.view.View
import scala.util.control.Exception._

package object support extends AsyncSupport {

  implicit class WrappedView[A <: View](view: A) extends TypedViewContainer {

    /**
     * 指定された ID に対応する View を見つけて返す
     *
     * @param id 対象のID
     * @return 指定されたIDに対応するView, 見つからなかった場合は null
     */
    protected def findView(id: Int): View = view.findViewById(id)

    /**
     * @return WrappedView[A] 自身を返す
     */
    def wrapped: WrappedView[A] = this

    def setTag[B](tag: B): Unit = view setTag tag
    def setTag[B](key: Int, tag: B): Unit = view.setTag(key, tag)

    def getTag[B]: Option[B] = allCatch opt view.getTag.asInstanceOf[B]
    def getTag[B](key: Int): Option[B] = allCatch opt view.getTag(key).asInstanceOf[B]

  }

}
