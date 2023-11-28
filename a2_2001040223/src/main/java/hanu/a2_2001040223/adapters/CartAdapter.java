package hanu.a2_2001040223.adapters;

import android.app.Activity;
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

import hanu.a2_2001040223.CartActivity;
import hanu.a2_2001040223.R;
import hanu.a2_2001040223.database.DbHelper;
import hanu.a2_2001040223.models.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private Context context;
    private List<Product> cartItemList;

    private DbHelper dbHelper;

    private Product cartItem;

    public long totalPayment;

    public CartAdapter(Context context, List<Product> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.dbHelper = new DbHelper(context);
    }



    public void setCartItemList(List<Product> cartItemList) {
        this.cartItemList = cartItemList;
    }
    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CartViewHolder cartViewHolder = new CartViewHolder(layoutInflater.inflate(R.layout.cart_item_view, parent, false));
        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        cartItem = cartItemList.get(position);

        ExecutorService es = Executors.newSingleThreadExecutor();
        Handler hdler = new Handler(Looper.getMainLooper());
        es.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmapImage = getBitmap(cartItem.getThumbnail());
                hdler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.cartItemImage.setImageBitmap(bitmapImage);
                    }
                });
            }
        });

        holder.cartItemName.setText(cartItem.getName());
        holder.cartItemUnitPrice.setText("đ " + cartItem.getUnitPrice());
        holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.cartItemEachItemPayment.setText("đ "+ String.valueOf(cartItem.getPayEachItem()));
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cartItem.setQuantity(cartItem.getQuantity()+1);
                        cartItem.setPayEachItem(cartItem.getQuantity() * cartItem.getUnitPrice());
                        dbHelper.update(cartItem);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
                                holder.cartItemEachItemPayment.setText("đ "+String.valueOf(cartItem.getPayEachItem()));
                                totalPayment();
                            }
                        });
                    }
                }).start();
            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (cartItem.getQuantity() > 0){
                            cartItem.setQuantity(cartItem.getQuantity()-1);
                            cartItem.setPayEachItem(cartItem.getQuantity() * cartItem.getUnitPrice());
                            dbHelper.update(cartItem);

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));
                                    holder.cartItemEachItemPayment.setText("đ " + String.valueOf(cartItem.getPayEachItem()));
                                    totalPayment();
                                }
                            });
                        }
                        if (cartItem.getQuantity() == 0){
                            deleteProduct(cartItem);
                        }
                    }
                }).start();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
        private ImageView cartItemImage;

        private TextView cartItemName, cartItemUnitPrice, cartItemQuantity, cartItemEachItemPayment;

        private ImageButton addBtn, minusBtn;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cartItemImage = itemView.findViewById(R.id.cartItemImage);
            this.cartItemName = itemView.findViewById(R.id.cartItemName);
            this.cartItemUnitPrice = itemView.findViewById(R.id.cartItemUnitPrice);
            this.cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            this.addBtn = itemView.findViewById(R.id.cartItemAddBtn);
            this.minusBtn = itemView.findViewById(R.id.cartItemMinusBtn);
            this.cartItemEachItemPayment = itemView.findViewById(R.id.costEachItem);
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

    private void deleteProduct(Product product) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dbHelper.deleteProduct(product.getId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        cartItemList.remove(product);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void totalPayment(){
        long totalPayment = 0;
        for (Product product: cartItemList){
            totalPayment += product.getQuantity() * product.getUnitPrice();
        }
        ((CartActivity) context).totalpayment.setText("đ " + String.valueOf(totalPayment));
    }

}
