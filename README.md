# Wraith: Nomba Android SDK

The Nomba Android SDK allows you bring the same great Nomba Checkout experience natively to your android apps. 
Accept payments in your app by bank transfer or card

[![Platform](/platform.svg)]()
[![JitPack](https://jitpack.io/v/kudi-inc/nomba-android-sdk.svg)](https://jitpack.io/#kudi-inc/nomba-android-sdk)
<br><br>

<p float="left">
  <img src="/ytscreens/1.png" width="24%" />
  <img src="/ytscreens/2.png" width="24%" />
  <img src="/ytscreens/3.png" width="24%" />
  <img src="/ytscreens/4.png" width="24%" /> 
</p>


<p float="left">
  <img src="/ytscreens/5.png" width="24%" />
  <img src="/ytscreens/6.png" width="24%" />
  <img src="/ytscreens/7.png" width="24%" />
  <img src="/ytscreens/8.png" width="24%" />
</p>
<br>


## 🚀 Getting Started

Add to your root build.gradle, the JitPack Repository

```
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}
```

Add to your project build.gradle file, the Nomba Android SDK

```
implementation("com.github.kudi-inc:nomba-android-sdk:v1.0.2")
```
<br>



## 📖 Documentation

All of your interactions with the Nomba Android SDK is done through a singleton, the NombaManager. 
Initialise it as early as you can, pass the activity (for Context), your Nomba accountID, your Nomba ClientID, 
your Nomba ClientKey (you can get this values from your Nomba Dashboard)
and the main ViewGroup which would house the NombaManager UI. The ViewGroup should be a constraintLayout or extend from it.    

```
val nombaManager = NombaManager.getInstance(this, "accountId",  clientId = "clientId", 
clientKey = "clientKey", main)
```

NombaManager handles it's own back stack, managing views and UI when it's presented and the back button or back gesture is 
triggered. You need to include in your application's onbackpressed callback, NombaManager's backstack handler. An example below

```
 val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // call this in your backstack management handling code,
            // to enable the back button response to Wraith's DisplayStates
            nombaManager.handleBackStack()
        }
}
onBackPressedDispatcher.addCallback(callback)
```

Before presenting the NombaManger PaymentView, pass in details for the current payment session 

```
nombaManager.paymentAmount = 10.0
nombaManager.customerEmail = "knightbenax@gmail.com"
nombaManager.customerName = "Emeka Bond"
nombaManager.orderReference = "7dc1558f-dcc3-4cc8-b4cd-6ba8603efcee"
nombaManager.customerId = "58530bfe-e1f9-405f-b1b0-249910e6a09f"
nombaManager.logo = R.drawable.logo
```


When you are ready for payment to occur, simple present the NombaManger payment view

```
// show the SDK when you want to make a purchase
button.setOnClickListener {
    nombaManager.showPaymentView()
}
```
<br>



## 📱 Projects

If you use the Nomba Android SDK in your project and would like it listed here, simply create a new issue with the title of your app, link to it on the PlayStore and tag it
with the label 'project'. It would get added here afterwards.

<br>



## 👨‍💻 Contributing

Pull requests with bugfixes and new features are much appreciated. We'll be happy to review them and merge them once they're ready, as long as they contain change that fit within the vision of Nomba's Android SDK and provide generally useful functionality.

Clone the repository to get started working on the project.

```bash
git clone https://github.com/kudi-inc/nomba-android-sdk
```
<br>




## ❤️ Acknowledgments

- [PinView](https://github.com/ChaosLeung/PinView) is used to provide the otp and pin textfields.
- [Retrofit](https://github.com/square/retrofit) is used for network request management.

