package com.example.rxandroid;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "RxAndroid MainActivity";
    private Observable<Student> myObservable;
    private DisposableObserver<Student> myObserver;
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

        // to emit from array use create operator
        myObservable = Observable.create(emitter -> {
            ArrayList<Student> studentArrayList = getStudents();
            for (Student s: studentArrayList) {
                emitter.onNext(s);
            }
            emitter.onComplete();
        });

        // cleaner way to add and subscribe to observables
        compositeDisposable.add(myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                        // see how to use the map operator to transform data before they get emmited
                        .map(s -> new Student(s.getName().toUpperCase(), s.getEmail(), s.getAge(), s.getRegistrationDate()))
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

    private DisposableObserver<Student> getObserver(){
        myObserver = new DisposableObserver<>() {
            @Override
            public void onNext(@NonNull Student i) {
                Log.i(TAG, "onNext" + i.getName());
                textView.setText(i.getName());
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

    private ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student("Hans","hans@email.com",23,"22.03.2024"));
        students.add(new Student("maia","maia@email.com",21,"14.05.2024"));
        students.add(new Student("pedro","pedro@email.com",27,"1.1.2024"));
        students.add(new Student("victor","victor@email.com",34,"11.1.2024"));
        return students;
    }
}