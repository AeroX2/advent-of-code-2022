package ch6

fun main() {
    val data = object{}.javaClass.getResource("/ch6/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val streams = data.split("\n")
    println(part1(streams))
    println(part2(streams))
}

fun part1(streams: List<String>): List<Int> {
    return streams.map {
        it.windowed(4).withIndex().find {
            it.value.toSet().size == 4
        }?.index?.plus(4) ?: -1
    }
}

fun part2(streams: List<String>): List<Int> {
    return streams.map {
        it.windowed(14).withIndex().find {
            it.value.toSet().size == 14
        }?.index?.plus(14) ?: -1
    }
}
