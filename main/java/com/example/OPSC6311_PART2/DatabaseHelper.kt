package com.example.OPSC6311_PART2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "budget_tracker.db"
        private const val DATABASE_VERSION = 3

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_PASSWORD = "password"

        // Category table
        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_CATEGORY_ID = "id"
        private const val COLUMN_CATEGORY_NAME = "name"
        private const val COLUMN_CATEGORY_COLOR = "color"
        private const val COLUMN_CATEGORY_BUDGET = "budget"
        private const val COLUMN_CATEGORY_USER_ID = "user_id"
        private const val COLUMN_CATEGORY_MIN_GOAL = "min_goal"
        private const val COLUMN_CATEGORY_MAX_GOAL = "max_goal"

        // Expense table
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_EXPENSE_ID = "id"
        private const val COLUMN_EXPENSE_AMOUNT = "amount"
        private const val COLUMN_EXPENSE_DESCRIPTION = "description"
        private const val COLUMN_EXPENSE_DATE = "date"
        private const val COLUMN_EXPENSE_CATEGORY_ID = "category_id"
        private const val COLUMN_EXPENSE_USER_ID = "user_id"
        private const val COLUMN_EXPENSE_IMAGE_PATH = "image_path"

        // Budget table
        private const val TABLE_BUDGETS = "budgets"
        private const val COLUMN_BUDGET_ID = "id"
        private const val COLUMN_BUDGET_AMOUNT = "amount"
        private const val COLUMN_BUDGET_PERIOD = "period"
        private const val COLUMN_BUDGET_START_DATE = "start_date"
        private const val COLUMN_BUDGET_END_DATE = "end_date"
        private const val COLUMN_BUDGET_USER_ID = "user_id"

        // Income table
        private const val TABLE_INCOME = "income"
        private const val COLUMN_INCOME_ID = "id"
        private const val COLUMN_INCOME_AMOUNT = "amount"
        private const val COLUMN_INCOME_SOURCE = "source"
        private const val COLUMN_INCOME_NOTE = "note"
        private const val COLUMN_INCOME_DATE = "date"
        private const val COLUMN_INCOME_USER_ID = "user_id"

        // Achievements table for gamification
        private const val TABLE_ACHIEVEMENTS = "achievements"
        private const val COLUMN_ACHIEVEMENT_ID = "id"
        private const val COLUMN_ACHIEVEMENT_NAME = "name"
        private const val COLUMN_ACHIEVEMENT_DESCRIPTION = "description"
        private const val COLUMN_ACHIEVEMENT_TYPE = "type"
        private const val COLUMN_ACHIEVEMENT_REQUIREMENT = "requirement"
        private const val COLUMN_ACHIEVEMENT_ICON = "icon"

        // User achievements table
        private const val TABLE_USER_ACHIEVEMENTS = "user_achievements"
        private const val COLUMN_USER_ACHIEVEMENT_ID = "id"
        private const val COLUMN_USER_ACHIEVEMENT_USER_ID = "user_id"
        private const val COLUMN_USER_ACHIEVEMENT_ACHIEVEMENT_ID = "achievement_id"
        private const val COLUMN_USER_ACHIEVEMENT_DATE_EARNED = "date_earned"
        private const val COLUMN_USER_ACHIEVEMENT_IS_UNLOCKED = "is_unlocked"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DatabaseHelper", "Creating database tables")

        // Create users table
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT,
                $COLUMN_USER_EMAIL TEXT UNIQUE,
                $COLUMN_USER_PASSWORD TEXT
            )
        """.trimIndent()
        db.execSQL(createUserTable)
        Log.d("DatabaseHelper", "Users table created")

        // Create categories table with min/max goals
        val createCategoryTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT,
                $COLUMN_CATEGORY_COLOR TEXT,
                $COLUMN_CATEGORY_BUDGET REAL,
                $COLUMN_CATEGORY_MIN_GOAL REAL DEFAULT 0,
                $COLUMN_CATEGORY_MAX_GOAL REAL DEFAULT 0,
                $COLUMN_CATEGORY_USER_ID INTEGER,
                FOREIGN KEY($COLUMN_CATEGORY_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        db.execSQL(createCategoryTable)
        Log.d("DatabaseHelper", "Categories table created")

        // Create expenses table
        val createExpenseTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXPENSE_AMOUNT REAL,
                $COLUMN_EXPENSE_DESCRIPTION TEXT,
                $COLUMN_EXPENSE_DATE TEXT,
                $COLUMN_EXPENSE_CATEGORY_ID INTEGER,
                $COLUMN_EXPENSE_USER_ID INTEGER,
                $COLUMN_EXPENSE_IMAGE_PATH TEXT,
                FOREIGN KEY($COLUMN_EXPENSE_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID),
                FOREIGN KEY($COLUMN_EXPENSE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        db.execSQL(createExpenseTable)
        Log.d("DatabaseHelper", "Expenses table created")

        // Create budgets table
        val createBudgetTable = """
            CREATE TABLE $TABLE_BUDGETS (
                $COLUMN_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BUDGET_AMOUNT REAL,
                $COLUMN_BUDGET_PERIOD TEXT,
                $COLUMN_BUDGET_START_DATE TEXT,
                $COLUMN_BUDGET_END_DATE TEXT,
                $COLUMN_BUDGET_USER_ID INTEGER,
                FOREIGN KEY($COLUMN_BUDGET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        db.execSQL(createBudgetTable)
        Log.d("DatabaseHelper", "Budgets table created")

        // Create income table
        val createIncomeTable = """
            CREATE TABLE $TABLE_INCOME (
                $COLUMN_INCOME_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INCOME_AMOUNT REAL NOT NULL,
                $COLUMN_INCOME_SOURCE TEXT NOT NULL,
                $COLUMN_INCOME_NOTE TEXT,
                $COLUMN_INCOME_DATE TEXT NOT NULL,
                $COLUMN_INCOME_USER_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_INCOME_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """.trimIndent()
        db.execSQL(createIncomeTable)
        Log.d("DatabaseHelper", "Income table created")

        // Create achievements table
        val createAchievementsTable = """
            CREATE TABLE $TABLE_ACHIEVEMENTS (
                $COLUMN_ACHIEVEMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACHIEVEMENT_NAME TEXT,
                $COLUMN_ACHIEVEMENT_DESCRIPTION TEXT,
                $COLUMN_ACHIEVEMENT_TYPE TEXT,
                $COLUMN_ACHIEVEMENT_REQUIREMENT REAL,
                $COLUMN_ACHIEVEMENT_ICON TEXT
            )
        """.trimIndent()
        db.execSQL(createAchievementsTable)
        Log.d("DatabaseHelper", "Achievements table created")

        // Create user achievements table
        val createUserAchievementsTable = """
            CREATE TABLE $TABLE_USER_ACHIEVEMENTS (
                $COLUMN_USER_ACHIEVEMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ACHIEVEMENT_USER_ID INTEGER,
                $COLUMN_USER_ACHIEVEMENT_ACHIEVEMENT_ID INTEGER,
                $COLUMN_USER_ACHIEVEMENT_DATE_EARNED TEXT,
                $COLUMN_USER_ACHIEVEMENT_IS_UNLOCKED INTEGER DEFAULT 0,
                FOREIGN KEY($COLUMN_USER_ACHIEVEMENT_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_USER_ACHIEVEMENT_ACHIEVEMENT_ID) REFERENCES $TABLE_ACHIEVEMENTS($COLUMN_ACHIEVEMENT_ID)
            )
        """.trimIndent()
        db.execSQL(createUserAchievementsTable)
        Log.d("DatabaseHelper", "User achievements table created")

        // Insert default achievements
        insertDefaultAchievements(db)
    }

    private fun insertDefaultAchievements(db: SQLiteDatabase) {
        val achievements = listOf(
            arrayOf("Budget Master", "Set your first budget", "budget", "1", "🏆"),
            arrayOf("Saver Extraordinaire", "Save over 20% of your income in a month", "savings", "20", "💰"),
            arrayOf("Consistency King", "Log expenses for 7 days in a row", "streak", "7", "👑"),
            arrayOf("Category Champion", "Stay within budget for all categories", "categories", "1", "⭐"),
            arrayOf("Expense Tracker", "Add 10 expenses", "expenses", "10", "📝"),
            arrayOf("Budget Pro", "Stay under budget for 3 months", "budget", "3", "🎯")
        )

        for (achievement in achievements) {
            val values = ContentValues().apply {
                put(COLUMN_ACHIEVEMENT_NAME, achievement[0])
                put(COLUMN_ACHIEVEMENT_DESCRIPTION, achievement[1])
                put(COLUMN_ACHIEVEMENT_TYPE, achievement[2])
                put(COLUMN_ACHIEVEMENT_REQUIREMENT, achievement[3].toDouble())
                put(COLUMN_ACHIEVEMENT_ICON, achievement[4])
            }
            db.insert(TABLE_ACHIEVEMENTS, null, values)
        }
        Log.d("DatabaseHelper", "Default achievements inserted")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "Upgrading database from $oldVersion to $newVersion")
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE_CATEGORIES ADD COLUMN $COLUMN_CATEGORY_MIN_GOAL REAL DEFAULT 0")
                db.execSQL("ALTER TABLE $TABLE_CATEGORIES ADD COLUMN $COLUMN_CATEGORY_MAX_GOAL REAL DEFAULT 0")
                Log.d("DatabaseHelper", "Added min/max goal columns to categories")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error upgrading database", e)
            }
        }
    }

    // ==================== USER OPERATIONS ====================

    fun addUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, user.name)
            put(COLUMN_USER_EMAIL, user.email)
            put(COLUMN_USER_PASSWORD, user.password)
        }
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        Log.d("DatabaseHelper", "User added with ID: $id")
        return id
    }

    fun getUser(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD),
            "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(email, password),
            null, null, null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun checkUser(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // ==================== CATEGORY OPERATIONS ====================

    fun addCategory(category: Category): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.name)
            put(COLUMN_CATEGORY_COLOR, category.color)
            put(COLUMN_CATEGORY_BUDGET, category.budget)
            put(COLUMN_CATEGORY_MIN_GOAL, category.minGoal)
            put(COLUMN_CATEGORY_MAX_GOAL, category.maxGoal)
            put(COLUMN_CATEGORY_USER_ID, category.userId)
        }
        val id = db.insert(TABLE_CATEGORIES, null, values)
        db.close()
        Log.d("DatabaseHelper", "Category added with ID: $id")
        return id
    }

    fun updateCategory(category: Category): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, category.name)
            put(COLUMN_CATEGORY_COLOR, category.color)
            put(COLUMN_CATEGORY_BUDGET, category.budget)
            put(COLUMN_CATEGORY_MIN_GOAL, category.minGoal)
            put(COLUMN_CATEGORY_MAX_GOAL, category.maxGoal)
        }
        val result = db.update(
            TABLE_CATEGORIES,
            values,
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(category.id.toString())
        )
        db.close()
        Log.d("DatabaseHelper", "Category updated, rows affected: $result")
        return result
    }

    fun deleteCategory(categoryId: Long): Int {
        val db = writableDatabase
        val result = db.delete(
            TABLE_CATEGORIES,
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(categoryId.toString())
        )
        db.close()
        Log.d("DatabaseHelper", "Category deleted, rows affected: $result")
        return result
    }
    // Add this method to DatabaseHelper.kt in the Statistics and summary methods section

    fun getTotalExpensesByCategory(userId: Long): List<CategoryExpenseSummary> {
        val summaries = mutableListOf<CategoryExpenseSummary>()
        val db = this.readableDatabase

        val query = """
        SELECT c.$COLUMN_CATEGORY_ID, c.$COLUMN_CATEGORY_NAME, c.$COLUMN_CATEGORY_COLOR, 
               c.$COLUMN_CATEGORY_BUDGET, COALESCE(SUM(e.$COLUMN_EXPENSE_AMOUNT), 0) as total_spent
        FROM $TABLE_CATEGORIES c
        LEFT JOIN $TABLE_EXPENSES e ON c.$COLUMN_CATEGORY_ID = e.$COLUMN_EXPENSE_CATEGORY_ID
            AND e.$COLUMN_EXPENSE_USER_ID = ?
        WHERE c.$COLUMN_CATEGORY_USER_ID = ?
        GROUP BY c.$COLUMN_CATEGORY_ID
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val summary = CategoryExpenseSummary(
                    categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                    categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_COLOR)),
                    budget = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_BUDGET)),
                    totalSpent = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent"))
                )
                summaries.add(summary)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        Log.d("DatabaseHelper", "Retrieved ${summaries.size} category expense summaries")
        return summaries
    }

    fun getAllCategories(userId: Long): List<Category> {
        val categories = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIES,
            arrayOf(COLUMN_CATEGORY_ID, COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_COLOR,
                COLUMN_CATEGORY_BUDGET, COLUMN_CATEGORY_MIN_GOAL, COLUMN_CATEGORY_MAX_GOAL),
            "$COLUMN_CATEGORY_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_COLOR)),
                    budget = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_BUDGET)),
                    minGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_MIN_GOAL)),
                    maxGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_MAX_GOAL)),
                    userId = userId
                )
                categories.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        Log.d("DatabaseHelper", "Retrieved ${categories.size} categories")
        return categories
    }

    // ==================== EXPENSE OPERATIONS ====================

    fun addExpense(expense: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EXPENSE_AMOUNT, expense.amount)
            put(COLUMN_EXPENSE_DESCRIPTION, expense.description)
            put(COLUMN_EXPENSE_DATE, expense.date)
            put(COLUMN_EXPENSE_CATEGORY_ID, expense.categoryId)
            put(COLUMN_EXPENSE_USER_ID, expense.userId)
            put(COLUMN_EXPENSE_IMAGE_PATH, expense.imagePath)
        }
        val id = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        Log.d("DatabaseHelper", "Expense added with ID: $id")

        // Check and update achievements after adding expense
        checkAndUpdateAchievements(expense.userId)

        return id
    }

    fun getAllExpenses(userId: Long): List<ExpenseWithCategory> {
        val expenses = mutableListOf<ExpenseWithCategory>()
        val db = readableDatabase

        val query = """
            SELECT e.*, c.name as category_name, c.color as category_color
            FROM $TABLE_EXPENSES e
            LEFT JOIN $TABLE_CATEGORIES c ON e.$COLUMN_EXPENSE_CATEGORY_ID = c.$COLUMN_CATEGORY_ID
            WHERE e.$COLUMN_EXPENSE_USER_ID = ?
            ORDER BY e.$COLUMN_EXPENSE_DATE DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val expense = ExpenseWithCategory(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_AMOUNT)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DESCRIPTION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DATE)),
                    categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_CATEGORY_ID)),
                    userId = userId,
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_IMAGE_PATH)),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                    categoryColor = cursor.getString(cursor.getColumnIndexOrThrow("category_color"))
                )
                expenses.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        Log.d("DatabaseHelper", "Retrieved ${expenses.size} expenses")
        return expenses
    }

    fun getExpensesByDateRange(userId: Long, startDate: String, endDate: String): List<ExpenseWithCategory> {
        val expenses = mutableListOf<ExpenseWithCategory>()
        val db = readableDatabase

        val query = """
            SELECT e.*, c.name as category_name, c.color as category_color
            FROM $TABLE_EXPENSES e
            LEFT JOIN $TABLE_CATEGORIES c ON e.$COLUMN_EXPENSE_CATEGORY_ID = c.$COLUMN_CATEGORY_ID
            WHERE e.$COLUMN_EXPENSE_USER_ID = ? 
            AND e.$COLUMN_EXPENSE_DATE BETWEEN ? AND ?
            ORDER BY e.$COLUMN_EXPENSE_DATE DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

        if (cursor.moveToFirst()) {
            do {
                val expense = ExpenseWithCategory(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_AMOUNT)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DESCRIPTION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_DATE)),
                    categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_CATEGORY_ID)),
                    userId = userId,
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_IMAGE_PATH)),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                    categoryColor = cursor.getString(cursor.getColumnIndexOrThrow("category_color"))
                )
                expenses.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenses
    }

    // ==================== INCOME OPERATIONS ====================

    fun addIncome(income: Income): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_INCOME_AMOUNT, income.amount)
            put(COLUMN_INCOME_SOURCE, income.source)
            put(COLUMN_INCOME_NOTE, income.note)
            put(COLUMN_INCOME_DATE, income.date)
            put(COLUMN_INCOME_USER_ID, income.userId)
        }
        val id = db.insert(TABLE_INCOME, null, values)
        db.close()
        Log.d("DatabaseHelper", "Income added with ID: $id")
        return id
    }

    fun getTotalIncomeByUser(userId: Long): Double {
        val db = readableDatabase
        var totalIncome = 0.0

        val query = "SELECT SUM($COLUMN_INCOME_AMOUNT) FROM $TABLE_INCOME WHERE $COLUMN_INCOME_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return totalIncome
    }

    fun getIncomeByDateRange(userId: Long, startDate: String, endDate: String): Double {
        val db = readableDatabase
        var totalIncome = 0.0

        val query = """
            SELECT SUM($COLUMN_INCOME_AMOUNT) FROM $TABLE_INCOME 
            WHERE $COLUMN_INCOME_USER_ID = ? AND $COLUMN_INCOME_DATE BETWEEN ? AND ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return totalIncome
    }

    // ==================== BUDGET OPERATIONS ====================

    fun addBudget(budget: Budget): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BUDGET_AMOUNT, budget.amount)
            put(COLUMN_BUDGET_PERIOD, budget.period)
            put(COLUMN_BUDGET_START_DATE, budget.startDate)
            put(COLUMN_BUDGET_END_DATE, budget.endDate)
            put(COLUMN_BUDGET_USER_ID, budget.userId)
        }
        val id = db.insert(TABLE_BUDGETS, null, values)
        db.close()
        Log.d("DatabaseHelper", "Budget added with ID: $id")
        return id
    }

    fun updateBudget(budget: Budget): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BUDGET_AMOUNT, budget.amount)
            put(COLUMN_BUDGET_PERIOD, budget.period)
            put(COLUMN_BUDGET_START_DATE, budget.startDate)
            put(COLUMN_BUDGET_END_DATE, budget.endDate)
        }
        val result = db.update(
            TABLE_BUDGETS,
            values,
            "$COLUMN_BUDGET_ID = ?",
            arrayOf(budget.id.toString())
        )
        db.close()
        return result
    }

    fun getBudgetByPeriod(userId: Long, period: String): Budget? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BUDGETS,
            null,
            "$COLUMN_BUDGET_USER_ID = ? AND $COLUMN_BUDGET_PERIOD = ?",
            arrayOf(userId.toString(), period),
            null, null, null
        )
        var budget: Budget? = null
        if (cursor.moveToFirst()) {
            budget = Budget(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_ID)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_AMOUNT)),
                period = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_PERIOD)),
                startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_START_DATE)),
                endDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_END_DATE)),
                userId = userId
            )
        }
        cursor.close()
        db.close()
        return budget
    }

    // ==================== STATISTICS METHODS ====================

    fun getExpensesByCategoryForPeriod(userId: Long, startDate: String, endDate: String): List<CategoryExpenseAnalytics> {
        val results = mutableListOf<CategoryExpenseAnalytics>()
        val db = readableDatabase

        val query = """
            SELECT c.$COLUMN_CATEGORY_ID, c.$COLUMN_CATEGORY_NAME, c.$COLUMN_CATEGORY_COLOR,
                   c.$COLUMN_CATEGORY_BUDGET, c.$COLUMN_CATEGORY_MIN_GOAL, c.$COLUMN_CATEGORY_MAX_GOAL,
                   COALESCE(SUM(e.$COLUMN_EXPENSE_AMOUNT), 0) as total_spent
            FROM $TABLE_CATEGORIES c
            LEFT JOIN $TABLE_EXPENSES e ON c.$COLUMN_CATEGORY_ID = e.$COLUMN_EXPENSE_CATEGORY_ID
                AND e.$COLUMN_EXPENSE_DATE BETWEEN ? AND ?
                AND e.$COLUMN_EXPENSE_USER_ID = ?
            WHERE c.$COLUMN_CATEGORY_USER_ID = ?
            GROUP BY c.$COLUMN_CATEGORY_ID
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(startDate, endDate, userId.toString(), userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val result = CategoryExpenseAnalytics(
                    categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                    categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                    categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_COLOR)),
                    budget = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_BUDGET)),
                    totalSpent = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent")),
                    minGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_MIN_GOAL)),
                    maxGoal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_MAX_GOAL))
                )
                results.add(result)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return results
    }

    fun getTotalExpensesByPeriod(userId: Long, startDate: String, endDate: String): Double {
        val db = readableDatabase
        var total = 0.0

        val query = """
            SELECT SUM($COLUMN_EXPENSE_AMOUNT) as total
            FROM $TABLE_EXPENSES
            WHERE $COLUMN_EXPENSE_USER_ID = ?
            AND $COLUMN_EXPENSE_DATE BETWEEN ? AND ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        db.close()
        return total
    }
    // ==================== GAMIFICATION METHODS ====================

    fun getUserAchievements(userId: Long): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        val db = this.readableDatabase

        val query = """
        SELECT a.*, 
               COALESCE(ua.$COLUMN_USER_ACHIEVEMENT_IS_UNLOCKED, 0) as is_unlocked,
               ua.$COLUMN_USER_ACHIEVEMENT_DATE_EARNED
        FROM $TABLE_ACHIEVEMENTS a
        LEFT JOIN $TABLE_USER_ACHIEVEMENTS ua ON a.$COLUMN_ACHIEVEMENT_ID = ua.$COLUMN_USER_ACHIEVEMENT_ACHIEVEMENT_ID 
            AND ua.$COLUMN_USER_ACHIEVEMENT_USER_ID = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                try {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_NAME)) ?: "Unknown"
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_DESCRIPTION)) ?: "No description"
                    val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_TYPE)) ?: "general"
                    val requirement = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_REQUIREMENT))
                    val icon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACHIEVEMENT_ICON)) ?: "🏆"
                    val isUnlocked = cursor.getInt(cursor.getColumnIndexOrThrow("is_unlocked")) == 1
                    val dateEarned = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ACHIEVEMENT_DATE_EARNED)) ?: ""

                    val achievement = Achievement(
                        id = id,
                        name = name,
                        description = description,
                        type = type,
                        requirement = requirement,
                        icon = icon,
                        isUnlocked = isUnlocked,
                        dateEarned = dateEarned
                    )
                    achievements.add(achievement)
                } catch (e: Exception) {
                    Log.e("DatabaseHelper", "Error parsing achievement", e)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        Log.d("DatabaseHelper", "Retrieved ${achievements.size} achievements for user $userId")
        return achievements
    }

    private fun checkAndUpdateAchievements(userId: Long) {
        Log.d("DatabaseHelper", "Checking achievements for user: $userId")

        // Check expense count achievement
        val expenseCount = getAllExpenses(userId).size
        if (expenseCount >= 10) {
            unlockAchievement(userId, "expenses", 10.0)
        }

        // Check streak achievement (simplified - check if expenses exist for last 7 days)
        checkStreakAchievement(userId)

        // Check category achievement
        val categories = getAllCategories(userId)
        var allWithinBudget = true
        for (category in categories) {
            val expenses = getAllExpenses(userId).filter { it.categoryId == category.id }
            val totalSpent = expenses.sumOf { it.amount }
            if (totalSpent > category.budget) {
                allWithinBudget = false
                break
            }
        }
        if (allWithinBudget && categories.isNotEmpty()) {
            unlockAchievement(userId, "categories", 1.0)
        }
    }

    private fun checkStreakAchievement(userId: Long) {
        val expenses = getAllExpenses(userId)
        if (expenses.size >= 7) {
            unlockAchievement(userId, "streak", 7.0)
        }
    }

    private fun unlockAchievement(userId: Long, type: String, requirement: Double) {
        val db = writableDatabase

        val query = """
            SELECT $COLUMN_ACHIEVEMENT_ID FROM $TABLE_ACHIEVEMENTS 
            WHERE $COLUMN_ACHIEVEMENT_TYPE = ? AND $COLUMN_ACHIEVEMENT_REQUIREMENT <= ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(type, requirement.toString()))

        if (cursor.moveToFirst()) {
            do {
                val achievementId = cursor.getLong(0)
                val checkQuery = """
                    SELECT * FROM $TABLE_USER_ACHIEVEMENTS 
                    WHERE $COLUMN_USER_ACHIEVEMENT_USER_ID = ? AND $COLUMN_USER_ACHIEVEMENT_ACHIEVEMENT_ID = ?
                """.trimIndent()
                val checkCursor = db.rawQuery(checkQuery, arrayOf(userId.toString(), achievementId.toString()))

                if (!checkCursor.moveToFirst()) {
                    val values = ContentValues().apply {
                        put(COLUMN_USER_ACHIEVEMENT_USER_ID, userId)
                        put(COLUMN_USER_ACHIEVEMENT_ACHIEVEMENT_ID, achievementId)
                        put(COLUMN_USER_ACHIEVEMENT_DATE_EARNED, java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()))
                        put(COLUMN_USER_ACHIEVEMENT_IS_UNLOCKED, 1)
                    }
                    db.insert(TABLE_USER_ACHIEVEMENTS, null, values)
                    Log.d("DatabaseHelper", "Achievement unlocked: $achievementId for user $userId")
                }
                checkCursor.close()
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun getUnlockedAchievementsCount(userId: Long): Int {
        val db = this.readableDatabase
        var count = 0
        try {
            val query = """
            SELECT COUNT(*) FROM $TABLE_USER_ACHIEVEMENTS 
            WHERE $COLUMN_USER_ACHIEVEMENT_USER_ID = ? AND $COLUMN_USER_ACHIEVEMENT_IS_UNLOCKED = 1
        """.trimIndent()
            val cursor = db.rawQuery(query, arrayOf(userId.toString()))
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting unlocked achievements count", e)
        }
        db.close()
        return count
    }
}