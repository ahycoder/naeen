<?php
require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');
require_once(getcwd() . '/town.php');

$key =$_POST['Key'];
$result=array();
if($key=="AdvertismentMain"){
    $db = new Db();
    $result = $db->query("SELECT * FROM advertisment");
    $db->close();
    echo json_encode($result);
}
if($key=="CurrentWeather"){
    echo "sdcsdc";
}
//AdvertismentMain