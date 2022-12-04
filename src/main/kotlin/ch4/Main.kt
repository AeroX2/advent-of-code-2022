package ch4

fun main() {
    val data = object{}.javaClass.getResource("/ch4/input.txt")?.readText()
    if (data == null) {
        println("Missing data file");
        return;
    }

    val assignments = data.trim().split("\n")
    println(part1(assignments))
    println(part2(assignments))
}

data class Range(val start: Int, val end: Int)

fun compare(r1: Range, r2: Range): Boolean {
    return r2.start >= r1.start && r2.end <= r1.end
}

fun compare2(r1: Range, r2: Range): Boolean {
    return r2.start <= r1.end && r2.end >= r1.start
}

fun part1(assignments: List<String>): Int {
    val pairs = assignments.map { it.split(",") }
    val rangesPairs = pairs.map { it.map { it.split("-") }.map { Range(it.first().toInt(), it.last().toInt()) } }

    return rangesPairs.map {
        compare(it.first(), it.last()) || compare(it.last(), it.first())
    }.count { it }
}

fun part2(assignments: List<String>): Int {
    val pairs = assignments.map { it.split(",") }
    val rangesPairs = pairs.map { it.map { it.split("-") }.map { Range(it.first().toInt(), it.last().toInt()) } }

    return rangesPairs.map {
        compare2(it.last(), it.first())
    }.count { it }
}
