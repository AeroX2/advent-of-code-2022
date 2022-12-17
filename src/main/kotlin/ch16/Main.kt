package ch16

import kotlin.collections.ArrayDeque
import kotlin.math.max
import kotlin.system.measureTimeMillis

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
        return distanceToMap.getOrPut(end) { pathFind(this, end).size }
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
    }
    return valves.toMap()
}

typealias Path = List<Pair<Set<Valve>, Int>>

fun backtrack(
    currValve: Valve,
    closedValves: List<Valve>,
    openValves: List<Valve>,
    timeLeft: Int = 30,
    currPressure: Int = 0
): Path {
    val currPressureRate = openValves.sumOf { it.rate }

    val pressureIfNothingHappens = currPressure + (timeLeft-1) * currPressureRate
    val r = mutableListOf(Pair(openValves.toSet(), pressureIfNothingHappens))
    for (valve in closedValves) {
        val d = currValve.distanceTo(valve)
        if (timeLeft - d > 0) {
            val newPressureRate = currPressureRate + valve.rate
            val closedValvesMod = closedValves.filter { it != valve }.toList()
            val pressure = backtrack(
                valve,
                closedValvesMod,
                openValves + valve,
                timeLeft-d,
                currPressure + currPressureRate * (d-1) + newPressureRate
            )
            r.addAll(pressure)
//            maxPressure = maxOf(maxPressure, pressure)

        }
    }
    return r.toList()
}

fun part1(lines: List<String>): Int {
    val valves = parseLines(lines)

    val closedValves = valves.values.filter { it.rate != 0 }
    val paths = backtrack(valves["AA"]!!, closedValves, listOf())
    return paths.maxOf { it.second }
}
fun part2(lines: List<String>): Int {
    val valves = parseLines(lines)

    val closedValves = valves.values.filter { it.rate != 0 }
    val paths = backtrack(valves["AA"]!!, closedValves, listOf(), 26).sortedByDescending { it.second }.asSequence()

    var maxPressure = 0
    paths.forEachIndexed { i, p1 ->
        paths.forEachIndexed { ii, p2 ->
            if (i > ii) {
                return@forEachIndexed
            }
            val openedValves1 = p1.first
            val pressureReleased1 = p1.second
            if (pressureReleased1 * 2 < maxPressure) {
                return@forEachIndexed
            }

            val openedValves2 = p2.first
            val pressureReleased2 = p2.second
            if (pressureReleased1 + pressureReleased2 > maxPressure &&
                openedValves1.intersect(openedValves2).isEmpty()
            ) {
                maxPressure = pressureReleased1 + pressureReleased2
            }
        }
    }
    return maxPressure
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

