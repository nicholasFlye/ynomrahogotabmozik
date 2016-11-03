/**
 * Created by XxFLYE on 12/24/2014.
 */
package com.ktgo.xxflye.kizombaharmony;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.io.IOException;

/**
 * LessonViewerVideo
 * Description: This class handles video viewing for KizombaToGo.
 */
public class LessonViewerVideo extends Activity{

    private ProgressDialog pDialog;
    private VideoView vidView;
    private String vidAddress;
    private Integer position;
    private Boolean downloadBool = false;
    private String cFront; //CloudFront link
    private StringBuilder linkbuild = new StringBuilder();
    private String[] lessonLinks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Resources res = getResources();
        lessonLinks = res.getStringArray(R.array.Level1_Lessons);
        cFront = getString(R.string.cFrontLink);

        //Get intent from previous Activity
        final Intent myIntent = getIntent();
        position = myIntent.getIntExtra("lessonNumber", -1);
        downloadBool = myIntent.getBooleanExtra("download", false);

        //If video file already exists on device, play the video.
        String path = getExternalFilesDir("." + Environment.DIRECTORY_DOWNLOADS).getPath();
        File lesson = new File(path + "/" + lessonLinks[position]);
        Boolean lessonDownloadStatus = lesson.exists();

        if(lessonDownloadStatus == true && !downloadBool){
            setContentView(com.ktgo.xxflye.kizombaharmony.R.layout.lessonlayout);
            play();

        }
        //If video does not exist, look to see if streaming/downloading is to be done.
        else {
            //If video download is not requested, stream content
            if (!downloadBool) {

                setContentView(com.ktgo.xxflye.kizombaharmony.R.layout.lessonlayout);

                pDialog = new ProgressDialog(LessonViewerVideo.this);
                pDialog.setMessage("Buffering...");
                pDialog.setIndeterminate(false);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(true);
                pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent myIntent = new Intent(LessonViewerVideo.this,
                                KizombaHarmonyMain.class);
                        startActivity(myIntent);
                    }
                });
                pDialog.show();

                vidView = (VideoView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.myVideo);

                //Error handling in case viewer's connection is interrupted
                vidView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        pDialog.cancel();

                        Intent myIntent = new Intent(LessonViewerVideo.this,
                                KizombaHarmonyMain.class);
                        startActivity(myIntent);

                        Context context = getApplicationContext();
                        CharSequence text = "The video failed to stream. Please check connection.";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        return false;
                    }
                });

                //Construct video URL address
                linkbuild.append(cFront);
                linkbuild.append(lessonLinks[position]);
                vidAddress = linkbuild.toString();

                //Play the video stream
                try {
                    MediaController vidControl = new MediaController(this);
                    vidControl.setAnchorView(vidView);
                    Uri vidUri = Uri.parse(vidAddress);
                    vidView.setVideoURI(vidUri);
                    vidView.setMediaController(vidControl);

                    vidView.requestFocus();
                    vidView.setOnPreparedListener(new OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            pDialog.dismiss();
                            vidView.start();
                        }
                    });
                    vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            Intent myIntent = new Intent(LessonViewerVideo.this,
                                    KizombaHarmonyMain.class);
                            myIntent.putExtra("level1Completed", position);
                            startActivity(myIntent);
                        }
                    });
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
            //If previous activity requests a download, download the video to device.
            else if (downloadBool && lessonDownloadStatus == false) {

                //Build the link
                linkbuild.append(cFront);
                linkbuild.append(lessonLinks[position]);
                vidAddress = linkbuild.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                //Ask viewer if they wish to download to the device.
                builder.setCancelable(false);
                builder.setTitle("Confirm Lesson Download");
                builder.setMessage("Are you sure you wish to download this lesson? (Please ensure you " +
                        "have enough storage for video lessons.)");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    //If "YES", download the viewer's video.
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            download();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                        Context context = getApplicationContext();
                        CharSequence text = "Your video is downloading. Please wait.";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent myIntent = new Intent(LessonViewerVideo.this,
                                KizombaHarmonyMain.class);
                        myIntent.putExtra("downloaded", position);
                        startActivity(myIntent);
                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    //If "NO", return viewer to previous activity.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent myIntent = new Intent(LessonViewerVideo.this,
                                KizombaHarmonyMain.class);
                        startActivity(myIntent);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            //If viewer has already downloaded the video, prompt the user to delete the video if
            //the same action was done to download the video.
            else if(downloadBool && lessonDownloadStatus == true){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setCancelable(false);
                builder.setTitle("Delete");
                builder.setMessage("This lesson has already been downloaded. Would you like to delete this" +
                        " lesson from your device?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    //If "YES", delete the viewer's video from the device.
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        SharedPreferences sharedPref = getPreferences(0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String lessonDownloadedToDelete = position.toString() + "downloaded";

                        editor.remove(lessonDownloadedToDelete);
                        editor.commit();

                        String path = getExternalFilesDir("." + Environment.DIRECTORY_DOWNLOADS).getPath();
                        File fileToDelete = new File(path + "/" + lessonLinks[position]);
                        Boolean isDeleted = fileToDelete.delete();

                        if(fileToDelete.exists()){
                            try {
                                fileToDelete.getCanonicalFile().delete();
                                if(fileToDelete.exists()){
                                    getApplicationContext().deleteFile(fileToDelete.getName());
                                    fileToDelete.delete();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Context context = getApplicationContext();
                        CharSequence text = "Your video has been deleted.";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent myIntent = new Intent(LessonViewerVideo.this,
                                KizombaHarmonyMain.class);
                        startActivity(myIntent);

                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    //If "NO", return viewer back to previous activity.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent myIntent = new Intent(LessonViewerVideo.this,
                                KizombaHarmonyMain.class);
                        startActivity(myIntent);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        }
    }

    /**
     * play()
     * Description: Provides general playback of a video lesson.
     * Parameters: N/A
     * Return: void
     */
    private void play(){

        Resources res = getResources();
        lessonLinks = res.getStringArray(R.array.Level1_Lessons);
        cFront = getString(R.string.cFrontLink);

        vidView = (VideoView) findViewById(com.ktgo.xxflye.kizombaharmony.R.id.myVideo);

        try {
            MediaController vidControl = new MediaController(this);
            vidControl.setAnchorView(vidView);
            String path = getExternalFilesDir("." + Environment.DIRECTORY_DOWNLOADS).getPath();
            vidView.setVideoPath(path + "/" + lessonLinks[position]);
            vidView.setMediaController(vidControl);

            vidView.requestFocus();
            vidView.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    vidView.start();
                }
            });
            vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    Intent myIntent = new Intent(LessonViewerVideo.this,
                            KizombaHarmonyMain.class);
                    myIntent.putExtra("level1Completed", position);
                    startActivity(myIntent);
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * download()
     * Description: Downloads using DownloadManager a video from a URL and places it inside of
     *              app's directory.
     *
     * Return: void
     * @throws IOException
     */
    private void download() throws IOException {

        Resources res = getResources();
        lessonLinks = res.getStringArray(R.array.Level1_Lessons);
        cFront = getString(R.string.cFrontLink);

        String path = getExternalFilesDir("." + Environment.DIRECTORY_DOWNLOADS).getPath();
        File lesson = new File(path + "/" + lessonLinks[position]);
        Boolean lessonDownloadStatus = lesson.exists();

        if(!lessonDownloadStatus) {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(vidAddress));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setDestinationInExternalFilesDir(this, "." + Environment.DIRECTORY_DOWNLOADS, lessonLinks[position]);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

        }
        else {

        }
    }

}
