package uqac.bigbrainstudio.touchfit;

import android.content.Intent;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import uqac.bigbrainstudio.touchfit.ui.LoginActivity;
import uqac.bigbrainstudio.touchfit.ui.devices.Devices;
import uqac.bigbrainstudio.touchfit.ui.devices.DevicesFragment;
import uqac.bigbrainstudio.touchfit.ui.devices.DevicesUDPReceive;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements DevicesFragment.OnListFragmentInteractionListener {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_training, R.id.nav_devices, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            if(!mFirebaseUser.isAnonymous()) {
                MenuItem logoutItem = navigationView.getMenu().findItem(R.id.logout);
                logoutItem.setVisible(true);
                logoutItem.setOnMenuItemClickListener(item -> {
                    mFirebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                    return true;
                });
            }
            String mUsername = mFirebaseUser.isAnonymous() ? getString(R.string.anonymous) : mFirebaseUser.getDisplayName() ;
            String email = mFirebaseUser.isAnonymous()  ? getString(R.string.nav_header_subtitle) : mFirebaseUser.getEmail();
            if (mFirebaseUser.getPhotoUrl() != null) {
                ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
                new GetBitmapFromURLAsync(imageView).execute(mFirebaseUser.getPhotoUrl().toString());
            }

            TextView textView = navigationView.getHeaderView(0).findViewById(R.id.loginName);
            textView.setText(mUsername);

            TextView emailView = navigationView.getHeaderView(0).findViewById(R.id.emailView);
            emailView.setText(email);
            if(mFirebaseUser.isAnonymous()) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("googleOnly", true);
                emailView.setOnClickListener(l -> startActivity(intent));
            }
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onListFragmentInteraction(Devices item) {
        item.turnOn();
        new DevicesUDPReceive(this).start();

    }



    private static class GetBitmapFromURLAsync extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<ImageView> imageView;
        public GetBitmapFromURLAsync(ImageView imageView){
            this.imageView = new WeakReference<>(imageView);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            for (String s : params) {
                try {
                    URL url = new URL(s);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    return BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.get().setImageBitmap(getRoundedCornerBitmap(bitmap));
        }
        Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 100;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
}
