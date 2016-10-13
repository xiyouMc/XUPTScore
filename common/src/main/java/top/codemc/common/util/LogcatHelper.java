package top.codemc.common.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * log��־ͳ�Ʊ���
 *
 * @author Administrator 2014-7-30
 */
public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private String PATH_LOGCAT = null;
    private LogDumper mLogDumper = null;
    private int mPId;

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;// 2012��10��03�� 23:41:31
    }

    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;// 2012-10-03 23:41:31
    }

    /**
     * ��ʼ��Ŀ¼
     */
    public void init(Context context) {
        String LOG_FILE_DIR = "scorelog";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// ���ȱ��浽SD����
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + LOG_FILE_DIR;
        } else {// ���SD�������ڣ��ͱ��浽��Ӧ�õ�Ŀ¼��
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + LOG_FILE_DIR;
        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        /**
         * �Ľ� thread��start ��ֹ java.lang.IllegalThreadStateException: Thread
         * already started
         */
        if (!mLogDumper.isAlive()) {
            mLogDumper.start();
        }

    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        String cmds = null;
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, "score-"
                        + getFileName() + ".log"));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * ��־�ȼ���*:v , *:d , *:w , *:e , *:f , *:s
             *
             * ��ʾ��ǰmPID����� E��W�ȼ�����־.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//��ӡ������־��Ϣ
            // cmds = "logcat -s way";//��ӡ��ǩ������Ϣ
            cmds = "logcat *:e  \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((getDateEN() + "  " + line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }
}