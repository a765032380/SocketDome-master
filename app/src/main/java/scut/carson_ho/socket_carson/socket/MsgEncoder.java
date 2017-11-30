package scut.carson_ho.socket_carson.socket;

import java.util.Arrays;

public class MsgEncoder {
	private BitOperator bitOperator;
	private TerminalProtocolUtils terminalProtocolUtils;

	public MsgEncoder() {
		this.bitOperator = new BitOperator();
		this.terminalProtocolUtils = new TerminalProtocolUtils();
	} 
	public byte[] encode4ServerCommonRespMsg(UpPackageData req,int commType,   int flowId)
			throws Exception {
		byte[] msgHeader = this.terminalProtocolUtils.generateMsgHeader( 
				commType,req.getMsgHeader().getTerminalId(),req.getMsgHeader().getMsgBodyLength(),flowId);
		byte[] headerAndBody;
		if (req.getMsgBodyBytes()!=null) {
			headerAndBody = this.bitOperator.concatAll(msgHeader, req.getMsgBodyBytes());
		}else {
			headerAndBody = this.bitOperator.concatAll(msgHeader);
		}

		// 校验码
		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}
	 

//	public byte[] encode4ParamSetting(byte[] msgBodyBytes, Session session) throws Exception {
//		// 消息头
//		/*int msgBodyProps = this.terminalProtocolUtils.generateMsgBodyProps(msgBodyBytes.length, 0b000, false, 0);
//		byte backByte = (Byte) null;
//		byte[] msgHeader = this.terminalProtocolUtils.generateMsgHeader(
//				TPMSConsts.cmd_terminal_param_settings, msgBodyBytes, msgBodyProps,  0);
//		// 连接消息头和消息体
//		byte[] headerAndBody = this.bitOperator.concatAll(msgHeader, msgBodyBytes);
//		// 校验码
//		int checkSum = this.bitOperator.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
//		// 连接并且转义
//		return this.doEncode(headerAndBody, checkSum);*/
//	    return null;
//	}

	private byte[] doEncode(byte[] headerAndBody, int checkSum) throws Exception {
		byte[] noEscapedBytes = this.bitOperator.concatAll(Arrays.asList(//
				new byte[] { (byte)TPMSConsts.up_pkg_delimiter,TPMSConsts.up_pkg_delimiter1, }, // 0x55,7a
				headerAndBody, // 消息头+ 消息体
				bitOperator.integerTo1Bytes(checkSum),// 校验码
				new byte[] { (byte)TPMSConsts.up_pkg_delimiter,TPMSConsts.up_pkg_delimiter1}/*结束符,
*/		));
		// 转义
		return terminalProtocolUtils.doEscape4Send(noEscapedBytes, 1, noEscapedBytes.length - 2);
	}
}
