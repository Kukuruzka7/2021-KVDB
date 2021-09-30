import org.junit.jupiter.api.Test
import java.io.File
import java.io.RandomAccessFile
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class Test1 {

    @Test
    fun testGetInputFile() {
        val args1 = arrayOf("add", "src", "test1.txt", "kjdsfhgshj")
        val args2 = arrayOf("find_from_file", "database", "src/test/TestGetInputFile/test2.txt")
        val args3 = arrayOf("add")
        val args4 = arrayOf("del", "sdfkj")
        val args5 = emptyArray<String>()
        assertEquals(null, getInputFile(args1))
        assertEquals(File("src/test/TestGetInputFile/test2.txt"), getInputFile(args2))
        assertEquals(null, getInputFile(args3))
        assertEquals(null, getInputFile(args4))
        assertEquals(null, getInputFile(args5))
    }

    @Test
    fun testGetInputBaseName() {
        val args1 = arrayOf("add", "src", "sdfkj", "kjdsfhgshj")
        val args2 = arrayOf("del", "src/sklfir")
        val args3 = arrayOf("add")
        val args4 = arrayOf("del", "sdfkj")
        val args5 = emptyArray<String>()
        assertEquals(DATA_BASES_DIR + "src", getInputBaseName(args1))
        assertEquals(DATA_BASES_DIR + "src/sklfir", getInputBaseName(args2))
        assertEquals(null, getInputBaseName(args3))
        assertEquals(DATA_BASES_DIR + "sdfkj", getInputBaseName(args4))
        assertEquals(null, getInputBaseName(args5))
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

    @Test
    fun testGetKeysFromFile() {
        val path = "src/test/TestGetKeysFromFile/"
        val array1 = arrayOf("2q847", "sf", "s", "ss", "3", " %%", "2", "(", ")")
        val array2 = emptyArray<Key>()
        val array3 = arrayOf("1823", "888  2", "1")
        val array4 = arrayOf("11")
        val array5 = arrayOf("^^^^")
        assertContentEquals(array1, getKeysFromFile(File(path + "test1.txt")))
        assertContentEquals(array2, getKeysFromFile(File(path + "test2.txt")))
        assertContentEquals(array3, getKeysFromFile(File(path + "test3.txt")))
        assertContentEquals(array4, getKeysFromFile(File(path + "test4.txt")))
        assertContentEquals(array5, getKeysFromFile(File(path + "test5.txt")))

    }

    @Test
    fun testCreateDataBase() {
        val path = "src/test/TestCreateDataBase/"
        val base1 = createDataBase(path + "TestBase1")
        val base2 = createDataBase(path + "TestBase2")
        assertEquals(true, File(path + "TestBase1/keys.txt").isFile)
        assertEquals(true, File(path + "TestBase1/values.txt").isFile)
        assertEquals(true, File(path + "TestBase1/texInfo.txt").isFile)
        assertEquals(true, File(path + "TestBase2/keys.txt").isFile)
        assertEquals(true, File(path + "TestBase2/values.txt").isFile)
        assertEquals(true, File(path + "TestBase2/texInfo.txt").isFile)
        assertEquals(base1.numberOfDelValues, 0)
        assertEquals(base2.numberOfDelValues, 256)
    }

    @Test
    fun testfindKeyPlace() {
        val path = "src/test/TestFindKeyPlace/"
        assertEquals(0, findKeyPlace(RandomAccessFile(path + "test1.txt", "rw"), "dfjh"))
        assertEquals(24, findKeyPlace(RandomAccessFile(path + "test2.txt", "rw"), "%"))
        assertEquals(null, findKeyPlace(RandomAccessFile(path + "test3.txt", "rw"), "sd"))
        assertEquals(4, findKeyPlace(RandomAccessFile(path + "test4.txt", "rw"), "sd"))
        assertEquals(null, findKeyPlace(RandomAccessFile(path + "test5.txt", "rw"), "ttt"))
    }

    @Test
    fun testGetRange() {
        assertEquals(Pair(1L, 38L), getRange("ii$1$38"))
        assertEquals(Pair(4L, 5L), getRange("i$4$5"))
        assertEquals(Pair(0L, 0L), getRange("iiksjf fkjs$0$0"))
        assertEquals(Pair(0L, -1L), getRange("$$$$"))
        assertEquals(Pair(0L, -1L), getRange("oo$99$38"))
    }

    @Test
    fun testFind() {
        val path = "src/test/TestFind/"
        val base1 = createDataBase(path + "TestBase1")
        val base2 = createDataBase(path + "TestBase2")
        val base3 = createDataBase(path + "TestBase3")
        val base4 = createDataBase(path + "TestBase4")
        val base5 = createDataBase(path + "TestBase5")
        assertEquals("skjdhf", find(base1, "i"))
        assertEquals(null, find(base1, "ii"))
        assertEquals("dsfjhjdsfh  ", find(base1, "&&"))
        assertEquals("1 0 2 ", find(base2, "ad"))
        assertEquals("aa", find(base2, "aaaa"))
        assertEquals(null, find(base2, "%%%"))
        assertEquals("skjdft", find(base3, "idskfj dkj"))
        assertEquals("skjdft", find(base3, "i d 0"))
        assertEquals(null, find(base3, "22"))
        assertEquals("sk", find(base4, "i))"))
        assertEquals("sk", find(base4, "h"))
        assertEquals("sk", find(base4, "g"))
        assertEquals(null, find(base5, ")) ))"))
        assertEquals(null, find(base5, "sdfh sod d"))
        assertEquals(null, find(base5, "sd dk 2 "))
    }

    @Test
    fun testFindFromFile() {
        val path = "src/test/TestFindFromFile/"
        val base1 = createDataBase(path + "TestBase1")
        val base2 = createDataBase(path + "TestBase2")
        val base3 = createDataBase(path + "TestBase3")
        val base4 = createDataBase(path + "TestBase4")
        val base5 = createDataBase(path + "TestBase5")
        assertContentEquals(
            arrayOf(null, null, null, "skjdhf", "dsfjhjdsfh  "),
            findFromFile(base1, File(path + "test1.txt"))
        )
        assertContentEquals(arrayOf("1 0 2 ", "aa", null, null, null), findFromFile(base2, File(path + "test2.txt")))
        assertContentEquals(arrayOf("skjdft", "skjdft", null), findFromFile(base3, File(path + "test3.txt")))
        assertContentEquals(arrayOf("sk", "sk", "sk", "sk", "sk"), findFromFile(base4, File(path + "test4.txt")))
        assertContentEquals(arrayOf(null, null, null, null, null), findFromFile(base5, File(path + "test5.txt")))
    }

    @Test
    fun testAdd1() {
        val path = "src/test/TestAdd/"
        File("src/test/TestAdd/TestBase1/keys.txt").delete()
        File("src/test/TestAdd/TestBase1/values.txt").delete()
        File("src/test/TestAdd/TestBase1/texInfo.txt").delete()
        File("src/test/TestAdd/TestBase1").delete()
        val base1 = createDataBase(path + "TestBase1")
        add(base1, KeyVal("1", "dj hff  "))
        assertEquals("dj hff  ", find(base1, "1"))
        add(base1, KeyVal("2", "dj hff  "))
        assertEquals("dj hff  ", find(base1, "2"))
        add(base1, KeyVal("1", "s"))
        assertEquals("s", find(base1, "1"))
    }

    @Test
    fun testAdd2() {
        val path = "src/test/TestAdd/"
        File("src/test/TestAdd/TestBase2/keys.txt").delete()
        File("src/test/TestAdd/TestBase2/values.txt").delete()
        File("src/test/TestAdd/TestBase2/texInfo.txt").delete()
        File("src/test/TestAdd/TestBase2").delete()
        val base2 = createDataBase(path + "TestBase2")
        add(base2, KeyVal("2", "dj hff  "))
        add(base2, KeyVal("2", "dj hff  "))
        assertEquals("dj hff  ", find(base2, "2"))
        add(base2, KeyVal("2", "aa"))
        assertEquals("aa", find(base2, "2"))
        assertEquals(null, find(base2, "1"))
        add(base2, KeyVal("1", "s"))
        assertEquals("s", find(base2, "1"))
        add(base2, KeyVal("y", "s"))
        assertEquals("s", find(base2, "y"))
    }

    @Test
    fun testDel1() {
        val path = "src/test/TestDel/"
        File("src/test/TestDel/TestBase1/keys.txt").delete()
        File("src/test/TestDel/TestBase1/values.txt").delete()
        File("src/test/TestDel/TestBase1/texInfo.txt").delete()
        File("src/test/TestDel/TestBase1").delete()
        val base1 = createDataBase(path + "TestBase1")
        add(base1, KeyVal("1", "dj hff  "))
        add(base1, KeyVal("1", "dj hff  "))
        add(base1, KeyVal("2", "d"))
        add(base1, KeyVal("3", "sss"))
        add(base1, KeyVal("5", "%%%"))
        assertEquals("dj hff  ", find(base1, "1"))
        del(base1, "1")
        assertEquals(null, find(base1, "1"))
        assertEquals("d", find(base1, "2"))
        del(base1, "2")
        assertEquals(null, find(base1, "2"))
        assertEquals("sss", find(base1, "3"))
        del(base1, "3")
        assertEquals(null, find(base1, "3"))
        assertEquals("%%%", find(base1, "5"))
        del(base1, "5")
        assertEquals(null, find(base1, "5"))
    }

    @Test
    fun testReorganize() {
        val path = "src/test/TestReorganize/"
        File("src/test/TestReorganize/TestBase1/keys.txt").delete()
        File("src/test/TestReorganize/TestBase1/values.txt").delete()
        File("src/test/TestReorganize/TestBase1/texInfo.txt").delete()
        File("src/test/TestReorganize/TestBase1").delete()
        val base1 = createDataBase(path + "TestBase1")
        repeat(MAX_DEL) {
            add(base1, KeyVal("1", "dj hff  "))
        }
        add(base1, KeyVal("2", "d"))
        add(base1, KeyVal("3", "sss"))
        add(base1, KeyVal("5", "%%%"))
        del(base1, "1")
        del(base1, "2")
        del(base1, "3")
        del(base1, "5")
        reorganize(base1)
        base1.keysFile.seek(0)
        base1.valuesFile.seek(0)
        assertEquals(null, base1.keysFile.readLine())
        assertEquals(null, base1.valuesFile.readLine())
        assertEquals(0, base1.numberOfDelValues)
    }

    @Test
    fun testProgramTime() {
        val path = "src/test/TestProgramTime/"
        File("src/test/TestProgramTime/TestBase1/keys.txt").delete()
        File("src/test/TestProgramTime/TestBase1/values.txt").delete()
        File("src/test/TestProgramTime/TestBase1/texInfo.txt").delete()
        File("src/test/TestProgramTime/TestBase1").delete()
        val base1 = createDataBase(path + "TestBase1")
        val longString = StringBuilder()
        repeat(100000) {
            longString.append("a")
        }
        repeat(2000) {
            add(base1, KeyVal(it.toString(), longString.toString()))
        }
        repeat(2000) {
            del(base1, it.toString())
        }
        reorganize(base1)
        base1.keysFile.seek(0)
        base1.valuesFile.seek(0)
        assertEquals(null, base1.keysFile.readLine())
        assertEquals(null, base1.valuesFile.readLine())
        assertEquals(0, base1.numberOfDelValues)
    }
}