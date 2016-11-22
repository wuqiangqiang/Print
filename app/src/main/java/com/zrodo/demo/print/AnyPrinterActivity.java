package com.zrodo.demo.print;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 不同机器usb的处理流的限制不同
 * 所以最好数据包已64字节为单位发送，如果嫌慢可以128字节
 * */
public class AnyPrinterActivity extends Activity{

	private Button btnWifiPrint,btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anyprinter);
		
		
		//打印
		btnWifiPrint = (Button)findViewById(R.id.btnWifiPrint);
		btnWifiPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//printBySock();
			}
		});
		
		//返回
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}
	
	/**
	 * 公司hp打印机的ip，但是不知道端口
	 * */
	private void printBySock(){
		try {
			  Socket sock = new Socket("192.168.1.141", 9100); // ip and port of printer
			  PrintWriter oStream = new PrintWriter(sock.getOutputStream());
			  oStream.println("\t\t Text to The Printer");
			  oStream.println("\n\n\n");
			  oStream.close();
			  sock.close();
			} catch (UnknownHostException e) {
			  e.printStackTrace();
			} catch (IOException e) {
			  e.printStackTrace();
			}

	}

	
}
