package net.matrixhome.kino.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import net.matrixhome.kino.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConnectionActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        intent = new Intent(this, FilmCatalogueActivity.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionActivity.this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.hasNoConnection)
                //.setIcon(R.drawable.ic_matrix_logo)
                .setCancelable(false)
                .setNegativeButton(R.string.exitAPP,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);

                            }
                        });

        builder.setPositiveButton(R.string.tryAgain, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}