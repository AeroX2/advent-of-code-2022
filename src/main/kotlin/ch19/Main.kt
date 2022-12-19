package ch19

fun main() {
    val data = object {}.javaClass.getResource("/ch19/example.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val blueprints = parseData(data)

    println(part1(blueprints))
    println(part2(blueprints))
}

typealias Material = String

data class Robot(val type: String, val cost: Map<Material, Int>)

data class Blueprint(val robots: List<Robot>)
fun parseData(data: String): List<Blueprint> {
    return data.split("\n\n").map {
        val r = """Each (.+?) robot costs (\d+) (.+?)(?: and (\d+) (.+?))?\.""".toRegex()
        Blueprint(r.findAll(it).map {
            val type = it.groups[1]!!.value
            val cost = mapOf(it.groups[3]!!.value to it.groups[2]!!.value.toInt()) +
                    if (it.groups[4] != null)
                        mapOf(it.groups[5]!!.value to it.groups[4]!!.value.toInt())
                    else mapOf()
          Robot(type, cost)
        }.toList())
    }
}

fun <T> addToMap(m: Map<T, Int>, type: T, amount: Int): Map<T, Int> {
    val currAmount = m.getOrDefault(type, 0)
    return m + (type to (currAmount + amount))
}

fun <T> minusFromMap(m: Map<T, Int>, type: T, amount: Int): Map<T, Int> {
    val currAmount = m.getOrDefault(type, 0)
    return m + (type to (currAmount - amount))
}
fun backtrack(
    blueprint: Blueprint,
    robots: Map<Robot, Int>,
    materials: Map<Material, Int>,
    timeLeft: Int
): Int {
    // Spend materials
    var maxGeodes = materials.getOrDefault("geode", 0)
    if (timeLeft == 0) {
        return maxGeodes
    }

    for (robot in blueprint.robots.reversed()) {
        if (robot.cost.entries
            .all { materials.getOrDefault(it.key, 0) >= it.value }
        ) {
            val newMaterials = robot.cost.entries.fold(materials) { materialsMap, (material, amount) ->
                minusFromMap(materialsMap, material, amount)
            }
            // TODO: Probably better way of doing this
            val currentMaterials = robots.entries.fold(newMaterials) { materialsMap, (robot, amount) ->
                addToMap(materialsMap, robot.type, amount)
            }
            val geodes = backtrack(
                blueprint,
                addToMap(robots, robot, 1),
                currentMaterials,
                timeLeft - 1,
            )
            maxGeodes = maxOf(maxGeodes, geodes)
            break
        }
    }

    val currentMaterials = robots.entries.fold(materials) { materialsMap, (robot, amount) ->
        addToMap(materialsMap, robot.type, amount)
    }

    maxGeodes = maxOf(maxGeodes, backtrack(
        blueprint,
        robots,
        currentMaterials,
        timeLeft - 1,
    ))

    return maxGeodes
}

fun part1(blueprints: List<Blueprint>): Int {
    return blueprints.map { backtrack(
        it,
        mapOf(Robot("ore", mapOf()) to 1),
        mapOf(),
        24
    ) }.withIndex().sumOf { it.index+1 * it.value }
}

fun part2(blueprints: List<Blueprint>): Int {
    return -1
}
