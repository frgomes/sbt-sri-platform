package sri.sbt.platform;

import utest._

object BaseTest extends TestSuite {
  val tests = this {
    "pass" - {
      assert(true)
    }
  }
}
