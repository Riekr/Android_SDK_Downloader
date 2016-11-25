# Android SDK Downloader
Simple portable utility for downloading android sdk zip files and use them on a computer with a slow connection.

<h2>Rationale:</h2>
If you want to use a android sdk on a computer with a slow internet connection you will have to download zip files from another station, put them in the <code>temp</code> subfolder of your android sdk, run android sdk and install them without big dowloads. Well.. sort of, internet connection is still required for zip file validation but only a small amount of data will be used.

<h2>Usage:</h2>

On a computer with a good/flat internet connection open a console and execute:

<code>java -jar Android_SDK_Downloader.jar</code>

then wait for the downloads to complete.

Here are the additional parameters:

<pre>
java com.riekr.android.sdk.downloader.Main [options...]
 --base-url (-U) VAL  : Specify alternative google repository (default: https://dl.google.com/android/repository/)
 --dest (-P) VAL      : Destination path (default: temp)
 --dry-run            : Dumps only urls, does not download anything (default: false)
 --lock               : Prevents multiple instances (beta) (default: false)
 --obsolete           : Download obsolete packages too (default: false)
 --verbose (-v)       : Verbose output (default: false)
 --xml-addons VAL     : Specify addons list file with version (default: addons_list-2.xml)
 --xml-repository VAL : Specify repository file with version (default: repository-11.xml)
</pre>

Please note that the <code>--xml-*</code> parameters specify the google list of packages and these file names are bundled with official android sdk. If you are aware of a new version specify them as command line arguments or check for updates.
<p>
If you relaunch the utility, already downloaded packages will be resumed, if the entire file has been downloaded it will be skipped.
</p>

<h2>Thanks</h2>
This utility uses:
<ul>
<li><a href="https://github.com/kohsuke/args4j/">Args4J</a> a small Java class library that makes it easy to parse command line options/arguments</li>
<li><a href="https://github.com/axet/wget">Axet wget</a> direct / multithread / singlethread java download library</li>
</ul>

Developement has been done using <a href="https://www.jetbrains.com/idea/">IntelliJ IDEA Community Edition</a>.
