package ch14

import kotlin.math.abs

fun main() {
    val data = object {}.javaClass.getResource("/ch14/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val lines = data.split("\n")
    println(part1(lines))
    println(part2(lines))
}

data class Coord(val x: Int, val y: Int)
class Grid {
    private var minX = Int.MAX_VALUE
    private var maxX = Int.MIN_VALUE
    private var minY = Int.MAX_VALUE
    var maxY: Int = Int.MIN_VALUE

    private val grid: HashMap<Coord, Char> = hashMapOf()

    fun addPoint(coord: Coord, c: Char) {
        minX = minOf(minX, coord.x)
        maxX = maxOf(maxX, coord.x)
        minY = minOf(minY, coord.y)
        maxY = maxOf(maxY, coord.y)

        grid[coord] = c
    }

    fun collides(coord: Coord): Boolean {
        return grid[coord] == '#' || grid[coord] == 'O'
    }

    fun exceedsLimits(coord: Coord): Boolean {
        return coord.x < minX ||
               coord.x > maxX ||
               coord.y < minY ||
               coord.y > maxY
    }

    fun print() {
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                print(grid.getOrDefault(Coord(x,y), '.'))
            }
            println()
        }
    }
}

fun parseLines(grid: Grid, lines: List<String>) {
   lines.forEach {
       it.split(" -> ").map {
           val coord = it.split(",")
           Coord(coord[0].toInt(), coord[1].toInt())
       }.windowed(2).forEach {
           val a = it[0]
           val b = it[1]
           if (abs(a.x - b.x) > 0) {
               val x1 = minOf(a.x, b.x)
               val x2 = maxOf(a.x, b.x)
               for (x in x1..x2) {
                   grid.addPoint(Coord(x, a.y), '#')
               }
           }
           if (abs(a.y - b.y) > 0) {
               val y1 = minOf(a.y, b.y)
               val y2 = maxOf(a.y, b.y)
               for (y in y1..y2) {
                   grid.addPoint(Coord(a.x, y), '#')
               }
           }
       }
   }
}

fun emulateSandParticle(grid: Grid): Coord? {
    var sand = Coord(500, 0)
    while (true) {
        sand = if (grid.exceedsLimits(sand)) {
            return null
        } else if (!grid.collides(Coord(sand.x, sand.y+1))) {
            Coord(sand.x, sand.y + 1)
        } else if (!grid.collides(Coord(sand.x-1, sand.y+1))) {
            Coord(sand.x - 1, sand.y + 1)
        } else if (!grid.collides(Coord(sand.x+1, sand.y+1))) {
            Coord(sand.x + 1, sand.y + 1)
        } else {
            return sand
        }
    }
}

fun emulateSand(grid: Grid, startingPoint: Coord): Int {
    var sand = 0
    grid.addPoint(startingPoint, '+')
    while (true) {
        val restPoint = emulateSandParticle(grid) ?: break

        sand++
        grid.addPoint(restPoint, 'O')
        if (restPoint == startingPoint) {
            break
        }
    }
    return sand
}

fun part1(lines: List<String>): Int {
    val grid = Grid()
    parseLines(grid, lines)

    return emulateSand(grid, Coord(500, 0))
}


fun part2(lines: List<String>): Int {
    val grid = Grid()
    parseLines(grid, lines)
    parseLines(grid, listOf("-99999,${grid.maxY+2} -> 99999,${grid.maxY+2}"))

    return emulateSand(grid, Coord(500, 0))
}
