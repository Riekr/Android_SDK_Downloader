#!/bin/sh
export http_proxy=http://localhost:8080
wget "http://dl.google.com/android/repository/addons_list-2.xml" -O -
wget -t 1 "http://dl.google.com/android/repository/sys-img/android/arm64-v8a-24_r07.zip" -O /dev/null
