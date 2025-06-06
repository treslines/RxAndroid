package com.example.rxandroid;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "RxAndroid MainActivity";
    private String greetings = "Hello from RxJava";
    private Observable<String> myObservable;
    //private Observer<String> myObserver;
    private DisposableObserver<String> myObserver;
    private DisposableObserver<String> myObserver2;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    //private Disposable myDisposable;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // init text view from activity_main.xml
        textView = findViewById(R.id.grettings);
        // instantiate an Observable
        myObservable = Observable.just(greetings);

        myObserver = new DisposableObserver<>() {
            @Override
            public void onNext(@NonNull String s) {
                Log.i(TAG, "onNext");
                textView.setText(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };

        // cleaner way to add and subscribe to observables
        compositeDisposable.add(myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(myObserver));

        myObserver2 = new DisposableObserver<>() {
            @Override
            public void onNext(@NonNull String s) {
                Log.i(TAG, "onNext");
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };

        // cleaner way to add and subscribe to observables
        compositeDisposable.add(myObservable
                .subscribeWith(myObserver2));

    }

    // IMPORTANT NOTE:
    // if we load things(let's say dispatch a network call) and while still loading,
    // if the user decides to navigate back, the subscription remains there causing potentially
    // memory leaks, ANR or app freeze. We should therefore always dispose the subscription.
    // another way memory leak could occur is when the OS decides to kill the app or view
    // because it need the memory for something else.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // instead of disposing every observer one by one, just use the composite disposable
        compositeDisposable.clear();
    }
}