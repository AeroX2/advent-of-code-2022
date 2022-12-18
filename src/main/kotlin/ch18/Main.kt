package ch18

fun main() {
    val data = object {}.javaClass.getResource("/ch18/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val cubes = data.split("\n").map {
        it.split(",").map { it.toInt() }
    }.map { Coord(it[0], it[1], it[2]) }.toSet()

    println(part1(cubes))
    println(part2(cubes))
}

data class Coord(val x: Int, val y: Int, val z: Int)

operator fun Coord.plus(c: Coord): Coord {
    return Coord(x + c.x, y + c.y, z + c.z)
}

val directions = listOf(
    Coord(0,0,1),
    Coord(0,0,-1),
    Coord(1,0,0),
    Coord(-1,0,0),
    Coord(0,1,0),
    Coord(0,-1,0),
)

fun part1(cubes: Set<Coord>): Int {
    return cubes.map { cube ->
        6 - directions.count { cubes.contains(cube + it) }
    }.sumOf { it }
}

fun part2(cubes: Set<Coord>): Int {
    // Bounding box
    var maxX = 0
    var maxY = 0
    var maxZ = 0
    for (cube in cubes) {
        maxX = maxOf(maxX, cube.x)
        maxY = maxOf(maxY, cube.y)
        maxZ = maxOf(maxZ, cube.z)
    }

    val seenCubes = mutableSetOf<Coord>()
    val cubeStack = ArrayDeque<Coord>()
    cubeStack.add(Coord(0,0,0))

    while (cubeStack.isNotEmpty()) {
        val cube = cubeStack.removeFirst()
        for (direction in directions) {
            val newCube = cube + direction
            if (newCube.x in -1..maxX+1 &&
                newCube.y in -1..maxY+1 &&
                newCube.z in -1..maxZ+1 &&
                !seenCubes.contains(newCube) &&
                !cubes.contains(newCube)
            ) {
                seenCubes.add(newCube)
                cubeStack.add(newCube)
            }
        }
    }

    return seenCubes.map { cube ->
        directions.count { cubes.contains(cube + it) }
    }.sumOf { it }
}
