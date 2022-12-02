package ch2
fun main() {
    val data = object{}.javaClass.getResource("/ch2/input.txt")?.readText()
    if (data == null) {
        println("Missing data file");
        return;
    }
    val rounds = data.trim().split("\n")

    println(part1(rounds))
    println(part2(rounds))
}

fun part1(rounds: List<String>): Int {
    val winMap = mapOf(
        "A X" to "draw",
        "A Y" to "win",
        "A Z" to "loss",
        "B X" to "loss",
        "B Y" to "draw",
        "B Z" to "win",
        "C X" to "win",
        "C Y" to "loss",
        "C Z" to "draw"
    )

    var score = 0
    for (round in rounds) {
        when (winMap[round]) {
            "win" -> {
                score += 6
            }
            "loss" -> {
                score += 0
            }
            "draw" -> {
                score += 3
            }
        }

        when (round.last()) {
            'X' -> {
                score += 1
            }
            'Y' -> {
                score += 2
            }
            'Z' -> {
                score += 3
            }
        }
    }
    return score
}

fun part2(rounds: List<String>): Int {
    // A rock, B paper, C scissors
    // X loss, Y draw, Z win
    val scoreMap = mapOf(
        "A X" to 3,
        "A Y" to 1,
        "A Z" to 2,
        "B X" to 1,
        "B Y" to 2,
        "B Z" to 3,
        "C X" to 2,
        "C Y" to 3,
        "C Z" to 1
    )

    var score = 0
    for (round in rounds) {
        val s1 = scoreMap[round]
        if (s1 == null) {
            println("Invalid!")
            continue
        }
        var s2 = 0;
        when (round.last()) {
            'X' -> {
                s2 += 0
            }
            'Y' -> {
                s2 += 3
            }
            'Z' -> {
                s2 += 6
            }
            else -> {
                println("Invalid!")
                continue
            }
        }
        score += s1 + s2
    }
    return score
}
