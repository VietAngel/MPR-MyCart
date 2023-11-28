package hanu.a2_2001040223;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_2001040223.adapters.CartAdapter;
import hanu.a2_2001040223.database.DbHelper;
import hanu.a2_2001040223.models.Product;

public class CartActivity extends AppCompatActivity {
    private CartAdapter cartAdapter;
    private List<Product> cartItemList;
    private RecyclerView recyclerView;
    private DbHelper dbHelper;

    public TextView totalpayment;

    long totalAmount = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        this.totalpayment = findViewById(R.id.totalPayment);
        this.recyclerView = findViewById(R.id.cartItemRecyclerView);
        this.dbHelper = new DbHelper(this);
        cartItemList = dbHelper.getAllProductInCart();
        this.cartAdapter = new CartAdapter(this, cartItemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);

        for (Product product : cartItemList) {
            totalAmount += product.getQuantity() * product.getUnitPrice();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                totalpayment.setText("đ " + totalAmount);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Product> products = dbHelper.getAllProductInCart();
        cartItemList.clear();
        cartItemList.addAll(products);
        cartAdapter.notifyDataSetChanged();

    }

}