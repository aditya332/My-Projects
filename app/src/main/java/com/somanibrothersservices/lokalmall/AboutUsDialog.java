package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import static android.provider.ContactsContract.CommonDataKinds.StructuredPostal.CITY;

public class AboutUsDialog {
    Context context;
    Dialog dialog;

    public AboutUsDialog(final Context context , boolean isFromStore , boolean isFromChooseStore) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.about_us_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.slider_background);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button withYou = dialog.findViewById(R.id.button_about_us_ok);
        final TextView body = dialog.findViewById(R.id.text_view_about_us_body) , title = dialog.findViewById(R.id.text_view_about_us_title);
        withYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (isFromStore) {
            title.setText(ChooseStoreActivity.STORE_NAME);
            body.setText(ChooseStoreActivity.STORE_ABOUT_US_BODY.replace("\\n" , "\n"));
        } else if (isFromChooseStore) {
            SplashActivity.USER_FIREBASE_FIRESTORE.collection("LOKAL MALL").document(ChooseCityActivity.CITY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        title.setText("Lokal Mall , " + CITY.substring(0 , 1).toUpperCase() + CITY.substring(1).toLowerCase());
                        body.setText(task.getResult().getString("about_us").replace("\\n" , "\n"));
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            SplashActivity.USER_FIREBASE_FIRESTORE.collection("LOKAL MALL").document("about_us").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        title.setText("Lokal Mall");
                        body.setText(task.getResult().getString("body").replace("\\n" , "\n"));
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        dialog.show();
    }
}
