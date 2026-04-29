package com.example.smartspend.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration; // Додано
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartspend.data.dao.BudgetDao;
import com.example.smartspend.data.dao.CategoryDao;
import com.example.smartspend.data.dao.TransactionDao;
import com.example.smartspend.data.entities.Budgets;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 1. ПІДНІМАЄМО ВЕРСІЮ ДО 2
@Database(entities = {Transaction_Log.class, Category.class, Budgets.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // 2. СТВОРЮЄМО ОБ'ЄКТ МІГРАЦІЇ
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Створюємо таблицю точно так, як ти прописала в Entity
            database.execSQL("CREATE TABLE IF NOT EXISTS `Budgets` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`limit_sum` REAL NOT NULL, " +
                    "`period` TEXT, " +
                    "`category_id` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`category_id`) REFERENCES `Category`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");

            // Додаємо унікальний індекс, який ти вказала в анотації @Index
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Budgets_category_id_period` " +
                    "ON `Budgets` (`category_id`, `period`)");
        }
    };

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "smartspend_database")
                            .addMigrations(MIGRATION_1_2) // 3. ДОДАЄМО МІГРАЦІЮ В БІЛДЕР
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CategoryDao dao = INSTANCE.categoryDao();
                String[] defaultCategories = {
                        "Продукти", "Кафе і ресторани", "Транспорт",
                        "Комунальні послуги", "Розваги", "Здоров'я",
                        "Краса та догляд", "Подарунки", "Одяг та взуття",
                        "Техніка", "Інше"
                };

                for (String catName : defaultCategories) {
                    Category category = new Category();
                    category.name = catName;
                    dao.insert(category);
                }
            });
        }
    };
}