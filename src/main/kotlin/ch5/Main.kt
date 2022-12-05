package ch5

data class Column(var crates: List<Char>) {
    fun take(amount: Int, reverse: Boolean = true): List<Char> {
        val items = this.crates.takeLast(amount)
        this.crates = this.crates.dropLast(amount)
        return if (reverse) items.reversed() else items
    }

    fun add(newCrates: List<Char>) {
        this.crates += newCrates
    }
}
fun main() {
    val data = object{}.javaClass.getResource("/ch5/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val cratesAndSteps = data.split("\n\n")
    val crates = cratesAndSteps[0].split("\n")
    val steps = cratesAndSteps[1].split("\n")

    val columns = crates.reversed().drop(1).map { it.filterIndexed { index, _ -> (index-1) % 4 == 0 } }
    val columnsTransposed = columns[0].mapIndexed { index, _ -> columns.fold(""){ acc, s -> acc + if (index < s.length) s[index] else "" }.trim().toList() }

    val columnObj = columnsTransposed.map { Column(it) }
    println(part1(columnObj, steps))

    val columnObj2 = columnsTransposed.map { Column(it) }
    println(part2(columnObj2, steps))
}

fun part1(columns: List<Column>, steps: List<String>): String {
    for (step in steps) {

        val match = """move ([0-9]+) from ([0-9]+) to ([0-9]+)""".toRegex().find(step)
        if (match?.groups != null) {
            val g = match.groups
            val amount = g[1]!!.value.toInt()
            val stack1 = g[2]!!.value.toInt()-1
            val stack2 = g[3]!!.value.toInt()-1

            val items = columns[stack1].take(amount)
            columns[stack2].add(items)
        }
    }
    return columns.map { it.crates.last() }.joinToString(""){ it.toString() }
}

fun part2(columns: List<Column>, steps: List<String>): String {
    for (step in steps) {

        val match = """move ([0-9]+) from ([0-9]+) to ([0-9]+)""".toRegex().find(step)
        if (match?.groups != null) {
            val g = match.groups
            val amount = g[1]!!.value.toInt()
            val stack1 = g[2]!!.value.toInt()-1
            val stack2 = g[3]!!.value.toInt()-1

            val items = columns[stack1].take(amount, false)
            columns[stack2].add(items)
        }
    }
    return columns.map { it.crates.last() }.joinToString(""){ it.toString() }
}
