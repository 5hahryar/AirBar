# AirBar
<p align="center"><img src="AirBar.png" /></p>

[![](https://jitpack.io/v/5hahryar/AirBar.svg)](https://jitpack.io/#5hahryar/AirBar)
[![Platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

# Usage
``` 
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
```
dependencies {
		implementation 'com.github.5hahryar:AirBar:Tag'
	}
  ```
  
  ### XML
  ```
  <com.shahryar.airbar.AirBar
        android:id="@+id/airbar"
        android:layout_width="70dp"
        android:layout_height="200dp"/>
  ```
  
   ### Jetpack Compose
  ```
AirBar(
	modifier = Modifier
	    .height(200.dp)
	    .width(80.dp),
	fillColor = androidx.compose.ui.graphics.Color.Blue,
	backgroundColor = androidx.compose.ui.graphics.Color.LightGray,
	cornerRadius = 20.dp,
	maxValue = 100.0,
	minValue = 0.0,
	onPercentageChanged = { percentage ->
	    // percentage changed
	}
)
  ```
  *Note: AirBar for compose has limited functionality, it does not support icon and gradient colors yet!
  
  ### Listener
  ```
  airbar.setOnProgressChangedListener(object : AirBar.OnProgressChangedListener{
            override fun onProgressChanged(airBar: AirBar, progress: Double, percentage: Double) {
                //Write your code
            }

            override fun afterProgressChanged(airBar: AirBar, progress: Double, percentage: Double) {
                //Write your code
            }
        })
  ```
  
  # Attributes
  Attribute | Functionality
  ------------ | ------------- 
  progressBarFillColor | Set color for level indicator
  progressBarColor0 | Set gradient color for bottom of the view
  progressBarColor1 | Set gradient color for top of the view
  backgroundFillColor | Set background color
  backgroundCornerRadius | Set corner radius 
  icon | Set icon
  max | Set Maximum value 
  min | Set minimum value
  
  # License
  AirBar is licensed under `MIT license`. View [LICENSE](LICENSE).
