import com.harmony.MavenUtil
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MavenUtilTest {
    @Test
    fun testGetLatestVersion() {
        val latestVersion = MavenUtil.getLatestVersion("com.google.j2objc", "j2objc-annotations")
        assertEquals("1.3", latestVersion)
    }
}
