package ch10

import kotlin.math.abs

fun main() {
	val data = object {}.javaClass.getResource("/ch10/input.txt")?.readText()
	if (data == null) {
		println("Missing data file")
		return
	}

	val commands = data.split("\n")
	println(part1(commands))
	println(part2(commands))
}

class Computer {
	open class Command(private val initialCycle: Int) {
		var cycleAmount = initialCycle
		fun reset() {
			cycleAmount = initialCycle
		}
		open operator fun invoke(registers: HashMap<String, Int>) { }
	}
	class Noop: Command(1) {
		override operator fun invoke(registers: HashMap<String, Int>) { cycleAmount-- }
	}

	class AddX(private val value: Int): Command(2) {
		override operator fun invoke(registers: HashMap<String, Int>) {
			cycleAmount--
			if (cycleAmount == 0) registers["X"] = registers["X"]!! + value
		}
	}

	private val commands = mutableListOf<Command>()
	private var currentCommandInd = 0
	val registers = hashMapOf("X" to 1)

	fun parse(commands: List<String>) {
		for (command in commands) {
			val c = command.split(" ")
			val opp = c[0]

			when (opp) {
				"noop" -> this.commands.add(Noop())
				"addx" -> this.commands.add(AddX(c[1].toInt()))
			}
		}
	}

	fun execute() {
		var currentCommand = commands[currentCommandInd]
		if (currentCommand.cycleAmount == 0) {
			currentCommandInd = (currentCommandInd + 1) % commands.size
			currentCommand = commands[currentCommandInd]
			currentCommand.reset()
		}
		currentCommand(registers)
	}
}

fun part1(commands: List<String>): Int {
	val computer = Computer()
	computer.parse(commands)

	var signalStrengthSum = 0
	for (cycle in 1..220) {
		if ((cycle - 20) % 40 == 0) {
			signalStrengthSum += cycle * computer.registers["X"]!!
		}
		computer.execute()
	}

	return signalStrengthSum
}

fun part2(commands: List<String>): String {
	val computer = Computer()
	computer.parse(commands)

	var crt = ""
	for (cycle in 0 until 240) {
		val hPos = cycle % 40
		val rPos = computer.registers["X"]!!
		crt += if (abs(hPos - rPos) <= 1) {
			'#'
		} else {
			' '
		}

		computer.execute()
		if (hPos == 40-1) {
			crt += "\n"
		}
	}
	return crt
}
