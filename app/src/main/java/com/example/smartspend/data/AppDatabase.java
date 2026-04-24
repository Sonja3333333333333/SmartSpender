package com.example.smartspend.data; // Перевір, щоб папка збігалася з твоєю

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartspend.data.dao.CategoryDao;
import com.example.smartspend.data.dao.TransactionDao;
import com.example.smartspend.data.entities.Category;
import com.example.smartspend.data.entities.Transaction_Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Transaction_Log.class, Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "smartspend_database")
                            // ПІДКЛЮЧАЄМО НАШ "СІДЕР" (засів бази)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- ЛОГІКА "ЗАСІВУ" БАЗИ ---
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                CategoryDao dao = INSTANCE.categoryDao();

                // Наш стандартний набір категорій
                String[] defaultCategories = {
                        "Продукти", "Кафе і ресторани", "Транспорт",
                        "Комунальні послуги", "Розваги", "Здоров'я",
                        "Краса та догляд", "Подарунки", "Одяг та взуття",
                        "Техніка", "Інше"
                };

                // Записуємо кожну категорію в базу
                for (String catName : defaultCategories) {
                    Category category = new Category();
                    category.name = catName;
                    dao.insert(category);
                }
            });
        }
    };
}