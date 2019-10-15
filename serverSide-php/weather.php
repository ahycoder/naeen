<?php
//header('Content-Type: text/html; charset=utf-8');
date_default_timezone_set("Asia/Tehran");

require_once(getcwd() . '/system/db.php');
require_once(getcwd() . '/config.php');


$key =$_POST['Key'];
if($key=="weather"){
    $db = new Db();
    $City =$_POST['City'];
    if ($City=='1'/*naeen*/ || $City=='4'/*bafran*/ || $City=='6'/*mohamadieh*/){
        $result = $db->query("SELECT * FROM weather WHERE weather_id=1 OR weather_id=2 OR weather_id=3 OR weather_id=4 OR weather_id=5 OR weather_id=21");
    }
    if ($City=='2'/*khor*/){
        $result = $db->query("SELECT * FROM weather WHERE weather_id=6 OR weather_id=7 OR weather_id=8 OR weather_id=9 OR weather_id=10 OR weather_id=22");
    }
    if ($City=='3'/*anarak*/){
        $result = $db->query("SELECT * FROM weather WHERE weather_id=11 OR weather_id=12 OR weather_id=13 OR weather_id=14 OR weather_id=15 OR weather_id=23");
    }

    if ($City=='5'/*nistanak*/){
        $result = $db->query("SELECT * FROM weather WHERE weather_id=16 OR weather_id=17 OR weather_id=18 OR weather_id=19 OR weather_id=20 OR weather_id=24");
    }
    $db->close();
    echo json_encode($result);
}
