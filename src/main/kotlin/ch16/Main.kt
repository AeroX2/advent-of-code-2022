package ch16

import ch12.Coord
import ch12.directions
import ch12.plus
import ch12.score
import java.util.*
import kotlin.collections.ArrayDeque

fun main() {
    val data = object {}.javaClass.getResource("/ch16/example.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val lines = data.split("\n")
    println(part1(lines))
    println(part2(lines))
}

data class Valve(var name: String, var rate: Int = -1, var childValves: List<Valve> = listOf()) {
    override fun hashCode(): Int {
        return name.hashCode()
    }
    override fun toString(): String {
        return "Valve($name, $rate, ${childValves.map { it.name }})"
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

fun backtrack(currValve: Valve, path: List<Valve>, openValves: Set<Valve>, closedValves: Set<Valve>, depth: Int = 0): Pair<List<Valve>, List<Valve>> {
//    println(depth)
//    println(currValve.name)
    if (depth == 30) {
        return Pair(path, openValves.toList()) // .sumOf { it.rate }
    }

//    if (seen.contains(currValve.name)) {// && openValves.contains(currValve)) {
//        return openValves.sumOf { it.rate }
//    }
//    val newSeen = seen + currValve.name

    var maxPressure = Pair<Int, Pair<List<Valve>, List<Valve>>>(-1, Pair(listOf(), listOf()))
    for (valve in openValves) {
        val openValvesMod = openValves.filter { it.name != valve.name }.toSet()
        val d = pathFind(currValve, valve).size - 1
        if (depth + d <= 30) {
            val openV = backtrack(valve, path + valve, openValvesMod, closedValves + valve, depth+d)
            val openVPressure = openV.second.sumOf { it.rate }
            if (openVPressure > maxPressure.first) {
                maxPressure = Pair(openVPressure, openV)
            }
        }
    }

    if (!closedValves.contains(currValve)) {
        val openValvesMod = openValves.filter { it.name != currValve.name }.toSet()
        val openV = backtrack(currValve, path + currValve, openValvesMod, closedValves + currValve, depth+1)
        val openVPressure = openV.second.sumOf { it.rate }
        if (openVPressure > maxPressure.first) {
            maxPressure = Pair(openVPressure, openV)
        }
    }

    return maxPressure.second
}

fun part1(lines: List<String>): Int {
    val valves = parseLines(lines)

    val g = backtrack(valves["AA"]!!, listOf(valves["AA"]!!), valves.values.toSet(), setOf())
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

