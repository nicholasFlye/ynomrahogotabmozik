/**
 * Created by XxFLYE on 12/10/2014.
 */
package com.ktgo.xxflye.kizombaharmony;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.content.Intent;

import com.android.vending.billing.IInAppBillingService;
import com.ktgo.xxflye.kizombaharmony.util.CustomListViewAdapter;
import com.ktgo.xxflye.kizombaharmony.util.CustomListViewAdapterP;
import java.io.File;

public class KizombaHarmonyMain extends Activity{

    private ViewFlipper mainMenu;
    private ListView lessonsList, lessonProgress;
    private Integer levelSelected = 1, progress = 0;
    private WebView blogView;
    private ImageView lesson, settings, about, blog, level1, level2, level3,level4, level5, level6,
            level7;


    //In-app purchase base64 Public Key
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuLbaTdKFnGVw8J+geRcBEbU2bieNezhnpHnSmeNvMU8QFTt0U2HS4uvsvKrXHEtZhTf4fxobHq0BRZqpj8ZeBaQRLD/0qYAfUxqmLGdYOlBDeeVWwo9yx9LszP1jwJu59VT2TgM2WbdVB1TQ6JX5h3UQmT58zAg7UkQfG6/e7NjyEUG08HQyTleNkQ7r5DRymtpUfEO2YEuOQ33ji2PPREoc43V97h+J3nzZxUJOHKpoBKNa7mhMfLjQqzANRlOS6usuRLRTi7MW+o9DZGB1BPUOVk7NsNL0Ogk8YuEBh7Z02mSO7u8PJ7t91nDSsC8/8cgt0JdSZXK5mv2kyF4nZwIDAQAB";

    //Initialize In-app billing service
    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    //Lesson names to be shown in list
    String[] lessonNames = {"Introduction",
            "The Kizomba Connection",
            "Basic 1 - Tarraxinha Basic",
            "Basic 2 - Side Basic",
            "The Kizomba Walk",
            "The Balance Step",
            "90 Degree Left Turn",
            "The Cha Cha Step",
            "Cha Cha Variations",
            "Rotational Walking",
            "Level 1 Demo",
            "Thank You"
    };
    //Lesson levels and progress
    String[] levelsNumbers = {"Level 1", "Level 2", "Level 3 (Locked)", "Level 4 (Locked)", "Level 5 (Locked)", "Level 6 (Locked)", "Level 7 (Locked)"};
    String[] progressNumbers = {"0/12", "TBA", "TBA", "TBA", "TBA", "TBA", "TBA"};
    //Lesson thumbnails
    Integer[] vidthumbnails = {
            com.ktgo.xxflye.kizombaharmony.R.drawable.introthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.kizombaconnectionthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.basicthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.sidebasicthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.kizombawalk,
            com.ktgo.xxflye.kizombaharmony.R.drawable.thebalancestepthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.degreeleftturn,
            com.ktgo.xxflye.kizombaharmony.R.drawable.chachastepthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.chachavariationthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.rotationalthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.demothumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.thankyouthumbnail
    };

    String[] drawerItemTitles = {"Main Menu", "Follow Series", "CrioloRadio", "About"};
    Integer[] drawerThumbnails = {
            com.ktgo.xxflye.kizombaharmony.R.drawable.introthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.introthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.introthumbnail,
            com.ktgo.xxflye.kizombaharmony.R.drawable.introthumbnail,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.ktgo.xxflye.kizombaharmony.R.layout.menuflipper);

        //Setup the navigation drawer
        /*final CustomListViewAdapter adapter = new CustomListViewAdapter(this, drawerItemTitles, drawerThumbnails);

        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.left_drawer);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){
                drawer.setDrawerListener( new DrawerLayout.SimpleDrawerListener(){
                    @Override
                    public void onDrawerClosed(View drawerView){
                        super.onDrawerClosed(drawerView);

                    }
                });
                drawer.closeDrawer(navList);
            }
        });*/

        //Setup billiing services
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        getProgressData();

        //Main menu view flipper setup
        mainMenu = (ViewFlipper) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.MenuFlipper);

        //Blog view setup
        blogView = (WebView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.blogView);
        blogView.canGoBack();
        blogView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
        blogView.getSettings().getJavaScriptEnabled();
        blogView.getSettings().setJavaScriptEnabled(true);
        blogView.loadUrl("http://kizombaharmony.com/blog/");

        //Lesson list & progress setup
        lessonsList = (ListView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.lessonList);
        lessonProgress = (ListView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.progressView);

        final CustomListViewAdapter lessonadapter = new CustomListViewAdapter(this, lessonNames, vidthumbnails);
        lessonsList.setAdapter(lessonadapter);

        CustomListViewAdapterP progressAdapter = new CustomListViewAdapterP(this, levelsNumbers, progressNumbers);
        lessonProgress.setAdapter(progressAdapter);

        lessonsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(KizombaHarmonyMain.this,
                        LessonViewerVideo.class);
                myIntent.putExtra("lessonNumber", position);
                myIntent.putExtra("download", true);
                startActivity(myIntent);
                return true;
            }
        });

        lessonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(KizombaHarmonyMain.this,
                        LessonViewerVideo.class);
                myIntent.putExtra("lessonNumber", position);
                startActivity(myIntent);
            }
        });

        //Setup About Us links
        TextView Facebook = (TextView)findViewById(R.id.Facebook);
        TextView Instagram = (TextView)findViewById(R.id.Instagram);
        TextView Youtube = (TextView)findViewById(R.id.YouTube);
        TextView Twitter = (TextView)findViewById(R.id.Twitter);
        TextView CrioloRadio = (TextView)findViewById(R.id.CrioloRadio);

        Facebook.setMovementMethod(LinkMovementMethod.getInstance());

        Instagram.setMovementMethod(LinkMovementMethod.getInstance());

        Youtube.setMovementMethod(LinkMovementMethod.getInstance());

        Twitter.setMovementMethod(LinkMovementMethod.getInstance());

        CrioloRadio.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    public void getProgressData(){

        Intent myIntent = getIntent();

        Resources res = getResources();
        SharedPreferences sharedPref = getPreferences(0);
        SharedPreferences.Editor editor = sharedPref.edit();
        Integer levelCompleted =  myIntent.getIntExtra("level1Completed", -1);

        progress = sharedPref.getInt("Progress", 0);
        progressNumbers[0] = progress.toString() + "/12";
        progress = 0;

        if(levelCompleted >= 0) {
            Boolean completed = sharedPref.getBoolean(levelCompleted.toString(), false);

            if (!completed) {
                editor.putBoolean(levelCompleted.toString(), true);
                progress = sharedPref.getInt("Progress1", 0);
                progress++;
                editor.putInt("Progress1", progress);
                editor.commit();
                progressNumbers[0] = progress.toString() + "/12";
                progress = 0;

            }
        }

        Integer downloaded = myIntent.getIntExtra("downloaded", -1);
        for (Integer x = 0; x < 12; x++){

            String[] lessonLinks = res.getStringArray(R.array.Level1_Lessons);

            String isDownloaded = x.toString() + "downloaded";
            Integer lessonDownloaded = sharedPref.getInt(isDownloaded, -1);
            String path = getExternalFilesDir("." + Environment.DIRECTORY_DOWNLOADS).getPath();
            File lesson = new File(path + "/" + lessonLinks[x]);
            Boolean lessonDownloadStatus = lesson.exists();

            if(downloaded == x){

                lessonNames[x] = lessonNames[x] + "*";
                editor.putInt(downloaded.toString() + "downloaded", downloaded);
                editor.commit();

            }
            else if(lessonDownloaded != x || !lessonDownloadStatus){
                if(lessonNames[x].endsWith("*")){
                    lessonNames[x] = lessonNames[x].substring(0, lessonNames[x].length()-1);
                }
            }
            else if(lessonDownloadStatus){
                lessonNames[x] = lessonNames[x] + "*";
            }
            else{

            }
        }
    }

    public void menuButtonOperations(View view){

        lesson = (ImageView)findViewById(com.ktgo.xxflye.kizombaharmony.R.id.lessons);
        settings = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.settings);
        about = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.about);
        blog = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.blog);

        switch (view.getId()){

            case com.ktgo.xxflye.kizombaharmony.R.id.lessons:
                try{
                    if(mainMenu.getDisplayedChild() == 2) {
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.rightout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.leftin);
                        lesson.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.lessonsbuttonselected);
                        settings.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.settingsbuttonnotselectedgray);
                        mainMenu.showPrevious();
                        mainMenu.showPrevious();
                    }
                    else if(mainMenu.getDisplayedChild() == 1){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.rightout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.leftin);
                        lesson.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.lessonsbuttonselected);
                        blog.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.blogbuttonnotselectedgray);
                        mainMenu.showPrevious();
                    }
                    else if(mainMenu.getDisplayedChild() == 3){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.leftout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.rightin);
                        lesson.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.lessonsbuttonselected);
                        about.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.aboutusbuttonnotselectedgray);
                        mainMenu.showNext();
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.blog:
                try{
                    if(mainMenu.getDisplayedChild() == 0) {
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.leftout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.rightin);
                        blog.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.blogbuttonselected);
                        lesson.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.lessonsbuttonnotselectedgray);
                        mainMenu.showNext();
                    }
                    else if(mainMenu.getDisplayedChild() == 2){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.rightout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.leftin);
                        blog.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.blogbuttonselected);
                        settings.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.settingsbuttonnotselectedgray);
                        mainMenu.showPrevious();
                    }
                    else if(mainMenu.getDisplayedChild() == 3){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.leftout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.rightin);
                        blog.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.blogbuttonselected);
                        about.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.aboutusbuttonnotselectedgray);
                        mainMenu.showNext();
                        mainMenu.showNext();
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.settings:
                try{
                    if(mainMenu.getDisplayedChild() == 0) {
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.leftout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.rightin);
                        settings.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.settingsbuttonselected);
                        lesson.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.lessonsbuttonnotselectedgray);
                        mainMenu.showNext();
                        mainMenu.showNext();
                    }
                    else if(mainMenu.getDisplayedChild() == 1){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.leftout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.rightin);
                        settings.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.settingsbuttonselected);
                        blog.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.blogbuttonnotselectedgray);
                        mainMenu.showNext();
                    }
                    else if(mainMenu.getDisplayedChild() == 3){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.rightout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.leftin);
                        settings.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.settingsbuttonselected);
                        about.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.aboutusbuttonnotselectedgray);
                        mainMenu.showPrevious();
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.about:
                try{
                    if(mainMenu.getDisplayedChild() == 0) {
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.rightout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.leftin);
                        about.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.aboutusbuttonselected);
                        lesson.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.lessonsbuttonnotselectedgray);
                        mainMenu.showPrevious();
                    }
                    else if(mainMenu.getDisplayedChild() == 1){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.rightout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.leftin);
                        about.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.aboutusbuttonselected);
                        blog.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.blogbuttonnotselectedgray);
                        mainMenu.showPrevious();
                        mainMenu.showPrevious();
                    }
                    else if(mainMenu.getDisplayedChild() == 2){
                        mainMenu.setOutAnimation(KizombaHarmonyMain.this, R.anim.leftout);
                        mainMenu.setInAnimation(KizombaHarmonyMain.this, R.anim.rightin);
                        about.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.aboutusbuttonselected);
                        settings.setImageResource(com.ktgo.xxflye.kizombaharmony.R.drawable.settingsbuttonnotselectedgray);
                        mainMenu.showNext();
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
        }
    }

    public void levelOperations(View view){

        level1 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level1);
        level2 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level2);
        level3 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level3);
        level4 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level4);
        level5 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level5);
        level6 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level6);
        level7 = (ImageView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.level7);

        ViewFlipper lessonflipper = (ViewFlipper) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.LessonFlipper);
        TextView comingsoon = (TextView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.comingsoon);

        switch(view.getId()){
            case com.ktgo.xxflye.kizombaharmony.R.id.level1:
                try{

                    if(levelSelected == 2) {

                        level2.setBackgroundResource(0);
                        level1.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showPrevious();
                    }
                    /*else if(levelSelected == 3){
                        level3.setBackgroundResource(0);
                        level1.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        //lessonflipper.showNext();
                    }
                    else if(levelSelected == 4){
                        level4.setBackgroundResource(0);
                        level1.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        //lessonflipper.showNext();
                    }
                    else if(levelSelected == 5){
                        level5.setBackgroundResource(0);
                        level1.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        //lessonflipper.showNext();
                    }
                    else if(levelSelected == 6){
                        level6.setBackgroundResource(0);
                        level1.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        //lessonflipper.showNext();
                    }
                    else if(levelSelected == 7){
                        level7.setBackgroundResource(0);
                        level1.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        //lessonflipper.showNext();
                    }
                    else{
                    }*/
                    levelSelected = 1;

                }
                catch(Exception e){

                    e.printStackTrace();

                }
            break;
            case com.ktgo.xxflye.kizombaharmony.R.id.level2:
                try{

                    comingsoon.setText("TBA!");

                    if(levelSelected == 1) {
                        level1.setBackgroundResource(0);
                        level2.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showNext();
                    }
                   /* else if(levelSelected == 3){
                        level3.setBackgroundResource(0);
                        level2.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 4){
                        level4.setBackgroundResource(0);
                        level2.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 5){
                        level5.setBackgroundResource(0);
                        level2.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 6){
                        level6.setBackgroundResource(0);
                        level2.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 7){
                        level7.setBackgroundResource(0);
                        level2.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else{
                    }*/
                    levelSelected = 2;

                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            /*case com.ktgo.xxflye.kizombaharmony.R.id.level3:
                try{

                    if(levelSelected == 1) {
                        level1.setBackgroundResource(0);
                        level3.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showNext();
                    }
                    else if(levelSelected == 2){
                        level2.setBackgroundResource(0);
                        level3.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 4){
                        level4.setBackgroundResource(0);
                        level3.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 5){
                        level5.setBackgroundResource(0);
                        level3.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 6){
                        level6.setBackgroundResource(0);
                        level3.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 7){
                        level7.setBackgroundResource(0);
                        level3.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else{
                    }
                    levelSelected = 3;
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.level4:
                try{

                    if(levelSelected == 1) {
                        level1.setBackgroundResource(0);
                        level4.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showNext();
                    }
                    else if(levelSelected == 2){
                        level2.setBackgroundResource(0);
                        level4.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 3){
                        level3.setBackgroundResource(0);
                        level4.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 5){
                        level5.setBackgroundResource(0);
                        level4.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 6){
                        level6.setBackgroundResource(0);
                        level4.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 7){
                        level7.setBackgroundResource(0);
                        level4.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else{
                    }
                    levelSelected = 4;
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.level5:
                try{

                    if(levelSelected == 1) {
                        level1.setBackgroundResource(0);
                        level5.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showNext();
                    }
                    else if(levelSelected == 2){
                        level2.setBackgroundResource(0);
                        level5.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 3){
                        level3.setBackgroundResource(0);
                        level5.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 4){
                        level4.setBackgroundResource(0);
                        level5.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 6){
                        level6.setBackgroundResource(0);
                        level5.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 7){
                        level7.setBackgroundResource(0);
                        level5.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else{
                    }
                    levelSelected = 5;
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.level6:
                try{

                    if(levelSelected == 1) {
                        level1.setBackgroundResource(0);
                        level6.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showNext();
                    }
                    else if(levelSelected == 2){
                        level2.setBackgroundResource(0);
                        level6.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 3){
                        level3.setBackgroundResource(0);
                        level6.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 4){
                        level4.setBackgroundResource(0);
                        level6.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 5){
                        level5.setBackgroundResource(0);
                        level6.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 7){
                        level7.setBackgroundResource(0);
                        level6.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else{
                    }
                    levelSelected = 6;
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case com.ktgo.xxflye.kizombaharmony.R.id.level7:
                try{

                    if(levelSelected == 1) {
                        level1.setBackgroundResource(0);
                        level7.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                        lessonflipper.showNext();
                    }
                    else if(levelSelected == 2){
                        level2.setBackgroundResource(0);
                        level7.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 3){
                        level3.setBackgroundResource(0);
                        level7.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 4){
                        level4.setBackgroundResource(0);
                        level7.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 5){
                        level5.setBackgroundResource(0);
                        level7.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else if(levelSelected == 6){
                        level6.setBackgroundResource(0);
                        level7.setBackgroundResource(com.ktgo.xxflye.kizombaharmony.R.drawable.levelselect);
                    }
                    else{
                    }
                    levelSelected = 7;
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;*/
        }

    }

    /*public void animateMainMenu(View view){

        Animation fadeIn, fadeOut;
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        lesson = (ImageView)findViewById(R.id.lessons);
        settings = (ImageView) findViewById(R.id.settings);
        about = (ImageView) findViewById(R.id.about);
        blog = (ImageView) findViewById(R.id.blog);

        switch (view.getId()){

            case R.id.lessons:
                try{
                    if(flipPosition == 2){
                        blog.setAnimation(fadeOut);
                        blog.startAnimation(fadeOut);
                        blog.setBackgroundResource(0);
                        lesson.setAnimation(fadeIn);
                        lesson.setBackgroundResource(R.drawable.highlightred);
                        lesson.startAnimation(fadeIn);

                    }
                    else if(flipPosition == 3) {
                        settings.setAnimation(fadeOut);
                        settings.startAnimation(fadeOut);
                        settings.setBackgroundResource(0);
                        lesson.setAnimation(fadeIn);
                        lesson.setBackgroundResource(R.drawable.highlightred);
                        lesson.startAnimation(fadeIn);

                    }
                    else if(flipPosition == 4){
                        about.setAnimation(fadeOut);
                        about.startAnimation(fadeOut);
                        about.setBackgroundResource(0);
                        lesson.setAnimation(fadeIn);
                        lesson.setBackgroundResource(R.drawable.highlightred);
                        lesson.startAnimation(fadeIn);
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case R.id.blog:
                try{
                    if(flipPosition == 1) {
                        lesson.setAnimation(fadeOut);
                        lesson.startAnimation(fadeOut);
                        lesson.setBackgroundResource(0);
                        blog.setAnimation(fadeIn);
                        blog.setBackgroundResource(R.drawable.highlightred);
                        blog.startAnimation(fadeIn);
                    }
                    else if(flipPosition == 3){
                        settings.setAnimation(fadeOut);
                        settings.startAnimation(fadeOut);
                        settings.setBackgroundResource(0);
                        blog.setAnimation(fadeIn);
                        blog.setBackgroundResource(R.drawable.highlightred);
                        blog.startAnimation(fadeIn);

                    }
                    else if(flipPosition == 4){
                        about.setAnimation(fadeOut);
                        about.startAnimation(fadeOut);
                        about.setBackgroundResource(0);
                        blog.setAnimation(fadeIn);
                        blog.setBackgroundResource(R.drawable.highlightred);
                        blog.startAnimation(fadeIn);
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case R.id.settings:
                try{
                    if(flipPosition == 1) {
                        lesson.setAnimation(fadeOut);
                        lesson.startAnimation(fadeOut);
                        lesson.setBackgroundResource(0);
                        settings.setAnimation(fadeIn);
                        settings.setBackgroundResource(R.drawable.highlightred);
                        settings.startAnimation(fadeIn);
                    }
                    else if(flipPosition == 2){
                        blog.setAnimation(fadeOut);
                        blog.startAnimation(fadeOut);
                        blog.setBackgroundResource(0);
                        settings.setAnimation(fadeIn);
                        settings.setBackgroundResource(R.drawable.highlightred);
                        settings.startAnimation(fadeIn);

                    }
                    else if(flipPosition == 4){
                        about.setAnimation(fadeOut);
                        about.startAnimation(fadeOut);
                        about.setBackgroundResource(0);
                        settings.setAnimation(fadeIn);
                        settings.setBackgroundResource(R.drawable.highlightred);
                        settings.startAnimation(fadeIn);
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
            case R.id.about:
                try{
                    if(flipPosition == 1) {
                        lesson.setAnimation(fadeOut);
                        lesson.startAnimation(fadeOut);
                        lesson.setBackgroundResource(0);
                        about.setAnimation(fadeIn);
                        about.setBackgroundResource(R.drawable.highlightred);
                        about.startAnimation(fadeIn);
                    }
                    else if(flipPosition == 2){
                        blog.setAnimation(fadeOut);
                        blog.startAnimation(fadeOut);
                        blog.setBackgroundResource(0);
                        about.setAnimation(fadeIn);
                        about.setBackgroundResource(R.drawable.highlightred);
                        about.startAnimation(fadeIn);

                    }
                    else if(flipPosition == 3){
                        settings.setAnimation(fadeOut);
                        settings.startAnimation(fadeOut);
                        settings.setBackgroundResource(0);
                        about.setAnimation(fadeIn);
                        about.setBackgroundResource(R.drawable.highlightred);
                        about.startAnimation(fadeIn);
                    }
                    else{
                    }
                }
                catch(Exception e){

                    e.printStackTrace();

                }
                break;
        }

    }*/

}
