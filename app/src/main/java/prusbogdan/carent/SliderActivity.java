package prusbogdan.carent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;

import prusbogdan.carent.Adapters.ImageAdapter;

public class SliderActivity extends AppCompatActivity {

    public int width,height;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SplashScreen.data.LoadUser(this);
        Context context = this;
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        /*if(SplashScreen.data.LoadUser(this)){
            alert.setMessage(SplashScreen.data.user.getId());
            alert.show();
            if(SplashScreen.data.user!=null)
            {
                //gotomain();
            }
            else{
                //gotologin();
            }
        }
        else {
            alert.setMessage("No");
            alert.show();
            //gotologin();
        }*/

        setContentView(R.layout.activity_slider);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this);
        final TextView textView = findViewById(R.id.slidertest);
        final ImageView car = findViewById(R.id.slidercar);
        final ImageView skip = findViewById(R.id.sliderskip);
        final Button signin = findViewById(R.id.slidersignin);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        setMargins(car, 0, height-430, 0, 0);
        setMargins(skip, width-300, height-410, 0, 0);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        /*car.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);
                int maskedAction = event.getActionMasked();
                switch (maskedAction) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        // TODO use data
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: { // a pointer was moved
                        // TODO use data
                        textView.setText(event.getX()+"");
                        setMargins(car, (int)event.getX(), height-430, 0, 0);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {

                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        // TODO use data
                        break;
                    }
                }
                return true;
            }
        });*/
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Handler handler = new Handler();
                //final int delay = 1; //milliseconds
                /*handler.postDelayed(new Runnable(){
                    public void run(){
                        //viewPager.scrollBy();
                        viewPager.scrollBy(viewPager.getScrollX()+1,0);
                        handler.postDelayed(this, delay);
                    }
                }, delay);*/

                viewPager.setCurrentItem(2);
            }
        });
        textView.setText(width+"    "+height);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        viewPager.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                car.setPadding(50+scrollX/3,0,0,0);
                textView.setText(scrollX+"");
                if(scrollX>width+200) {
                    signin.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.INVISIBLE);
                }
                else {
                    skip.setVisibility(View.VISIBLE);
                    signin.setVisibility(View.INVISIBLE);
                }
            }
        });
        viewPager.setAdapter(adapter);
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


    private void gotomain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void gotologin()
    {
        Intent intent = new Intent(this, SliderActivity.class);
        startActivity(intent);
    }
}
