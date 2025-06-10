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
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "RxAndroid MainActivity";
    private Observable<Student> myObservable;
    private DisposableObserver<Student> myObserver;
    //private Observer<Integer> myObserver;
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

        // Buffer operators
//        myObservable = Observable.range(1,20);
//        myObservable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .buffer(4)
//                .skip(6) // skips emissions of first 6 items
//                .skipLast(4) // skips emissions of last 4 items
//                .distinct() // filters out duplicates
//                .filter(new Predicate<List<Student>>() {
//                    @Override
//                    public boolean test(List<Student> students) throws Throwable {
//                        // my check condition goes in here
//                        return false;
//                    }
//                })
//                .subscribe(new Observer<List<Integer>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull List<Integer> integers) {
//                        Log.i(TAG, "onNext: " + integers.toString());
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

        // cleaner way to add and subscribe to observables
        compositeDisposable.add(myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                        // see how to use the map operator to transform data before they get emmited
                        //.map(s -> new Student(s.getName().toUpperCase(), s.getEmail(), s.getAge(), s.getRegistrationDate()))
                .flatMap(new Function<Student, ObservableSource<Student>>() {
                    @Override
                    public ObservableSource<Student> apply(Student student) throws Throwable {
                        Student student1 = new Student("Müller","hans@email.com",28,"05.02.2025");
                        Student student2 = new Student("Ueli","hans@email.com",35,"26.04.2023");

                        return Observable.just(student, student1, student2);
                    }
                })
                .concatMap(new Function<Student, ObservableSource<Student>>() {
                    @Override
                    public ObservableSource<Student> apply(Student student) throws Throwable {
                        Student student1 = new Student("Müller","hans@email.com",28,"05.02.2025");
                        Student student2 = new Student("Ueli","hans@email.com",35,"26.04.2023");

                        return Observable.just(student, student1, student2);
                    }
                })
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