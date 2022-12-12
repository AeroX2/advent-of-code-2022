package ch12

import java.lang.Exception
import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
	val data = object {}.javaClass.getResource("/ch12/input.txt")?.readText()
	if (data == null) {
		println("Missing data file")
		return
	}

	val grid = data.split("\n").map { it.toList() }
	println(part1(grid))
	println(part2(grid))
}

data class Coord(val x: Int, val y: Int)

operator fun Coord.plus(c: Coord): Coord {
	return Coord(x + c.x, y + c.y)
}

val directions = listOf(
	Coord(0,1),
	Coord(0,-1),
	Coord(-1,0),
	Coord(1,0),
)
fun check(old: Char, new: Char): Boolean {
	return old == 'S' ||
			new.code - old.code == 1 ||
			new.code - old.code <= 0
}

fun score(start: Coord, end: Coord): Int {
	val dx = abs(start.x - end.x)
	val dy = abs(start.y - end.y)
	return (dx + dy) + (-2) * minOf(dx, dy)
}

fun pathFind(grid: List<List<Char>>, start: Coord): List<Coord> {
	val end = grid.indices
		.flatMap { y -> grid[y].indices.map { x -> Coord(x, y) } }
		.find { (x,y) -> grid[y][x] == 'E' } ?: throw Exception("Couldn't find end point")


	val stack = PriorityQueue<Coord> {
		a,b -> score(b, end) - score(a, end)
	}
	stack.add(start)
	val seen = mutableSetOf(start)

	val costFromStart = mutableMapOf(start to 0)
	val cameFrom = mutableMapOf<Coord, Coord>()
	val estimatedTotalCost = mutableMapOf(start to score(start, end))

	while (stack.isNotEmpty()) {
		val coord = stack.poll()

		val current = grid[coord.y][coord.x]
		if (current == 'E') {
			return generatePath(coord, cameFrom)
		}

		for (direction in directions) {
			val newCoord = coord + direction
			if (seen.contains(newCoord)) {
				continue
			}

			val new = grid.getOrNull(newCoord.y)?.getOrNull(newCoord.x)
			if (new == null || !check(current, new)) {
				continue
			}

			if (new == 'E' && current != 'z' && current != 'y') {
				continue
			}

			val score = costFromStart.getValue(coord) + 1
			if (score < costFromStart.getOrDefault(newCoord, 999999)) {
				if (!stack.contains(newCoord)) {
					stack.add(newCoord)
				}
				cameFrom[newCoord] = coord
				costFromStart[newCoord] = score
				estimatedTotalCost[newCoord] = score + score(newCoord, end)
			}
		}
	}
	return listOf()
}

fun generatePath(currentPos: Coord, cameFrom: MutableMap<Coord, Coord>): List<Coord> {
	val path = mutableListOf(currentPos)
	var current = currentPos
	while (cameFrom.containsKey(current)) {
		current = cameFrom.getValue(current)
		path.add(0, current)
	}
	return path.toList()
}

fun part1(grid: List<List<Char>>): Int {
	val start = grid.indices
		.flatMap { y -> grid[y].indices.map { x -> Coord(x, y) } }
		.find { (x,y) -> grid[y][x] == 'S' } ?: throw Exception("Couldn't find start point")

	val path = pathFind(grid, start)

//	var line = mutableListOf<String>()
//	for (y in 0..90) {
//		for (x in 0..90) {
//			if (path.contains(Coord(x,y))) {
//				line.add(path.indexOf(Coord(x,y)).toString().padEnd(3, ' '))
//			} else {
//				line.add("...")
//			}
//		}
//		println(line.joinToString(" "){ it })
//		line = mutableListOf()
//	}
	return path.size - 1
}

fun part2(grid: List<List<Char>>): Int {
	return grid.mapIndexed { y, row ->
		row.mapIndexed { x, c ->
			if (c == 'a') pathFind(grid, Coord(x, y)).size-1
			else 999999999
		}.filter { it != -1 }.min()
	}.min()
//	val start = grid.indices
//		.flatMap { y -> grid[y].indices.map { x -> Coord(x, y) } }
//		.find { (x,y) -> grid[y][x] == 'S' } ?: throw Exception("Couldn't find start point")
//
//	val path = pathFind(grid, start)
}
