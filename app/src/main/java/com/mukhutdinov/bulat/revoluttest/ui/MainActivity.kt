package com.mukhutdinov.bulat.revoluttest.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mukhutdinov.bulat.revoluttest.R
import com.mukhutdinov.bulat.revoluttest.ui.adapter.CurrencyAdapter
import com.mukhutdinov.bulat.revoluttest.util.showError
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel<MainAndroidViewModel>()

    private lateinit var adapter: CurrencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = CurrencyAdapter(viewModel.baseCurrency, this)

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

        viewModel.error.observe(this, Observer {
            loading.visibility = GONE

            showError(it)
        })
    }

    override fun onStop() {
        super.onStop()
        adapter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::adapter.isInitialized) {
            adapter.onDestroy()
        }
    }
}