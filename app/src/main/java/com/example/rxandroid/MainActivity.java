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
    private Integer[] greetings = {1,2,3,4,5,6,7,8,9,10};
    private Observable<Integer> myObservable;
    private DisposableObserver<Integer> myObserver;
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

        // to emit from array use fromArray
        // myObservable = Observable.just(greetings);
        // to emit from array use fromArray
        // myObservable = Observable.fromArray(greetings);
        // to emit from array use fromArray
        myObservable = Observable.range(1, 20);

        // cleaner way to add and subscribe to observables
        compositeDisposable.add(myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));

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

    private DisposableObserver<Integer> getObserver(){
        myObserver = new DisposableObserver<>() {
            @Override
            public void onNext(@NonNull Integer i) {
                Log.i(TAG, "onNext" + i);
                textView.setText(String.valueOf(i));
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
        return myObserver;
    }
}