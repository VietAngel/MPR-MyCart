package hanu.a2_2001040223.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_2001040223.models.Product;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DB_TABLE_NAME = "my_cart";

    private static final String PRODUCT_COLUMN_ID = "id";
    private static final String PRODUCT_COLUMN_THUMB_NAIL = "thumbnail";
    private static final String PRODUCT_COLUMN_NAME = "name";
    private static final String PRODUCT_COLUMN_CATEGORY = "category";
    private static final String PRODUCT_COLUMN_UNIT_PRICE = "unitPrice";
    private static final String PRODUCT_COLUMN_QUANTITY = "quantity";
    private static final String PRODUCT_COLUMN_PAYEACHITEM = "payEachItem";

    private static final String DB_NAME = "mycart.db";

    public static final String CREATE_CART_TABLE_SQL = "create table " +
            DB_TABLE_NAME + " (" + PRODUCT_COLUMN_ID + " integer primary key, " +
            PRODUCT_COLUMN_THUMB_NAIL + " Text Not null, " +
            PRODUCT_COLUMN_NAME + " Text Not null, " +
            PRODUCT_COLUMN_CATEGORY + " Text Not null, " +
            PRODUCT_COLUMN_UNIT_PRICE + " integer, " +
            PRODUCT_COLUMN_QUANTITY + " integer, " +
            PRODUCT_COLUMN_PAYEACHITEM + " integer)";


    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CART_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + DB_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //CRUD

    public void insertProductToCart(Product product){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PRODUCT_COLUMN_ID, product.getId());
        contentValues.put(PRODUCT_COLUMN_THUMB_NAIL, product.getThumbnail());
        contentValues.put(PRODUCT_COLUMN_NAME, product.getName());
        contentValues.put(PRODUCT_COLUMN_CATEGORY, product.getCategory());
        contentValues.put(PRODUCT_COLUMN_UNIT_PRICE, product.getUnitPrice());
        contentValues.put(PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        contentValues.put(PRODUCT_COLUMN_PAYEACHITEM, product.getPayEachItem());

        sqLiteDatabase.insert(DB_TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();


    }

    public List<Product> getAllProductInCart(){
        List<Product> resultList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DB_TABLE_NAME, null );
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false){
            Product product = new Product(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getLong(6)
            );
            resultList.add(product);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();


        return resultList;
    }

    public long deleteProduct(long productId){
        int result = 0;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        result = sqLiteDatabase.delete(DB_TABLE_NAME,
                "id = ?", new String[]{"" + productId});
        return result;
    }

    public Product getProductById(long id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DB_TABLE_NAME +" WHERE "
                + PRODUCT_COLUMN_ID + "=" + id, null);

        if (cursor.moveToFirst()) {
            Product product = new Product();
            product.setId(cursor.getLong(0));
            product.setThumbnail(cursor.getString(1));
            product.setName(cursor.getString(2));
            product.setCategory(cursor.getString(3));
            product.setUnitPrice(cursor.getLong(4));
            product.setQuantity(cursor.getInt(5));
            product.setPayEachItem(cursor.getLong(6));
            return product;
        } else {
            return null;
        }
    }

    public boolean update(Product product){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_COLUMN_QUANTITY, product.getQuantity());
        contentValues.put(PRODUCT_COLUMN_PAYEACHITEM, product.getPayEachItem());

        int rowCount = sqLiteDatabase.update(DB_TABLE_NAME, contentValues,
                PRODUCT_COLUMN_ID + "=?", new String[]{"" + product.getId()});

        return rowCount > 0;
    }
}
