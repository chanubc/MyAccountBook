package com.example.myaccountbook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myaccountbook.SpendActivity
import java.text.SimpleDateFormat
import java.util.*

class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if(intent.action.equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            var bundle = intent.extras!!
            var messages =parseMessage(bundle)

            if(messages != null){
                var phone_call = messages[0]?.originatingAddress.toString()
                //농협 카드(15881600)에만 반응
                if(phone_call == "15881600"){
                    var content = messages[0]?.messageBody.toString()
                    var vec = content.split("\n")
                    //문자의 줄이 5줄일 때만 작동
                    if(vec.size == 5) {
                        var smsAmount = vec[2].replace("[^0-9]".toRegex(), "")
                        var nYear =
                            SimpleDateFormat("yyyy-").format(Date(System.currentTimeMillis()))
                        var nSec = SimpleDateFormat(":ss").format(Date(System.currentTimeMillis())) // 초 정보는 현재 초를 사용
                        var smsDandT = (nYear + vec[3].replace('/', '-') + nSec).split(" ")
                        var smsDate = smsDandT[0] // 날짜 정보 ex) 2022-06-05
                        var smsTime = smsDandT[1] // 시간 정보 ex) 15:23:16
                        var smsMemo = vec[4]
                        var ddd = smsAmount + " / " + smsDate + " / " + smsMemo
                        //문자 내용을 파싱하여 특정 액티비티에 전달(지출 기록)
                        //실 사용시, 해당 액티비티로 수정
                        val intent = Intent(context, SpendActivity::class.java)
                        intent.putExtra("smsDate", smsDate)
                        intent.putExtra("smsTime", smsTime)
                        intent.putExtra("smsAmount", smsAmount)
                        intent.putExtra("smsMemo", smsMemo)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        ContextCompat.startActivity(context, intent, null)
                    }
                }

            }
        }
    }

    private fun parseMessage(bundle: Bundle): Array<SmsMessage?> {
        val temp = bundle["pdus"] as Array<Any>
        val messages: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(temp!!.size)
        for (i in temp!!.indices) {
            val format = bundle.getString("format")
            messages[i] = SmsMessage.createFromPdu(temp[i] as ByteArray, format)
        }
        return messages
    }
}