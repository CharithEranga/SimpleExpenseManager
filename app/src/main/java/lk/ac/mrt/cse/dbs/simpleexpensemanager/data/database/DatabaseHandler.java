package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Charith on 12/6/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "130395P";

    // Table names
    private static final String TABLE_ACCOUNT = "Account";
    private static final String TABLE_TRANSACTION = "Transaction";

    // Account Table Columns names
    private static final String ACCOUNT_ACCOUNT_NO = "accountNo";
    private static final String ACCOUNT_BANK_NAME = "bankName";
    private static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String ACCOUNT_BALANCE = "balance";

    // Transaction Table Columns names
    private static final String TRANSACTION_ACCOUNT_NO = "accountNo";
    private static final String TRANSACTION_DATE = "date";
    private static final String TRANSACTION_EXPENSE_TYPE = "expenseType";
    private static final String TRANSACTION_AMOUNT = "amount";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("
                + ACCOUNT_ACCOUNT_NO + " TEXT PRIMARY KEY,"
                + ACCOUNT_BANK_NAME + " TEXT,"
                + ACCOUNT_HOLDER_NAME + " TEXT,"
                + ACCOUNT_BALANCE + " DECIMAL(12,2)" + ")";

        String CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + TRANSACTION_ACCOUNT_NO + " TEXT,"
                + TRANSACTION_DATE + " DATE,"
                + TRANSACTION_EXPENSE_TYPE + " TEXT,"
                + TRANSACTION_AMOUNT + " DECIMAL(12,2),"
                + " PRIMARY KEY( " + TRANSACTION_DATE + ", " + TRANSACTION_ACCOUNT_NO + " ))";

        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Account
     */

    // Adding new account
    public void addAccount(Account newAccount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_ACCOUNT_NO, newAccount.getAccountNo());
        values.put(ACCOUNT_BANK_NAME, newAccount.getBankName());
        values.put(ACCOUNT_HOLDER_NAME, newAccount.getAccountHolderName());
        values.put(ACCOUNT_BALANCE, newAccount.getBalance());

        // Inserting Row
        db.insert(TABLE_ACCOUNT, null, values);
        db.close(); // Closing database connection
    }

    // Getting single account
    public Account getAccount(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACCOUNT,
                new String[]{ACCOUNT_ACCOUNT_NO, ACCOUNT_BANK_NAME, ACCOUNT_HOLDER_NAME, ACCOUNT_BALANCE},
                ACCOUNT_ACCOUNT_NO + "=?",
                new String[]{String.valueOf(accountNo)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Account newAccount = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                Double.parseDouble(cursor.getString(3)));

        // return account
        return newAccount;
    }

    // Getting all accounts
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<Account>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)));
                // Adding account to list
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        // return account list
        return accountList;
    }

    // Updating single account
    public int updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_ACCOUNT_NO, account.getAccountNo());
        values.put(ACCOUNT_BANK_NAME, account.getBankName());
        values.put(ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(ACCOUNT_BALANCE, account.getBalance());

        // updating row
        return db.update(TABLE_ACCOUNT, values, ACCOUNT_ACCOUNT_NO + " = ?",
                new String[]{String.valueOf(account.getAccountNo())});
    }

    // Deleting single account
    public void deleteAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, ACCOUNT_ACCOUNT_NO + " = ?",
                new String[]{accountNo});
        db.close();
    }

    // Getting accounts count
    public int getAccountsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ACCOUNT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Transaction
     */

    // Adding new transaction
    public void addTransaction(Transaction newTransaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String expenseType;
        if (newTransaction.getExpenseType() == ExpenseType.INCOME) {
            expenseType = "INCOME";
        } else {
            expenseType = "EXPENSE";
        }

        ContentValues values = new ContentValues();
        values.put(TRANSACTION_ACCOUNT_NO, newTransaction.getAccountNo());
        values.put(TRANSACTION_DATE, dateFormat.format(newTransaction.getDate()));
        values.put(TRANSACTION_EXPENSE_TYPE, expenseType);
        values.put(TRANSACTION_AMOUNT, newTransaction.getAmount());

        // Inserting Row
        db.insert(TABLE_TRANSACTION, null, values);
        db.close(); // Closing database connection
    }

    // Getting all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = new ArrayList<Transaction>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        ExpenseType expenseType;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                try {
                    date = dateFormat.parse(cursor.getString(1));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                if (cursor.getString(2) == "INCOME") {
                    expenseType = ExpenseType.INCOME;
                } else {
                    expenseType = ExpenseType.EXPENSE;
                }

                Transaction transaction = new Transaction(date, cursor.getString(0), expenseType, Double.parseDouble(cursor.getString(3)));
                // Adding transaction to list
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        // return transaction list
        return transactionList;
    }

    // Getting transactions count
    public int getTransactionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRANSACTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}