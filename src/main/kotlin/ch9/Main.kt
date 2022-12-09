package ch9

fun main() {
	val data = object {}.javaClass.getResource("/ch9/input.txt")?.readText()
	if (data == null) {
		println("Missing data file")
		return
	}

	val commands = data.split("\n")
	println(part1(commands))
	println(part2(commands))
}

data class Coord(var x: Int, var y: Int)

operator fun Coord.plusAssign(c2: Coord) {
	this.x += c2.x
	this.y += c2.y
}

fun Coord.distance(c2: Coord): Int {
	val x = c2.x - this.x
	val y = c2.y - this.y
	return x*x + y*y
}

fun Coord.moveTowards(c2: Coord) {
	val x = maxOf(minOf(c2.x - this.x, 1), -1)
	val y = maxOf(minOf(c2.y - this.y, 1), -1)
	this.x += x
	this.y += y
}

fun execute(commands: List<String>, tailAmount: Int): Set<Coord> {
	val head = Coord(0,0)
	val tails = List(tailAmount) { Coord(0,0) }
	val visited = mutableSetOf(Coord(0,0))

	for (command in commands) {
		val c = command.split(" ")
		val direction = c[0]
		val steps = c[1].toInt()
		val d = when (direction) {
			"U" -> Coord(0, -1)
			"D" -> Coord(0, 1)
			"L" -> Coord(-1, 0)
			"R" -> Coord(1, 0)
			else -> Coord(0,0)
		}
		for (step in 1..steps) {
			head += d

			var previousTail = head
			for (i in tails.indices) {
				val tail = tails[i]
				if (previousTail.distance(tail) >= 4) {
					tail.moveTowards(previousTail)
					if (i == tails.size-1) visited.add(tail.copy())
				}
				previousTail = tail
			}

//			for (y in -10..10) {
//				for (x in -10..10) {
//					if (head.x == x && head.y == y) print('H')
//					else if (tails.contains(Coord(x,y))) print(tails.indexOf(Coord(x,y)))
//					else if (visited.contains(Coord(x,y))) print('#')
//					else print('.')
//				}
//				println()
//			}
//			println()
		}

	}

	for (y in -10..10) {
		for (x in -10..10) {
			if (visited.contains(Coord(x, y))) print('#')
			else print('.')
		}
		println()
	}
	println()

	return visited.toSet()
}

fun part1(commands: List<String>): Int {
	return execute(commands, 1).size
}

fun part2(commands: List<String>): Int {
	return execute(commands, 9).size
}
