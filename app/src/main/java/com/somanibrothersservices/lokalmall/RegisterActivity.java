package com.somanibrothersservices.lokalmall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class RegisterActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    public static boolean onResetPasswordFragment = false;
    public static boolean setSignUpFragment = false;
    public static boolean disableCloseButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this , R.color.logoBackground));

        frameLayout = findViewById(R.id.register_frame_layout);
        if (setSignUpFragment) {
            setSignUpFragment = false;
            setDefaultFragment(new SignUpFragment());
        } else {
            setDefaultFragment(new SignInFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onResetPasswordFragment) {
                onResetPasswordFragment = false;
                setFragment(new SignInFragment(), 0);
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment, int default1) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (default1 == 0)
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}
