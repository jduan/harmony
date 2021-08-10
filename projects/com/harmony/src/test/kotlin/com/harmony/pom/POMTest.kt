import com.harmony.pom.POM
import junit.framework.TestCase.assertTrue
import org.junit.Test

class POMTest {
    @Test
    fun test() {
        val pom = POM.loadFromStream(javaClass.getResourceAsStream("guava_pom.xml"))
        assertTrue(true)
    }
}
