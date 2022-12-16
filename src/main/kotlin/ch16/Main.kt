package ch16

import kotlin.collections.ArrayDeque

fun main() {
    val data = object {}.javaClass.getResource("/ch16/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val lines = data.split("\n")
    println(part1(lines))
    println(part2(lines))
}

data class Valve(var name: String, var rate: Int = -1, var childValves: List<Valve> = listOf()) {
    private val distanceToMap: MutableMap<Valve, Int> = mutableMapOf()
    override fun hashCode(): Int {
        return name.hashCode()
    }
    override fun toString(): String {
        return "Valve($name, $rate, ${childValves.map { it.name }})"
    }

    fun distanceTo(end: Valve): Int {
        return distanceToMap.getOrPut(end) { pathFind(this, end).size-1 }
    }
}
fun parseLines(lines: List<String>): Map<String, Valve> {
    val valves = mutableMapOf<String, Valve>()
    lines.forEach {
        val r = """Valve ([A-Z]+?) has flow rate=(\d+); tunnels? leads? to valves? (.+)""".toRegex()
        val m = r.matchEntire(it)!!.groups

        val name = m[1]!!.value
        val rate = m[2]!!.value.toInt()
        val childValves = m[3]!!.value.split(", ")

        val valve = valves.getOrPut(name) { Valve(name) }
        valve.rate = rate
        valve.childValves = childValves.map { valves.getOrPut(it) { Valve(it) } }
//        valves[name] = Valve(name, newRate, zv)
    }
    return valves.toMap()
}

//data class Step(val path: List<Valve> = listOf(), val seen: Set<Valve> = setOf())

fun backtrack(currValve: Valve, path: List<Valve>, openValves: Set<Valve>, closedValves: Set<Valve>, depth: Int = 30, pressure: Int = 0): Pair<List<Valve>, Int> {
    if (depth <= 0) {
        return Pair(path, pressure) // .sumOf { it.rate }
    }

    var maxPressure = Pair(-1, Pair(path, pressure))
    for (valve in closedValves) {
        val closedValvesMod = closedValves.filter { it != valve }.toSet()
//        val d = pathFind(currValve, valve).size - 1
        val d = currValve.distanceTo(valve)+1
        if (depth - d >= 0) {
            val openV = backtrack(valve, path + valve, openValves + valve, closedValvesMod,  depth-d, pressure + openValves.sumOf { it.rate } * d)
            val openVPressure = openV.second
            if (openVPressure > maxPressure.first) {
                maxPressure = Pair(openVPressure, openV)
            }
        }
    }

    return maxPressure.second
}

fun part1(lines: List<String>): Int {
    val valves = parseLines(lines)

    val closedValves = valves.values.toSet()
    val g = backtrack(valves["AA"]!!, listOf(valves["AA"]!!), setOf(), closedValves)
    println(g)
    return 0
}
fun part2(lines: List<String>): Int {
    return -1
}

fun pathFind(start: Valve, end: Valve): List<Valve> {
    val stack = ArrayDeque<Valve>()
    stack.add(start)
    val seen = mutableSetOf(start)

    val costFromStart = mutableMapOf(start to 0)
    val cameFrom = mutableMapOf<Valve, Valve>()
    val estimatedTotalCost = mutableMapOf(start to 1)

    while (stack.isNotEmpty()) {
        val valve = stack.removeFirst()

        if (valve.name == end.name) {
            return generatePath(valve, cameFrom)
        }

        for (newValve in valve.childValves) {
            if (seen.contains(newValve)) {
                continue
            }

            val score = costFromStart.getValue(valve) + 1
            if (score < costFromStart.getOrDefault(newValve, 999999)) {
                if (!stack.contains(newValve)) {
                    stack.add(newValve)
                }
                cameFrom[newValve] = valve
                costFromStart[newValve] = score
                estimatedTotalCost[newValve] = score + 1 //score(newCoord, end)
            }
        }
    }
    return listOf()
}

fun generatePath(currentValve: Valve, cameFrom: MutableMap<Valve, Valve>): List<Valve> {
    val path = mutableListOf(currentValve)
    var current = currentValve
    while (cameFrom.containsKey(current)) {
        current = cameFrom.getValue(current)
        path.add(0, current)
    }
    return path.toList()
}

