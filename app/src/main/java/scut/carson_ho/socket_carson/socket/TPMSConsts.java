package scut.carson_ho.socket_carson.socket;

import java.nio.charset.Charset;

public class TPMSConsts {


	public static final String string_encoding = "GBK";

	public static final Charset string_charset = Charset.forName(string_encoding);
	// 标识位
	public static final int up_pkg_delimiter = 0xaa;
	// 标识位
	public static final int up_pkg_delimiter1 = 0x75;
	// 标识位
	public static final int down_pkg_delimiter = 0x55;
	// 标识位
	public static final int down_pkg_delimiter1 = 0x7a;
	// 客户端发呆15分钟后,服务器主动断开连接
	public static int tcp_client_idle_minutes = 30;
 
	// 手机端向业务服务器发送声音播放地址
	public static final int msg_id_phone_audio_url_req = 0x00A0;	
	// 应答手机下发的声音播放地址,应答0xA0
	public static final int msg_id_phone_audio_url_resp = 0x00B0;
	// 手机端通知业务服务器退出视频播放、停止流推送
	public static final int msg_id_phone_stop_video = 0x00A1;	
	// 应答手机下发的退出视频播放、停止流推送,应答0xA1
	public static final int msg_id_phone_stop_video_resp = 0x00B1;
	// 手机端通知业务服务器，5秒后执行手机挂断视频
	public static final int msg_id_phone_video_five_stop = 0x00A2;	
	// 应答手机下发的5秒后手机挂断视频流程,应答0xA2
	public static final int msg_id_phone_video_five_stop_resp = 0x00B2;	
	// 手机端应答收到服务器下发手机视频播放地址
	public static final int msg_id_phone_video_url_resp = 0x00A3; 
	// 手机端应答收到服务器下发手机视频播放地址
	public static final int msg_id_phone_video_url = 0x00B3;
	// ARM登陆服务器
	public static final int msg_id_phone_login = 0x86;
	// 应答手机登陆服务器 0xA4
	// 手机向服务器上传心跳 
	public static final int msg_id_phone_login_resp = 0x00B4;	
	public static final int msg_id_phone_heart_beat = 0x00A5; 
	// 应答手机上传服务器心跳 0xA5
	public static final int msg_id_phone_heart_beat_resp = 0x00B5;	
	// 手机向服务器上传开门指令 
	public static final int msg_id_phone_open_door = 0x00A6; 
	// 应答手机上传服务器开门指令 0xA6
	public static final int msg_id_phone_open_door_resp = 0x00B6;



	//应答ARM向业务服务器上传的门号、播放地址，应答0x80
	public static final int ARM_SERVICER_UP_ADDRESS=0x90;
	//应答ARM向业务服务器上传的心跳包，应答0x81
	public static final int ARM_SERVICER_UP_HEART=0x91;
	//向ARM下发声音播放地址，ARM播放声音
	public static final int ARM_SERVICER_DOWM_START_VIDEO=0x92;
	//向ARM下发停止视频播放、停止流推送
	public static final int ARM_SERVICER_DOWM_STOP_VIDEO=0x93;
	//向ARM下发用户ID号
	public static final int ARM_SERVICER_DOWM_USERID=0x94;
	//向ARM下发广告播放连接
	public static final int ARM_SERVICER_DOWM_AD=0x95;
	//应答 ARM向服务器上报登陆 应答 0x86
	public static final int ARM_SERVICER_UP_LOGIN=0x96;
	//向ARM服务器下发开门指令
	public static final int ARM_SERVICER_DOWM_OPENDOOR=0x97;
	//ARM向服务器发送门牌号和视频播放地址
	public static final int ARM_SEND_DOWM_OPENDOOR=0x80;
	//ARM向服务器发送心跳包
	public static final int ARM_SEND_DOWM_HEART=0x81;
	//应答业务服务器向ARM下发声音播放地址，应答0x92
	public static final int ARM_SEND_DOWM_START_VIDEO=0x82;
	//应答业务服务器向ARM下发停止视频播放，应答0x93
	public static final int ARM_SEND_DOWM_STOP_VIDEO=0x83;
	//应答业务服务器向ARM下发用户ID号   应答 0x94
	public static final int ARM_SEND_DOWM_USERID=0x84;
	//应答业务服务器向ARM下发广告播放连接   应答 0x95
	public static final int ARM_SEND_DOWM_AD=0x85;
	//应答业务服务器下发开门指令 应答0x97
	public static final int ARM_SEND_DOWM_OPEN_DOOR=0x87;

}
