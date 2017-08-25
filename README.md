# Realm & Handler Test

### 練習使用 Realm 串接 Handler 

1. 透過background thread 來接收按鍵響應，來判斷是否需要儲存還是要查詢
2. 按下button1 為查詢,在主執行續 uihandler查詢完畢後，將結果傳送至textview顯示結果
3. 按下button2 為儲存，目前是將資料寫死，未來可以增加EditText或是其他資料來源做 Realm database insert
4. 按下button3 為模擬onDestory()時，要關閉Realm 以及 backgroundHandler
