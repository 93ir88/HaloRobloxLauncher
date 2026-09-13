.class public Lcom/roblox/client/halo/HaloActivity;
.super Landroid/app/Activity;

.field private webView:Landroid/webkit/WebView;

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V
    return-void
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .registers 5
    .param p1, "savedInstanceState"

    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    # new WebView(this)
    new-instance v0, Landroid/webkit/WebView;
    invoke-direct {v0, p0}, Landroid/webkit/WebView;-><init>(Landroid/content/Context;)V
    iput-object v0, p0, Lcom/roblox/client/halo/HaloActivity;->webView:Landroid/webkit/WebView;

    # setContentView(webView)
    invoke-virtual {p0, v0}, Landroid/app/Activity;->setContentView(Landroid/view/View;)V

    # settings
    invoke-virtual {v0}, Landroid/webkit/WebView;->getSettings()Landroid/webkit/WebSettings;
    move-result-object v1

    const/4 v2, 0x1

    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setJavaScriptEnabled(Z)V
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setAllowFileAccess(Z)V
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setAllowUniversalAccessFromFileURLs(Z)V
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setDomStorageEnabled(Z)V

    # mixed content: MIXED_CONTENT_ALWAYS_ALLOW = 0
    const/4 v2, 0x0
    invoke-virtual {v1, v2}, Landroid/webkit/WebSettings;->setMixedContentMode(I)V

    # addJavascriptInterface(new HaloBridge(this), "Android")
    new-instance v2, Lcom/roblox/client/halo/HaloBridge;
    invoke-direct {v2, p0}, Lcom/roblox/client/halo/HaloBridge;-><init>(Landroid/content/Context;)V
    const-string v3, "Android"
    invoke-virtual {v0, v2, v3}, Landroid/webkit/WebView;->addJavascriptInterface(Ljava/lang/Object;Ljava/lang/String;)V

    # loadUrl
    const-string v2, "file:///android_asset/halo/index.html"
    invoke-virtual {v0, v2}, Landroid/webkit/WebView;->loadUrl(Ljava/lang/String;)V

    return-void
.end method

.method public onBackPressed()V
    .registers 2
    iget-object v0, p0, Lcom/roblox/client/halo/HaloActivity;->webView:Landroid/webkit/WebView;
    if-eqz v0, :skip

    invoke-virtual {v0}, Landroid/webkit/WebView;->canGoBack()Z
    move-result v1
    if-eqz v1, :skip

    invoke-virtual {v0}, Landroid/webkit/WebView;->goBack()V
    return-void

    :skip
    return-void
.end method

.method public onResume()V
    .registers 2
    invoke-super {p0}, Landroid/app/Activity;->onResume()V
    iget-object v0, p0, Lcom/roblox/client/halo/HaloActivity;->webView:Landroid/webkit/WebView;
    if-eqz v0, :skip
    invoke-virtual {v0}, Landroid/webkit/WebView;->onResume()V
    :skip
    return-void
.end method

.method public onPause()V
    .registers 2
    invoke-super {p0}, Landroid/app/Activity;->onPause()V
    iget-object v0, p0, Lcom/roblox/client/halo/HaloActivity;->webView:Landroid/webkit/WebView;
    if-eqz v0, :skip
    invoke-virtual {v0}, Landroid/webkit/WebView;->onPause()V
    :skip
    return-void
.end method

.method public onDestroy()V
    .registers 2
    iget-object v0, p0, Lcom/roblox/client/halo/HaloActivity;->webView:Landroid/webkit/WebView;
    if-eqz v0, :skip
    invoke-virtual {v0}, Landroid/webkit/WebView;->destroy()V
    :skip
    invoke-super {p0}, Landroid/app/Activity;->onDestroy()V
    return-void
.end method
