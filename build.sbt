scalaVersion := "2.9.1"

seq(giter8Settings :_*)

resolvers += "Local Maven Repository" at ("file://" + Path.userHome.absolutePath + "/.m2/repository")
