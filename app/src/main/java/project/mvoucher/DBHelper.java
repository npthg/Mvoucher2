package project.mvoucher;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "MVoucher.db";
    public static final int DATABASE_VERSION = 1;

    static class VoucherID
    {
        public static final String TABLE = "cards";
        public static final String ID = "_id";
        public static final String STORE = "store";
        public static final String CARD_ID = "cardid";
        public static final String VALUE = "value";
        public static final String RECEIPT = "receipt";
    }

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + VoucherID.TABLE + "(" +
                VoucherID.ID + " INTEGER primary key autoincrement," +
                VoucherID.STORE + " TEXT not null," +
                VoucherID.CARD_ID + " TEXT not null," +
                VoucherID.VALUE + " TEXT not null," +
                VoucherID.RECEIPT + " TEXT not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE IF EXISTS " + VoucherID.TABLE);
        onCreate(db);
    }

    public boolean insertGiftCard(final String store, final String cardId, final String value,
                                  final String receipt)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VoucherID.STORE, store);
        contentValues.put(VoucherID.CARD_ID, cardId);
        contentValues.put(VoucherID.VALUE, value);
        contentValues.put(VoucherID.RECEIPT, receipt);

        SQLiteDatabase db = getWritableDatabase();
        final long newId = db.insert(VoucherID.TABLE, null, contentValues);
        db.close();

        return (newId != -1);
    }


    public boolean updateGiftCard(final int id, final String store, final String cardId,
                                  final String value, final String receipt)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VoucherID.STORE, store);
        contentValues.put(VoucherID.CARD_ID, cardId);
        contentValues.put(VoucherID.VALUE, value);
        contentValues.put(VoucherID.RECEIPT, receipt);

        SQLiteDatabase db = getWritableDatabase();
        int rowsUpdated = db.update(VoucherID.TABLE, contentValues,
                VoucherID.ID + "=?",
                new String[]{Integer.toString(id)});
        db.close();

        return (rowsUpdated == 1);
    }

    public GiftCard getGiftCard(final int id)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + VoucherID.TABLE +
                " where " + VoucherID.ID + "=?", new String[]{String.format("%d", id)});

        GiftCard card = null;

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            card = GiftCard.toGiftCard(cursor);
        }

        cursor.close();
        db.close();

        return card;
    }

    public boolean deleteGiftCard (final int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted =  db.delete(VoucherID.TABLE,
                VoucherID.ID + " = ? ",
                new String[]{String.format("%d", id)});
        db.close();
        return (rowsDeleted == 1);
    }

    public Cursor getGiftCardCursor()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from " + VoucherID.TABLE +
                " ORDER BY " + VoucherID.STORE, null);
        return cursor;
    }

    public int getGiftCardCount()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT Count(*) FROM " + VoucherID.TABLE, null);

        int numItems = 0;

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            numItems = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return numItems;
    }
}

