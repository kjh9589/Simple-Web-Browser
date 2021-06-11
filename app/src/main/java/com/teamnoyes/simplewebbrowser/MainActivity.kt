package com.teamnoyes.simplewebbrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {
    private val addressBar: EditText by lazy {
        findViewById<EditText>(R.id.addressBar)
    }

    private val goHomeButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goHomeButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goForwardButton)
    }

    private val goGoBackButton: ImageButton by lazy {
        findViewById<ImageButton>(R.id.goBackButton)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
    }

    private val webView: WebView by lazy {
        findViewById<WebView>(R.id.webView)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById<ContentLoadingProgressBar>(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
    }

    override fun onBackPressed() {
        // history가 남아있는지에 따라
        if (webView.canGoBack()){
            webView.goBack()
        }
        else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        // 기본 동작 url 핸들링 가능한 앱을 시작함
//        webView.loadUrl("http://www.google.com")

        //WebViewClient 컨텐츠 로딩
        //WebChromeClient 브라우저 차원 (자바스크립트 alert 등)
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
            // 자동으로 키보드가 나온다면
            requestFocus()
        }
    }

    private fun bindViews() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }
        // 액션이 발생했을 때
        addressBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                // http 혹은 https가 붙어있는 지 확인 둘다 없으면 false 반환
                if (URLUtil.isNetworkUrl(loadingUrl)){
                    webView.loadUrl(loadingUrl)
                }
                else{
                    webView.loadUrl("http://${loadingUrl}")
                }
            }

            // ACTION_DONE 기능 중 하나 키보드를 닫는다.
            // 이 또한 액션을 소비하기에 true라면 키보드가 닫히지 않는다.
            return@setOnEditorActionListener false
        }

        goGoBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    inner class WebViewClient: android.webkit.WebViewClient(){
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            goGoBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient: android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object{
        private const val DEFAULT_URL = "http://www.google.com"
    }
}