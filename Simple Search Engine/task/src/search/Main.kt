package search

import java.io.File

fun main(args: Array<String>) {

    val fileNameIdx = args.indexOf("--data") + 1
    if (fileNameIdx == 0) return

    val fileName = args[fileNameIdx]
    val lines = peopleList(fileName)
    val invertedIndex = createInvertedIdx(lines)

    while (true) {
        when (menu()) {
            0 -> return
            1 -> findPerson(lines, invertedIndex)
            2 -> printAll(lines)
        }
    }
}

fun createInvertedIdx(lines: List<String>): Map<String, Set<Int>> {
    val invertedIdx = mutableMapOf<String, MutableSet<Int>>()
    for (i in lines.indices) {
        val words = lines[i].lowercase().split("\\s+".toRegex())
        for(word in words) {
            if (invertedIdx.containsKey(word)) {
                invertedIdx[word]!!.add(i)
            } else {
                invertedIdx.put(word, mutableSetOf(i))
            }
        }
    }
    return invertedIdx
}

fun printAll(lines: List<String>) {
    println("=== List of people ===")
    for (line in lines)
        println(line)

    println()
}

fun findPerson(lines: List<String>, invertedIdx: Map<String, Set<Int>>) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = readln()
    println()

    println("Enter a name or email to search all matching people.")
    val searchWords = readln().lowercase().split("\\s+".toRegex())
    println()

//    var results = mutableListOf<Set<Int>>()
    var results: Set<Int>? = null
    if (strategy == "NONE") {
        results = (0 .. lines.lastIndex).toSet()
    }

    for(word in searchWords) {
        if (invertedIdx.containsKey(word)) {
            when(strategy) {
                "ALL" -> {
                    if (results == null) {
                        results = invertedIdx[word]!!
                    } else {
                        results = results intersect invertedIdx[word]!!
                    }
                }
                "ANY" -> {
                    if (results == null) {
                        results = invertedIdx[word]!!
                    } else {
                        results = results union invertedIdx[word]!!
                    }
                }
                "NONE" -> {
                    results = results!! subtract invertedIdx[word]!!
                }
            }
        }
    }

    if ((results != null) && results.isNotEmpty()) {
        val linesCnt = results.size
        println("$linesCnt person${if (linesCnt > 1) "s" else ""} found:")

        for(idx in results) {
            println(lines[idx])
        }
    } else {
        println("No matching people found.")
    }
    println()
}

fun peopleList(fileName: String): List<String> {
    return File(fileName).readLines()
}

fun menu(): Int {
    while (true) {
        println("=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")

        val rc = readln().toInt()
        println()

        if (rc in 0..2) {
            return rc
        } // else
        println("Incorrect option! Try again.")
    }

}