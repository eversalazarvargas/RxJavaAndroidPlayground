package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.everardo.rxjavaplayground.R
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_behavior_subject.*

class SubjectActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_behavior_subject)

        val subjectFirstName: BehaviorSubject<String> = BehaviorSubject.create<String>()
        val subjectLastName: BehaviorSubject<String> = BehaviorSubject.create<String>()

        ArrayAdapter.createFromResource(this,
                R.array.first_names,
                android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFirstName.adapter = adapter
        }
        spinnerFirstName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val firstName = parent.getItemAtPosition(position) as String
                subjectFirstName.onNext(firstName)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        ArrayAdapter.createFromResource(this,
                R.array.last_names,
                android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLastName.adapter = adapter
        }
        spinnerLastName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val lastName = parent.getItemAtPosition(position) as String
                subjectLastName.onNext(lastName)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        Observable.combineLatest(arrayOf(subjectFirstName, subjectLastName)) {
            it.joinToString(separator = ",")
        }.subscribe {
            tvFirstAndLastName.text = it
        }
    }
}