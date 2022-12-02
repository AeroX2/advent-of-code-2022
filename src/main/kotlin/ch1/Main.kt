package ch1

fun main() {
    val data = object{}.javaClass.getResource("/ch1/input.txt")?.readText()
    if (data == null) {
        println("Missing data file");
        return;
    }

    val elves = data.split("\n\n")
    val weights = elves.map { it.split("\n").map { it.toInt() } }

    val maxWeight = weights.map { it.sum() }.maxOf { it }
    println(maxWeight)

    val maximumWeights = weights.map { it.sum() }.sortedDescending().take(3).sum()
    print(maximumWeights)
}