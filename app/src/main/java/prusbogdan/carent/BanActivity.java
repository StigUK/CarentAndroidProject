package prusbogdan.carent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import prusbogdan.carent.Classes.Banlist;

public class BanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        setTitle(R.string.banned);
        Banlist ban = SplashScreen.data.ban;
        TextView startdate = findViewById(R.id.ban_from_value);
        TextView enddate = findViewById(R.id.ban_to_value);
        TextView reason = findViewById(R.id.ban_reason);
        startdate.setText(ban.getDate_from());
        enddate.setText(ban.getDate_to());
        reason.setText(ban.getReason());
        Button logout = findViewById(R.id.ban_logout);
        final Context context = this;
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SplashScreen.data.destroy(context))
                {
                    Intent intent = new Intent(context, SliderActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        final Context context = this;
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(SplashScreen.data.destroy(context))
                        {
                            Intent intent = new Intent(context, SliderActivity.class);
                            startActivity(intent);
                        }
                    }
                });
        alert.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert.setMessage(getText(R.string.logout)+"?");
        alert.show();
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        //super.onBackPressed();
    }
}
