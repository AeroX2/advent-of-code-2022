package ch11


var magic = -1
fun main() {
	val data = object {}.javaClass.getResource("/ch11/input.txt")?.readText()
	if (data == null) {
		println("Missing data file")
		return
	}

	val lines = data.split("\n\n")
	var monkies = parse(lines)
	magic = monkies.map { it.test.first }.reduce { acc, v -> acc * v }

	println(part1(monkies))
	monkies = parse(lines)
	println(part2(monkies))
}

fun parse(lines: List<String>): List<Monkey> {
	return lines.map {
		val monkeyLines = it.split("\n")

		val items = monkeyLines[1].split(": ").last().split(", ").map { Item(it.toLong()) }
		val operation = monkeyLines[2].split(": ").last()
		val test = Triple(
			monkeyLines[3].split(" ").last().toInt(),
			monkeyLines[4].split(" ").last().toInt(),
			monkeyLines[5].split(" ").last().toInt(),
		)
		Monkey(
			test,
			operation,
			items.toMutableList(),
		)
	}
}

data class Item(var worry: Long)

data class Monkey(
	val test: Triple<Int, Int, Int>,
	val operation: String,
	var items: MutableList<Item>,
	var inspectAmount: Long = 0,
) {
	fun testItems(partTwo: Boolean = false): List<Pair<Int, Item>> {
		items.forEach {
			applyOperation(it, partTwo)
			inspectAmount++
		}
		return items.map {
			val ind = if (it.worry.mod(test.first) == 0) test.second else test.third
			Pair(ind, it)
		}
	}

	private fun applyOperation(item: Item, partTwo: Boolean) {
		val g = operation.split("= ")
		val rightSide = g.last().split(" ")

		var a = if (rightSide[0] == "old") item.worry else rightSide[0].toLong()
		var b = if (rightSide[2] == "old") item.worry else rightSide[2].toLong()

		a %= magic
		b %= magic

		when (rightSide[1]) {
			"-" -> item.worry = a - b
			"+" -> item.worry = a + b
			"*" -> item.worry = a * b
			"/" -> item.worry = a / b
		}

		if (!partTwo) {
			item.worry /= 3
		}
	}

}

fun part1(monkies: List<Monkey>): Long {
	for (round in 1..20) {
		monkies.forEach {
			it.testItems().forEach {
				monkies[it.first].items.add(it.second)
			}
			it.items.clear()
		}

//		println("Round $round")
//		println(monkies.mapIndexed { index, monkey ->
//			"Monkey $index: ${monkey.items.map { it.worry.toString() }.joinToString { it }}"
//		}.joinToString("\n"){ it })
//		println()
	}

	val g = monkies.map { it.inspectAmount }.sortedDescending()
	return g[0] * g[1]
}

fun part2(monkies: List<Monkey>): Long {
	for (round in 1..10000) {
		monkies.forEach {
			it.testItems(true).forEach {
				monkies[it.first].items.add(it.second)
			}
			it.items.clear()
		}

//		println("Round $round")
//		println(monkies.mapIndexed { index, monkey ->
//			"Monkey $index: ${monkey.items.map { it.worry.toString() }.joinToString { it }}"
//		}.joinToString("\n"){ it })
//		println()

//		val z = when (round) {
//			1 -> true
//			20 -> true
//			1000 -> true
//			2000 -> true
//			3000 -> true
//			4000 -> true
//			5000 -> true
//			6000 -> true
//			7000 -> true
//			8000 -> true
//			9000 -> true
//			10000 -> true
//			else -> false
//		}
//
//		if (z) println(monkies.map { it.inspectAmount })
	}

	val g = monkies.map { it.inspectAmount }.sortedDescending()
	return g[0] * g[1]
}
