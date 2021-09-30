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

data class KeyVal(val key: Key, val value: Value) {}
data class DataBase(
    val keysFile: RandomAccessFile,
    val valuesFile: RandomAccessFile,
    val texInfoFile: File,
    var numberOfDelValues: Int
) {}
typealias BaseName = String
typealias Key = String
typealias Value = String

fun main(args: Array<String>) {
    if (!args.isEmpty()) {
        val command = Command.getCommandFromString(args[0])
        val baseName = getInputBaseName(args)
        if (baseName != null && command != Command.ERR) {
            val dataBase = createDataBase(baseName)
            when (command) {
                Command.ADD -> add(dataBase, getInputKeyVal(args))
                Command.DEL -> del(dataBase, getInputKey(args))
                Command.FIND -> println(find(dataBase, getInputKey(args)) ?: printErr(Error.BAD_KEY))
                Command.FIND_FROM_FILE -> findFromFile(dataBase, getInputFile(args))?.forEach {
                    println(
                        it ?: printErr(Error.BAD_KEY)
                    )
                } ?: printErr(Error.BAD_FILE)
                Command.PRINT -> print(dataBase)
                Command.ERR -> printErr(Error.BAD_COMMAND)
            }
            reorganize(dataBase)
            close(dataBase)
        } else {
            printErr(Error.BAD_COMMAND)
        }
    }
}

fun getInputFile(args: Array<String>): File? {
    if (args.size < 3 || !File(args[2]).isFile) {
        return null
    }
    return File(args[2])
}
//получение пути к файлу из аргументов командной строки

fun getInputBaseName(args: Array<String>): BaseName? {
    if (args.size < 2) {
        return null
    }
    return DATA_BASES_DIR + args[1]
}
//получение названия базы данных из аргументов командной строки

fun getInputKeyVal(args: Array<String>): KeyVal? {
    if (args.size < 4) {
        return null
    }
    return KeyVal(args[2], args[3])
}
//получение пары ключ-значение из аргументов командной строки

fun getInputKey(args: Array<String>): Key? {
    if (args.size < 3) {
        return null
    }
    return args[2]
}
//получение ключа из аргументов командной строки

fun getKeysFromFile(file: File): Array<Key> {
    val lines = file.readLines()
    val keys = Array(lines.size, { i -> lines[i] })
    return keys
}
//получение списка ключей из файла

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
        File(dataBaseName + "/texInfo.txt"), 0
    )
    val scanner = Scanner(dataBase.texInfoFile)
    dataBase.numberOfDelValues = scanner.nextInt()
    return dataBase
}
//создание базы данных, если ее еще не было, создание Random Access файлов и собирание данных о количестве удаленных элементов

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
//поиск места начала строки key в Random Access файле ключей

fun getRange(line: String): Pair<Long, Long> {
    val lineArguments = line.split("$")
    if (lineArguments.size < 3 || line.first() == '$' || lineArguments[1].toLong() > lineArguments[2].toLong()) {
        return Pair(0L, -1L)
    } else {
        return Pair(lineArguments[1].toLong(), lineArguments[2].toLong())
    }
}
//получение интервала в котором лежит значение в Random Access файле значений по строке из Random Access файла ключей

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

fun findFromFile(dataBase: DataBase, inputFile: File?): Array<Value?>? {
    if (inputFile == null) {
        return null
    }
    val keys = getKeysFromFile(inputFile)
    val values = Array(keys.size, { i -> find(dataBase, keys[i]) })
    return values
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
        } else {
            printErr(Error.BAD_KEY)
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
    while (line != null) {
        if (line[0] != '$') {
            val key = line.split("$")[0]
            println(key + "$" + find(dataBase, key))
        }
        line = dataBase.keysFile.readLine()
    }
}

fun printErr(error: Error) {
    when (error) {
        Error.BAD_COMMAND -> println("Ваша команда некорректна, попробуйте еще раз.")
        Error.BAD_KEY -> println("Вашего ключа нет в базе данных, попробуйте еще раз.")
        Error.BAD_FILE -> println("Путь к файлу не корректен, попробуйте еще раз.")
    }
}

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
//переписывание базы данных если в ней удаленных элементов становится больше чем MAX_DEL

fun close(dataBase: DataBase) {
    dataBase.keysFile.close()
    dataBase.valuesFile.close()
    val pw = dataBase.texInfoFile.printWriter()
    pw.println(dataBase.numberOfDelValues)
    pw.close()
}