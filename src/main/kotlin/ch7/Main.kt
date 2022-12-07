package ch7

fun main() {
    val data = object {}.javaClass.getResource("/ch7/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val rootNode = parse(data)
    println(rootNode.print())
    println(part1(rootNode))
    println(part2(rootNode))
}

fun parse(data: String): Node {
    val matches = """\$ ([a-z]+) ?(.*?)\n((.|\n)*?)(?=(\$|$))""".toRegex().findAll(data)

    val rootNode = Node(Type.ROOT, "/", null)
    var currentNode = rootNode
    for (match in matches) {
        val command = match.groups[1]?.value
        when (command) {
            "cd" -> {
                val directory = match.groups[2]?.value
                currentNode = when (directory) {
                    "/" -> rootNode
                    ".." -> currentNode.parentNode!!
                    else -> currentNode.nodes[directory]!!
                }
            }

            "ls" -> {
                val files = match.groups[3]?.value!!
                for (file in files.trim().split("\n")) {
                    val z = file.split(" ")
                    val sizeOrDir = z[0]
                    val name = z[1]
                    if (sizeOrDir == "dir") {
                        currentNode.nodes[name] = Node(Type.DIR, name, currentNode)
                    } else {
                        currentNode.nodes[name] = Node(Type.FILE, name, currentNode, sizeOrDir.toInt())
                    }
                }
            }
        }
    }
    return rootNode
}

enum class Type {
    ROOT,
    FILE,
    DIR,
}

data class Node(
    val type: Type,
    val name: String,
    val parentNode: Node?,
    val fileSize: Int = -1,
    val nodes: HashMap<String, Node> = hashMapOf(),
) {
    fun size(): Int {
        return when (type) {
            Type.ROOT, Type.DIR -> nodes.values.sumOf { it.size() }
            Type.FILE -> fileSize
        }
    }

    fun dirSizes(): List<Pair<Type, Int>> {
        return when (type) {
            Type.ROOT, Type.DIR -> {
                val g = nodes.values.flatMap { it.dirSizes() }
                return listOf(Pair(Type.DIR, size())) + g
            }
            Type.FILE -> listOf(Pair(Type.FILE, fileSize))
        }
    }

    fun print(depth: Int = 0): String {
        return " ".repeat(depth) + when (type) {
            Type.ROOT, Type.DIR -> "- $name (dir, size=${size()}) \n${nodes.values.joinToString("\n") { it.print(depth + 1) }}"
            Type.FILE -> "- $name (file, size=${fileSize})"
        }
    }
}

fun part1(rootNode: Node): Int {
    return rootNode.dirSizes()
        .filter { it.first == Type.DIR && it.second < 100000 }
        .sumOf { it.second }
}

fun part2(rootNode: Node): Int {
    val unused = 70000000 - rootNode.size()
    return rootNode.dirSizes()
        .filter { it.first == Type.DIR }
        .sortedBy { it.second }
        .find { it.second >= 30000000 - unused }?.second!!
}
