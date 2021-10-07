import java.io.File

class Input constructor(val args: Array<String>) {
    //получение пути к файлу из аргументов командной строки
    fun getInputFile(): File? {
        if (args.size < 3 || !File(args[2]).isFile) {
            return null
        }
        return File(args[2])
    }

    //получение названия базы данных из аргументов командной строки
    fun getInputBaseName(): BaseName? {
        if (args.size < 2) {
            return null
        }
        return DATA_BASES_DIR + args[1]
    }

    //получение пары ключ-значение из аргументов командной строки
    fun getInputKeyVal(): KeyVal? {
        if (args.size < 4) {
            return null
        }
        return KeyVal(args[2], args[3])
    }

    //получение ключа из аргументов командной строки
    fun getInputKey(): Key? {
        if (args.size < 3) {
            return null
        }
        return args[2]
    }
}
