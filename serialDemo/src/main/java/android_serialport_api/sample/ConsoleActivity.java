/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.sample;

import java.io.IOException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConsoleActivity extends SerialPortActivity {

	EditText mReception = null;
	EditText mEmission = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);

		mReception = (EditText) findViewById(R.id.EditTextReception);
		mEmission = (EditText) findViewById(R.id.EditTextEmission);

		Button buttSend = (Button) findViewById(R.id.ButtonSend);
		buttSend.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int i;
				CharSequence t = mEmission.getText();
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					mOutputStream.write(new String(text).getBytes());
					mOutputStream.write('\r');
					mOutputStream.write('\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mReception != null) {
					mReception.append(new String(buffer, 0, size));
				}
			}
		});
	}
}
