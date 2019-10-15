<?php
//header('Content-Type: text/html; charset=utf-8');
date_default_timezone_set("Asia/Tehran");
require 'vendor/autoload.php';
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');
use Kavenegar\KavenegarApi;

$Key=$_POST["Key"];
$userId=$_POST["userId"];
$chanceSecondTimeForLogin=3*60;
if($Key=="GetMobile"){
    $mobile=$_POST["mobile"];
    $verifyCode =rand(11111, 99999);
    $date=date("Y-m-d H:i:s");
    $finalArray=array();
    try{
        $api = new KavenegarApi("6C75512B38784C76636B382B2F7A4558536A757333673D3D");
        $sender = "1000596446";//1000596446
        $message = "کد فعالسازی : ".$verifyCode;
        $receptor = $mobile;
        //$result = $api->Send($sender,$receptor,$message);
        $result = true;
        if($result){
            $db = new Db();
            $result = $db->insert("INSERT INTO login (login_mobile,login_verifyCode,login_userId,login_date) VALUES
       (:login_mobile,:login_verifyCode,:login_userId,:login_date)", array(
                'login_mobile' => $mobile,
                'login_verifyCode' => $verifyCode,
                'login_userId' => $userId,
                'login_date' => $date
            ));
            $db->close();
            $finalArray[0]='200';
            $finalArray[1]=$chanceSecondTimeForLogin*1000;
            echo json_encode($finalArray);
        }else{
            $finalArray[0]='400';
            $finalArray[1]="عملیات ثبت نام ناموفق بود دقایقی دیگر تلاش کنید";
            echo json_encode($finalArray);
        }
    }
    catch(Exception $e){
        $finalArray[0]='400';
        $finalArray[1]="عملیات ثبت نام ناموفق بود دقایقی دیگر 2 تلاش کنید";
        echo json_encode($finalArray);
    }
}
if($Key=="SendCode"){
    $verifyCodeAndroid=$_POST["verifyCode"];
    $db = new Db();
    $results = $db->query("SELECT login_verifyCode,login_date,login_mobile FROM login WHERE login_userId LIKE (:login_userId) ORDER BY login_id DESC LIMIT 1", array(
        'login_userId' => $userId
    ));
    $db->close();
    $finalArray=array();
    foreach ($results as $result) {
        $login_verifyCode = $result['login_verifyCode'];
        $login_date = $result['login_date'];
        $login_mobile = $result['login_mobile'];
        $diff = abs(strtotime(date("Y-m-d H:i:s")) - strtotime($login_date));
        if ($diff < 50*60*60) { //time to get SMS
            if ($verifyCodeAndroid . "" == $login_verifyCode . "") {
                $db = new Db();
                $login_isOk=1;
                $result = $db->insert("UPDATE login SET login_isOk=:login_isOk WHERE login_userId LIKE :login_userId", array(
                    'login_isOk' => $login_isOk,
                    'login_userId' => $userId
                ));
                $login_isOk=0;
                $result = $db->insert("UPDATE login SET login_isOk=:login_isOk WHERE login_mobile LIKE :login_mobile AND login_userId NOT LIKE :login_userId", array(
                    'login_isOk' => $login_isOk,
                    'login_mobile' => $login_mobile,
                    'login_userId' => $userId
                ));
                $db->close();
                $finalArray[0]='200';
                echo json_encode($finalArray);
            } else {
                $finalArray[0]='400';
                echo json_encode($finalArray);
            }
        }else{
            //No Time
            $finalArray[0]='300';
            echo json_encode($finalArray);
        }
    }
}
if($Key=="CheckLoginIsOk"){
    $db = new Db();
    $results = $db->query("SELECT login_isOk FROM login WHERE login_userId LIKE (:login_userId) ORDER BY login_id DESC LIMIT 1", array(
        'login_userId' => $userId
    ));
    $db->close();
    if($results[0]['login_isOk']=="1"){
        echo '200';
    }else{
        echo '400';
    }
}

if($Key=="RemindTime"){
    $db = new Db();
    $results = $db->query("SELECT login_date FROM login WHERE login_userId LIKE (:login_userId) ORDER BY login_id DESC LIMIT 1", array(
        'login_userId' => $userId
    ));
    $db->close();
        $diff = abs(strtotime(date("Y-m-d H:i:s")) - strtotime($results[0]['login_date']));
    $finalArray=array();
        if ($diff < $chanceSecondTimeForLogin) {
            $finalArray[0]='200';
            $finalArray[1]=($chanceSecondTimeForLogin-$diff)*1000;
            echo json_encode($finalArray);
        }else{
            $finalArray[0]='400';
            echo json_encode($finalArray);
        }
}
if($Key=="completeLogin"){
    $login_userName=$_POST["login_userName"];
    $login_imgUserUrl=$_POST["login_imgUserUrl"];
    $db = new Db();
    $result = $db->insert("UPDATE login SET login_userName=:login_userName,login_imgUserUrl=:login_imgUserUrl WHERE login_userId LIKE :login_userId", array(
        'login_userName' => $login_userName,
        'login_imgUserUrl' => $login_imgUserUrl,
        'login_userId' => $userId
    ));
    $db->close();
}

if($Key=="ChangeUserName"){
    $login_userName=$_POST["login_userName"];
    $db = new Db();
    $result = $db->insert("UPDATE login SET login_userName=:login_userName WHERE login_userId LIKE :login_userId", array(
        'login_userName' => $login_userName,
        'login_userId' => $userId
    ));
    $db->close();
    if($result=="1"){
        echo '200';
    }else{
        echo '400';
    }
}
