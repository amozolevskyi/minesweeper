package minesweeper

import kotlin.random.Random

const val rows = 9
const val columns = 9

var fieldList = Array(rows) { Array(columns) { '.' } }
var currentField = Array(rows) { Array(columns) { '.' } }
var minesSet = emptySet<List<Int>>()

var firstMove = true
var mines = 0
var isMine = false

fun main() {
    print("How many mines do you want on the field? ")
    mines = readln().toInt()

    printGameField(currentField)

    startGame()
}

fun startGame() {
    loop@ while (true) {
        print("Set/unset mines marks or claim a cell as free: ")
        val (yStr, xStr, parameter) = readln().split(" ")

        val x = xStr.toInt() - 1
        val y = yStr.toInt() - 1

        if (firstMove) {
            initGame(mines, x, y)
            firstMove = false
        }

        val xy = fieldList[x][y]

        when {
            parameter == "mine" -> if (currentField[x][y] == '.') currentField[x][y] = '*' else currentField[x][y] = '.'

            parameter == "free" && xy.isDigit() -> currentField[x][y] = fieldList[x][y]

            parameter == "free" && xy == '.' -> openEmptyCells(x, y)

            parameter == "free" && xy == 'X' -> setMines()
        }

        printGameField(currentField)

        if (isMine) {
            println("You stepped on a mine and failed!")
            break
        }

        if (isGameFinish()) {
            println("Congratulations! You found all the mines!")
            break
        }
    }
}

fun setMines(){
    minesSet.forEach { currentField[it.first()][it.last()] = 'X' }
    isMine = true
}

fun isGameFinish(): Boolean {
    var hit = 0
    var last = 0
    var safeCells = 0

    for (arr in currentField) {
        safeCells += arr.count { it == '.' }
    }

    for (x in 0 until rows) {
        for (y in 0 until columns) {
            val list = listOf(x, y)

            if (currentField[x][y] == '*' && list in minesSet) hit++
            if (currentField[x][y] == '.' && list in minesSet) last++
        }
    }

    return hit == minesSet.size || (safeCells == minesSet.size && last == minesSet.size)
}

fun openEmptyCells(x: Int, y: Int){
    if (x < 0 || x > rows - 1 || y < 0 || y > columns - 1) return

    if (currentField[x][y] != '.' && currentField[x][y] != '*') return

    currentField[x][y] = if (fieldList[x][y] == '.') '/' else fieldList[x][y]

    if (currentField[x][y] == '/') {
        openEmptyCells(x - 1 , y - 1)
        openEmptyCells(x - 1, y)
        openEmptyCells(x - 1, y + 1)
        openEmptyCells(x, y - 1)
        openEmptyCells(x, y + 1)
        openEmptyCells(x + 1, y - 1)
        openEmptyCells(x + 1, y)
        openEmptyCells(x + 1, y + 1)
    }
}

fun insertNumbers(x: Int, y: Int) {
    if (x < 0 || x > rows - 1 || y < 0 || y > columns - 1) return

    if (fieldList[x][y] == 'X') return

    fieldList[x][y] = if (fieldList[x][y] == '.') '1' else (fieldList[x][y].code + 1).toChar()
}

fun insertMines(x: Int, y: Int){
    fieldList[x][y] = 'X'

    insertNumbers(x - 1 , y - 1)
    insertNumbers(x - 1, y)
    insertNumbers(x - 1, y + 1)
    insertNumbers(x, y - 1)
    insertNumbers(x, y + 1)
    insertNumbers(x + 1, y - 1)
    insertNumbers(x + 1, y)
    insertNumbers(x + 1, y + 1)
}

fun initGame(minesNumber: Int, x: Int, y: Int) {
    minesSet = minesList(minesNumber, rows, columns, x, y)
    minesSet.forEach { insertMines(it.first(), it.last()) }
}

fun printGameField(fieldList: Array<Array<Char>>) {
    print(" |${(1..columns).joinToString("")}|\n")
    print("-|${"-".repeat(columns)}|\n")
    fieldList.forEach { println("${fieldList.indexOf(it) + 1}|${it.joinToString("")}|") }
    print("-|${"-".repeat(columns)}|\n")
}

fun minesList(setSize: Int, rows: Int, columns: Int, x: Int, y: Int): Set<List<Int>> {
    val set = mutableSetOf<List<Int>>()
    val input = listOf(x, y)

    do {
        set += getRandomXY(rows, columns)
    } while (set.size < setSize)

    // no user input x y
    while (input in set) {
        set.remove(input)
        set += getRandomXY(rows, columns)
    }

    return set
}

fun getRandomXY(x: Int, y: Int): List<Int> = listOf(Random.nextInt(0, x), Random.nextInt(0, y))