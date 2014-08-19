WindowManagerSample
===================
skypeのような通話アプリの着信時の動作を検討するプロジェクトです。

## 着信時の要件
- アプリ利用時、アプリの前面に着信画面が出る
- ロック時、ロック画面の前面に着信画面が出る
- ロック画面で着信画面がでてるときに、ロック解除を(なんらかの方法で)したとき、着信画面が出る
- 画面OFF時、画面がONになり着信画面が出る

## 着信画面の要件
- ボタンが押せる

## やらないこと  
以下は着信画面表示をキャンセルするので検証しない
- 電話中の着信
- skype等の通話アプリで通話中の着信

## 動作の流れ
- Serviceが着信を受ける
- Serviceは着信音を鳴らす
- ServiceはActivityを呼び出す
- Activityは以下をやる
  - スクリーンOFF解除
  - ロック解除
    - !!! ココでロック解除できない問題が発生 詳細は別途 !!!
- Activityで受話器を取ったらServiceにIntentで知らせる
- Serviceは受話器を取ったIntentを受けたら、着信音を止める
- ActivityのonStop時
  - Activityが終了した旨のIntentをServiceに投げる
  - Activityをfinishする(ホームキーが押された場合に殺すため)
- ServiceはActivity終了のIntentを受け、かつ、受話器を取っていない場合、SystemAlertレイヤーに着信画面を出す
  - このときのユーザ見えの動作
    - 画面はAndroidのロック画面になる
    - ロックを解除すると、SystemAlertレイヤーの着信画面が表示されている
- SystemAlertレイヤーの着信画面で受話器を取る

## ロック解除できない問題
- 現象
  - Activityでロック解除がうまくいかない
  - そのときActivityはonStart/onStopが高速に呼ばれ、その後またonStartが呼ばれている
  - 高速onStopにより
    - Activityが死ぬため、着信画面が表示されない
    - SystemAlertレイヤーの着信画面が無駄に出る
- 改善策
  - 高速onStopを判定して、Activityを殺さないようにする
  - 判定方法: onStart-onStopの時間が0.3秒以内の場合、高速onStopとみなす
