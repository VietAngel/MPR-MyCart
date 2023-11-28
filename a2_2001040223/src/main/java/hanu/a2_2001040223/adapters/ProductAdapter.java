package hanu.a2_2001040223.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import hanu.a2_2001040223.R;
import hanu.a2_2001040223.database.DbHelper;
import hanu.a2_2001040223.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    private DbHelper dbHelper;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new DbHelper(context);
    }

//    public List<Product> getProductList() {
//        return productList;
//    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ProductViewHolder productViewHolder = new ProductViewHolder(layoutInflater.inflate(R.layout.product_item_view, parent, false));
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        ExecutorService es = Executors.newSingleThreadExecutor();
        Handler hdler = new Handler(Looper.getMainLooper());
        es.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmapImage = getBitmap(product.getThumbnail());
                hdler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.imageView.setImageBitmap(bitmapImage);
                    }
                });
            }
        });
        holder.productId = productList.get(position).getId();
        holder.productNameTextView.setText(product.getName());
        holder.priceTV.setText("đ " + product.getUnitPrice());
        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product productSaving = dbHelper.getProductById(product.getId());
                if (productSaving != null){
                    productSaving.setQuantity(productSaving.getQuantity()+1);
                    productSaving.setPayEachItem(productSaving.getQuantity() * productSaving.getUnitPrice());
                    dbHelper.update(productSaving);
                    Toast.makeText(context, "this product has been added one more!", Toast.LENGTH_SHORT).show();
                }else{
                    product.setQuantity(1);
                    product.setPayEachItem(product.getQuantity() * product.getUnitPrice());
                    dbHelper.insertProductToCart(product);
                    Toast.makeText(context, "this product has been added!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView productNameTextView;
        private TextView priceTV;
        private ImageButton addToCartButton;

        private long productId;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.productImgView);
            this.addToCartButton = itemView.findViewById(R.id.addToCartButton);
            this.productNameTextView = itemView.findViewById(R.id.productTextView);
            this.priceTV = itemView.findViewById(R.id.priceTV);
        }
    }

    public static Bitmap getBitmap (String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputFile = connection.getInputStream();
            Bitmap bitmapImage = BitmapFactory.decodeStream(inputFile);
            inputFile.close();
            return bitmapImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSearchedProductList(List<Product> findOutProducts) {
        this.productList.clear();
        this.productList.addAll(findOutProducts);
        notifyDataSetChanged();
    }
}
