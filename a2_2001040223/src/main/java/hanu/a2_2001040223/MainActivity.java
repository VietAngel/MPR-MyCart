package hanu.a2_2001040223;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import hanu.a2_2001040223.adapters.ProductAdapter;
import hanu.a2_2001040223.models.Product;

public class MainActivity extends AppCompatActivity {
    private List<Product> products;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

    private EditText searchEditText;

    private ImageButton searchBtn;

    private List<Product> searchList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = findViewById(R.id.ProductRecycleView);

        products = new ArrayList<>();
        this.productAdapter = new ProductAdapter(this, products);

        getListProduct(new OnProductListLoadedListener() {
            @Override
            public void onProductListLoaded(List<Product> productList) {
                products.clear();
                products.addAll(productList);
                productAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(productAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        this.searchEditText = findViewById(R.id.searchField);
        this.searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchEditText.getText().toString().trim().isEmpty()) {
                    getListProduct(new OnProductListLoadedListener() {
                        @Override
                        public void onProductListLoaded(List<Product> productList) {
                            products.clear();
                            products.addAll(productList);
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    search(String.valueOf(searchEditText.getText().toString().trim()));
                }
            }
        });
    }

    public String loadJSON(String link) {

        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //read data as json string
            InputStream is = connection.getInputStream();
            Scanner sc = new Scanner(is);
            StringBuilder json = new StringBuilder();

            while (sc.hasNext()) {
                String line = sc.nextLine();
                json.append(line);
            }
            //return json string
            return json.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_activity:
                startActivity(new Intent(this, CartActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getListProduct(OnProductListLoadedListener listener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String json = loadJSON("https://hanu-congnv.github.io/mpr-cart-api/products.json");
                List<Product> productList = new ArrayList<>();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product myObject = new Product();
                        myObject.setId(jsonObject.getInt("id"));
                        myObject.setThumbnail(jsonObject.getString("thumbnail"));
                        myObject.setName(jsonObject.getString("name"));
                        myObject.setCategory(jsonObject.getString("category"));
                        myObject.setUnitPrice(jsonObject.getLong("unitPrice"));
                        productList.add(myObject);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onProductListLoaded(productList);
                    }
                });
            }
        });
    }

    public interface OnProductListLoadedListener {
        void onProductListLoaded(List<Product> productList);
    }


    private void search(String searchText) {
        searchList.clear();
        for (Product product : products) {
            if (product.getName().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase()))
                searchList.add(product);
        }
        productAdapter.setSearchedProductList(searchList);
        productAdapter.notifyDataSetChanged();
    }


}