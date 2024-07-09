package argent.data.yatzy

import kotlin.random.Random

object DiceThrowing {
    fun rollAll(): DiceSet {
        val dice = mutableMapOf<Int, Int>().withDefault { 0 }
        for (i in 1..5){
            val die = Random.nextInt(5) + 1
            dice[die] = dice[die]!! + 1
        }
        return DiceSet(dice)
    }

    fun reRoll(keptDice: DiceSet): DiceSet {
        val diceToRoll = 5 - keptDice.values.size
        assert(diceToRoll > 0)
        if(diceToRoll == 5){
            return rollAll()
        }
        val dice = keptDice.values.toMutableMap().withDefault { 0 }
        for (i in 1..diceToRoll){
            val die = Random.nextInt(5) + 1
            dice[die] = dice[die]!! + 1
        }
        return DiceSet(dice)
    }
}