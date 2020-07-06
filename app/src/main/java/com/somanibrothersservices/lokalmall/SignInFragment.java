package com.somanibrothersservices.lokalmall;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_DATA;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAccount , otpTime;
    private FrameLayout parentFrameLayout;

    private EditText editMobileNo, editOtp;
    private ImageButton buttonClose;
    private Button buttonSignIn, buttonGenOtp;
    private ProgressBar progressBar;
    private String mVerificationId;
    private boolean notRegistered;

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAccount = view.findViewById(R.id.sign_in_text_dont_have_account);
        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        editMobileNo = view.findViewById(R.id.sign_in_edit_mobile_no_);
        editOtp = view.findViewById(R.id.sign_in_edit_otp);
        buttonClose = view.findViewById(R.id.sign_in_image_button_close);
        buttonGenOtp = view.findViewById(R.id.sign_in_button_verify_otp);
        buttonSignIn = view.findViewById(R.id.sign_in_button);
        progressBar = view.findViewById(R.id.sign_in_progress_bar);
        otpTime = view.findViewById(R.id.textView3);

        firebaseAuth = FirebaseAuth.getInstance();
        notRegistered = true;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RegisterActivity.disableCloseButton)
                    startActivity(new Intent(getActivity(), ChooseStoreActivity.class));
                getActivity().finish();
            }
        });

        editMobileNo.addTextChangedListener(checkInputs);

        buttonGenOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail();
            }
        });
    }

    private TextWatcher checkInputs = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(editMobileNo.getText())) {
                buttonSignIn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkEmail() {
        if (editMobileNo.length() == 10) {
            USER_FIREBASE_FIRESTORE.collection("USERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String mobileNo = documentSnapshot.getString("mobileNo");
                            if (mobileNo.equals(editMobileNo.getText().toString())) {
                                notRegistered = false;
                                break;
                            }
                        }
                        if (!notRegistered) {
                            progressBar.setVisibility(View.VISIBLE);
                            buttonGenOtp.setVisibility(View.INVISIBLE);
                            buttonGenOtp.setEnabled(false);
                            editOtp.setVisibility(View.VISIBLE);
                            otpTime.setVisibility(View.VISIBLE);
                            editMobileNo.setFocusable(false);
                            buttonSignIn.setVisibility(View.VISIBLE);
                            buttonSignIn.setEnabled(true);
                            editMobileNo.setText(editMobileNo.getText().toString());
                            sendVerificationCode(editMobileNo.getText().toString());
                            buttonSignIn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!TextUtils.isEmpty(editOtp.getText().toString())) {
                                        verifyVerificationCode(editOtp.getText().toString().trim());
                                    } else {
                                        editOtp.requestFocus();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Mobile No. is not registered :) , Try Signing Up", Toast.LENGTH_LONG).show();
                            setFragment(new SignUpFragment());
                        }
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Wrong Mobile No !!!", Toast.LENGTH_LONG).show();
        }
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91 " + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editOtp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            USER_FIREBASE_FIRESTORE.collection("USERS").document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final String name = task.getResult().getString("fullName") , mobileNo = task.getResult().getString("mobileNo") , profile = task.getResult().getString("profile");
                                        List<String> visited = new ArrayList<>();
                                        visited = (List<String>) task.getResult().get("visited");
                                        if (visited.contains(CITY + STORE)) {
                                            CURR_USER = firebaseAuth.getCurrentUser();
                                            DBqueries.fullName = name;
                                            DBqueries.mobileNo = mobileNo;
                                            DBqueries.profile = profile;
                                            if (!RegisterActivity.disableCloseButton)
                                                startActivity(new Intent(getActivity(), SplashActivity.class));
                                            getActivity().finish();
                                        } else {
                                            visited.add(CITY + STORE);
                                            USER_FIREBASE_FIRESTORE.collection("USERS").document(firebaseAuth.getUid()).update("visited" , visited)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                Map<String, Object> wishlistMap = new HashMap<>();
                                                                wishlistMap.put("list_size", (long) 0);

                                                                Map<String, Object> myRatingsMap = new HashMap<>();
                                                                myRatingsMap.put("list_size", (long) 0);

                                                                Map<String, Object> myCartMap = new HashMap<>();
                                                                myCartMap.put("list_size", (long) 0);

                                                                Map<String, Object> myAddressesMap = new HashMap<>();
                                                                myAddressesMap.put("list_size", (long) 0);

                                                                Map<String, Object> notificationsMap = new HashMap<>();
                                                                notificationsMap.put("list_size", (long) 0);

                                                                final List<String> documentNames = new ArrayList<>();
                                                                documentNames.add("MY_WISHLIST");
                                                                documentNames.add("MY_RATINGS");
                                                                documentNames.add("MY_CARTLIST");
                                                                documentNames.add("MY_NOTIFICATIONS");

                                                                final List<Map<String, Object>> documentFields = new ArrayList<>();
                                                                documentFields.add(wishlistMap);
                                                                documentFields.add(myRatingsMap);
                                                                documentFields.add(myCartMap);
                                                                documentFields.add(notificationsMap);

                                                                for (int x = 0; x < documentNames.size(); x++) {
                                                                    final int finalX = x;
                                                                    USER_FIREBASE_FIRESTORE.collection("USERS").document(firebaseAuth.getUid())
                                                                            .collection(STORE_USER_DATA).document(documentNames.get(x))
                                                                            .set(documentFields.get(x))
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        if (finalX == (documentNames.size() - 1)) {
                                                                                            CURR_USER = firebaseAuth.getCurrentUser();
                                                                                            DBqueries.fullName = name;
                                                                                            DBqueries.mobileNo = mobileNo;
                                                                                            DBqueries.profile = profile;
                                                                                            if (!RegisterActivity.disableCloseButton)
                                                                                                startActivity(new Intent(getActivity(), SplashActivity.class));
                                                                                            getActivity().finish();
                                                                                        }
                                                                                    } else {
                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                        buttonSignIn.setEnabled(true);
                                                                                        String error = task.getException().getMessage();
                                                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            } else {
                                                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
                    }
                });
    }
}
