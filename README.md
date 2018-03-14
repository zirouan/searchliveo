# SearchLiveo [![](https://jitpack.io/v/rudsonlive/searchliveo.svg)](https://jitpack.io/#rudsonlive/searchliveo)
<br>
Library SearchView material design
<br>
<br>
<img src="https://github.com/rudsonlive/SearchView-MaterialDesign/blob/master/Screenshot/SearchLiveo.png">
<br>

# Gradle
Step 1 - Add the JitPack repository to your root build.gradle file
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

Step 2 - Add the dependencies:

dependencies {
  implementation 'com.github.rudsonlive:searchliveo:1.0.1'
}
```
<br>

# Quick Start

<b>1</b> - Add SearchLiveo to your layout
```xml```
    <AppBarLayout...

        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <Toolbar.../>

            <br.com.liveo.searchliveo.SearchLiveo
                android:id="@+id/search_liveo"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
        </FrameLayout>

    </AppBarLayout>
```xml```
<br>

<b>2</b> - Add SearchLiveo to my Activity
```xml```
    //findViewById...
    mSearchLiveo.with(this).build();

    //Starting SearchLiveo...
    mSearchLiveo.show();
```xml```
# License
```
Copyright 2018 Rudson Lima

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
