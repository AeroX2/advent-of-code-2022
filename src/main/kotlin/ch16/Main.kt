package ch16

import kotlin.collections.ArrayDeque
import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {
    val data = object {}.javaClass.getResource("/ch16/input.txt")?.readText()
    if (data == null) {
        println("Missing data file")
        return
    }

    val lines = data.split("\n")
    println(measureTimeMillis { part1(lines) })
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

fun backtrack(
    currValve: Valve,
    closedValves: List<Valve>,
    openValves: List<Valve>,
    timeLeft: Int = 30,
    currPressure: Int = 0
): Int {
    val currPressureRate = openValves.sumOf { it.rate }
    var maxPressure = currPressure + (timeLeft-1) * currPressureRate
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
            maxPressure = maxOf(maxPressure, pressure)
        }
    }

    return maxPressure
}

fun backtrack2(
    currValve: Valve,
    currElephantValve: Valve,
    closedValves: List<Valve>,
    timeLeft: Int = 26,
    currPressureRate: Int = 0,
    currPressure: Int = 0
): Int {
//    var maxPressure = currPressure + (timeLeft-1) * currPressureRate
//    for (possibleValve in closedValves) {
//        for (possibleElephantValve in closedValves.filter { it != possibleValve }) {
//            val d1 = currValve.distanceTo(possibleValve)
//            val d2 = currElephantValve.distanceTo(possibleElephantValve)
//            val md = minOf(d1, d2)
//            val d = maxOf(d1, d2)
//            if (timeLeft - d > 0) {
//                val closedValvesMod = closedValves.filter { it != possibleValve && it != possibleElephantValve }.toList()
//
//                val h = if (md == d1) possibleValve.rate else possibleElephantValve.rate
//                val l = if (md == d1) possibleElephantValve.rate else possibleValve.rate
//                val newPressure = currPressure + currPressureRate * (d-1) + h * abs(d1 - d2) + l
//
//                val newPressureRate = currPressureRate + possibleValve.rate + possibleElephantValve.rate
//                val pressure = backtrack2(
//                    possibleValve,
//                    possibleElephantValve,
//                    closedValvesMod,
//                    timeLeft - d,
//                    newPressureRate,
//                    newPressure,
//                )
//                maxPressure = maxOf(maxPressure, pressure)
//            }
//        }
//    }
//
//    return maxPressure
    return -1
}

fun part1(lines: List<String>): Int {
    val valves = parseLines(lines)

    val closedValves = valves.values.filter { it.rate != 0 }
    return backtrack(valves["AA"]!!, closedValves, listOf())
}
fun part2(lines: List<String>): Int {
    val valves = parseLines(lines)

    val closedValves = valves.values.filter { it.rate != 0 }
    return backtrack2(valves["AA"]!!, valves["AA"]!!, closedValves)
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

