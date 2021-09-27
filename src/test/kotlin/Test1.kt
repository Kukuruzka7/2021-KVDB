import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

internal class Test1 {

    @Test
    fun testGetInputDir() {
        val args1 = arrayOf("add", "src", "sdfkj", "kjdsfhgshj")
        val args2 = arrayOf("del", "src/sklfir")
        val args3 = arrayOf("add")
        val args4 = arrayOf("del", "sdfkj")
        val args5 = emptyArray<String>()
        assertEquals(File(DATA_BASES_DIR + "src"), getInputDir(args1))
        assertEquals(File(DATA_BASES_DIR + "src/sklfir"), getInputDir(args2))
        assertEquals(null, getInputDir(args3))
        assertEquals(File(DATA_BASES_DIR + "sdfkj"), getInputDir(args4))
        assertEquals(null, getInputDir(args5))
    }

    @Test
    fun testGetInputKeyVal() {
        val args1 = arrayOf("add", "src", "sdfkj", "kjdsfhgshj")
        val args2 = arrayOf("add", "", "sdfkjsf  ", "k  jdsfhgshj")
        val args3 = arrayOf("del", "src/sklfir")
        val args4 = arrayOf("add")
        val args5 = arrayOf("del", "sdfkj")
        val args6 = emptyArray<String>()
        assertEquals(KeyVal("sdfkj", "kjdsfhgshj"), getInputKeyVal(args1))
        assertEquals(KeyVal("sdfkjsf  ", "k  jdsfhgshj"), getInputKeyVal(args2))
        assertEquals(null, getInputKeyVal(args3))
        assertEquals(null, getInputKeyVal(args4))
        assertEquals(null, getInputKeyVal(args5))
        assertEquals(null, getInputKeyVal(args6))
    }

    @Test
    fun testGetInputKey() {
        val args1 = arrayOf("add", "src", "sdfkj", "kjdsfhgshj")
        val args2 = arrayOf("add", "", "sdfkjsf  ", "k  jdsfhgshj")
        val args3 = arrayOf("del", "src/sklfir")
        val args4 = arrayOf("add")
        val args5 = arrayOf("del", "sdfkj")
        val args6 = emptyArray<String>()
        assertEquals("sdfkj", getInputKey(args1))
        assertEquals("sdfkjsf  ", getInputKey(args2))
        assertEquals(null, getInputKey(args3))
        assertEquals(null, getInputKey(args4))
        assertEquals(null, getInputKey(args5))
        assertEquals(null, getInputKey(args6))
    }
}
