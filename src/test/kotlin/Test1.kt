import org.junit.jupiter.api.Test
import java.io.File
import java.io.RandomAccessFile
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class Test1 {

    @Test
    fun testGetInputFile() {
        val args1 = Input(arrayOf("add", "src", "test1.txt", "kjdsfhgshj"))
        val args2 = Input(arrayOf("find_from_file", "database", "Tests/TestGetInputFile/test2.txt"))
        val args3 = Input(arrayOf("add"))
        val args4 = Input(arrayOf("del", "sdfkj"))
        val args5 = Input(emptyArray<String>())
        assertEquals(null, args1.getInputFile())
        assertEquals(File("Tests/TestGetInputFile/test2.txt"), args2.getInputFile())
        assertEquals(null, args3.getInputFile())
        assertEquals(null, args4.getInputFile())
        assertEquals(null, args5.getInputFile())
    }

    @Test
    fun testGetInputBaseName() {
        val args1 = Input(arrayOf("add", "src", "sdfkj", "kjdsfhgshj"))
        val args2 = Input(arrayOf("del", "src/sklfir"))
        val args3 = Input(arrayOf("add"))
        val args4 = Input(arrayOf("del", "sdfkj"))
        val args5 = Input(emptyArray<String>())
        assertEquals(DATA_BASES_DIR + "src", args1.getInputBaseName())
        assertEquals(DATA_BASES_DIR + "src/sklfir", args2.getInputBaseName())
        assertEquals(null, args3.getInputBaseName())
        assertEquals(DATA_BASES_DIR + "sdfkj", args4.getInputBaseName())
        assertEquals(null, args5.getInputBaseName())
    }

    @Test
    fun testGetInputKeyVal() {
        val args1 = Input(arrayOf("add", "src", "sdfkj", "kjdsfhgshj"))
        val args2 = Input(arrayOf("add", "", "sdfkjsf  ", "k  jdsfhgshj"))
        val args3 = Input(arrayOf("del", "src/sklfir"))
        val args4 = Input(arrayOf("add"))
        val args5 = Input(arrayOf("del", "sdfkj"))
        val args6 = Input(emptyArray<String>())
        assertEquals(KeyVal("sdfkj", "kjdsfhgshj"), args1.getInputKeyVal())
        assertEquals(KeyVal("sdfkjsf  ", "k  jdsfhgshj"), args2.getInputKeyVal())
        assertEquals(null, args3.getInputKeyVal())
        assertEquals(null, args4.getInputKeyVal())
        assertEquals(null, args5.getInputKeyVal())
        assertEquals(null, args6.getInputKeyVal())
    }

    @Test
    fun testGetInputKey() {
        val args1 = Input(arrayOf("add", "src", "sdfkj", "kjdsfhgshj"))
        val args2 = Input(arrayOf("add", "", "sdfkjsf  ", "k  jdsfhgshj"))
        val args3 = Input(arrayOf("del", "src/sklfir"))
        val args4 = Input(arrayOf("add"))
        val args5 = Input(arrayOf("del", "sdfkj"))
        val args6 = Input(emptyArray<String>())
        assertEquals("sdfkj", args1.getInputKey())
        assertEquals("sdfkjsf  ", args2.getInputKey())
        assertEquals(null, args3.getInputKey())
        assertEquals(null, args4.getInputKey())
        assertEquals(null, args5.getInputKey())
        assertEquals(null, args6.getInputKey())
    }

    @Test
    fun testGetKeysFromFile() {
        val path = "Tests/TestGetKeysFromFile/"
        val array1 = listOf("2q847", "sf", "s", "ss", "3", " %%", "2", "(", ")")
        val array2 = emptyList<Key>()
        val array3 = listOf("1823", "888  2", "1")
        val array4 = listOf("11")
        val array5 = listOf("^^^^")
        assertContentEquals(array1, getKeysFromFile(File(path + "test1.txt")))
        assertContentEquals(array2, getKeysFromFile(File(path + "test2.txt")))
        assertContentEquals(array3, getKeysFromFile(File(path + "test3.txt")))
        assertContentEquals(array4, getKeysFromFile(File(path + "test4.txt")))
        assertContentEquals(array5, getKeysFromFile(File(path + "test5.txt")))

    }

    @Test
    fun testCreateDataBase() {
        val path = "Tests/TestCreateDataBase/"
        val base1 = DataBase(path + "TestBase1")
        val base2 = DataBase(path + "TestBase2")
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
        val path = "Tests/TestFindKeyPlace/"
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
        val path = "Tests/TestFind/"
        val base1 = DataBase(path + "TestBase1")
        val base2 = DataBase(path + "TestBase2")
        val base3 = DataBase(path + "TestBase3")
        val base4 = DataBase(path + "TestBase4")
        val base5 = DataBase(path + "TestBase5")
        assertEquals("skjdhf", base1.find("i"))
        assertEquals(null, base1.find("ii"))
        assertEquals("dsfjhjdsfh  ", base1.find("&&"))
        assertEquals("1 0 2 ", base2.find("ad"))
        assertEquals("aa", base2.find("aaaa"))
        assertEquals(null, base2.find("%%%"))
        assertEquals("skjdft", base3.find("idskfj dkj"))
        assertEquals("skjdft", base3.find("i d 0"))
        assertEquals(null, base3.find("22"))
        assertEquals("sk", base4.find("i))"))
        assertEquals("sk", base4.find("h"))
        assertEquals("sk", base4.find("g"))
        assertEquals(null, base5.find(")) ))"))
        assertEquals(null, base5.find("sdfh sod d"))
        assertEquals(null, base5.find("sd dk 2 "))
    }

    @Test
    fun testFindFromFile() {
        val path = "Tests/TestFindFromFile/"
        val base1 = DataBase(path + "TestBase1")
        val base2 = DataBase(path + "TestBase2")
        val base3 = DataBase(path + "TestBase3")
        val base4 = DataBase(path + "TestBase4")
        val base5 = DataBase(path + "TestBase5")
        assertContentEquals(
            listOf(null, null, null, "skjdhf", "dsfjhjdsfh  "),
            base1.findFromFile(File(path + "test1.txt"))
        )
        assertContentEquals(listOf("1 0 2 ", "aa", null, null, null), base2.findFromFile(File(path + "test2.txt")))
        assertContentEquals(listOf("skjdft", "skjdft", null), base3.findFromFile(File(path + "test3.txt")))
        assertContentEquals(listOf("sk", "sk", "sk", "sk", "sk"), base4.findFromFile(File(path + "test4.txt")))
        assertContentEquals(listOf(null, null, null, null, null), base5.findFromFile(File(path + "test5.txt")))
    }

    @Test
    fun testAdd1() {
        val path = "Tests/TestAdd/"
        File("Tests/TestAdd/TestBase1/keys.txt").delete()
        File("Tests/TestAdd/TestBase1/values.txt").delete()
        File("Tests/TestAdd/TestBase1/texInfo.txt").delete()
        File("Tests/TestAdd/TestBase1").delete()
        val base1 = DataBase(path + "TestBase1")
        base1.add(KeyVal("1", "dj hff  "))
        assertEquals("dj hff  ", base1.find("1"))
        base1.add(KeyVal("2", "dj hff  "))
        assertEquals("dj hff  ", base1.find("2"))
        base1.add(KeyVal("1", "s"))
        assertEquals("s", base1.find("1"))
    }

    @Test
    fun testAdd2() {
        val path = "Tests/TestAdd/"
        File("Tests/TestAdd/TestBase2/keys.txt").delete()
        File("Tests/TestAdd/TestBase2/values.txt").delete()
        File("Tests/TestAdd/TestBase2/texInfo.txt").delete()
        File("Tests/TestAdd/TestBase2").delete()
        val base2 = DataBase(path + "TestBase2")
        base2.add(KeyVal("2", "dj hff  "))
        base2.add(KeyVal("2", "dj hff  "))
        assertEquals("dj hff  ", base2.find("2"))
        base2.add(KeyVal("2", "aa"))
        assertEquals("aa", base2.find("2"))
        assertEquals(null, base2.find("1"))
        base2.add(KeyVal("1", "s"))
        assertEquals("s", base2.find("1"))
        base2.add(KeyVal("y", "s"))
        assertEquals("s", base2.find("y"))
    }

    @Test
    fun testDel1() {
        val path = "Tests/TestDel/"
        File("Tests/TestDel/TestBase1/keys.txt").delete()
        File("Tests/TestDel/TestBase1/values.txt").delete()
        File("Tests/TestDel/TestBase1/texInfo.txt").delete()
        File("Tests/TestDel/TestBase1").delete()
        val base1 = DataBase(path + "TestBase1")
        base1.add(KeyVal("1", "dj hff  "))
        base1.add(KeyVal("1", "dj hff  "))
        base1.add(KeyVal("2", "d"))
        base1.add(KeyVal("3", "sss"))
        base1.add(KeyVal("5", "%%%"))
        assertEquals("dj hff  ", base1.find("1"))
        base1.del("1")
        assertEquals(null, base1.find("1"))
        assertEquals("d", base1.find("2"))
        base1.del("2")
        assertEquals(null, base1.find("2"))
        assertEquals("sss", base1.find("3"))
        base1.del("3")
        assertEquals(null, base1.find("3"))
        assertEquals("%%%", base1.find("5"))
        base1.del("5")
        assertEquals(null, base1.find("5"))
    }

    @Test
    fun testReorganize() {
        val path = "Tests/TestReorganize/"
        File("Tests/TestReorganize/TestBase1/keys.txt").delete()
        File("Tests/TestReorganize/TestBase1/values.txt").delete()
        File("Tests/TestReorganize/TestBase1/texInfo.txt").delete()
        File("Tests/TestReorganize/TestBase1").delete()
        val base1 = DataBase(path + "TestBase1")
        repeat(MAX_DEL) {
            base1.add(KeyVal("1", "dj hff  "))
        }
        base1.add(KeyVal("2", "d"))
        base1.add(KeyVal("3", "sss"))
        base1.add(KeyVal("5", "%%%"))
        base1.del("1")
        base1.del("2")
        base1.del("3")
        base1.del("5")
        base1.reorganize()
        base1.keysFile.seek(0)
        base1.valuesFile.seek(0)
        assertEquals(null, base1.keysFile.readLine())
        assertEquals(null, base1.valuesFile.readLine())
        assertEquals(0, base1.numberOfDelValues)
    }

    @Test
    fun testProgramTime() {
        val path = "Tests/TestProgramTime/"
        File("Tests/TestProgramTime/TestBase1/keys.txt").delete()
        File("Tests/TestProgramTime/TestBase1/values.txt").delete()
        File("Tests/TestProgramTime/TestBase1/texInfo.txt").delete()
        File("Tests/TestProgramTime/TestBase1").delete()
        val base1 = DataBase(path + "TestBase1")
        val longString = StringBuilder()
        repeat(100000) {
            longString.append("a")
        }
        repeat(2000) {
            base1.add(KeyVal(it.toString(), longString.toString()))
        }
        repeat(2000) {
            base1.del(it.toString())
        }
        base1.reorganize()
        base1.keysFile.seek(0)
        base1.valuesFile.seek(0)
        assertEquals(null, base1.keysFile.readLine())
        assertEquals(null, base1.valuesFile.readLine())
        assertEquals(0, base1.numberOfDelValues)
    }
}