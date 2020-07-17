NestedCeilingEffect project
===============

一个嵌套滑动吸顶交互效果的控件。

[也许是最贴近京东体验的嵌套滑动吸顶效果](http://solart.cc/2020/07/17/nested_ceiling_effect/)

效果展示（由于 gif 图过大，或请通过 run demo查看）：

<img src="./preview/live.gif" width=220>

提供了两种外层的嵌套滑动控件 `NestedParentRecyclerView` 以及 `NestedParentScrollView` ，首先介绍 `NestedParentScrollView` 的用法：

## Usage `NestedParentScrollView` 

#### Step 1. 通过布局文件引入

在外层布局文件中添加 NestedParentScrollView，

```xml
<cc.solart.nestedceiling.widget.NestedParentScrollView
    android:id="@+id/nested_parent"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- 头部布局 -->
        <HeaderView
            ... />
		<!-- 需要吸附的布局 -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <com.google.android.material.tabs.TabLayout
                android:id="@+id/tab_layout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

            <androidx.viewpager2.widget.ViewPager2
                android:id="@+id/view_pager"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />
        </LinearLayout>

    </LinearLayout>


</cc.solart.nestedceiling.widget.NestedParentScrollView>
```

在子 tab 布局文件中添加 NestedChildRecyclerView

```xml
<cc.solart.nestedceiling.widget.NestedChildRecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</cc.solart.nestedceiling.widget.NestedChildRecyclerView>
```

#### Step 2. 在代码中使用

```kotlin
val nestedParent: NestedParentScrollView = findViewById(R.id.nested_sv)
...
```

#### Step 3. 添加吸附状态监听

```kotlin
// 如果需要监听吸附的状态变化
nestedParent.addOnChildAttachStateListener(object :OnChildAttachStateListener{
            override fun onChildDetachedFromTop() {
                topAnchor.visibility = View.GONE
            }

            override fun onChildAttachedToTop() {
                topAnchor.visibility = View.VISIBLE
            }

        })
```

## Usage `NestedParentRecyclerView` 

#### Step 1. 通过布局文件引入

在外层布局文件中添加 NestedParentRecyclerView

```xml
<cc.solart.nestedceiling.widget.NestedParentRecyclerView
	android:id="@+id/nested_rv"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

</cc.solart.nestedceiling.widget.NestedParentRecyclerView>
```

在子 tab 布局文件中添加 NestedChildRecyclerView

```xml
<cc.solart.nestedceiling.widget.NestedChildRecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</cc.solart.nestedceiling.widget.NestedChildRecyclerView>
```

#### Step 2. 在代码中使用

```kotlin
val recyclerView: NestedParentRecyclerView = findViewById(R.id.nested_rv)
recyclerView.layoutManager = LinearLayoutManager(this)
recyclerView.adapter = adapter
...
```

将要吸附的控件，作为最后一个 RecyclerView 的 item 加入 adapter 中即可。

#### Step 3. 添加吸附状态监听

```kotlin
// 如果需要监听吸附的状态变化
nestedParent.addOnChildAttachStateListener(object :OnChildAttachStateListener{
            override fun onChildDetachedFromTop() {
                topAnchor.visibility = View.GONE
            }

            override fun onChildAttachedToTop() {
                topAnchor.visibility = View.VISIBLE
            }

        })
```

以上用法如有疑问，请参看 demo，欢迎建议交流、Issues、Star

#### 已知问题

- Fling 状态传递偶有略微延迟（跟递归查找滑动子 View 有关，如果要优化可从外部传入状态，省去查找时间，为了保持灵活性暂未处理）

## Thanks

- [MultiType](https://github.com/drakeet/MultiType)
- [Banner](https://github.com/youth5201314/banner)

License
-------

    Copyright 2016 solartisan/blue
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.