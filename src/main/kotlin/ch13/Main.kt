package ch13

fun main() {
    val data = object {}.javaClass.getResource("/ch13/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val pairs = data.split("\n\n").map {
        val v = it.split("\n");
        Pair(parse(v[0]), parse(v[1]))
    }
    println(part1(pairs))
    println(part2(pairs))
}

data class Node(
	val value: Int?,
	val children: MutableList<Node> = mutableListOf(),
	val divider: Boolean = false
) {
    override fun toString(): String = if (value != null) "$value" else "[${children.joinToString { it.toString() }}]"
}

fun parse(s: String, divider: Boolean = false): Node {
    val nodeStack = ArrayDeque<Node>()
    nodeStack.add(Node(null, mutableListOf(), divider))
    var number: Int? = null
    for (c in s) {
        if (c.isDigit()) {
            if (number == null) number = 0
            number *= 10
            number += c.digitToInt()
        } else if (c == ',') {
            if (number != null) {
                nodeStack.last().children.add(Node(number))
                number = null
            }
        } else if (c == '[') {
            nodeStack.add(Node(null))
        } else if (c == ']') {
            if (number != null) {
                nodeStack.last().children.add(Node(number))
                number = null
            }

            val n = nodeStack.removeLast()
            nodeStack.last().children.add(n)
        }
    }
    return nodeStack.last()
}

fun check(left: Node, right: Node): Int {
    if (left.value != null && right.value != null)
        return right.value - left.value

    if (left.value == null && right.value == null) {
        for (i in left.children.indices) {
            val leftSide = left.children[i]
            val rightSide = right.children.getOrNull(i) ?: return -1

			val v = check(leftSide, rightSide)
            if (v != 0) return v
        }
        return right.children.size - left.children.size
    }

	return if (left.value != null) {
		check(Node(null, mutableListOf(Node(left.value))), right)
	} else {
		check(left, Node(null, mutableListOf(Node(right.value))))
	}
}

fun part1(pairs: List<Pair<Node, Node>>): Int {
    return pairs.foldIndexed(0) { index, acc, pair ->
        acc + if (check(pair.first, pair.second) > 0) index + 1 else 0
    }
}

fun part2(pairs: List<Pair<Node, Node>>): Int {
    val divider = Pair(parse("[[2]]", true), parse("[[6]]", true))
    val arr = (pairs + divider).flatMap { listOf(it.first, it.second) }
    val sortedArr = arr.sortedWith { a, b -> check(b, a) }
    return sortedArr.withIndex().filter { it.value.divider }.map { it.index + 1 }.reduce { acc, v -> acc * v }
}
