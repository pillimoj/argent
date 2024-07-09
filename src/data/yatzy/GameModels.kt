package argent.data.yatzy

import kotlinx.serialization.Serializable

data class DiceSet(val values: Map<Int, Int>) {
    val one: Int get() = values.getValue(1)
    val two: Int get() = values.getValue(2)
    val three: Int get() = values.getValue(3)
    val four: Int get() = values.getValue(4)
    val five: Int get() = values.getValue(5)
    val six: Int get() = values.getValue(6)
    val sum: Int get() = values.toList().sumOf { it.first * it.second }
    fun isValid(): Boolean {
        return values.values.sum() == 5
    }
}

interface Goal {
    abstract fun valid(diceSet: DiceSet): Boolean
    abstract fun points(diceSet: DiceSet): Int
}

sealed interface YatzyGoal : Goal {
    companion object {
        val allGoals: List<YatzyGoal> by lazy {
            listOf(
                Ones,
                Twos,
                Threes,
                Fours,
                Fives,
                Sixes,
                OnePair,
                TwoPair,
                ThreeOfAKind,
                FourOfAKind,
                SmallStraight,
                BigStraight,
                FullHouse,
                Chance,
                Yatzy
            )
        }
    }
}

@Serializable
class YatzyRow(private val goal: YatzyGoal, var taken: Boolean = false, var score: Int = 0) : Goal by goal {
    fun take(diceSet: DiceSet) {
        assert(!taken)
        assert(goal.valid(diceSet))
        taken = true
        score = goal.points(diceSet)
    }

    fun cross() {
        assert(!taken)
        taken = true
    }
}

data object Ones : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.one > 0
    override fun points(diceSet: DiceSet) = diceSet.one
}

data object Twos : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.two > 0
    override fun points(diceSet: DiceSet) = diceSet.two
}

data object Threes : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.three > 0
    override fun points(diceSet: DiceSet) = diceSet.three
}

data object Fours : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.four > 0
    override fun points(diceSet: DiceSet) = diceSet.four
}

data object Fives : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.five > 0
    override fun points(diceSet: DiceSet) = diceSet.five
}

data object Sixes : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.six > 0
    override fun points(diceSet: DiceSet) = diceSet.six
}

data object OnePair : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.values.any { it.value >= 2 }
    override fun points(diceSet: DiceSet) = diceSet.values.filter { it.value >= 2 }.maxOf { it.key } * 2
}

data object TwoPair : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.values.count { it.value >= 2 } >= 2
    override fun points(diceSet: DiceSet) = diceSet.values.filter { it.value >= 2 }.map { it.key }.sum() * 2
}

data object ThreeOfAKind : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.values.any { it.value >= 3 }
    override fun points(diceSet: DiceSet): Int = diceSet.values.entries.first { it.value >= 3 }.value * 3
}

data object FourOfAKind : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.values.any { it.value >= 4 }
    override fun points(diceSet: DiceSet): Int = diceSet.values.entries.first { it.value >= 4 }.value * 4
}

data object SmallStraight : YatzyGoal {
    override fun valid(diceSet: DiceSet): Boolean {
        for (i in 1..5) {
            if (diceSet.values.getValue(i) != 1) return false
        }
        return true
    }

    override fun points(diceSet: DiceSet): Int = 15
}

data object BigStraight : YatzyGoal {
    override fun valid(diceSet: DiceSet): Boolean {
        for (i in 2..6) {
            if (diceSet.values.getValue(i) != 1) return false
        }
        return true
    }

    override fun points(diceSet: DiceSet): Int = 20
}

data object FullHouse : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.values.any { it.value == 2 } && diceSet.values.any { it.value == 3 }
    override fun points(diceSet: DiceSet) = diceSet.sum
}

data object Chance : YatzyGoal {
    override fun valid(diceSet: DiceSet) = true
    override fun points(diceSet: DiceSet) = diceSet.sum
}

data object Yatzy : YatzyGoal {
    override fun valid(diceSet: DiceSet) = diceSet.values.any { it.value >= 5 }
    override fun points(diceSet: DiceSet): Int = 50
}

@Serializable
class YatzyColumn(private val rows: Map<YatzyGoal, YatzyRow>) {
    fun getNumberScore(): Int {
        return rows[Ones]!!.score +
            rows[Twos]!!.score +
            rows[Threes]!!.score +
            rows[Fours]!!.score +
            rows[Fives]!!.score +
            rows[Sixes]!!.score
    }

    fun getScore(): Int {
        return rows.values.sumOf { it.score }
    }

    fun take(goal: YatzyGoal, diceSet: DiceSet) {
        rows[goal]!!.take(diceSet)
    }

    fun cross(goal: YatzyGoal) {
        rows[goal]!!.cross()
    }

    fun turnsLeft(){
        rows.count { !it.value.taken }
    }

    companion object {
        fun create(): YatzyColumn {
            return YatzyColumn(YatzyGoal.allGoals.associateWith { YatzyRow(it) })
        }
    }
}