import java.io.File
import java.io.RandomAccessFile
import java.util.Scanner
import kotlin.text.StringBuilder

const val DATA_BASES_DIR = ""
//место, где мы храним папку с базами данных

const val MAX_DEL = 1000
//Момент, когда удаленных значений становится слишком много и нам надо переписать базу данных

enum class Command(val command: String) {
    ADD("add"), DEL("del"), FIND("find"), FIND_FROM_FILE("find_from_file"), PRINT("print"), ERR("");

    companion object {
        fun getCommandFromString(str: String): Command {
            for (value in values()) {
                if (value.command == str) {
                    return value
                }
            }
            return ERR
        }
    }
}

enum class Error {
    BAD_COMMAND, BAD_KEY, BAD_FILE
}

data class KeyVal(val key: Key, val value: Value)
class DataBase(
    val keysFile: RandomAccessFile,
    val valuesFile: RandomAccessFile,
    val texInfoFile: File,
    var numberOfDelValues: Int
)
typealias BaseName = String
typealias Key = String
typealias Value = String

fun main(args: Array<String>) {
    val input = Input(args)
    if (!args.isEmpty()) {
        val command = Command.getCommandFromString(args[0])
        val baseName = input.getInputBaseName()
        if (baseName != null && command != Command.ERR) {
            val dataBase = createDataBase(baseName)
            when (command) {
                Command.ADD -> add(dataBase, input.getInputKeyVal())
                Command.DEL -> del(dataBase, input.getInputKey())
                Command.FIND -> println(find(dataBase, input.getInputKey()) ?: GetErrText(Error.BAD_KEY))
                Command.FIND_FROM_FILE -> findFromFile(dataBase, input.getInputFile())?.forEach {
                    println(
                        it ?: GetErrText(Error.BAD_KEY)
                    )
                } ?: println(GetErrText(Error.BAD_FILE))
                Command.PRINT -> print(dataBase)
                Command.ERR -> println(GetErrText(Error.BAD_COMMAND))
            }
            reorganize(dataBase)
            close(dataBase)
        } else {
            println(GetErrText(Error.BAD_COMMAND))
        }
    }
}

//получение списка ключей из файла
fun getKeysFromFile(file: File): List<Key> {
    return file.readLines()
}

//создание базы данных, если ее еще не было, создание Random Access файлов и собирание данных о количестве удаленных элементов
fun createDataBase(dataBaseName: BaseName): DataBase {
    if (!File(dataBaseName).exists()) {
        File(dataBaseName).mkdir()
        File("$dataBaseName/values.txt").createNewFile()
        File("$dataBaseName/keys.txt").createNewFile()
        File("$dataBaseName/texInfo.txt").createNewFile()
        File("$dataBaseName/texInfo.txt").writeText("0")
    }
    val dataBase = DataBase(
        RandomAccessFile("$dataBaseName/keys.txt", "rw"),
        RandomAccessFile("$dataBaseName/values.txt", "rw"),
        File("$dataBaseName/texInfo.txt"), 0
    )
    val scanner = Scanner(dataBase.texInfoFile)
    dataBase.numberOfDelValues = scanner.nextInt()
    return dataBase
}

//поиск места начала строки key в Random Access файле ключей
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

//получение интервала в котором лежит значение в Random Access файле значений по строке из Random Access файла ключей
fun getRange(line: String): Pair<Long, Long> {
    val lineArguments = line.split("$")
    return if (lineArguments.size < 3 || line.first() == '$' || lineArguments[1].toLong() > lineArguments[2].toLong()) {
        Pair(0L, -1L)
    } else {
        Pair(lineArguments[1].toLong(), lineArguments[2].toLong())
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

fun findFromFile(dataBase: DataBase, inputFile: File?): List<Value?>? {
    if (inputFile == null) {
        return null
    }
    val keys = getKeysFromFile(inputFile)
    return List(keys.size, { i -> find(dataBase, keys[i]) })
}

fun del(dataBase: DataBase, key: Key?) {
    if (key == null) {
        println(GetErrText(Error.BAD_COMMAND))
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
        } else {
            println(GetErrText(Error.BAD_KEY))
        }
    }
}

fun add(dataBase: DataBase, kv: KeyVal?) {
    if (kv == null) {
        println(GetErrText(Error.BAD_COMMAND))
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
    while (line != null) {
        if (line[0] != '$') {
            val key = line.split("$")[0]
            println(key + "$" + find(dataBase, key))
        }
        line = dataBase.keysFile.readLine()
    }
}

fun GetErrText(error: Error): String {
    when (error) {
        Error.BAD_COMMAND -> return "Ваша команда некорректна, попробуйте еще раз."
        Error.BAD_KEY -> return "Вашего ключа нет в базе данных, попробуйте еще раз."
        Error.BAD_FILE -> return "Путь к файлу не корректен, попробуйте еще раз."
    }
}

//переписывание базы данных если в ней удаленных элементов становится больше чем MAX_DEL
fun reorganize(dataBase: DataBase) {
    if (dataBase.numberOfDelValues > MAX_DEL) {
        val keyValList = mutableListOf<KeyVal>()
        dataBase.numberOfDelValues = 0
        dataBase.keysFile.seek(0)
        var line = dataBase.keysFile.readLine()
        while (line != null) {
            if (line[0] != '$') {
                val key = line.split("$")[0]
                keyValList.add(KeyVal(key, find(dataBase, key)!!))
            }
            line = dataBase.keysFile.readLine()
        }
        dataBase.keysFile.setLength(0)
        dataBase.valuesFile.setLength(0)
        for (i in keyValList) {
            add(dataBase, i)
        }
    }
    val pw = dataBase.texInfoFile.printWriter()
    pw.println(dataBase.numberOfDelValues)
    pw.close()
}

fun close(dataBase: DataBase) {
    dataBase.keysFile.close()
    dataBase.valuesFile.close()
    val pw = dataBase.texInfoFile.printWriter()
    pw.println(dataBase.numberOfDelValues)
    pw.close()
}
