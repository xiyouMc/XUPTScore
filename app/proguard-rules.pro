# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
# Add any project specific keep options here:
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-dontwarn cn.sharesdk.sina.weibo.**
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class m.framework.**{*;}

#-ignorewarnings
# �����ݾ����SDK�汾�޸�
#-libraryjars libs/BmobIM_V1.1.9beta_20150820.jar
#-libraryjars libs/BmobSDK_V3.4.3_0820.jar
-libraryjars libs/BmobSDK_V3.4.5_1111.jar
-keepattributes Signature
-dontwarn cn.bmob.v3.**

-keep class cn.bmob.v3.** {*;}
-keep class cn.bmob.push.** {*;}
-keep class cn.bmob.im.**{*;}
-keep class com.xy.fy.main.MainActivity.**{*;}

# ��֤�̳���BmobObject��BmobUser���JavaBean��������
-keep class com.example.bmobexample.bean.Person{*;}
-keep class com.example.bmobexample.bean.MyUser{*;}
-keep class com.example.bmobexample.bean.BankCard{*;}
-keep class com.example.bmobexample.file.Movie{*;}
-keep class com.example.bmobexample.file.Song{*;}
-keep class com.example.bmobexample.relationaldata.Weibo{*;}
-keep class com.example.bmobexample.relationaldata.Comment{*;}

-keep class java.lang.reflect.**{*;}
-keep class com.baidu.mapapi.**{*;}
-keep class com.baidu.platform.**{*;}
-keep class org.apache.commons.mail.**{*;}
-dontwarn org.apache.commons.mail.**
-dontwarn org.apache.harmony.awt.**
-keep class org.apache.harmony.awt.**{*;}
-dontwarn com.sun.mail.imap.**
-keep class com.sun.mail.imap.**{*;}
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**

-keep class javamail.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.** {*;}

-keep class com.sun.mail.dsn.** {*;}
-keep class com.sun.mail.handlers.** {*;}
-keep class com.sun.mail.smtp.** {*;}
-keep class com.sun.mail.util.** {*;}
-keep class mailcap.** {*;}
-keep class mimetypes.** {*;}
-keep class myjava.awt.datatransfer.** {*;}
-keep class org.apache.harmony.awt.** {*;}
-keep class org.apache.harmony.misc.** {*;}

-dontwarn javax.activation.**
-keep class javax.activation.**{*;}
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
