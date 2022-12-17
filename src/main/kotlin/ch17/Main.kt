package ch17

fun main() {
    val data = object {}.javaClass.getResource("/ch17/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    println(part1(data))
    println(part2(data))
}

data class Coord(val x: Int, val y: Int)

operator fun Coord.plus(c: Coord): Coord {
    return Coord(x + c.x, y + c.y)
}

val shapes = listOf(
"""
####
""".trimIndent(),
"""
 #
###
 #
""".trimIndent(),
"""
  #
  #
###
""".trimIndent(),
"""
#
#
#
#
""".trimIndent(),
"""
##
##
""".trimIndent(),
).map { shapeToCoords(it) }

fun shapeToCoords(shape: String): Pair<Coord, List<Coord>> {
    val split = shape.split("\n")
    return Pair(
        Coord(split.maxOf { it.length }, split.size),
        shape.split("\n").flatMapIndexed {
                y, s -> s.withIndex().filter { it.value == '#' }.map { Coord(it.index, split.size-y-1) }
        }
    )
}

typealias Grid = HashMap<Coord, Char>

fun Grid.print(maxHeight: Int) {
    val ss = mutableListOf<String>()
    for (y in 0..maxHeight) {
        var s = "|"
        for (x in 0..6) {
            s += getOrDefault(Coord(x,y), '.')
        }
        s += "|"
        ss.add(s)
    }
    println(ss.reversed().joinToString("\n"){ it })
    println("+-------+")
}

data class Shape(
    var pos: Coord,
    val width: Int,
    val height: Int,
    val vertices: List<Coord>
)

fun dropShape(grid: Grid, shape: Shape, jetStream: Iterator<Char>) {
    for (c in jetStream) {
        val px = shape.pos.x
        val py = shape.pos.y
        var x = shape.pos.x
        var y = shape.pos.y

        if (c == '<' && x > 0) x -= 1
        if (c == '>' && x < 7 - shape.width) x += 1

        var collision = shape.vertices.map { it + Coord(x, y) }.any { grid[it] != null }
        if (collision) {
            x = px
        }

        y -= 1
        collision = shape.vertices.map { it + Coord(x, y) }.any { grid[it] != null }
        if (y < 0 || collision) {
            shape.pos = Coord(x, py)
            return
        }

        shape.pos = Coord(x, y)
    }
}

fun dropRocks(data: String, amount: Int = 2022): MutableList<Int> {
    val grid: HashMap<Coord, Char> = hashMapOf()
    val jetStream = generateSequence(0) { it + 1 }.map { data[it % data.length] }.iterator()
    val pattern = mutableListOf<Int>()

    var shapeIndex = 0
    var maxHeight = 0
    for (rock in 0 until amount) {
        val shapeData = shapes[shapeIndex]
        val shape = Shape(
            Coord(2, maxHeight + 3),
            shapeData.first.x,
            shapeData.first.y,
            shapeData.second,
        )
        dropShape(grid, shape, jetStream)

        val g = maxHeight
        for (vertex in shape.vertices) {
            val v = vertex + shape.pos
            maxHeight = maxOf(maxHeight, v.y+1)
            grid[v] = '#'
        }
        pattern.add(maxHeight - g)

        shapeIndex = (shapeIndex + 1) % shapes.size
    }
    return pattern
}

fun part1(data: String): Int {
    val pattern = dropRocks(data)
    val maxHeight = pattern.sumOf { it }
    return maxHeight - 2
}

fun findRepeatingPattern(l: List<Int>): IntRange {
    val s = l.joinToString(""){ it.toString() }

    var firstPattern: MatchResult? = null
    for (end in s.length downTo 0) {
        val r = s.substring(end, s.length).toRegex()
        val m = r.findAll(s).toList()
        val w = m.map { it.range }.windowed(2)
        if (w.size > 2 && w.all { it[0].last+1 == it[1].first }) {
            firstPattern = m[0]
            break
        }
    }
    if (firstPattern == null) throw Exception("AGGHHH!!")

    return firstPattern.range
}

fun part2(data: String): Long {
    val pattern = dropRocks(data, 10000)
    val patternRange = findRepeatingPattern(pattern)

    val startSection = pattern.subList(0, patternRange.first)
    val startSectionSum = startSection.sumOf { it }

    val repeatingSection = pattern.subList(patternRange.first, patternRange.last+1)
    val repeatingSectionSum = repeatingSection.sumOf { it }

    val rocks = 1000000000000
    val numFullRepeates = (rocks - startSection.size) / repeatingSection.size
    val partialRepeatLenth = (rocks - startSection.size) % repeatingSection.size

    val partialRepeat = pattern.subList(patternRange.first, (patternRange.first + partialRepeatLenth).toInt())
    val partialRepeatSum = partialRepeat.sumOf { it }

    return startSectionSum + (numFullRepeates * repeatingSectionSum) + partialRepeatSum
}
