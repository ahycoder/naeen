<?php
date_default_timezone_set("Asia/Tehran");
$diff = abs(strtotime(date("Y-m-d H:i:s")) - strtotime('2019-05-11 08:43:00'));
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
$key =$_POST['Key'];

if($key=="UpdateDB"){
    $resultFinal=array();
    $db = new Db();
    $resultImage = $db->query("SELECT * FROM banners");
    $resultPartMatch = $db->query("SELECT * FROM partmatch");
    $resultAnswerable = $db->query("SELECT * FROM answerabls");
    $needscat = $db->query("SELECT * FROM needscat");
    $city = $db->query("SELECT * FROM city");
    $db->close();
    $resultFinal[0]['banner']=$resultImage;
    $resultFinal[0]['partMatch']=$resultPartMatch;
    $resultFinal[0]['answerable']=$resultAnswerable;
    $resultFinal[0]['needscat']=$needscat;
    $resultFinal[0]['city']=$city;
    echo json_encode($resultFinal);
}
