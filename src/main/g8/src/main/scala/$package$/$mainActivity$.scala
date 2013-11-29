package $package$

import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.widget.TextView
import $package$.support._

class MainActivity extends ActionBarActivity with ActivitySupport {

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.main)
    findView(TR.textview) foreach { _ setText "hello, world!" }
  }

}
