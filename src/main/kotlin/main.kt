import java.io.File

const val DATA_BASES_DIR = "/"
//место, где мы храним папку с базами данных

enum class Command {
    ADD, DEL, FIND, PRINT
}

enum class Error {
    BAD_COMMAND
}

data class KeyVal(val key: String, val value: Any) {
}

// Нужно добавить --- вернуть много сразу значений получить по клучам из файла
// add <dataBaseName> <key> <val> --- добавить ключ - значение в базу данных dataBaseName
// del <dataBaseName> <key> --- удалить значение по ключу в базе данных dataBaseName
// find <dataBaseName> <key> --- найти значение по ключу в базе данных dataBaseName
// print <dataBaseName> --- вывести базу данных dataBaseName

fun main(args: Array<String>) {
    val command = Command.valueOf(args[0].uppercase())
    when (command) {
        Command.ADD -> add(getInputDir(args), getInputKeyVal(args))
        Command.DEL -> del(getInputDir(args), getInputKey(args))
        Command.FIND -> find(getInputDir(args), getInputKey(args))
        Command.PRINT -> print(getInputDir(args))
    }
}

fun getInputDir(args: Array<String>): File? {
    if (args.size < 2) {
        return null
    }
    return File(DATA_BASES_DIR + args[1])
}

fun getInputKeyVal(args: Array<String>): KeyVal? {
    if (args.size < 4) {
        return null
    }
    return KeyVal(args[2], args[3])
}

fun getInputKey(args: Array<String>): String? {
    if (args.size < 3) {
        return null
    }
    return args[2]
}

fun add(dataBase: File?, kv: KeyVal?) {
    TODO()
}

fun del(dataBase: File?, key: String?) {
    TODO()
}

fun find(dataBase: File?, key: String?) {
    TODO()
}

fun print(dataBase: File?) {
    TODO("Not yet implemented")
}

fun printErr(error: Error) {
    when (error) {
        Error.BAD_COMMAND -> println("Ваша команда некорректна, попробуйте еще раз.")
    }
}
