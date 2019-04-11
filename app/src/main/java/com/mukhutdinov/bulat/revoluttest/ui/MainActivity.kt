package com.mukhutdinov.bulat.revoluttest.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mukhutdinov.bulat.revoluttest.R
import com.mukhutdinov.bulat.revoluttest.ui.adapter.CurrencyAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel<MainAndroidViewModel>()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = CurrencyAdapter(viewModel.baseCurrency)

        adapter.setHasStableIds(true)

        currencies.setHasFixedSize(true)
        currencies.adapter = adapter
        currencies.layoutManager = LinearLayoutManager(this)

        viewModel.currencies.observe(this, Observer {
            loading.visibility = GONE

            adapter.updateCurrencies(it)
        })

        viewModel.isUpToDate.observe(this, Observer {
            status.visibility = VISIBLE

            if (it) {
                status.setImageDrawable(getDrawable(R.drawable.online))
            } else {
                status.setImageDrawable(getDrawable(R.drawable.offline))
            }
        })
    }
}