import java.io.File
import java.io.RandomAccessFile

const val DATA_BASES_DIR = ""
//место, где мы храним папку с базами данных

const val MAX_DEL = 1000
//Момент, когда удаленных значений становится слишком много и нам надо переписать базу данных

typealias BaseName = String
typealias Key = String
typealias Value = String

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

fun main(args: Array<String>) {
    val input = Input(args)
    if (!args.isEmpty()) {
        val command = Command.getCommandFromString(args[0])
        val baseName = input.getInputBaseName()
        if (baseName != null && command != Command.ERR) {
            val dataBase = DataBase(baseName)
            when (command) {
                Command.ADD -> dataBase.add(input.getInputKeyVal())
                Command.DEL -> dataBase.del(input.getInputKey())
                Command.FIND -> println(dataBase.find(input.getInputKey()) ?: GetErrText(Error.BAD_KEY))
                Command.FIND_FROM_FILE -> dataBase.findFromFile(input.getInputFile())?.forEach {
                    println(
                        it ?: GetErrText(Error.BAD_KEY)
                    )
                } ?: println(GetErrText(Error.BAD_FILE))
                Command.PRINT -> dataBase.print()
                Command.ERR -> println(GetErrText(Error.BAD_COMMAND))
            }
            dataBase.reorganize()
            dataBase.close()
        } else {
            println(GetErrText(Error.BAD_COMMAND))
        }
    }
}

//получение списка ключей из файла
fun getKeysFromFile(file: File): List<Key> {
    return file.readLines()
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

fun GetErrText(error: Error): String {
    when (error) {
        Error.BAD_COMMAND -> return "Ваша команда некорректна, попробуйте еще раз."
        Error.BAD_KEY -> return "Вашего ключа нет в базе данных, попробуйте еще раз."
        Error.BAD_FILE -> return "Путь к файлу не корректен, попробуйте еще раз."
    }
}
