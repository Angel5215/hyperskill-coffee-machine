package machine

class CoffeeMachine {
    private var availableMoney = 550
    private var availableWater = 400
    private var availableMilk = 540
    private var availableCoffee = 120
    private var availableCups = 9
    private var currentState = State.CHOOSING_ACTION

    val isRunning: Boolean
        get() = currentState != State.FINISHED_RUNNING

    val prompt: String
        get() = when (currentState) {
            State.CHOOSING_ACTION -> "Write action (buy, fill, take, remaining, exit):"
            State.CHOOSING_COFFEE -> "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:"
            State.FILLING_WATER -> "Write how many ml of water you want to add:"
            State.FILLING_MILK -> "Write how many ml of milk you want to add:"
            State.FILLING_COFFEE -> "Write how many grams of coffee beans you want to add:"
            State.FILLING_CUPS -> "Write how many disposable cups you want to add:"
            State.FINISHED_RUNNING -> ""
        }

    fun receive(input: String) {
        when (currentState) {
            State.CHOOSING_ACTION -> chooseAction(input)
            State.CHOOSING_COFFEE -> chooseCoffee(input)
            State.FILLING_WATER -> fillWater(input.toInt())
            State.FILLING_MILK -> fillMilk(input.toInt())
            State.FILLING_COFFEE -> fillCoffee(input.toInt())
            State.FILLING_CUPS -> fillCups(input.toInt())
            State.FINISHED_RUNNING -> return
        }
    }

    // MARK: - Private methods

    private fun chooseAction(action: String) {
        this.currentState = when (Action.valueOf(action.uppercase())) {
            Action.BUY -> State.CHOOSING_COFFEE
            Action.FILL -> State.FILLING_WATER
            Action.TAKE -> {
                takeMoney()
                State.CHOOSING_ACTION
            }
            Action.REMAINING -> {
                remainingAction()
                State.CHOOSING_ACTION
            }
            Action.EXIT -> State.FINISHED_RUNNING
        }
    }

    private fun chooseCoffee(action: String) {
        this.currentState = State.CHOOSING_ACTION
        val coffeeType = when (action) {
            ESPRESSO -> CoffeeType.ESPRESSO
            LATTE -> CoffeeType.LATTE
            CAPPUCCINO -> CoffeeType.CAPPUCCINO
            else -> return
        }
        tryCoffeePreparation(coffeeType.water, coffeeType.milk, coffeeType.quantity, coffeeType.cost)
    }

    private fun tryCoffeePreparation(water: Int, milk: Int, coffee: Int, money: Int) {
        if (hasEnoughSupplies(water, milk, coffee)) {
            makeCoffee(water, milk, coffee, money)
        } else {
            describeMissingSupplies(water, milk, coffee)
        }
    }

    private fun hasEnoughSupplies(water: Int, milk: Int, coffee: Int) =
        availableWater - water >= 0 && availableMilk - milk >= 0 && availableCoffee - coffee >= 0 && availableCups > 0

    private fun makeCoffee(water: Int, milk: Int, coffee: Int, money: Int) {
        availableWater -= water
        availableMilk -= milk
        availableCoffee -= coffee
        availableMoney += money
        --availableCups
        println("I have enough resources, making you a coffee!\n")
    }

    private fun describeMissingSupplies(water: Int, milk: Int, coffee: Int) {
        if (availableWater - water < 0) {
            println("Sorry, not enough water!")
        }

        if (availableMilk - milk < 0) {
            println("Sorry, not enough milk!")
        }

        if (availableCoffee - coffee < 0) {
            println("Sorry, not enough coffee!")
        }

        if (availableCups - 1 < 0) {
            println("Sorry, not enough cups!")
        }

        println()
    }

    private fun fillWater(quantity: Int) {
        this.availableWater += quantity
        this.currentState = State.FILLING_MILK
    }

    private fun fillMilk(quantity: Int) {
        this.availableMilk += quantity
        this.currentState = State.FILLING_COFFEE
    }

    private fun fillCoffee(quantity: Int) {
        this.availableCoffee += quantity
        this.currentState = State.FILLING_CUPS
    }

    private fun fillCups(quantity: Int) {
        this.availableCups += quantity
        this.currentState = State.CHOOSING_ACTION
    }

    private fun takeMoney() {
        println("\nI gave you \$${this.availableMoney}\n")
        this.availableMoney = 0
    }

    private fun remainingAction() {
        println()
        println("The coffee machine has:")
        println("${this.availableWater} ml of water")
        println("${this.availableMilk} ml of milk")
        println("${this.availableCoffee} g of coffee beans")
        println("${this.availableCups} disposable cups")
        println("\$${this.availableMoney} of money")
        println()
    }

    // MARK: - Helpers

    private enum class State {
        CHOOSING_ACTION,
        CHOOSING_COFFEE,
        FILLING_WATER,
        FILLING_MILK,
        FILLING_COFFEE,
        FILLING_CUPS,
        FINISHED_RUNNING
    }

    private enum class Action {
        BUY, FILL, TAKE, EXIT, REMAINING
    }

    private companion object {
        const val ESPRESSO = "1"
        const val LATTE = "2"
        const val CAPPUCCINO = "3"
        const val BACK = "back"
    }

    private enum class CoffeeType(val water: Int, val milk: Int, val quantity: Int, val cost: Int) {
        ESPRESSO(250, 0, 16, 4),
        LATTE(350, 75, 20, 7),
        CAPPUCCINO(200, 100, 12, 6);
    }
}

fun main() {
    val coffeeMachine = CoffeeMachine()
    while (coffeeMachine.isRunning) {
        println(coffeeMachine.prompt)
        val action = readln()
        coffeeMachine.receive(action)
    }
}

