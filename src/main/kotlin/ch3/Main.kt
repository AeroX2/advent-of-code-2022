package ch3

fun main() {
    val data = object{}.javaClass.getResource("/ch3/input.txt")?.readText()
    if (data == null) {
        println("Missing data file");
        return;
    }

    val sacks = data.trim().split("\n")
    println(part1(sacks))
    println(part2(sacks))
}

fun part1(sacks: List<String>): Int {
    var score = 0
    for (sack in sacks) {
        val leftSack = sack.take(sack.length / 2).toSet()
        val rightSack = sack.takeLast(sack.length / 2).toSet()

        val uniqueItems = leftSack.intersect(rightSack)
        val unique = uniqueItems.first()

        score += if (unique.isLowerCase()) {
            1 + (unique.code - 'a'.code)
        } else {
            27 + (unique.code - 'A'.code)
        }
    }
    return score
}

fun part2(sacks: List<String>): Int {
    val setOfThreeSacks = sacks.withIndex().groupBy { it.index / 3 }.map { it.value.map { it.value } }
    val sackIntersections = setOfThreeSacks.map { it.map { it.toSet() }.reduce { acc, sack -> acc.intersect(sack) } }
    return sackIntersections.map { it.first() }.sumOf {
        if (it.isLowerCase()) {
            1 + (it.code - 'a'.code)
        } else {
            27 + (it.code - 'A'.code)
        }
    }
}
