package ch15

import kotlin.math.abs

fun main() {
    val resource = "/ch15/input.txt"
    val data = object {}.javaClass.getResource(resource)?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val lines = data.split("\n")
    println(part1(lines, if (resource.contains("example")) 10 else 2000000))
    println(part2(lines, if (resource.contains("example")) 20 else 4000000))
}

data class Coord(val x: Int, val y: Int)

fun Coord.distance(coord: Coord): Int {
    return abs(x-coord.x) + abs(y-coord.y)
}

class Grid {
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE

    val grid: HashMap<Coord, Char> = hashMapOf()

    fun addPoint(coord: Coord, c: Char) {
        minX = minOf(minX, coord.x)
        maxX = maxOf(maxX, coord.x)
        minY = minOf(minY, coord.y)
        maxY = maxOf(maxY, coord.y)

        grid[coord] = c
    }

    fun checkPoint(coord: Coord): Char? {
        return grid[coord]
    }

    fun beaconCrossover(checkY: Int): Int {
        return (minX..maxX).map { grid[Coord(it, checkY)] }.count { it == '+' }
    }

    fun print() {
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                print(grid.getOrDefault(Coord(x, y), '.'))
            }
            println()
        }
    }
}

fun parseLines(lines: List<String>): List<Pair<Coord, Coord>> {
    return lines.map {
        val r = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
        val m = r.matchEntire(it)!!
        val sensor = Coord(m.groups[1]!!.value.toInt(), m.groups[2]!!.value.toInt())
        val beacon = Coord(m.groups[3]!!.value.toInt(), m.groups[4]!!.value.toInt())
        Pair(sensor, beacon)
    }
}

fun part1(lines: List<String>, checkY: Int): Int {
    val grid = Grid()

    val sensorBeaconPairs = parseLines(lines)
    sensorBeaconPairs.forEach {
        val sensor = it.first
        val beacon = it.second

        grid.addPoint(sensor, 'S')
        grid.addPoint(beacon, 'B')

        val d = sensor.distance(beacon)
        grid.addPoint(Coord(sensor.x, sensor.y - d), '#')
        grid.addPoint(Coord(sensor.x, sensor.y + d), '#')
        grid.addPoint(Coord(sensor.x - d, sensor.y), '#')
        grid.addPoint(Coord(sensor.x + d, sensor.y), '#')
    }

    (grid.minX..grid.maxX).forEach { checkX ->
        val checkCoord = Coord(checkX, checkY)
        val f = sensorBeaconPairs.find {
            val sensor = it.first
            val beacon = it.second

            sensor.distance(beacon) >= checkCoord.distance(sensor)
        }

        val g = grid.checkPoint(checkCoord)
        if (f != null && (g == null || g == '#')) {
            grid.addPoint(checkCoord, '+')
        }
    }

//    grid.print()
    return grid.beaconCrossover(checkY)
}

data class Diamond(
    val center: Coord,
    val dist: Int,
) {
    val top get() = Coord(center.x, center.y - dist)
    val bottom get() = Coord(center.x, center.y + dist)
}

data class Range(val start: Int, val end: Int)

fun List<Range>.combine(): List<Range> {
    return sortedBy { it.start }.fold(emptyList()) { result: List<Range>, current: Range ->
        if (result.isEmpty()) {
            listOf(current)
        } else {
            val previous = result.last()
            if (current.start-1 <= previous.end) {
                result.dropLast(1) + Range(previous.start, maxOf(current.end, previous.end))
            } else {
                result + current
            }
        }
    }
}
fun part2(lines: List<String>, maxY: Int): Long {
    val sensorBeaconPairs = parseLines(lines)
    val sensorDiamonds = sensorBeaconPairs.map {
        val sensor = it.first
        val beacon = it.second

        Diamond(
            sensor,
            sensor.distance(beacon)
        )
    }
    for (y in 0..maxY) {
        val ranges = sensorDiamonds.filter {  it.top.y <= y && it.bottom.y >= y }.map {
            val rs = it.center.x - (it.dist - abs(it.center.y - y))
            val re = it.center.x + (it.dist - abs(it.center.y - y))
            Range(rs, re)
        }.combine()
//        println(ranges)
//        println(ranges.combine())
        if (ranges.size > 1) return (ranges[0].end+1).toLong() * 4000000 + y
    }
    return -1
}
