# Курс основ программирования на МКН СПбГУ
## Проект 2: key-value база данных

[Постановка задачи](./TASK.md)

Если хотите хранить базу данных в определенном месте, то измените const val DATA_BASES_DIR.
Можно использовать только однобайтные буквы, и нельзя использовать $ и "\n", ключ не может быть пустой строкой!
Все аргументы передаются как параметры командной строки 

Функционал:
// add <dataBaseName> <key> <val> --- добавить ключ - значение в базу данных dataBaseName
// del <dataBaseName> <key> --- удалить значение по ключу в базе данных dataBaseName
// find <dataBaseName> <key> --- найти значение по ключу в базе данных dataBaseName
// find_from_file <dataBaseName> <file> --- найти значения по ключам из file в базе данных dataBaseName
// print <dataBaseName> --- вывести базу данных dataBaseName

В будущем возможно расширение функционала: 
Объединение двух баз данных 
Возможность добавлять ключи-значения из файла
