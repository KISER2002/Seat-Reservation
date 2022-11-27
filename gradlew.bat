<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="3dp"
    android:orientation="horizontal">

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="3"
        android:background="#00FFFFFF"
        android:orientation="vertical">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#00FFFFFF"
            android:orientation="horizontal">

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="match_parent"
                android:layout_weight="3"
                android:orientation="vertical">

                <TextView
                    android:id="@+id/date_contain"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="0"
                    android:gravity="center_vertical"
                    android:paddingLeft="10dp"
                    android:paddingRight="10dp"
                    android:text="TextView"
                    android:textColor="#000000"
                    android:textSize="20sp" />

                <TextView
                    android:id="@+id/post_title_item"
                    android:layout_width="match_parent"
                    android:layout_height="100dp"
                    android:layout_weight="2"
                    android:background="@drawable/round"
                    android:gravity="center_vertical"
                    android:paddingLeft="10dp"
                    android:paddingRight="10dp"
                    android:text="TextView"
                    android:textColor="#000000"
                    android:textSize="30sp" />
            </LinearLayout>

        </LinearLayout>

    </LinearLayout>

<!--    <LinearLayout-->
<!--        android:layout_width="0dp"-->
<!--        android:layout_height="match_parent"-->
<!--        android:layout_weight="1"-->
<!--        android:orientation="vertical"-->
<!--        android:paddingLeft="5dp"-->
<!--        android:paddingRight="5dp">-->

<!--        <Button-->
<!--            android:id="@+id/view_btn"-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="0dp"-->
<!--            android:layout_weight="1"-->
<!--        