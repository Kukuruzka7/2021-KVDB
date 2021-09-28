import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset
import kotlin.text.StringBuilder

const val DATA_BASES_DIR = ""
val charset = Charset.forName("UTF-16")
//место, где мы храним папку с базами данных

enum class Command {
    ADD, DEL, FIND, PRINT
}

enum class Error {
    BAD_COMMAND, BAD_KEY
}

data class KeyVal(val key: Key, val value: Value) {}
data class DataBase(
    val keysFile: RandomAccessFile,
    val valuesFile: RandomAccessFile,
    val texInfoFile: RandomAccessFile,
    var numberOfDelValues: Int
) {}
typealias BaseName = String
typealias Key = String
typealias Value = String

// Нужно добавить --- вернуть много сразу значений получить по клучам из файла
// add <dataBaseName> <key> <val> --- добавить ключ - значение в базу данных dataBaseName
// del <dataBaseName> <key> --- удалить значение по ключу в базе данных dataBaseName
// find <dataBaseName> <key> --- найти значение по ключу в базе данных dataBaseName
// print <dataBaseName> --- вывести базу данных dataBaseName

fun main(args: Array<String>) {
    val command = Command.valueOf(args[0].uppercase())
    val baseName = getInputBaseName(args)
    if (baseName != null) {
        val dataBase = createDataBase(baseName)
        when (command) {
            Command.ADD -> add(dataBase, getInputKeyVal(args))
            Command.DEL -> del(dataBase, getInputKey(args))
            Command.FIND -> println(find(dataBase, getInputKey(args)) ?: "Такого ключа нет в базе данных.")
            Command.PRINT -> print(dataBase)
        }
        reorganize(dataBase)
        close(dataBase)
    } else {
        printErr(Error.BAD_COMMAND)
    }
}

fun getInputBaseName(args: Array<String>): BaseName? {
    if (args.size < 2) {
        return null
    }
    return DATA_BASES_DIR + args[1]
}

fun getInputKeyVal(args: Array<String>): KeyVal? {
    if (args.size < 4) {
        return null
    }
    return KeyVal(args[2], args[3])
}

fun getInputKey(args: Array<String>): Key? {
    if (args.size < 3) {
        return null
    }
    return args[2]
}

fun createDataBase(dataBaseName: BaseName): DataBase {
    if (!File(dataBaseName).exists()) {
        File(dataBaseName).mkdir()
        File(dataBaseName + "/values.txt").createNewFile()
        File(dataBaseName + "/keys.txt").createNewFile()
        File(dataBaseName + "/texInfo.txt").createNewFile()
        File(dataBaseName + "/texInfo.txt").writeText("0")
    }
    val dataBase = DataBase(
        RandomAccessFile(dataBaseName + "/keys.txt", "rw"),
        RandomAccessFile(dataBaseName + "/values.txt", "rw"),
        RandomAccessFile(dataBaseName + "/texInfo.txt", "rw"), 0
    )
    dataBase.texInfoFile.seek(0)
    dataBase.numberOfDelValues = dataBase.texInfoFile.readLine().toInt()
    return dataBase
}

fun findKeyPlace(keysFile: RandomAccessFile, key: Key): Long? {
    keysFile.seek(0)
    var line = keysFile.readLine()
    var count = 0L
    while (line != null) {
        val keyInLine = line.split("$")[0]
        if (keyInLine == key) {
            return count
        }
        count += line.length + 1
        line = keysFile.readLine()
    }
    return null
}

fun getRange(line: String): Pair<Long, Long> {
    val lineArguments = line.split("$")
    if (lineArguments.size < 3) {
        return Pair(0L, 0L)
    } else {
        return Pair(lineArguments[1].toLong(), lineArguments[2].toLong())
    }
}

fun find(dataBase: DataBase, key: Key?): Value? {
    if (key == null) {
        return null
    }
    val keyPlace = findKeyPlace(dataBase.keysFile, key)
    if (keyPlace != null) {
        dataBase.keysFile.seek(keyPlace)
        val keyLine = dataBase.keysFile.readLine()
        val valueRange = getRange(keyLine)
        val value = StringBuilder()
        dataBase.valuesFile.seek(valueRange.first)
        for (i in valueRange.first..valueRange.second) {
            value.append(dataBase.valuesFile.read().toChar())
        }
        return value.toString()
    } else {
        return null
    }
}

fun del(dataBase: DataBase, key: Key?) {
    if (key == null) {
        printErr(Error.BAD_COMMAND)
    } else {
        val keyPlace = findKeyPlace(dataBase.keysFile, key)
        if (keyPlace != null) {
            dataBase.keysFile.seek(keyPlace)
            val keyLine = dataBase.keysFile.readLine()
            val emptyLine = StringBuilder()
            repeat(keyLine.length) {
                emptyLine.append("$")
            }
            dataBase.keysFile.seek(keyPlace)
            dataBase.keysFile.write(emptyLine.toString().toByteArray())
            dataBase.numberOfDelValues++
        }
    }
}

fun add(dataBase: DataBase, kv: KeyVal?) {
    if (kv == null) {
        printErr(Error.BAD_COMMAND)
    } else {
        if (find(dataBase, kv.key) != null) {
            del(dataBase, kv.key)
        }
        val placeInKeysFile = dataBase.keysFile.length()
        dataBase.keysFile.seek(placeInKeysFile)
        val placeInValFile = dataBase.valuesFile.length()
        dataBase.keysFile.write((kv.key + "$" + placeInValFile.toString() + "$" + (placeInValFile + kv.value.length - 1).toString() + "\n").toByteArray())
        dataBase.valuesFile.seek(placeInValFile)
        dataBase.valuesFile.write((kv.value).toByteArray())
    }
}

fun print(dataBase: DataBase) {
    var line = dataBase.keysFile.readLine()
    while (line != "") {
        val key = line.split("$")[0]
        println(key + "$" + find(dataBase, key))
        line = readLine()
    }
}

fun printErr(error: Error) {
    when (error) {
        Error.BAD_COMMAND -> println("Ваша команда некорректна, попробуйте еще раз.")
        Error.BAD_KEY -> println("Вашего ключа нет в базе данных, попробуйте еще раз.")
    }
}

fun reorganize(dataBase: DataBase) {
    TODO("Not yet implemented")
}

fun close(dataBase: DataBase) {
    dataBase.keysFile.close()
    dataBase.valuesFile.close()
    dataBase.texInfoFile.close()
}