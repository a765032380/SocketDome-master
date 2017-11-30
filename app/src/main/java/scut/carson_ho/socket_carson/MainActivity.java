package scut.carson_ho.socket_carson;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scut.carson_ho.socket_carson.socket.BitOperator;
import scut.carson_ho.socket_carson.socket.MsgDecoder;
import scut.carson_ho.socket_carson.socket.MsgEncoder;
import scut.carson_ho.socket_carson.socket.TPMSConsts;
import scut.carson_ho.socket_carson.socket.TerminalProtocolUtils;
import scut.carson_ho.socket_carson.socket.UpPackageData;

public class MainActivity extends AppCompatActivity implements RtmpHandler.RtmpListener,
        SrsEncodeHandler.SrsEncodeListener,View.OnClickListener {
    /**
     * ID
     * 用户表
     * 卡号
     * 手机号
     * 名字
     * 房间号
     *
     **/

    /**
     * 主 变量
     */
    // Socket变量
    private Socket socket;
    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;
    /**
     * 在没有接到服务器应答是多次发送心跳包的次数
     */
    private int heartCount=0;
    // 输入流对象
    InputStream is;
    // 输入流读取器对象
    InputStreamReader isr ;
    BufferedReader br ;
    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;

    /**
     * 按钮 变量
     */
    // 连接 断开连接 发送数据到服务器 的按钮变量
    private Button btnConnect, btnDisconnect, btnSend,sendMsg;
    // 显示接收服务器消息 按钮
    private TextView Receive,receive_message;
    // 输入需要发送的消息 输入框
    private EditText mEdit;
    //数据转换的类
    private MsgEncoder msgEncoder=new MsgEncoder();
    //数据的实体类
    private UpPackageData msg;
    //发送的数据字节
    byte[] bs ;
    //流水号
    byte flowId=0;
    private static final String TAG = "Yasea";
    //推流的按键
    Button btnPublish = null;
    Button btnSwitchCamera = null;
    //设备ID
    private String Id;
    //推流的地址
    private String rtmpUrl;
    //视频的控件
    private SrsPublisher mPublisher;
    //判断当前是否是在心跳状态
    private boolean isSendHeart=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Id=getIMEI();
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        connect();
//        receive();

    }
    /**
     * 初始化
     */
    private void init(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 初始化所有按钮
        btnConnect = (Button) findViewById(R.id.connect);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        btnSend = (Button) findViewById(R.id.send);
        mEdit = (EditText) findViewById(R.id.edit);
        receive_message = (TextView) findViewById(R.id.receive_message);
        Receive = (Button) findViewById(R.id.Receive);
        sendMsg= (Button) findViewById(R.id.sendMsg);
        btnSwitchCamera = (Button) findViewById(R.id.swCam);

        btnConnect.setOnClickListener(this);
        Receive.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnSwitchCamera.setOnClickListener(this);

        mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.preview));
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setPreviewResolution(640, 360);
        mPublisher.setOutputResolution(360, 640);
        mPublisher.setVideoSmoothMode();
    }
    /**
     * 设置发送信息的byte数组数据
     * @param strMsg 消息体的内容
     * @param com 命令字符
     * @return
     */
    public byte[] sendMsg(String strMsg,int com){
        msg=new UpPackageData();
        UpPackageData.UpMsgHeader header=new UpPackageData.UpMsgHeader();
        if (strMsg!=null) {
            msg.setMsgBodyBytes(strMsg.getBytes());
            header.setMsgBodyLength(strMsg.getBytes().length);
        }
        header.setTerminalId(Id);
        msg.setMsgHeader(header);
        try {
            bs = msgEncoder.encode4ServerCommonRespMsg(msg, com, getFlowId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str=null;
        for (int i=0;i<bs.length;i++){
            str+="-"+bs[i];
        }
        Log.i("SendData","str="+str+"");
        return bs;
    }
    private void send(final byte[] loginByte) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (socket!=null) {
                    try {
                        // 步骤1：从Socket 获得输出流对象OutputStream
                        // 该对象作用：发送数据
                        outputStream = socket.getOutputStream();

                        // 步骤2：写入需要发送的数据到输出流对象中
                        outputStream.write(loginByte);
                        // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                        // 步骤3：发送数据到服务端
                        outputStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    connect();
                }

            }
        });
    }

    /**
     * 连接Socket服务器
     */
    private void connect(){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    // 创建Socket对象 & 指定服务端的IP 及 端口号
                    socket = new Socket("192.168.1.229", 20088);
                    socket.setKeepAlive(true);
                    // 判断客户端和服务器是否连接成功
                    System.out.println(socket.isConnected());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void disConnect(){
        try {
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            if (outputStream!=null) {
                outputStream.close();
            }

            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            if (br!=null)
                br.close();

            // 最终关闭整个Socket连接
            if (socket!=null)
                socket.close();
            if (mPublisher != null)
                mPublisher.stopPublish();
            // 判断客户端和服务器是否已经断开连接
//            System.out.println(socket.isConnected());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送心跳。如果现在是心跳状态。一分钟后再次发送。如果不是心跳状态，立即发送心跳。
     * 如果发送30次以后没有返回结果就认为断开连接了，重新发起连接和登陆。
     * @param isSendHeart1
     */
    private void sendHeart(boolean isSendHeart1){
            isSendHeart=isSendHeart1;
            mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (isSendHeart){
                            Thread.sleep(60 * 1000);
                            heartCount=0;
                            isSendHeart=false;
                        }else {
                            send(sendMsg(null,TPMSConsts.ARM_SEND_DOWM_HEART));
                            heartCount++;
                            if (heartCount==30){
                                connect();
                                send(sendMsg(null,TPMSConsts.msg_id_phone_login));
                                heartCount=0;
                            }
                            isSendHeart=true;
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
}
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.connect:
                connect();
                break;
            case R.id.disconnect:
                disConnect();
                break;
            case R.id.Receive:
                receive();
                break;
            case R.id.send:
                send(sendMsg(null,TPMSConsts.msg_id_phone_login));

                break;
            case R.id.sendMsg:
                String door="0200108030";
                rtmpUrl= "rtmp://47.93.48.248/live/"+Id+getTime();
                mPublisher.startCamera();
                mPublisher.startPublish(rtmpUrl);
                Log.i("LLLL",rtmpUrl);
                send(sendMsg(door+rtmpUrl,TPMSConsts.ARM_SEND_DOWM_OPENDOOR));

                break;
            case R.id.swCam:
                mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
                break;
        }

    }
    /**
     * 接受服务器返回的信息
     */
    private void receive(){
    // 利用线程池直接开启一个线程 & 执行该线程
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    if (socket != null) {
                        // 步骤1：创建输入流对象InputStream
                        is = socket.getInputStream();
                        while (!socket.isClosed() && !socket.isInputShutdown()) {
                            //                        if (is.available() > 0) {
                            byte[] byteOne = new byte[is.available()];
                            is.read(byteOne);
                            String str = null;
                            for (int i = 0; i < byteOne.length; i++) {
                                str += "-" + byteOne[i];

                            }
//                            Log.i("UpPackageData", "str=" + str + "");
                            if (byteOne.length > 0) {
                                Log.i("UpPackageData", "str=" + str + "");
                                MsgDecoder msgDecoder = new MsgDecoder();
                                UpPackageData upPackageData = msgDecoder.bytes2PackageData(byteOne);
                                Log.i("UpPackageData", "TerminalId=" + upPackageData.getMsgHeader().getTerminalId() + "");
                                Log.i("UpPackageData", "MsgId=" + upPackageData.getMsgHeader().getMsgId() + "");
                                Log.i("UpPackageData", "MsgId=" + (upPackageData.getMsgHeader().getMsgId()&0xff) + "");

                                switch (upPackageData.getMsgHeader().getMsgId()&0xff) {
                                    //应答ARM向业务服务器上传的门号、播放地址，应答0x80
                                    //                                case TPMSConsts.ARM_SEND_DOWM_OPENDOOR:
                                    //                                    break;
                                    //向ARM下发声音播放地址，ARM播放声音
                                    case TPMSConsts.ARM_SERVICER_DOWM_START_VIDEO:
                                        send(sendMsg(null, TPMSConsts.ARM_SEND_DOWM_START_VIDEO));
                                        break;
                                    //向ARM下发停止视频播放、停止流推送
                                    case TPMSConsts.ARM_SERVICER_DOWM_STOP_VIDEO:
                                        send(sendMsg(null, TPMSConsts.ARM_SEND_DOWM_STOP_VIDEO));
                                        if (mPublisher != null)
                                            mPublisher.stopPublish();
                                        break;
                                    //向ARM下发用户ID号
                                    case TPMSConsts.ARM_SERVICER_DOWM_USERID:
                                        send(sendMsg(null, TPMSConsts.ARM_SEND_DOWM_USERID));
                                        break;
                                    //向ARM下发广告播放连接
                                    case TPMSConsts.ARM_SERVICER_DOWM_AD:
                                        send(sendMsg(null, TPMSConsts.ARM_SEND_DOWM_AD));
                                        break;
                                    //应答 ARM向服务器上报登陆 应答 0x86
                                    case TPMSConsts.ARM_SERVICER_UP_LOGIN:
                                        Log.i("UpPackageData","登陆成功");
                                        sendHeart(true);
                                        //                                sendMsg(null,TPMSConsts.msg_id_phone_login);
                                        break;
                                    //向ARM服务器下发开门指令
                                    case TPMSConsts.ARM_SERVICER_DOWM_OPENDOOR:
                                        send(sendMsg(null, TPMSConsts.ARM_SEND_DOWM_OPEN_DOOR));
                                        break;
                                    //应答ARM向业务服务器上传的心跳包，应答0x81
                                    case TPMSConsts.ARM_SERVICER_UP_HEART:
                                        sendHeart(true);
                                        break;
//                                    default:
//                                        sendHeart(false);
//                                        break;
                                }
                                //                                      if (upPackageData.getMsgHeader().getMsgId()!=TPMSConsts.msg_id_phone_login){
                                //                                          send(sendMsg(null,TPMSConsts.msg_id_phone_login));
                                //                                      }
                                //                                  while (byteOne[0] != (byte) TPMSConsts.down_pkg_delimiter
                                //                                          &&byteOne[1] != (byte) TPMSConsts.down_pkg_delimiter1) { // 帧头Head：A5
                                //
                                //                                      is.read(byteOne);
                                //                                  }
                                //                                  Log.i("LLLL",byteOne[0]+"");
                                //                                  Log.i("LLLL",byteOne[1]+"");
                                ////                                  byte[] byteHead = new byte[1];
                                ////                                  byteHead[0] = byteOne[0];
                                ////                                  is.read(byteOne); // byteOne[0]:消息长度Length
                                //                                  byte[] bytesDataFrame = new byte[1];
                                //                                  is.read(bytesDataFrame);
                                //                                  byte cmd=bytesDataFrame[0];
                                //
                                //                                  Log.i("LLLL",cmd+"");
                                //                                  if (cmd==TPMSConsts.ARM_SERVICER_UP_ADDRESS){
                                //                                      //应答ARM向业务服务器上传的门号、播放地址，应答0x80
                                //                                      Log.i("LLL","接到了90");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_UP_HEART){
                                //                                      //应答ARM向业务服务器上传的心跳包，应答0x81
                                //                                      Log.i("LLL","接到了91");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_DOWM_START_VIDEO){
                                //                                      //向ARM下发声音播放地址，ARM播放声音
                                //                                      Log.i("LLL","接到了92");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_DOWM_STOP_VIDEO){
                                //                                      //向ARM下发停止视频播放、停止流推送
                                //                                      Log.i("LLL","接到了93");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_DOWM_USERID){
                                //                                      //向ARM下发用户ID号
                                //                                      Log.i("LLL","接到了94");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_DOWM_AD){
                                //                                      //向ARM下发广告播放连接
                                //                                      Log.i("LLL","接到了95");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_UP_LOGIN){
                                //                                      //应答 ARM向服务器上报登陆 应答 0x86
                                //                                      Log.i("LLL","接到了95");
                                //                                  }else if (cmd==TPMSConsts.ARM_SERVICER_DOWM_OPENDOOR){
                                //                                      //向ARM服务器下发开门指令
                                //                                      Log.i("LLL","接到了97");
                                //                                  }
                            }
                        }
                        //                    }

                        //                                  // 步骤2：创建输入流读取器对象 并传入输入流对象
                        //                              // 该对象作用：获取服务器返回的数据
                        //                              isr = new InputStreamReader(is);
                        //                              br = new BufferedReader(isr);
                        //
                        //                              // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                        //                              response = br.readLine();
                        //
                        //                              // 步骤4:通知主线程,将接收的消息显示到界面
                        //                              Message msg = Message.obtain();
                        //                              msg.what = 0;
                        //                              mMainHandler.sendMessage(msg);

                    }else {
                        connect();
                    }
                    } catch(IOException e){
                        e.printStackTrace();
                    }


            }
        });
    }
    /**
     * 获取流水号
     */
    private byte getFlowId(){
        if (flowId==127){
            return 0;
        }else {
            return flowId++;
        }
    }
    /**
     * 获取时间戳
     */
    public String getTime(){
        long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
        String  str=String.valueOf(time);
        return str;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
    }
    @SuppressLint("MissingPermission")
    private String getIMEI(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Id = "00" + telephonyManager.getDeviceId();

        return Id;
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.setScreenOrientation(newConfig.orientation);
        mPublisher.startCamera();
    }
    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
//            btnPublish.setText("publish");
        } catch (Exception e1) {
            // Ignore
        }
    }
    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRtmpVideoStreaming() {
    }
    @Override
    public void onRtmpAudioStreaming() {
    }
    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.i(TAG, String.format("Output Fps: %f", fps));
    }
    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }
    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }
    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }
    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }
    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }
    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }
    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }


}
