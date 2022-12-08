package ch8

data class Tree(val x: Int, val y: Int, val height: Int)

fun main() {
    val data = object {}.javaClass.getResource("/ch8/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val lines = data.split("\n").mapIndexed { y, l -> l.mapIndexed { x, h -> Tree(x, y, h.digitToInt()) } }
    println(part1(lines))
    println(part2(lines))
}

fun visible(lines: List<List<Tree>>): List<List<Tree>> {
    return lines.map { line ->
        line.fold(Pair(-1, emptyList<Tree>())) { acc, tree ->
            if (tree.height > acc.first) {
                Pair(tree.height, acc.second + tree)
            } else {
                acc
            }
        }.second
    }
}

fun transpose(lines: List<List<Tree>>): List<List<Tree>> {
    return List(lines[0].size) { colIndex -> lines.map { it[colIndex] } }
}

fun part1(lines: List<List<Tree>>): Int {
    val leftSide = visible(lines)
    val rightSide = visible(lines.map { it.reversed() })
    val topSide = visible(transpose(lines))
    val bottomSide = visible(transpose(lines).map { it.reversed() })

    val visibleTrees = (leftSide + rightSide + topSide + bottomSide).flatten().toSet()

//    for (y in 0..lines.size) {
//        for (x in 0..lines[0].size) {
//            val f = visibleTrees.any { it.x == x && it.y == y }
//            print(if (f) lines[y][x].height else ' ')
//        }
//        println()
//    }

    return visibleTrees.size
}

fun visible2(lines: List<List<Tree>>, x: Int, y: Int): Int {
    val tree = lines[y][x]
    val treeLine = lines[y].take(x).reversed()
    val i = treeLine.indexOfFirst { it.height >= tree.height }
    return if (i == -1) treeLine.size else i+1
}

fun part2(lines: List<List<Tree>>): Int {
    val leftSide = lines
    val rightSide = lines.map { it.reversed() }
    val topSide = transpose(lines)
    val bottomSide = transpose(lines).map { it.reversed() }

    return lines.mapIndexed { y, line ->
        List(line.size) { x ->
            val v1 = visible2(leftSide, x, y)
            val v2 = visible2(rightSide, line.size - x - 1, y)
            val v3 = visible2(topSide, y, x)
            val v4 = visible2(bottomSide, lines.size - y - 1, x)

            v1 * v2 * v3 * v4
        }.maxOf { it }
    }.maxOf { it }
}
