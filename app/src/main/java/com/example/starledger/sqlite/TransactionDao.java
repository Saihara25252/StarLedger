package com.example.starledger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.starledger.bean.Transaction;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    private UserDbHelper mHelper;

    public TransactionDao(Context context) {
        mHelper = new UserDbHelper(context);
    }

    // 添加交易记录
    public boolean addTransaction(Transaction transaction) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", transaction.getUserId());
        values.put("amount", transaction.getAmount());
        values.put("category", transaction.getCategory());
        values.put("description", transaction.getDescription());
        values.put("date", transaction.getDate());
        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1; // 如果返回-1，表示插入失败
    }

    // 查询所有交易记录
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("transactions", null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(0));
                transaction.setUserId(cursor.getInt(1));
                transaction.setAmount(cursor.getDouble(2));
                transaction.setCategory(cursor.getString(3));
                transaction.setDescription(cursor.getString(4));
                transaction.setDate(cursor.getString(5));
                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return transactions;
    }

    // 根据用户ID查询交易记录
    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("transactions", null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(0));
                transaction.setUserId(cursor.getInt(1));
                transaction.setAmount(cursor.getDouble(2));
                transaction.setCategory(cursor.getString(3));
                transaction.setDescription(cursor.getString(4));
                transaction.setDate(cursor.getString(5));
                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return transactions;
    }

    // 根据ID更新交易记录
    public boolean updateTransaction(Transaction transaction) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", transaction.getUserId());
        values.put("amount", transaction.getAmount());
        values.put("category", transaction.getCategory());
        values.put("description", transaction.getDescription());
        values.put("date", transaction.getDate());
        int result = db.update("transactions", values, "id=?", new String[]{String.valueOf(transaction.getId())});
        db.close();
        return result > 0; // 如果返回大于0，表示更新成功
    }

    // 根据ID删除交易记录
    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int result = db.delete("transactions", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; // 如果返回大于0，表示删除成功
    }

    // 根据用户ID和日期查询交易记录
    public List<Transaction> getTransactionsByUserIdAndDate(int userId, String date) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query("transactions", null, "user_id=? AND date=?", new String[]{String.valueOf(userId), date}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(0));
                transaction.setUserId(cursor.getInt(1));
                transaction.setAmount(cursor.getDouble(2));
                transaction.setCategory(cursor.getString(3));
                transaction.setDescription(cursor.getString(4));
                transaction.setDate(cursor.getString(5));
                transactions.add(transaction);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return transactions;
    }

    public double getTotalSpentByMonth(int year, int month, int userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        double totalSpent = 0.0;

        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y', date) = ? AND strftime('%m', date) = ? AND amount < 0";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), String.valueOf(year), String.format("%02d", month) });

        if (cursor.moveToFirst()) {
            totalSpent = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return -totalSpent; // 将负数转换为正数表示消费金额
    }

    public double getTotalIncomeByMonth(int year, int month, int userId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        double totalIncome = 0.0;

        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y', date) = ? AND strftime('%m', date) = ? AND amount > 0";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), String.valueOf(year), String.format("%02d", month) });

        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalIncome;
    }
    public double getTotalExpenseByDate(int userId, String date) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        double totalExpense = 0.0;

        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND date = ? AND amount < 0";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), date });

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return -totalExpense; // 将负数转换为正数表示消费金额
    }

    public double getTotalExpenseByMonth(int userId, String month) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        double totalExpense = 0.0;

        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y-%m', date) = ? AND amount < 0";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), month });

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return -totalExpense; // 将负数转换为正数表示消费金额
    }
    public List<PieEntry> getIncomeCategoryByDate(int userId, String date) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category, SUM(amount) FROM transactions WHERE user_id = ? AND date = ? AND amount > 0 GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), date });

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                entries.add(new PieEntry(amount, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public List<PieEntry> getExpenseCategoryByDate(int userId, String date) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category, SUM(amount) FROM transactions WHERE user_id = ? AND date = ? AND amount < 0 GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), date });

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                entries.add(new PieEntry(-amount, category)); // 将负数转换为正数表示消费金额
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public List<PieEntry> getIncomeCategoryByMonth(int userId, String month) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category, SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y-%m', date) = ? AND amount > 0 GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), month });

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                entries.add(new PieEntry(amount, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public List<PieEntry> getExpenseCategoryByMonth(int userId, String month) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category, SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y-%m', date) = ? AND amount < 0 GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), month });

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                entries.add(new PieEntry(-amount, category)); // 将负数转换为正数表示消费金额
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public List<PieEntry> getIncomeCategoryByYear(int userId, String year) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category, SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y', date) = ? AND amount > 0 GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), year });

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                entries.add(new PieEntry(amount, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public List<PieEntry> getExpenseCategoryByYear(int userId, String year) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<PieEntry> entries = new ArrayList<>();

        String query = "SELECT category, SUM(amount) FROM transactions WHERE user_id = ? AND strftime('%Y', date) = ? AND amount < 0 GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), year });

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                float amount = cursor.getFloat(1);
                entries.add(new PieEntry(-amount, category)); // 将负数转换为正数表示消费金额
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

}
