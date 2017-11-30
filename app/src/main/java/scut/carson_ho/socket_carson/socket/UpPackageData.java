package scut.carson_ho.socket_carson.socket;

import java.util.Arrays;


public class UpPackageData {

	/**
	 * 16byte 消息头
	 */
	protected UpMsgHeader msgHeader;

	// 消息体字节数组
	protected byte[] msgBodyBytes;

	/**
	 * 校验码 1byte
	 */
	protected int checkSum;


	public UpMsgHeader getMsgHeader() {
		return msgHeader;
	}

	public void setMsgHeader(UpMsgHeader msgHeader) {
		this.msgHeader = msgHeader;
	}

	public byte[] getMsgBodyBytes() {
		return msgBodyBytes;
	}

	public void setMsgBodyBytes(byte[] msgBodyBytes) {
		this.msgBodyBytes = msgBodyBytes;
	}

	public int getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(int checkSum) {
		this.checkSum = checkSum;
	}


	@Override
	public String toString() {
		return "PackageData [msgHeader=" + msgHeader + ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) + ", checkSum="
				+ checkSum + ", address=" +  "]";
	}

	public static class UpMsgHeader {
		// 消息ID
		protected byte msgId; 
		// 消息体长度
		protected int msgBodyLength; 
		// 设备ID号
		protected String terminalId;
		// 流水号
		protected int flowId;
		 
		public int getMsgId() {
			return msgId;
		}

		public void setMsgId(byte msgId) {
			this.msgId = msgId;
		}

		public int getMsgBodyLength() {
			return msgBodyLength;
		}

		public void setMsgBodyLength(int msgBodyLength) {
			this.msgBodyLength = msgBodyLength;
		}
		public String getTerminalId() {
			return terminalId;
		}

		public void setTerminalId(String terminalId) {
			this.terminalId = terminalId;
		}

		public int getFlowId() {
			return flowId;
		}

		public void setFlowId(int flowId) {
			this.flowId = flowId;
		}
		@Override
		public String toString() {
			return "MsgHeader [msgId=" + msgId + ", msgBodyLength="
					+ msgBodyLength + ",terminalId="+terminalId+", flowId=" + flowId + " ]";
		}

	}

}
