import java.io.File
import java.io.RandomAccessFile
import java.util.*

class DataBase(dataBaseName: BaseName) {
    var keysFile: RandomAccessFile
    var valuesFile: RandomAccessFile
    private var texInfoFile: File
    var numberOfDelValues: Int

    init {
        if (!File(dataBaseName).exists()) {
            File(dataBaseName).mkdir()
            File("$dataBaseName/values.txt").createNewFile()
            File("$dataBaseName/keys.txt").createNewFile()
            File("$dataBaseName/texInfo.txt").createNewFile()
            File("$dataBaseName/texInfo.txt").writeText("0")
        }
        keysFile = RandomAccessFile("$dataBaseName/keys.txt", "rw")
        valuesFile = RandomAccessFile("$dataBaseName/values.txt", "rw")
        texInfoFile = File("$dataBaseName/texInfo.txt")
        val scanner = Scanner(texInfoFile)
        numberOfDelValues = scanner.nextInt()
    }

    fun find(key: Key?): Value? {
        if (key == null) {
            return null
        }
        val keyPlace = findKeyPlace(keysFile, key)
        if (keyPlace != null) {
            keysFile.seek(keyPlace)
            val keyLine = keysFile.readLine()
            val valueRange = getRange(keyLine)
            val value = StringBuilder()
            valuesFile.seek(valueRange.first)
            for (i in valueRange.first..valueRange.second) {
                value.append(valuesFile.read().toChar())
            }
            return value.toString()
        } else {
            return null
        }
    }

    fun findFromFile(inputFile: File?): List<Value?>? {
        if (inputFile == null) {
            return null
        }
        val keys = getKeysFromFile(inputFile)
        return List(keys.size, { i -> find(keys[i]) })
    }

    fun del(key: Key?) {
        if (key == null) {
            println(GetErrText(Error.BAD_COMMAND))
        } else {
            val keyPlace = findKeyPlace(keysFile, key)
            if (keyPlace != null) {
                keysFile.seek(keyPlace)
                val keyLine = keysFile.readLine()
                val emptyLine = StringBuilder()
                repeat(keyLine.length) {
                    emptyLine.append("$")
                }
                keysFile.seek(keyPlace)
                keysFile.write(emptyLine.toString().toByteArray())
                numberOfDelValues++
            } else {
                println(GetErrText(Error.BAD_KEY))
            }
        }
    }

    fun add(kv: KeyVal?) {
        if (kv == null) {
            println(GetErrText(Error.BAD_COMMAND))
        } else {
            if (find(kv.key) != null) {
                del(kv.key)
            }
            val placeInKeysFile = keysFile.length()
            keysFile.seek(placeInKeysFile)
            val placeInValFile = valuesFile.length()
            keysFile.write((kv.key + "$" + placeInValFile.toString() + "$" + (placeInValFile + kv.value.length - 1).toString() + "\n").toByteArray())
            valuesFile.seek(placeInValFile)
            valuesFile.write((kv.value).toByteArray())
        }
    }

    fun print() {
        var line = keysFile.readLine()
        while (line != null) {
            if (line[0] != '$') {
                val key = line.split("$")[0]
                println(key + "$" + find(key))
            }
            line = keysFile.readLine()
        }
    }

    //переписывание базы данных если в ней удаленных элементов становится больше чем MAX_DEL
    fun reorganize() {
        if (numberOfDelValues > MAX_DEL) {
            val keyValList = mutableListOf<KeyVal>()
            numberOfDelValues = 0
            keysFile.seek(0)
            var line = keysFile.readLine()
            while (line != null) {
                if (line[0] != '$') {
                    val key = line.split("$")[0]
                    keyValList.add(KeyVal(key, find(key)!!))
                }
                line = keysFile.readLine()
            }
            keysFile.setLength(0)
            valuesFile.setLength(0)
            for (i in keyValList) {
                add(i)
            }
        }
        val pw = texInfoFile.printWriter()
        pw.println(numberOfDelValues)
        pw.close()
    }

    fun close() {
        keysFile.close()
        valuesFile.close()
        val pw = texInfoFile.printWriter()
        pw.println(numberOfDelValues)
        pw.close()
    }
}